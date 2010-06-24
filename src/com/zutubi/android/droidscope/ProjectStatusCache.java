package com.zutubi.android.droidscope;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.zutubi.android.libpulse.ProjectStatus;

/**
 * Quick and dirty cache for project statuses.  A temporary measure until they
 * are stored more sensibly.
 */
public class ProjectStatusCache
{
    private static Map<String, ProjectStatus> nameToStatusMap = new HashMap<String, ProjectStatus>();

    public static Collection<ProjectStatus> getAll()
    {
        return new LinkedList<ProjectStatus>(nameToStatusMap.values());
    }
    
    public static ProjectStatus findByProjectName(String projectName)
    {
        return nameToStatusMap.get(projectName);
    }
    
    public static <T extends ProjectStatus> void put(T status)
    {
        nameToStatusMap.put(status.getProjectName(), status);
    }
    
    public static void putAll(Collection<? extends ProjectStatus> statuses)
    {
        for (ProjectStatus s: statuses)
        {
            nameToStatusMap.put(s.getProjectName(), s);
        }
    }
    
    public static void clear()
    {
        nameToStatusMap.clear();
    }
}
