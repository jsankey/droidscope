package com.zutubi.android.libpulse.internal;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xmlrpc.android.XMLRPCException;

import com.zutubi.android.libpulse.BuildResult;
import com.zutubi.android.libpulse.IPulse;
import com.zutubi.android.libpulse.ProjectStatus;
import com.zutubi.android.libpulse.ResultStatus;

/**
 * Default implementation of the {@link IPulse} interface.  Uses an
 * {@link IPulseClient} implementation to talk to a real Pulse server.
 */
public class Pulse implements IPulse
{
    private IPulseClient client;

    /**
     * Creates a new Pulse implementation that will use the given client to
     * contact a Pulse server.
     * 
     * @param client client to use to talk to a Pulse server
     */
    public Pulse(IPulseClient client)
    {
        this.client = client;
    }
    
    @Override
    public List<ProjectStatus> getAllProjectStatuses()
    {
        try
        {
            return getProjectStatuses(client.getAllProjectNames());
        }
        catch (XMLRPCException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ProjectStatus> getMyProjectStatuses()
    {
        try
        {
            return getProjectStatuses(client.getMyProjectNames());
        }
        catch (XMLRPCException e)
        {
            throw new RuntimeException(e);
        }
    }

    private List<ProjectStatus> getProjectStatuses(String[] projectNames) throws XMLRPCException
    {
        List<ProjectStatus> result = new LinkedList<ProjectStatus>();
        for (String name: projectNames)
        {
            result.add(getProjectStatus(name));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private ProjectStatus getProjectStatus(String name) throws XMLRPCException
    {
        Object[] builds = client.getLatestBuildsForProject(name, false, 2);
        BuildResult latestCompletedBuild = null;
        BuildResult runningBuild = null;
        if (builds.length > 0)
        {
            BuildResult latestBuild = convertBuild((Map<String, Object>) builds[0]);
            if (latestBuild.isComplete())
            {
                latestCompletedBuild = latestBuild;
            }
            else
            {
                runningBuild = latestBuild;
                if (builds.length > 1)
                {
                    latestCompletedBuild = convertBuild((Map<String, Object>) builds[1]);
                }
            }
        }
        
        return new ProjectStatus(name, latestCompletedBuild, runningBuild);
    }

    private BuildResult convertBuild(Map<String, Object> map)
    {
        return new BuildResult((Integer) map.get("id"), convertStatus((String) map.get("status")), (Integer) map.get("progress"));
    }

    private ResultStatus convertStatus(String s)
    {
        s = s.toUpperCase().replaceAll(" ", "_");
        return ResultStatus.valueOf(s);
    }

    @Override
    public void close() throws IOException
    {
        client.close();
    }
}
