package com.zutubi.android.libpulse.internal;

import java.io.Closeable;

import org.xmlrpc.android.XMLRPCException;

/**
 * Interface for talking to a Pulse server.  An incomplete representation of
 * the Pulse remote API.
 */
public interface IPulseClient extends Closeable
{
    /**
     * Retrieves the names of all projects selected by the logged in user for
     * display on their dashboard.
     * 
     * @return an array of the user's selected project names
     * @throws XMLRPCException on error
     */
    String[] getMyProjectNames() throws XMLRPCException;

    /**
     * Retrieves the names of all concrete projects configured on the server.
     * 
     * @return an array of all project names
     * @throws XMLRPCException on error
     */
    String[] getAllProjectNames() throws XMLRPCException;

    /**
     * Retrieves details of the latest builds for a named project.  Builds are
     * ordered from most to least recent.
     * 
     * @param projectName   the name of the project to retrieve the builds for
     * @param completedOnly if true, in progress builds are not returned
     * @param maxResults    the maximum number of builds to return
     * @return an array builds - each build is a map of key-value pairs (see
     *         the Pulse remote API documentation for details)
     * @throws XMLRPCException on error
     */
    Object[] getLatestBuildsForProject(String projectName, boolean completedOnly, int maxResults) throws XMLRPCException;
    
    /**
     * Triggers a new build of the give project.  Requires trigger permission.
     * 
     * @param projectName name of the project to trigger
     * @throws XMLRPCException on error
     */
    void triggerBuild(String projectName) throws XMLRPCException;
}
