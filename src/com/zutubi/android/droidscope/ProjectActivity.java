package com.zutubi.android.droidscope;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.zutubi.android.libpulse.BuildResult;
import com.zutubi.android.libpulse.ProjectStatus;

/**
 * Displays information about a single project.
 */
public class ProjectActivity extends ActivitySupport
{
    public static final String PARAM_PROJECT_NAME = "project.name";
    
    private String projectName;
    private LinearLayout containerLayout;
    private ImageView healthImage;
    private ProjectStatusCache projectStatusCache;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        projectStatusCache = DroidScopeApplication.getProjectStatusCache();
        
        setContentView(R.layout.project);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            projectName = extras.getString(PARAM_PROJECT_NAME);
            showProject();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        showProject();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.refresh:
            {
                RefreshTask task = new RefreshTask();
                task.execute(projectName);
                break;
            }
            case R.id.trigger:
            {
                TriggerTask task = new TriggerTask();
                task.execute(projectName);
                break;
            }
        }

        return true;
    }

    @Override
    protected void postRefresh()
    {
        showProject();
    }
    
    private void showProject()
    {
        containerLayout = (LinearLayout) findViewById(R.id.project_container);
        healthImage = (ImageView) findViewById(R.id.project_health_icon);

        ProjectStatus status = projectStatusCache.findByProjectName(projectName);
        if (status != null)
        {
            healthImage.setImageResource(UiUtils.healthToResourceId(status.getHealth()));
            healthImage.setAnimation(UiUtils.getProjectAnimation(status));
            setTextById(R.id.project_name, status.getProjectName());
            BuildResult currentBuild = showBuildIfPresent(status.getRunningBuild(), R.id.project_current_build_table);
            BuildResult completedBuild = showBuildIfPresent(status.getLatestCompletedBuild(),
                    R.id.project_completed_build_table);
            if (currentBuild == null && completedBuild == null)
            {
                TextView textView = new TextView(this);
                textView.setText(R.string.project_never_built);
                containerLayout.addView(textView);
            }
        }
        
        containerLayout.invalidate();
    }

    private BuildResult showBuildIfPresent(BuildResult build, int tableId)
    {
        TableLayout buildTable = (TableLayout) findViewById(tableId);
        if (build == null)
        {
            buildTable.setVisibility(View.GONE);
        }
        else
        {
            addBuildDetailsToTable(build, buildTable);
            buildTable.setVisibility(View.VISIBLE);
        }
        return build;
    }

    private void addBuildDetailsToTable(BuildResult build, TableLayout table)
    {
        table.removeViews(1, table.getChildCount() - 1);
        table.addView(createRow(R.string.build_id, build.getNumber()));
        table.addView(createRow(R.string.build_status, build.getStatus().pretty()));
        if (build.getRevision() != null)
        {
            table.addView(createRow(R.string.build_revision, build.getRevision()));
        }
        table.addView(createRow(R.string.build_tests, build.getTests()));
        if (build.getStartTime() >= 0)
        {
            table.addView(createRow(R.string.build_when, DateUtils.getRelativeDateTimeString(this, build.getStartTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.YEAR_IN_MILLIS, 0)));
        }

        if (build.getEndTime() >= 0)
        {
            table.addView(createRow(R.string.build_elapsed, DateUtils.formatElapsedTime((build.getEndTime() - build.getStartTime()) / 1000)));
        }
    }
    
    private TableRow createRow(int nameId, Object value)
    {
        LayoutInflater inflator = getLayoutInflater();
        TableRow row = (TableRow) inflator.inflate(R.layout.detail_row, null);
        ((TextView) row.getChildAt(0)).setText(nameId);
        ((TextView) row.getChildAt(1)).setText(value.toString());
        return row;
    }

    private void setTextById(int id, Object textValue)
    {
        TextView textView = (TextView) findViewById(id);
        if (textView != null)
        {
            textView.setText(textValue.toString());
        }
    }
    
    public void setProjectStatusCache(ProjectStatusCache projectStatusCache)
    {
        this.projectStatusCache = projectStatusCache;
    }
}
