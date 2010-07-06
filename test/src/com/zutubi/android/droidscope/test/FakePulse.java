package com.zutubi.android.droidscope.test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.zutubi.android.libpulse.BuildResult;
import com.zutubi.android.libpulse.IPulse;
import com.zutubi.android.libpulse.ProjectStatus;
import com.zutubi.android.libpulse.ResultStatus;
import com.zutubi.android.libpulse.TestSummary;

/**
 * A testing implementation of the {@link IPulse} interface.
 */
class FakePulse implements IPulse
{
    private boolean   waitOnProjectStatuses     = false;
    private Semaphore getAllProjectStatusesFlag = new Semaphore(0);
    private String[]  projectNames              = new String[0];
    private long      timestamp                 = 0;
    
    @Override
    public List<ProjectStatus> getAllProjectStatuses()
    {
        try
        {
            if (waitOnProjectStatuses)
            {
                getAllProjectStatusesFlag.acquire();
            }
            return getProjectStatuses(projectNames);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    private List<ProjectStatus> getProjectStatuses(String[] names)
    {
        List<ProjectStatus> statuses = new LinkedList<ProjectStatus>();
        for (String name: names)
        {
            statuses.add(new ProjectStatus(name, new BuildResult(1, ResultStatus.SUCCESS, "rev", new TestSummary(), 0, 0, 100), null, timestamp));
        }
        
        return statuses;
    }

    @Override
    public List<ProjectStatus> getMyProjectStatuses()
    {
        return null;
    }
    
    @Override
    public ProjectStatus getProjectStatus(String project)
    {
        return null;
    }

    @Override
    public void triggerBuild(String project)
    {
    }

    public void releaseGetAllProjectStatuses()
    {
        getAllProjectStatusesFlag.release();
    }
    
    public void setWaitOnProjectStatuses(boolean waitOnProjectStatuses)
    {
        this.waitOnProjectStatuses = waitOnProjectStatuses;
    }

    public void setProjectNames(String... projectNames)
    {
        this.projectNames = projectNames;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }
    
    @Override
    public void close() throws IOException
    {
    }
}