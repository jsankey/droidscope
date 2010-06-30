package com.zutubi.android.droidscope;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.zutubi.android.libpulse.IPulse;
import com.zutubi.android.libpulse.ProjectStatus;

/**
 * Helper base class for implementation of activities.  Includes common
 * functionality such as refreshing and triggering projects.
 */
public abstract class ActivitySupport extends Activity
{
    protected ProjectStatusCache projectStatusCache;
    protected Dialog visibleDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        projectStatusCache = DroidScopeApplication.getProjectStatusCache();
    }

    protected void showErrorDialog(String message)
    {
        visibleDialog.hide();
        visibleDialog = new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.error).setMessage(message).setPositiveButton(android.R.string.ok,
                                        new OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                visibleDialog = null;
                                            }
                                        }).create();
        visibleDialog.show();
    }

    public Dialog getVisibleDialog()
    {
        return visibleDialog;
    }

    public void hideDialog()
    {
        visibleDialog.hide();
        visibleDialog = null;
    }

    /**
     * Triggers a project in the background, so as not to hold up the UI.
     */
    protected class TriggerTask extends AsyncTask<String, Integer, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                DroidScopeApplication.getPulse().triggerBuild(params[0]);
            }
            catch (Exception e)
            {
                return e.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String errorMessage)
        {
            if (errorMessage == null)
            {
                Toast.makeText(getApplicationContext(), R.string.triggered, Toast.LENGTH_SHORT).show();
            }
            else
            {
                showErrorDialog("Unable to trigger project: " + errorMessage);
            }
        }
    }

    /**
     * Callback invoked after a successful refresh.  The refreshed information
     * can be found in the status cache.
     */
    protected abstract void postRefresh();
    
    /**
     * Handles talking to the Pulse server in the background.
     */
    protected class RefreshTask extends AsyncTask<String, Integer, RefreshResult>
    {
        @Override
        protected void onPreExecute()
        {
            visibleDialog = ProgressDialog.show(ActivitySupport.this, "", getText(R.string.refreshing), true);
        }

        @Override
        protected RefreshResult doInBackground(String... params)
        {
            IPulse pulse = DroidScopeApplication.getPulse();
            try
            {
                if (params.length == 0)
                {
                    List<ProjectStatus> result = pulse.getAllProjectStatuses();
                    projectStatusCache.clear();
                    projectStatusCache.putAll(result);
                }
                else
                {
                    for (String project: params)
                    {
                        projectStatusCache.put(pulse.getProjectStatus(project));
                    }
                }
                return new RefreshResult();
            }
            catch (RuntimeException e)
            {
                return new RefreshResult(e);
            }
        }

        @Override
        protected void onPostExecute(RefreshResult result)
        {
            if (result.isSuccessful())
            {
                postRefresh();
                hideDialog();
            }
            else if (result.error != null)
            {
                showErrorDialog(result.error.getMessage());
            }
            else
            {
                hideDialog();
            }
        }
    }

    private static class RefreshResult
    {
        private boolean successful;
        private Exception error;

        public RefreshResult()
        {
            this.successful = true;
        }

        public RefreshResult(Exception error)
        {
            this.successful = false;
            this.error = error;
        }
        
        public boolean isSuccessful()
        {
            return successful;
        }
    }
}
