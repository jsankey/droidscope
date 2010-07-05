package com.zutubi.android.libpulse;

/**
 * Holds the status for a single project, based around the latest build results.
 */
public class ProjectStatus
{
    private String projectName;
    private BuildResult latestCompletedBuild;
    private BuildResult runningBuild;
    private long timestamp;
    
    /**
     * Creates a new status with the given project information.
     * 
     * @param projectName          name of the project
     * @param latestCompletedBuild the latest completed build for the project,
     *                             or null if there is no such build
     * @param runningBuild         the currently-running build for the project,
     *                             or null if there is no such build
     * @param timestamp            the time at which this status information
     *                             was gathered (in millis since the epoch)
     */
    public ProjectStatus(String projectName, BuildResult latestCompletedBuild, BuildResult runningBuild, long timestamp)
    {
        super();
        this.projectName = projectName;
        this.latestCompletedBuild = latestCompletedBuild;
        this.runningBuild = runningBuild;
        this.timestamp = timestamp;
    }

    /**
     * Returns the project name.
     * 
     * @return the project name
     */
    public String getProjectName()
    {
        return projectName;
    }
    
    /**
     * Returns the latest completed build for this project.
     * 
     * @return the latest completed build for this project, may be null if
     *         there are no completed builds
     */
    public BuildResult getLatestCompletedBuild()
    {
        return latestCompletedBuild;
    }
    
    /**
     * Returns the currently running build for this project.
     * 
     * @return the currently running build for this project, may be null if
     *         there are no builds running
     */
    public BuildResult getRunningBuild()
    {
        return runningBuild;
    }
    
    /**
     * Indicates the current health of the project, based on the latest
     * completed build (if any).
     * 
     * @return the current health of this project
     */
    public Health getHealth()
    {
        if (latestCompletedBuild == null)
        {
            return Health.UNKNOWN;
        }
        else if (latestCompletedBuild.getStatus() == ResultStatus.SUCCESS)
        {
            return Health.OK;
        }
        else
        {
            return Health.BROKEN;
        }
    }
    
    /**
     * Returns the time at which this status information was retrieved from the
     * Pulse server.
     * 
     * @return the time this status was retrieved, in milliseconds since the
     *         epoch
     */
    public long getTimestamp()
    {
        return timestamp;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((latestCompletedBuild == null) ? 0 : latestCompletedBuild.hashCode());
        result = prime * result + ((projectName == null) ? 0 : projectName.hashCode());
        result = prime * result + ((runningBuild == null) ? 0 : runningBuild.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        ProjectStatus other = (ProjectStatus) obj;
        if (latestCompletedBuild == null)
        {
            if (other.latestCompletedBuild != null)
            {
                return false;
            }
        }
        else if (!latestCompletedBuild.equals(other.latestCompletedBuild))
        {
            return false;
        }
        if (projectName == null)
        {
            if (other.projectName != null)
            {
                return false;
            }
        }
        else if (!projectName.equals(other.projectName))
        {
            return false;
        }
        if (runningBuild == null)
        {
            if (other.runningBuild != null)
            {
                return false;
            }
        }
        else if (!runningBuild.equals(other.runningBuild))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return projectName + ": " + getHealth().name().toLowerCase();
    }
}
