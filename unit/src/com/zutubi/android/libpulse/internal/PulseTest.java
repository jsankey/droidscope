package com.zutubi.android.libpulse.internal;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.xmlrpc.android.XMLRPCException;

import com.zutubi.android.libpulse.BuildResult;
import com.zutubi.android.libpulse.ProjectStatus;
import com.zutubi.android.libpulse.ResultStatus;

public class PulseTest
{
    private static final String PROJECT_1 = "p1";
    private static final String PROJECT_2 = "p2";
    private static final String PROJECT_3 = "p3";

    private FakePulseClient client;
    private Pulse pulse;
    
    @Before
    public void setUp()
    {
        client = new FakePulseClient();
        pulse = new Pulse(client);
    }
    
    @Test
    public void testGetAllProjectStatusesNoProjects() throws XMLRPCException
    {
        assertEquals(Collections.<ProjectStatus>emptyList(), pulse.getAllProjectStatuses());
    }

    @Test
    public void testGetAllProjectStatusesSingleProjectNeverBuilt() throws XMLRPCException
    {
        client.addProject(PROJECT_1);
        assertEquals(asList(emptyStatus(PROJECT_1)), pulse.getAllProjectStatuses());
        
    }
    
    @Test
    public void testGetAllProjectStatusesMultipleProjectsNeverBuilt() throws XMLRPCException
    {
        client.addProject(PROJECT_1);
        client.addProject(PROJECT_2);
        client.addProject(PROJECT_3);
        
        assertEquals(asList(emptyStatus(PROJECT_1), emptyStatus(PROJECT_2), emptyStatus(PROJECT_3)), pulse.getAllProjectStatuses());
    }

    @Test
    public void testGetAllProjectStatusesSingleRunningBuild() throws XMLRPCException
    {
        client.addProject(PROJECT_1);
        client.addBuild(PROJECT_1, 1, ResultStatus.IN_PROGRESS);
        
        ProjectStatus expectedStatus = new ProjectStatus(PROJECT_1, null, build(1, ResultStatus.IN_PROGRESS), 0);
        assertEquals(asList(expectedStatus), pulse.getAllProjectStatuses());
    }

    @Test
    public void testGetAllProjectStatusesSingleCompletedBuild() throws XMLRPCException
    {
        client.addProject(PROJECT_1);
        client.addBuild(PROJECT_1, 1, ResultStatus.SUCCESS);
        
        ProjectStatus expectedStatus = new ProjectStatus(PROJECT_1, build(1, ResultStatus.SUCCESS), null, 0);
        assertEquals(asList(expectedStatus), pulse.getAllProjectStatuses());
    }

    @Test
    public void testGetAllProjectStatusesRunningAndCompletedBuild() throws XMLRPCException
    {
        client.addProject(PROJECT_1);
        client.addBuild(PROJECT_1, 1, ResultStatus.SUCCESS);
        client.addBuild(PROJECT_1, 2, ResultStatus.IN_PROGRESS);
        
        ProjectStatus expectedStatus = new ProjectStatus(PROJECT_1, build(1, ResultStatus.SUCCESS), build(2, ResultStatus.IN_PROGRESS), 0);
        assertEquals(asList(expectedStatus), pulse.getAllProjectStatuses());
    }
    
    @Test
    public void testGetMyProjectStatusesNoProjects()
    {
        assertEquals(Collections.<ProjectStatus>emptyList(), pulse.getMyProjectStatuses());
    }

    @Test
    public void testGetMyProjectStatusesNoMyProjects()
    {
        client.addProject(PROJECT_1);
        
        assertEquals(Collections.<ProjectStatus>emptyList(), pulse.getMyProjectStatuses());
    }

    @Test
    public void testGetMyProjectStatusesSubsetMyProjects()
    {
        client.addProject(PROJECT_1);
        client.addProject(PROJECT_2);
        client.addProject(PROJECT_3);
        client.addMyProject(PROJECT_1);
        client.addMyProject(PROJECT_3);
        
        assertEquals(asList(emptyStatus(PROJECT_1), emptyStatus(PROJECT_3)), pulse.getMyProjectStatuses());
    }

    @Test
    public void testGetProjectStatus()
    {
        client.addProject(PROJECT_1);
        client.addBuild(PROJECT_1, 1, ResultStatus.SUCCESS);
        
        assertEquals(new ProjectStatus(PROJECT_1, build(1, ResultStatus.SUCCESS), null, 0), pulse.getProjectStatus(PROJECT_1));
    }
    
    @Test
    public void testGetProjectStatusNoBuild()
    {
        client.addProject(PROJECT_1);
        
        assertEquals(new ProjectStatus(PROJECT_1, null, null, 0), pulse.getProjectStatus(PROJECT_1));
    }

    @Test(expected = com.zutubi.android.libpulse.PulseException.class)
    public void testGetProjectStatusNoSuchProject()
    {
        pulse.getProjectStatus(PROJECT_1);
    }
    
    private BuildResult build(int id, ResultStatus status)
    {
        return new BuildResult(id, status, null, null, -1, -1, -1);
    }

    private ProjectStatus emptyStatus(String project)
    {
        return new ProjectStatus(project, null, null, 0);
    }
}
