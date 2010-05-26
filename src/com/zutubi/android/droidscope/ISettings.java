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
}
