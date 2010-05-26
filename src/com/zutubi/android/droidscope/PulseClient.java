package com.zutubi.android.droidscope;

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

    public PulseClient(ISettings settings)
    {
        String url = settings.getURL();
        if (!url.endsWith("/"))
        {
            url += "/";
        }
        url += "xmlrpc";

        this.client = new XMLRPCClient(url);
        this.username = settings.getUsername();
        this.password = settings.getPassword();
    }

    /**
     * @see com.zutubi.android.droidscope.IPulseClient#getAllProjectNames()
     */
    public Object[] getAllProjectNames() throws XMLRPCException
    {
        ensureToken();
        return doCall("RemoteApi.getAllProjectNames", token);
    }

    /**
     * @see com.zutubi.android.droidscope.IPulseClient#getLatestBuildsForProject(java.lang.String,
     *      boolean, int)
     */
    public Object[] getLatestBuildsForProject(String projectName, boolean completedOnly, int maxResults) throws XMLRPCException
    {
        ensureToken();
        return doCall("RemoteApi.getLatestBuildsForProject", token, projectName, completedOnly, maxResults);
    }

    @SuppressWarnings("unchecked")
    private <T> T doCall(String methodName, Object... args) throws XMLRPCException
    {
        Object result = client.callEx(methodName, args);
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

    private void logoutIfRequired() throws XMLRPCException
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
