package com.zutubi.android.libpulse.internal;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.zutubi.android.libpulse.BuildResult;
import com.zutubi.android.libpulse.PulseException;
import com.zutubi.android.libpulse.ResultStatus;
import com.zutubi.android.libpulse.TestSummary;

public class StructConverterTest
{
    @Test
    public void testConvertTestSummaryEmptyMap()
    {
        assertEquals(new TestSummary(), StructConverter.convertTestSummary(Collections.<String, Object>emptyMap()));
    }

    @Test
    public void testConvertTestSummarySomeKeysMissing()
    {
        Map<String, Object> summary = new HashMap<String, Object>();
        summary.put(StructConverter.KEY_TOTAL, 5);
        summary.put(StructConverter.KEY_SKIPPED, 4);
        summary.put(StructConverter.KEY_PASSED, 1);
        assertEquals(new TestSummary(5, 0, 0, 0, 4, 1), StructConverter.convertTestSummary(summary));
    }

    @Test
    public void testConvertTestSummaryComplete()
    {
        Map<String, Object> summary = new HashMap<String, Object>();
        summary.put(StructConverter.KEY_TOTAL, 15);
        summary.put(StructConverter.KEY_ERRORS, 1);
        summary.put(StructConverter.KEY_FAILURES, 2);
        summary.put(StructConverter.KEY_EXPECTED_FAILURES, 3);
        summary.put(StructConverter.KEY_SKIPPED, 4);
        summary.put(StructConverter.KEY_PASSED, 5);
        assertEquals(new TestSummary(15, 1, 2, 3, 4, 5), StructConverter.convertTestSummary(summary));
    }

    @Test(expected = PulseException.class)
    public void testConvertTestSummaryBadType()
    {
        Map<String, Object> summary = new HashMap<String, Object>();
        summary.put(StructConverter.KEY_TOTAL, "hello");
        StructConverter.convertTestSummary(summary);
    }

    @Test(expected = PulseException.class)
    public void testConvertBuildResultEmptyMap()
    {
        StructConverter.convertBuild(Collections.<String, Object>emptyMap());
    }

    @Test(expected = PulseException.class)
    public void testConvertBuildResultMissingId()
    {
        Map<String, Object> build = new HashMap<String, Object>();
        build.put(StructConverter.KEY_STATUS, ResultStatus.ERROR.pretty());
        StructConverter.convertBuild(build);
    }

    @Test(expected = PulseException.class)
    public void testConvertBuildResultMissingStatus()
    {
        Map<String, Object> build = new HashMap<String, Object>();
        build.put(StructConverter.KEY_ID, 1);
        StructConverter.convertBuild(build);
    }
    
    @Test(expected = PulseException.class)
    public void testConvertBuildInvalidId()
    {
        Map<String, Object> build = new HashMap<String, Object>();
        build.put(StructConverter.KEY_ID, "invalid");
        build.put(StructConverter.KEY_STATUS, ResultStatus.ERROR.pretty());
        StructConverter.convertBuild(build);
    }

    @Test(expected = PulseException.class)
    public void testConvertBuildInvalidStatus()
    {
        Map<String, Object> build = new HashMap<String, Object>();
        build.put(StructConverter.KEY_ID, 1);
        build.put(StructConverter.KEY_STATUS, "invalid");
        StructConverter.convertBuild(build);
    }

    @Test(expected = PulseException.class)
    public void testConvertBuildInvalidStatusType()
    {
        Map<String, Object> build = new HashMap<String, Object>();
        build.put(StructConverter.KEY_ID, 1);
        build.put(StructConverter.KEY_STATUS, 3);
        StructConverter.convertBuild(build);
    }

    @Test
    public void testConvertBuildMinimalValid()
    {
        Map<String, Object> build = new HashMap<String, Object>();
        build.put(StructConverter.KEY_ID, 1);
        build.put(StructConverter.KEY_STATUS, ResultStatus.ERROR.pretty());
        assertEquals(new BuildResult(1, ResultStatus.ERROR, null, new TestSummary(), -1, -1, -1), StructConverter.convertBuild(build));
    }
    
    @Test
    public void testConvertBuildComplete()
    {
        Map<String, Object> tests = new HashMap<String, Object>();
        tests.put(StructConverter.KEY_TOTAL, 4);
        
        Map<String, Object> build = new HashMap<String, Object>();
        build.put(StructConverter.KEY_ID, 5);
        build.put(StructConverter.KEY_STATUS, ResultStatus.SUCCESS.pretty());
        build.put(StructConverter.KEY_REVISION, "rev");
        build.put(StructConverter.KEY_TESTS, tests);
        build.put(StructConverter.KEY_START_TIME_MILLIS, "100");
        build.put(StructConverter.KEY_END_TIME_MILLIS, "110");
        build.put(StructConverter.KEY_PROGRESS, 33);

        assertEquals(new BuildResult(5, ResultStatus.SUCCESS, "rev", new TestSummary(4, 0, 0, 0, 0, 0), 100, 110, 33), StructConverter.convertBuild(build));
    }
}
