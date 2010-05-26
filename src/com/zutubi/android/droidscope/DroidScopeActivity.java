package com.zutubi.android.droidscope;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xmlrpc.android.XMLRPCException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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

import com.zutubi.android.droidscope.ProjectState.Health;

/**
 * The main activity for DroidScope: shows a list of projects with their health
 * on refresh.
 */
public class DroidScopeActivity extends Activity implements OnSharedPreferenceChangeListener
{
    private ISettings                  settings;
    private ArrayAdapter<ProjectState> adapter;
    private IPulseClient               client;
    private Dialog                     visibleDialog;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
        settings = new PreferencesSettings(preferences);
        setContentView(R.layout.main);
        ListView list = (ListView) findViewById(R.id.list);
        adapter = new ArrayAdapter<ProjectState>(this, R.layout.project);
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
                    }).show();
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
            if (client != null)
            {
                client.close();
            }
        }
        catch (IOException e)
        {
        }

        client = null;
        settings = new PreferencesSettings(preferences);
    }

    public Dialog getVisibleDialog()
    {
        return visibleDialog;
    }

    public void setClient(IPulseClient client)
    {
        this.client = client;
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
            if (client == null)
            {
                client = new PulseClient(settings);
            }

            try
            {
                List<ProjectState> result = new LinkedList<ProjectState>();
                Object[] projects = client.getAllProjectNames();
                for (Object project : projects)
                {
                    String projectName = (String) project;
                    result.add(new ProjectState(projectName, getProjectHealth(client, projectName)));
                }

                return new RefreshResult(result);
            }
            catch (XMLRPCException e)
            {
                return new RefreshResult(e);
            }
        }

        private Health getProjectHealth(IPulseClient client, String projectName) throws XMLRPCException
        {
            Object[] builds = client.getLatestBuildsForProject(projectName, true, 1);
            if (builds.length == 0)
            {
                return Health.UNKNOWN;
            }
            else
            {
                @SuppressWarnings("unchecked")
                Map<String, Object> build = (Map<String, Object>) builds[0];
                if ((Boolean) build.get("succeeded"))
                {
                    return Health.OK;
                }
                else
                {
                    return Health.BROKEN;
                }
            }
        }

        @Override
        protected void onPostExecute(RefreshResult result)
        {
            adapter.clear();
            if (result.states != null)
            {
                for (ProjectState state : result.states)
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
                                }).show();
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
        private List<ProjectState> states;
        private XMLRPCException    error;

        public RefreshResult(List<ProjectState> states)
        {
            this.states = states;
        }

        public RefreshResult(XMLRPCException error)
        {
            this.error = error;
        }
    }
}
