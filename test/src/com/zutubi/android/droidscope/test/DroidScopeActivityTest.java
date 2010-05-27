package com.zutubi.android.droidscope.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import org.xmlrpc.android.XMLRPCException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.zutubi.android.droidscope.DroidScopeActivity;
import com.zutubi.android.droidscope.IPulseClient;
import com.zutubi.android.droidscope.ISettings;
import com.zutubi.android.droidscope.R;

public class DroidScopeActivityTest extends ActivityInstrumentationTestCase2<DroidScopeActivity>
{
    private DroidScopeActivity activity;
    private View               list;
    private FakePulseClient    client;

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
        client = new FakePulseClient();
        activity.setClient(client);
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
        client.releaseGetAllProjectNames();
        waitForDialogToDisappear();
        assertEquals(0, list.getTouchables().size());
    }

    public void testProjects() throws Throwable
    {
        client.setProjectNames("p1", "p2");

        sendKeys(KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_R);
        getInstrumentation().waitForIdleSync();
        client.releaseGetAllProjectNames();
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

    private static class FakePulseClient implements IPulseClient
    {
        private Semaphore getAllProjectNamesFlag = new Semaphore(0);
        private Object[]  projectNames           = new Object[0];

        @Override
        public Object[] getAllProjectNames() throws XMLRPCException
        {
            try
            {
                getAllProjectNamesFlag.acquire();
                return projectNames;
            }
            catch (InterruptedException e)
            {
                throw new XMLRPCException(e);
            }
        }

        @Override
        public Object[] getLatestBuildsForProject(String projectName, boolean completedOnly, int maxResults) throws XMLRPCException
        {
            HashMap<String, Object> build = new HashMap<String, Object>();
            build.put("succeeded", true);
            return new Object[] { build };
        }

        public void releaseGetAllProjectNames()
        {
            getAllProjectNamesFlag.release();
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
