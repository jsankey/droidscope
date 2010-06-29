package com.zutubi.android.droidscope.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;

import com.zutubi.android.droidscope.DroidScopeActivity;
import com.zutubi.android.droidscope.DroidScopeApplication;
import com.zutubi.android.droidscope.SetupConnectionActivity;

public class DroidScopeActivityLifeCycleTest extends ActivityInstrumentationTestCase2<DroidScopeActivity>
{
    private static final long TIMEOUT = 10000;
    
    public DroidScopeActivityLifeCycleTest()
    {
        super("com.zutubi.android.droidscope", DroidScopeActivity.class);
        System.setProperty(DroidScopeActivity.PROPERTY_TEST_MODE, Boolean.TRUE.toString());
    }
    
    public void testResumeWithIncompleteSettings()
    {
        DroidScopeApplication.setSettings(new FakeSettings("", "", ""));
        
        Instrumentation instrumentation = getInstrumentation();
        ActivityMonitor activityMonitor = new ActivityMonitor(SetupConnectionActivity.class.getName(), null, false);
        instrumentation.addMonitor(activityMonitor);

        getActivity();

        Activity setupActivity = activityMonitor.waitForActivityWithTimeout(TIMEOUT);
        assertNotNull(setupActivity);
        setupActivity.finish();
    }
}

