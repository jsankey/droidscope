package com.zutubi.android.libpulse;

/**
 * Possible states for a build, stage or command result.
 */
public enum ResultStatus
{
    /**
     * The build is waiting to start.
     */
    PENDING(false),
    /**
     * The build is currently running.
     */
    IN_PROGRESS(false),
    /**
     * A request has been received to terminate the build, and we are waiting
     * for the build to complete.
     */
    TERMINATING(false),
    /**
     * The build completed successfully.
     */
    SUCCESS(true),
    /**
     * The build failed due to an actual build problem (e.g. compile error,
     * test failure).
     */
    FAILURE(true),
    /**
     * The build failed due to an external problem (e.g. incorrect
     * configuration, network failure).
     */
    ERROR(true);

    private boolean complete;
    
    private ResultStatus(boolean complete)
    {
        this.complete = complete;
    }
    
    /**
     * Indicates if this status denotes a complete build.
     *  
     * @return true if builds with this status are complete, false if they are
     *         yet to start or still in progress
     */
    public boolean isComplete()
    {
        return complete;
    }

    public String pretty()
    {
        return name().toLowerCase().replace('_', ' ');
    }
}
