package com.zutubi.android.droidscope;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.xmlrpc.android.XMLRPCException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.app.KeyguardManager.KeyguardLock;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.zutubi.android.libpulse.Health;
import com.zutubi.android.libpulse.IPulse;
import com.zutubi.android.libpulse.ProjectStatus;
import com.zutubi.android.libpulse.internal.IPulseClient;
import com.zutubi.android.libpulse.internal.Pulse;
import com.zutubi.android.libpulse.internal.PulseClient;

/**
 * The main activity for DroidScope: shows a list of projects with their health
 * on refresh.
 */
public class DroidScopeActivity extends Activity implements OnSharedPreferenceChangeListener
{
    public static final String PROPERTY_TEST_MODE = "droidscope.test";
    
    private ISettings                   settings;
    private ArrayAdapter<ProjectStatus> adapter;
    private IPulse                      pulse;
    private Dialog                      visibleDialog;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    
        if (Boolean.getBoolean(PROPERTY_TEST_MODE))
        {
            KeyguardManager keyGuardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            KeyguardLock lock = keyGuardManager.newKeyguardLock(getClass().getName());
            lock.disableKeyguard();
        }
        
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
        settings = new PreferencesSettings(preferences);
        setContentView(R.layout.main);
        ListView list = (ListView) findViewById(R.id.list);
        adapter = new ArrayAdapter<ProjectStatus>(this, R.layout.project);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.refresh:
                if (checkSettings())
                {
                    RefreshTask task = new RefreshTask();
                    task.execute();
                }
                break;

            case R.id.settings:
                Intent i = new Intent(this, Preferences.class);
                startActivity(i);
                break;
        }

        return true;
    }

    private boolean checkSettings()
    {
        if (settings.getURL().length() == 0 || settings.getUsername().length() == 0)
        {
            visibleDialog = new AlertDialog.Builder(this).setTitle(R.string.error).setMessage(
                    getText(R.string.error_settings_incomplete)).setPositiveButton(android.R.string.ok,
                    new OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            visibleDialog = null;
                        }
                    }).create();
            visibleDialog.show();
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key)
    {
        try
        {
            if (pulse != null)
            {
                pulse.close();
            }
        }
        catch (IOException e)
        {
        }

        pulse = null;
        settings = new PreferencesSettings(preferences);
    }

    public Dialog getVisibleDialog()
    {
        return visibleDialog;
    }

    public void setPulse(IPulse pulse)
    {
        this.pulse = pulse;
    }

    public void setSettings(ISettings settings)
    {
        this.settings = settings;
    }

    /**
     * Handles talking to the Pulse server in the background.
     */
    private class RefreshTask extends AsyncTask<Object, Integer, RefreshResult>
    {
        @Override
        protected void onPreExecute()
        {
            visibleDialog = ProgressDialog.show(DroidScopeActivity.this, "", getText(R.string.refreshing), true);
        }

        @Override
        protected RefreshResult doInBackground(Object... params)
        {
            if (pulse == null)
            {
                pulse = new Pulse(new PulseClient(settings.getURL(), settings.getUsername(), settings.getPassword()));
            }

            try
            {
                List<ProjectStatus> result = pulse.getAllProjectStatuses();
                return new RefreshResult(result);
            }
            catch (RuntimeException e)
            {
                return new RefreshResult(e);
            }
        }

        @Override
        protected void onPostExecute(RefreshResult result)
        {
            adapter.clear();
            if (result.statuses != null)
            {
                for (ProjectStatus state : result.statuses)
                {
                    adapter.add(state);
                }

                adapter.notifyDataSetChanged();
                visibleDialog.hide();
                visibleDialog = null;
            }
            else if (result.error != null)
            {
                visibleDialog.hide();
                visibleDialog = new AlertDialog.Builder(DroidScopeActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.error).setMessage(result.error.getMessage()).setPositiveButton(
                                android.R.string.ok, new OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        visibleDialog = null;
                                    }
                                }).create();
                visibleDialog.show();
            }
            else
            {
                visibleDialog.hide();
                visibleDialog = null;
            }
        }
    }

    private static class RefreshResult
    {
        private List<ProjectStatus> statuses;
        private Exception           error;

        public RefreshResult(List<ProjectStatus> statuses)
        {
            this.statuses = statuses;
        }

        public RefreshResult(Exception error)
        {
            this.error = error;
        }
    }
}
