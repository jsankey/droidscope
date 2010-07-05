package com.zutubi.android.libpulse.internal;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xmlrpc.android.XMLRPCException;

import com.zutubi.android.libpulse.ResultStatus;

/**
 * Implementation of {@link IPulseClient} useful for querying projects and
 * builds.
 */
public class FakePulseClient implements IPulseClient
{
    private Map<String, List<Map<String,Object>>> projects = new LinkedHashMap<String, List<Map<String,Object>>>();
    private List<String> myProjects = new LinkedList<String>();
    
    @Override
    public String[] getMyProjectNames() throws XMLRPCException
    {
        return myProjects.toArray(new String[myProjects.size()]);
    }
    
    public void addMyProject(String project)
    {
        myProjects.add(project);
    }

    @Override
    public String[] getAllProjectNames() throws XMLRPCException
    {
        Collection<String> names = projects.keySet();
        return names.toArray(new String[names.size()]);
    }

    public void addProject(String project)
    {
        projects.put(project, new LinkedList<Map<String,Object>>());
    }
    
    public void addBuild(String project, int id, ResultStatus status)
    {
        Map<String, Object> build = new HashMap<String, Object>();
        build.put("id", id);
        build.put("status", status.toString().toLowerCase().replaceAll("_", " "));
        projects.get(project).add(build);
    }
    
    @Override
    public Object[] getLatestBuildsForProject(String projectName, boolean completedOnly, int maxResults) throws XMLRPCException
    {
        if (!projects.containsKey(projectName))
        {
            throw new XMLRPCException("No such project '" + projectName + "'");
        }

        List<Map<String, Object>> rawBuilds = new LinkedList<Map<String, Object>>(projects.get(projectName));
        
        if (completedOnly)
        {
            Iterator<Map<String, Object>> it = rawBuilds.iterator();
            while (it.hasNext())
            {
                Map<String, Object> build = it.next();
                Object completed = build.get("completed");
                if (completed == null || !(Boolean) completed)
                {
                    it.remove();
                }
            }
        }

        Collections.reverse(rawBuilds);
        
        if (rawBuilds.size() > maxResults)
        {
            rawBuilds = rawBuilds.subList(0, maxResults);
        }
        
        return rawBuilds.toArray(new Object[rawBuilds.size()]);
    }

    @Override
    public void triggerBuild(String projectName) throws XMLRPCException
    {
    }

    @Override
    public void close() throws IOException
    {
    }
}
