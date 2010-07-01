package com.zutubi.android.droidscope.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.test.InstrumentationTestCase;

import com.zutubi.android.droidscope.DroidScopeActivity;
import com.zutubi.android.droidscope.DroidScopeApplication;
import com.zutubi.android.droidscope.SetupConnectionActivity;

public class DroidScopeActivityLifeCycleTest extends InstrumentationTestCase
{
    private static final long TIMEOUT = 10000;
    
    private Activity droidScopeActivity;
    private Activity setupActivity;
    
    public DroidScopeActivityLifeCycleTest()
    {
        super();
        System.setProperty(DroidScopeActivity.PROPERTY_TEST_MODE, Boolean.TRUE.toString());
    }
    
    @Override
    protected void tearDown() throws Exception
    {        
        if (droidScopeActivity != null)
        {
            droidScopeActivity.finish();
            droidScopeActivity = null;
        }

        if (setupActivity != null)
        {
            setupActivity.finish();
            setupActivity = null;
        }

        super.tearDown();
    }
    
    public void testResumeWithIncompleteSettings() throws InterruptedException
    {
        Instrumentation instrumentation = getInstrumentation();
        instrumentation.waitForIdleSync();
        
        DroidScopeApplication.setSettings(new FakeSettings("", "", ""));
        
        ActivityMonitor activityMonitor = new ActivityMonitor(SetupConnectionActivity.class.getName(), null, false);
        instrumentation.addMonitor(activityMonitor);

        droidScopeActivity = launchActivity("com.zutubi.android.droidscope", DroidScopeActivity.class, null);

        setupActivity = activityMonitor.waitForActivityWithTimeout(TIMEOUT);
        assertNotNull(setupActivity);
    }
}

