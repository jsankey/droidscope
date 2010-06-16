package com.zutubi.android.libpulse;

/**
 * Holds the status for a single project, based around the latest build results.
 */
public class ProjectStatus
{
    private String projectName;
    private BuildResult latestCompletedBuild;
    private BuildResult runningBuild;
    
    /**
     * Creates a new status with the given project information.
     * 
     * @param projectName          name of the project
     * @param latestCompletedBuild the latest completed build for the project,
     *                             or null if there is no such build
     * @param runningBuild         the currently-running build for the project,
     *                             or null if there is no such build
     */
    public ProjectStatus(String projectName, BuildResult latestCompletedBuild, BuildResult runningBuild)
    {
        super();
        this.projectName = projectName;
        this.latestCompletedBuild = latestCompletedBuild;
        this.runningBuild = runningBuild;
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
    
    @Override
    public String toString()
    {
        return projectName + ": " + getHealth().name().toLowerCase();
    }
}
