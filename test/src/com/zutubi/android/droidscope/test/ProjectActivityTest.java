package com.zutubi.android.droidscope.test;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.zutubi.android.droidscope.DroidScopeApplication;
import com.zutubi.android.droidscope.ProjectActivity;
import com.zutubi.android.droidscope.ProjectStatusCache;
import com.zutubi.android.droidscope.UiUtils;
import com.zutubi.android.libpulse.BuildResult;
import com.zutubi.android.libpulse.ProjectStatus;
import com.zutubi.android.libpulse.ResultStatus;
import com.zutubi.android.libpulse.TestSummary;

public class ProjectActivityTest extends ActivityInstrumentationTestCase2<ProjectActivity>
{
    private static final String PROJECT_NAME = "p1";

    private ProjectStatusCache statusCache;

    public ProjectActivityTest()
    {
        super("com.zutubi.android.droidscope", ProjectActivity.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        UiUtils.ENABLE_ANIMATIONS = false;
        
        statusCache = DroidScopeApplication.getProjectStatusCache();
        statusCache.clear();
        
        FakePulse pulse = new FakePulse();
        DroidScopeApplication.setPulse(pulse);
        DroidScopeApplication.setSettings(new FakeSettings("url", "user", "password"));
        
        Intent intent = new Intent();
        intent.putExtra(ProjectActivity.PARAM_PROJECT_NAME, PROJECT_NAME);
        setActivityIntent(intent);
        
        getInstrumentation().waitForIdleSync();
    }
    
    public void testProjectDoesNotExist()
    {
        ProjectActivity activity = getActivity();
        
        assertMessageVisible(activity, com.zutubi.android.droidscope.R.string.project_unknown);
    }
    
    public void testProjectNeverBuilt()
    {
        statusCache.put(new ProjectStatus(PROJECT_NAME, null, null, 0));

        ProjectActivity activity = getActivity();
        
        assertMessageVisible(activity, com.zutubi.android.droidscope.R.string.project_never_built);
    }
    
    public void testFirstBuildInProgress()
    {
        statusCache.put(new ProjectStatus(PROJECT_NAME, null, build(1, ResultStatus.IN_PROGRESS), 0));

        ProjectActivity activity = getActivity();
        
        assertBuildVisible(activity, com.zutubi.android.droidscope.R.id.project_current_build_table, 1);
        assertBuildNotVisible(activity, com.zutubi.android.droidscope.R.id.project_completed_build_table);
    }

    public void testFirstBuildComplete()
    {
        statusCache.put(new ProjectStatus(PROJECT_NAME, build(1, ResultStatus.SUCCESS), null, 0));

        ProjectActivity activity = getActivity();
        
        assertBuildNotVisible(activity, com.zutubi.android.droidscope.R.id.project_current_build_table);
        assertBuildVisible(activity, com.zutubi.android.droidscope.R.id.project_completed_build_table, 1);
    }

    public void testCompleteAndRunningBuilds()
    {
        statusCache.put(new ProjectStatus(PROJECT_NAME, build(1, ResultStatus.SUCCESS), build(2, ResultStatus.IN_PROGRESS), 0));

        ProjectActivity activity = getActivity();
        
        assertBuildVisible(activity, com.zutubi.android.droidscope.R.id.project_current_build_table, 2);
        assertBuildVisible(activity, com.zutubi.android.droidscope.R.id.project_completed_build_table, 1);
    }

    private BuildResult build(int id, ResultStatus status)
    {
        return new BuildResult(id, status, null, new TestSummary(), -1, -1, 0);
    }

    private void assertMessageNotVisible(ProjectActivity activity)
    {
        TextView messageText = (TextView) activity.findViewById(com.zutubi.android.droidscope.R.id.project_message);
        assertEquals(View.GONE, messageText.getVisibility());
    }

    private void assertMessageVisible(ProjectActivity activity, int stringId)
    {
        TextView messageText = (TextView) activity.findViewById(com.zutubi.android.droidscope.R.id.project_message);
        assertEquals(View.VISIBLE, messageText.getVisibility());
        assertEquals(activity.getString(stringId), messageText.getText());
        
        assertBuildNotVisible(activity, com.zutubi.android.droidscope.R.id.project_current_build_table);
        assertBuildNotVisible(activity, com.zutubi.android.droidscope.R.id.project_completed_build_table);
    }

    private void assertBuildNotVisible(ProjectActivity activity, int tableId)
    {
        assertEquals(View.GONE, activity.findViewById(tableId).getVisibility());
    }

    private void assertBuildVisible(ProjectActivity activity, int tableId, int buildId)
    {
        assertMessageNotVisible(activity);
        
        TableLayout buildTable = (TableLayout) activity.findViewById(tableId);
        assertEquals(View.VISIBLE, buildTable.getVisibility());
        TableRow row = (TableRow) buildTable.getChildAt(1);
        TextView idText = (TextView) row.getChildAt(1);
        assertEquals(Integer.toString(buildId), idText.getText());
    }
}
