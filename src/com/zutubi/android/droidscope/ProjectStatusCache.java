package com.zutubi.android.droidscope;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import com.zutubi.android.libpulse.ProjectStatus;

/**
 * Simple cache for project statuses.
 */
public class ProjectStatusCache
{
    private Map<String, ProjectStatus> nameToStatusMap = new LinkedHashMap<String, ProjectStatus>();

    /**
     * Retrieves all the project statuses, in the order they were added to the
     * cache.
     * 
     * @return all project statuses in the cache
     */
    public Collection<ProjectStatus> getAll()
    {
        return new LinkedList<ProjectStatus>(nameToStatusMap.values());
    }
    
    /**
     * Returns the status of the given project, if it exists.
     * 
     * @param projectName name of the project to retrieve the status of
     * @return status for the named project, or null if it does not exist
     */
    public ProjectStatus findByProjectName(String projectName)
    {
        return nameToStatusMap.get(projectName);
    }
    
    /**
     * Adds the given status to the cache.
     * 
     * @param <T>   actual status type
     * @param status the status instance to add
     */
    public <T extends ProjectStatus> void put(T status)
    {
        nameToStatusMap.put(status.getProjectName(), status);
    }
    
    /**
     * Adds all of the given statuses, in the iteration order of the given
     * collection, to the cache.
     * 
     * @param statuses collection of instances to add
     */
    public void putAll(Collection<? extends ProjectStatus> statuses)
    {
        for (ProjectStatus s: statuses)
        {
            nameToStatusMap.put(s.getProjectName(), s);
        }
    }
    
    /**
     * Empties the cache, removing all current status instances.
     */
    public void clear()
    {
        nameToStatusMap.clear();
    }
}
