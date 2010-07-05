package com.zutubi.android.libpulse;

/**
 * Summarises the results of tests run during a build.
 */
public class TestSummary
{
    private int total;
    private int errors;
    private int failures;
    private int expectedFailures;
    private int skipped;
    private int passed;
    
    /**
     * Creates an instance representing zero tests.
     */
    public TestSummary()
    {
    }

    public TestSummary(int total, int errors, int failures, int expectedFailures, int skipped, int passed)
    {
        super();
        this.total = total;
        this.errors = errors;
        this.failures = failures;
        this.expectedFailures = expectedFailures;
        this.skipped = skipped;
        this.passed = passed;
    }

    /**
     * Indicates how many tests cases were found.
     * 
     * @return the total number of test cases found
     */
    public int getTotal()
    {
        return total;
    }

    /**
     * Indicates how many tests cases ended in errors.
     * 
     * @return the number of test cases that ended in an error
     */
    public int getErrors()
    {
        return errors;
    }

    /**
     * Indicates how many tests cases failed.
     * 
     * @return the number of failed test cases
     */
    public int getFailures()
    {
        return failures;
    }

    /**
     * Indicates how many tests cases failed, but were marked as expected to
     * fail.
     * 
     * @return the number of test cases that failed as expected
     */
    public int getExpectedFailures()
    {
        return expectedFailures;
    }

    /**
     * Indicates how many tests cases were skipped - i.e. not executed.
     * 
     * @return the number of test cases that were skipped
     */
    public int getSkipped()
    {
        return skipped;
    }

    /**
     * Indicates how many tests cases passed.
     * 
     * @return the number of test cases that passed
     */
    public int getPassed()
    {
        return passed;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + errors;
        result = prime * result + expectedFailures;
        result = prime * result + failures;
        result = prime * result + passed;
        result = prime * result + skipped;
        result = prime * result + total;
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
        TestSummary other = (TestSummary) obj;
        if (errors != other.errors)
        {
            return false;
        }
        if (expectedFailures != other.expectedFailures)
        {
            return false;
        }
        if (failures != other.failures)
        {
            return false;
        }
        if (passed != other.passed)
        {
            return false;
        }
        if (skipped != other.skipped)
        {
            return false;
        }
        if (total != other.total)
        {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString()
    {
        if (total == 0)
        {
            return "none";
        }
        else
        {
            String result;
            if (passed == total - skipped)
            {
                result = String.format("all %d passed", passed);
            }
            else
            {
                result = String.format("%d of %d broken", errors + failures + expectedFailures, total - skipped);
            }
            
            if (skipped > 0)
            {
                result += String.format(" (%d skipped)", skipped);
            }
        
            return result;
        }
    }
}
