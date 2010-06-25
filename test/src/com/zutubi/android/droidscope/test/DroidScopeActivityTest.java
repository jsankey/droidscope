package com.zutubi.android.droidscope.test;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.View;

import com.zutubi.android.droidscope.DroidScopeActivity;
import com.zutubi.android.droidscope.ProjectStatusView;
import com.zutubi.android.droidscope.R;
import com.zutubi.android.droidscope.SettingsHolder;

public class DroidScopeActivityTest extends ActivityInstrumentationTestCase2<DroidScopeActivity>
{
    private DroidScopeActivity activity;
    private View               list;
    private FakePulse          pulse;
    private FakeSettings       settings;

    public DroidScopeActivityTest()
    {
        super("com.zutubi.android.droidscope", DroidScopeActivity.class);
        System.setProperty(DroidScopeActivity.PROPERTY_TEST_MODE, Boolean.TRUE.toString());
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        settings = new FakeSettings("http://localhost", "admin", "admin");
        SettingsHolder.setSettings(settings);

        setActivityInitialTouchMode(false);
        activity = getActivity();
        getInstrumentation().waitForIdleSync();

        list = activity.findViewById(R.id.list);
        pulse = new FakePulse();
        activity.setPulse(pulse);
    }
    
    public void testNoProjects() throws Throwable
    {
        sendKeys(KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_R);
        getInstrumentation().waitForIdleSync();
        ProgressDialog progressDialog = (ProgressDialog) activity.getVisibleDialog();
        assertNotNull(progressDialog);
        pulse.releaseGetAllProjectStatuses();
        waitForDialogToDisappear();
        assertEquals(0, list.getTouchables().size());
    }

    public void testProjects() throws Throwable
    {
        pulse.setProjectNames("p1", "p2");

        sendKeys(KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_R);
        getInstrumentation().waitForIdleSync();
        pulse.releaseGetAllProjectStatuses();
        waitForDialogToDisappear();
        ArrayList<View> listItems = list.getTouchables();
        assertEquals(2, listItems.size());
        assertEquals("p1: ok", ((ProjectStatusView) listItems.get(0)).getStatus().toString());
        assertEquals("p2: ok", ((ProjectStatusView) listItems.get(1)).getStatus().toString());
    }

    private void waitForDialogToDisappear()
    {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 5000)
        {
            if (activity.getVisibleDialog() == null)
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

        fail("Timed out waiting for dialog to disappear");
    }
}
