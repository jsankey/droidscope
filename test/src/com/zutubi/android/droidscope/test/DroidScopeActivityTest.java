package com.zutubi.android.droidscope.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.xmlrpc.android.XMLRPCException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.zutubi.android.droidscope.DroidScopeActivity;
import com.zutubi.android.droidscope.ISettings;
import com.zutubi.android.droidscope.R;
import com.zutubi.android.libpulse.BuildResult;
import com.zutubi.android.libpulse.IPulse;
import com.zutubi.android.libpulse.ProjectStatus;
import com.zutubi.android.libpulse.ResultStatus;
import com.zutubi.android.libpulse.internal.IPulseClient;

public class DroidScopeActivityTest extends ActivityInstrumentationTestCase2<DroidScopeActivity>
{
    private DroidScopeActivity activity;
    private View               list;
    private FakePulse          pulse;

    public DroidScopeActivityTest()
    {
        super("com.zutubi.android.droidscope", DroidScopeActivity.class);
        System.setProperty(DroidScopeActivity.PROPERTY_TEST_MODE, Boolean.TRUE.toString());
    }

    @Override
    protected void setUp() throws Exception
    {
        getInstrumentation().setInTouchMode(false);
        activity = getActivity();
        activity.setSettings(new FakeSettings("http://localhost", "admin", "admin"));
        list = activity.findViewById(R.id.list);
        pulse = new FakePulse();
        activity.setPulse(pulse);
    }

    public void testIncompleteSettings() throws Throwable
    {
        activity.setSettings(new FakeSettings("", "admin", "admin"));
        sendKeys(KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_R);
        getInstrumentation().waitForIdleSync();
        Dialog errorDialog = activity.getVisibleDialog();
        assertNotNull(errorDialog);
        TextView messageView = (TextView) errorDialog.getWindow().findViewById(android.R.id.message);
        assertNotNull(messageView);
        assertEquals(activity.getText(R.string.error_settings_incomplete), messageView.getText());
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
        assertEquals("p1: ok", ((TextView) listItems.get(0)).getText());
        assertEquals("p2: ok", ((TextView) listItems.get(1)).getText());
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

    private static class FakeSettings implements ISettings
    {
        String url;
        String username;
        String password;

        public FakeSettings(String url, String username, String password)
        {
            this.url = url;
            this.username = username;
            this.password = password;
        }

        @Override
        public String getURL()
        {
            return url;
        }

        @Override
        public String getUsername()
        {
            return username;
        }

        @Override
        public String getPassword()
        {
            return password;
        }
    }

    private static class FakePulse implements IPulse
    {
        private Semaphore getAllProjectStatusesFlag = new Semaphore(0);
        private String[]  projectNames              = new String[0];

        @Override
        public List<ProjectStatus> getAllProjectStatuses()
        {
            try
            {
                getAllProjectStatusesFlag.acquire();
                return getProjectStatuses(projectNames);
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }

        private List<ProjectStatus> getProjectStatuses(String[] names)
        {
            List<ProjectStatus> statuses = new LinkedList<ProjectStatus>();
            for (String name: names)
            {
                statuses.add(new ProjectStatus(name, new BuildResult(1, ResultStatus.SUCCESS, 100), null));
            }
            
            return statuses;
        }

        @Override
        public List<ProjectStatus> getMyProjectStatuses()
        {
            // TODO Auto-generated method stub
            return null;
        }

        public void releaseGetAllProjectStatuses()
        {
            getAllProjectStatusesFlag.release();
        }

        public void setProjectNames(String... projectNames)
        {
            this.projectNames = projectNames;
        }

        @Override
        public void close() throws IOException
        {
        }

    }
}
