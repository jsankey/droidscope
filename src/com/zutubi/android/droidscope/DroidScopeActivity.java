package com.zutubi.android.droidscope;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.zutubi.android.libpulse.ProjectStatus;

/**
 * The main activity for DroidScope: shows a list of projects with their health
 * on refresh.
 */
public class DroidScopeActivity extends ActivitySupport implements OnItemClickListener
{
    private static final int ID_CONTEXT_TRIGGER = 1;
 
    private ProjectStatusAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

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
            startActivity(new Intent(this, SetupConnectionActivity.class));
        }
        else
        {
            long timestamp = projectStatusCache.getOldestTimestamp();
            if (timestamp == -1)
            {
                timestamp = lastRefreshTime;
            }
            
            if (settings.isRefreshOnResume() && isStale(timestamp))
            {
                refresh();
            }
        }
    }

    private void updateList()
    {
        adapter.clear();
        for (ProjectStatus state : projectStatusCache.getAll())
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
        intent.putExtra(ProjectActivity.PARAM_PROJECT_NAME, statusView.getStatus().getProjectName());
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.context_title);
        menu.add(0, ID_CONTEXT_TRIGGER, 0, getText(R.string.trigger));
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
                    refresh();
                }
                break;

            case R.id.settings:
                Intent i = new Intent(this, Preferences.class);
                startActivity(i);
                break;
        }

        return true;
    }

    private void refresh()
    {
        RefreshTask task = new RefreshTask();
        task.execute();
    }

    private boolean checkSettings()
    {
        if (settingsIncomplete())
        {
            startActivity(new Intent(this, SetupConnectionActivity.class));
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
    protected void postRefresh()
    {
        updateList();
    }
    
    public void setSettings(ISettings settings)
    {
        this.settings = settings;
    }
}
