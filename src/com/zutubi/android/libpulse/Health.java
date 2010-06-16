package com.zutubi.android.libpulse;

/**
 * Possible health values for a project.  Project health is based on the status
 * of the latest completed build.
 */
public enum Health
{
    /**
     * The health is unknown as no completed builds exist for the project.
     */
    UNKNOWN,
    /**
     * The latest completed build succeeded.
     */
    OK,
    /**
     * The latest completed build was unsuccessful.
     */
    BROKEN
}
