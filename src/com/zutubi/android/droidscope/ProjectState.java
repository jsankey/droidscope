package com.zutubi.android.droidscope;

/**
 * Encapsulates the state of a Pulse project.
 */
public class ProjectState
{
    /**
     * Project health values, which indicate the status of the project based
     * on its last completed build.
     */
    public enum Health
    {
        UNKNOWN, OK, BROKEN
    }

    private String project;
    private Health health;

    public ProjectState(String project, Health health)
    {
        this.project = project;
        this.health = health;
    }

    /**
     * Returns the project name.
     * 
     * @return the name of the corresponding project
     */
    public String getProject()
    {
        return project;
    }

    /**
     * Indicates the current status of the project, based on the latest
     * completed build.
     * 
     * @return the project health
     */
    public Health getHealth()
    {
        return health;
    }

    @Override
    public String toString()
    {
        return project + ": " + health.name().toLowerCase();
    }
}
