package com.zutubi.android.droidscope;

import java.io.IOException;
import java.util.List;

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
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.zutubi.android.libpulse.IPulse;
import com.zutubi.android.libpulse.ProjectStatus;
import com.zutubi.android.libpulse.internal.Pulse;
import com.zutubi.android.libpulse.internal.PulseClient;

/**
 * The main activity for DroidScope: shows a list of projects with their health
 * on refresh.
 */
public class DroidScopeActivity extends Activity implements OnSharedPreferenceChangeListener, OnItemClickListener
{
    public static final String PROPERTY_TEST_MODE = "droidscope.test";

    private static final int ID_CONTEXT_TRIGGER = 1;
    private static final int ID_DIALOG_CONNECTION = 1;

    private ISettings settings;
    private ProjectStatusAdapter adapter;
    private IPulse pulse;
    private Dialog visibleDialog;

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
        adapter = new ProjectStatusAdapter(this);
        list.setAdapter(adapter);
        registerForContextMenu(list);
        list.setOnItemClickListener(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        updateList();
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();

        if (settingsIncomplete())
        {
            showDialog(ID_DIALOG_CONNECTION);
        }
    }

    private void updateList()
    {
        adapter.clear();
        for (ProjectStatus state : ProjectStatusCache.getAll())
        {
            adapter.add(state);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> list, View view, int position, long id)
    {
        ProjectStatusView statusView = (ProjectStatusView) view;
        Intent intent = new Intent(this, ProjectActivity.class);
        intent.putExtra(ProjectActivity.PARAM_PROJECT_STATUS, statusView.getStatus().getProjectName());
        startActivity(intent);
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case ID_DIALOG_CONNECTION:
            {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.connection, (ViewGroup) findViewById(R.id.connection_root));
                
                final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DroidScopeActivity.this);
                populateField(preferences, layout, PreferencesSettings.PROPERTY_URL, R.id.connection_pulse_url);
                populateField(preferences, layout, PreferencesSettings.PROPERTY_USERNAME, R.id.connection_username);
                populateField(preferences, layout, PreferencesSettings.PROPERTY_PASSWORD, R.id.connection_password);
                
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.connection);
                builder.setView(layout);
                builder.setPositiveButton(android.R.string.ok, new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        AlertDialog alertDialog = (AlertDialog) dialog;
                        CharSequence url = ((TextView) alertDialog.findViewById(R.id.connection_pulse_url)).getText();
                        CharSequence username = ((TextView) alertDialog.findViewById(R.id.connection_username))
                                        .getText();
                        CharSequence password = ((TextView) alertDialog.findViewById(R.id.connection_password))
                                        .getText();

                        Editor editor = preferences.edit();
                        editor.putString(PreferencesSettings.PROPERTY_URL, url.toString());
                        editor.putString(PreferencesSettings.PROPERTY_USERNAME, username.toString());
                        editor.putString(PreferencesSettings.PROPERTY_PASSWORD, password.toString());
                        editor.commit();

                        hideDialog();
                    }
                });

                builder.setNegativeButton(android.R.string.cancel, new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        hideDialog();
                    }
                });

                AlertDialog dialog = builder.create();
                visibleDialog = dialog;
                return dialog;
            }
            default:
            {
                return null;
            }
        }
    }

    private void populateField(final SharedPreferences preferences, View layout, String property, int viewId)
    {
        String value = preferences.getString(property, null);
        if (value != null)
        {
            ((TextView) layout.findViewById(viewId)).setText(value);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, ID_CONTEXT_TRIGGER, 0, getText(R.string.context_trigger));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case ID_CONTEXT_TRIGGER:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                ProjectStatus status = adapter.getItem(info.position);
                TriggerTask task = new TriggerTask();
                task.execute(status.getProjectName());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
        if (settingsIncomplete())
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

    private boolean settingsIncomplete()
    {
        return settings.getURL().length() == 0 || settings.getUsername().length() == 0;
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

    private void showErrorDialog(String message)
    {
        visibleDialog.hide();
        visibleDialog = new AlertDialog.Builder(DroidScopeActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
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

    public void setPulse(IPulse pulse)
    {
        this.pulse = pulse;
    }

    public void setSettings(ISettings settings)
    {
        this.settings = settings;
    }

    /**
     * Triggers a project in the background, so as not to hold up the UI.
     */
    private class TriggerTask extends AsyncTask<String, Integer, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                pulse.triggerBuild(params[0]);
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
            if (errorMessage != null)
            {
                showErrorDialog("Unable to trigger project: " + errorMessage);
            }
        }
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
                ProjectStatusCache.clear();
                ProjectStatusCache.putAll(result);
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
                updateList();
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
