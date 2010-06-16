package com.zutubi.android.droidscope.test;

import android.app.Dialog;
import android.app.Instrumentation;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

import com.zutubi.android.droidscope.DroidScopeActivity;
import com.zutubi.android.droidscope.R;

public class DroidScopeActivityLifeCycleTest extends ActivityInstrumentationTestCase2<DroidScopeActivity>
{
    private DroidScopeActivity activity;

    public DroidScopeActivityLifeCycleTest()
    {
        super("com.zutubi.android.droidscope", DroidScopeActivity.class);
        System.setProperty(DroidScopeActivity.PROPERTY_TEST_MODE, Boolean.TRUE.toString());
    }
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        activity = getActivity();
    }

    public void testResumeWithIncompleteSettings()
    {
        Instrumentation instrumentation = getInstrumentation();
        instrumentation.callActivityOnPause(activity);
        
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        
        instrumentation.callActivityOnResume(activity);
        
        instrumentation.waitForIdleSync();
        Dialog setupDialog = activity.getVisibleDialog();
        assertNotNull(setupDialog);
        assertNotNull(setupDialog.findViewById(R.id.connection_pulse_url));
    }
}
