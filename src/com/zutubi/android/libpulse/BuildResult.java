package com.zutubi.android.libpulse;

/**
 * Represents the result of a Pulse build.
 */
public class BuildResult
{
    private int number;
    private ResultStatus status;
    private String revision;
    private String tests;
    private long startTime;
    private long endTime;
    private int progress;
    
    /**
     * Creates a new build with the given details.
     * 
     * @param number   the build number (also known as the id)
     * @param status   current status of the build
     * @param progress for in progress builds, their estimated percentage
     *                 complete (0-100); may be -1 if no estimate can be made
     */
    public BuildResult(int number, ResultStatus status, String revision, String tests, long startTime, long endTime, int progress)
    {
        super();
        this.number = number;
        this.status = status;
        this.revision = revision;
        this.tests = tests;
        this.startTime = startTime;
        this.endTime = endTime;
        this.progress = progress;
    }

    /**
     * Indicates the status of this build.
     * 
     * @return this build's status (e.g. in progress, failed)
     */
    public ResultStatus getStatus()
    {
        return status;
    }
    
    /**
     * A unique number for this build in the scope of its project.  Build
     * numbers for each project start at 1, are sequential and increasing.
     * 
     * @return the unique number for this build
     */
    public int getNumber()
    {
        return number;
    }    

    /**
     * The SCM revision the build occurred at, if any.
     *  
     * @return the revision of the build, may be null
     */
    public String getRevision()
    {
        return revision;
    }
    
    /**
     * Returns a short summary string describing the test results for the
     * build.
     * 
     * @return a short summary of the test results, e.g. "all 33 passed"
     */
    public String getTests()
    {
        return tests;
    }
    
    /**
     * Returns the time that the build commenced, in milliseconds since the
     * Unix epoch, if available.
     * 
     * @return the time the build commenced, or -1 if it is pending
     */
    public long getStartTime()
    {
        return startTime;
    }
    
    /**
     * Returns the time that the build completed, in milliseconds since the
     * Unix epoch, if available.
     * 
     * @return the time the build ended, or -1 if it is not yet done
     */
    public long getEndTime()
    {
        return endTime;
    }
    
    /**
     * Indicates the estimated percentage progress for this build.  This is
     * based on the latest successful build of the same project.  Note that
     * estimates are not always available.
     * 
     * @return the estimated percentage complete for this build (0-100); or -1
     *          if no estimate is available
     */
    public int getProgress()
    {
        return progress;
    }

    /**
     * Indicates if this build is complete.
     * 
     * @return true if the build is complete, false if it is yet to start or
     *         still in progress
     */
    public boolean isComplete()
    {
        return status.isComplete();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (endTime ^ (endTime >>> 32));
        result = prime * result + number;
        result = prime * result + progress;
        result = prime * result + ((revision == null) ? 0 : revision.hashCode());
        result = prime * result + (int) (startTime ^ (startTime >>> 32));
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((tests == null) ? 0 : tests.hashCode());
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
        BuildResult other = (BuildResult) obj;
        if (endTime != other.endTime)
        {
            return false;
        }
        if (number != other.number)
        {
            return false;
        }
        if (progress != other.progress)
        {
            return false;
        }
        if (revision == null)
        {
            if (other.revision != null)
            {
                return false;
            }
        }
        else if (!revision.equals(other.revision))
        {
            return false;
        }
        if (startTime != other.startTime)
        {
            return false;
        }
        if (status != other.status)
        {
            return false;
        }
        if (tests == null)
        {
            if (other.tests != null)
            {
                return false;
            }
        }
        else if (!tests.equals(other.tests))
        {
            return false;
        }
        return true;
    }
}
