package com.zutubi.android.droidscope.test;

import java.util.ArrayList;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.zutubi.android.droidscope.DroidScopeActivity;
import com.zutubi.android.droidscope.DroidScopeApplication;
import com.zutubi.android.droidscope.ProjectStatusCache;
import com.zutubi.android.droidscope.ProjectStatusView;
import com.zutubi.android.droidscope.R;

public class DroidScopeActivityTest extends ActivityInstrumentationTestCase2<DroidScopeActivity>
{
    private DroidScopeActivity activity;
    private Instrumentation    instrumentation;
    private FakePulse          pulse;
    private FakeSettings       settings;
    private View               list;

    public DroidScopeActivityTest()
    {
        super("com.zutubi.android.droidscope", DroidScopeActivity.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        
        setActivityInitialTouchMode(false);
        instrumentation = getInstrumentation();
        instrumentation.waitForIdleSync();
        
        pulse = new FakePulse();
        
        settings = new FakeSettings("http://localhost", "admin", "admin");
        DroidScopeApplication.setSettings(settings);
        DroidScopeApplication.setProjectStatusCache(new ProjectStatusCache());
        DroidScopeApplication.setPulse(pulse);

        // Initialises the activity.
        activity = getActivity();

        list = activity.findViewById(R.id.list);
    }
    
    public void testNoProjects() throws Throwable
    {
        pulse.setWaitOnProjectStatuses(true);
        instrumentation.invokeMenuActionSync(activity, R.id.refresh, 0);
        instrumentation.waitForIdleSync();
        assertTrue(activity.isInProgress());
        pulse.releaseGetAllProjectStatuses();
        waitForRefreshToComplete();
        assertEquals(0, list.getTouchables().size());
    }

    public void testProjects() throws Throwable
    {
        pulse.setProjectNames("p1", "p2");

        instrumentation.invokeMenuActionSync(activity, R.id.refresh, 0);
        instrumentation.waitForIdleSync();
        waitForRefreshToComplete();
        ArrayList<View> listItems = list.getTouchables();
        assertEquals(2, listItems.size());
        assertEquals("p1: ok", ((ProjectStatusView) listItems.get(0)).getStatus().toString());
        assertEquals("p2: ok", ((ProjectStatusView) listItems.get(1)).getStatus().toString());
    }
    
    public void testNoRefreshOnResume() throws Throwable
    {
        assertEquals(0, list.getTouchables().size());

        pulse.setProjectNames("p1");
        settings.setStaleAge(0);
        activity.setLastRefreshTime(System.currentTimeMillis());
        
        pauseAndResume();
        
        assertEquals(0, list.getTouchables().size());
    }

    public void testRefreshOnResume() throws Throwable
    {
        assertEquals(0, list.getTouchables().size());

        pulse.setProjectNames("p1");
        settings.setRefreshOnResume(true);
        settings.setStaleAge(0);
        activity.setLastRefreshTime(System.currentTimeMillis() - 1000000);
        
        pauseAndResume();
        
        assertEquals(1, list.getTouchables().size());
    }

    public void testRefreshOnResumeNotStale() throws Throwable
    {
        assertEquals(0, list.getTouchables().size());

        pulse.setProjectNames("p1");
        pulse.setTimestamp(Long.MAX_VALUE);
        settings.setRefreshOnResume(true);
        settings.setStaleAge(1000000);
        activity.setLastRefreshTime(System.currentTimeMillis());
        
        pauseAndResume();
        
        assertEquals(0, list.getTouchables().size());
    }

    private void pauseAndResume()
    {
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                instrumentation.callActivityOnPause(activity);
                instrumentation.callActivityOnResume(activity);
            }
        });

        waitForRefreshToComplete();
    }

    private void waitForRefreshToComplete()
    {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 5000)
        {
            if (!activity.isInProgress())
            {
                getInstrumentation().waitForIdleSync();
                return;
            }

            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
            }
        }

        fail("Timed out waiting for refresh to complete");
    }
}
