package com.zutubi.android.libpulse.internal;

import java.io.IOException;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import android.util.Log;

/**
 * Implementation of  {@link IPulseClient} that talks to the Pulse server over
 * XML-RPC.
 */
public class PulseClient implements IPulseClient
{
    private static final long MAX_TOKEN_AGE = 25 * 60 * 1000;

    private String            username;
    private String            password;

    private XMLRPCClient      client;
    private String            token;
    private long              tokenTimestamp;

    public PulseClient(String url, String username, String password)
    {
        if (!url.endsWith("/"))
        {
            url += "/";
        }
        url += "xmlrpc";

        this.client = new XMLRPCClient(url);
        this.username = username;
        this.password = password;
    }

    /**
     * @see com.zutubi.android.libpulse.internal.IPulseClient#getMyProjectNames()
     */
    @Override
    public String[] getMyProjectNames() throws XMLRPCException
    {
        ensureToken();
        Object[] names = doCall("getMyProjectNames", token);
        return convertStrings(names);
    }

    /**
     * @see com.zutubi.android.libpulse.internal.IPulseClient#getAllProjectNames()
     */
    @Override
    public String[] getAllProjectNames() throws XMLRPCException
    {
        ensureToken();
        Object[] names = doCall("getAllProjectNames", token);
        return convertStrings(names);
    }

    private String[] convertStrings(Object[] objects)
    {
        String[] strings = new String[objects.length];
        System.arraycopy(objects, 0, strings, 0, objects.length);
        return strings;
    }

    /**
     * @see com.zutubi.android.libpulse.internal.IPulseClient#getLatestBuildsForProject(java.lang.String,
     *      boolean, int)
     */
    @Override
    public Object[] getLatestBuildsForProject(String projectName, boolean completedOnly, int maxResults) throws XMLRPCException
    {
        ensureToken();
        return doCall("getLatestBuildsForProject", token, projectName, completedOnly, maxResults);
    }

    @Override
    public void triggerBuild(String projectName) throws XMLRPCException
    {
        ensureToken();
        doCall("triggerBuild", token, projectName);
    }
    
    @SuppressWarnings("unchecked")
    private synchronized <T> T doCall(String methodName, Object... args) throws XMLRPCException
    {
        Object result = client.callEx("RemoteApi." + methodName, args);
        return (T) result;
    }

    private void ensureToken() throws XMLRPCException
    {
        long currentTime = System.currentTimeMillis();
        if (token == null || currentTime - tokenTimestamp > MAX_TOKEN_AGE)
        {
            logoutIfRequired();
            token = (String) client.call("RemoteApi.login", username, password);
            tokenTimestamp = currentTime;
        }
    }

    private synchronized void logoutIfRequired() throws XMLRPCException
    {
        if (token != null)
        {
            client.call("RemoteApi.logout", token);
        }
    }

    @Override
    public void close() throws IOException
    {
        try
        {
            logoutIfRequired();
        }
        catch (XMLRPCException e)
        {
            Log.e(getClass().getName(), "Unable to logout", e);
        }
    }
}
