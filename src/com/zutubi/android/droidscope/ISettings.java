package com.zutubi.android.droidscope;

/**
 * Abstraction over the settings used by DroidScope.
 */
public interface ISettings
{
    /**
     * Retrieves the Pulse server URL.
     *  
     * @return url of the Pulse server
     */
    String getURL();

    /**
     * Retrieves the username for logging in to Pulse.
     * 
     * @return username to use for logging in to Pulse 
     */
    String getUsername();

    /**
     * Retrieves the password for logging in to Pulse.
     * 
     * @return password to use for logging in to Pulse 
     */
    String getPassword();
    
    /**
     * Indicates if views showing active data should refresh when they are
     * displayed.
     * 
     * @return true to automatically refresh data on display of an active view
     */
    boolean isRefreshOnResume();

    /**
     * Returns the maximum age, in seconds, of a view before it is considered
     * stale.  Used in conjunction with {@link #isRefreshOnResume()}.
     * 
     * @return the maximum age, in seconds, of a view before it is considered
     *         stale
     */
    int getStaleAge();
}
