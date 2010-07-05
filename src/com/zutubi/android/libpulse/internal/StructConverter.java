package com.zutubi.android.libpulse.internal;

import java.util.Map;

import com.zutubi.android.libpulse.BuildResult;
import com.zutubi.android.libpulse.PulseException;
import com.zutubi.android.libpulse.ResultStatus;
import com.zutubi.android.libpulse.TestSummary;

/**
 * Converts between XML-RPC structures (represented in Maps) and Java
 * instances.
 */
public class StructConverter
{
    public static final String KEY_ID = "id";
    public static final String KEY_STATUS = "status";
    public static final String KEY_REVISION = "revision";
    public static final String KEY_START_TIME_MILLIS = "startTimeMillis";
    public static final String KEY_END_TIME_MILLIS = "endTimeMillis";
    public static final String KEY_PROGRESS = "progress";
    public static final String KEY_TESTS = "tests";
    
    public static final String KEY_TOTAL = "total";
    public static final String KEY_ERRORS = "errors";
    public static final String KEY_FAILURES = "failures";
    public static final String KEY_EXPECTED_FAILURES = "expectedFailures";
    public static final String KEY_PASSED = "passed";
    public static final String KEY_SKIPPED = "skipped";

    /**
     * Converts the given struct into a {@link BuildResult}.  Build results
     * must have at least an id and a status.
     * 
     * @param struct the struct to convert
     * @return a build result created from the details in the struct
     */
    public static BuildResult convertBuild(Map<String, Object> struct)
    {
        checkRequiredKeys(struct, KEY_ID, KEY_STATUS);
        
        return new BuildResult(
                getInt(struct, KEY_ID, -1),
                convertStatus(getString(struct, KEY_STATUS)),
                getString(struct, KEY_REVISION),
                extractBuildTestSummary(struct),
                getLong(struct, KEY_START_TIME_MILLIS),
                getLong(struct, KEY_END_TIME_MILLIS),
                getInt(struct, KEY_PROGRESS, -1));
    }

    @SuppressWarnings("unchecked")
    public static TestSummary extractBuildTestSummary(Map<String, Object> struct)
    {
        Object value = struct.get(KEY_TESTS);
        if (value == null)
        {
            return new TestSummary();
        }
        else if (value instanceof Map)
        {
            return convertTestSummary((Map<String, Object>) value);
        }
        else
        {
            throw new PulseException("Expected a struct containing the test summary, got '" + value.getClass() + "'");
        }
    }

    /**
     * Converts the given struct into a {@link TestSummary}.
     * 
     * @param struct the struct to convert
     * @return a test summary created from the details in the struct
     */
    public static TestSummary convertTestSummary(Map<String, Object> struct)
    {
        return new TestSummary(
                getInt(struct, KEY_TOTAL, 0),
                getInt(struct, KEY_ERRORS, 0),
                getInt(struct, KEY_FAILURES, 0),
                getInt(struct, KEY_EXPECTED_FAILURES, 0),
                getInt(struct, KEY_SKIPPED, 0),
                getInt(struct, KEY_PASSED, 0)
        );
    }
    
    private static String getString(Map<String, Object> struct, String key)
    {
        Object o = struct.get(key);
        if (o == null)
        {
            return null;
        }
        else if (o instanceof String)
        {
            return (String) o;
        }
        else
        {
            throw new PulseException("Expected a string for key '" + key + "', got '" + o.getClass().getName() + "'");
        }
    }
    
    private static void checkRequiredKeys(Map<String, Object> struct, String... keys)
    {
        for (String key: keys)
        {
            if (!struct.containsKey(key))
            {
                throw new PulseException("Required key '" + key + "' not present in XML-RPC struct");
            }
        }
    }

    private static long getLong(Map<String, Object> struct, String key)
    {
        String value = getString(struct, key);
        if (value == null)
        {
            return -1;
        }
        else
        {
            try
            {
                return Long.parseLong(value);
            }
            catch (NumberFormatException e)
            {
                throw new PulseException("Expected a 64-bit integer encoded as a string for key '" + key + "', got '" + value + "'");
            }
        }
    }

    private static int getInt(Map<String, Object> map, String key, int defaultValue)
    {
        Object o = map.get(key);
        if (o == null)
        {
            return defaultValue;
        }
        else if (o instanceof Integer)
        {
            return (Integer) o;
        }
        else
        {
            throw new PulseException("Expecting an integer for key '" + key + "', got '" + o.getClass() + "'");
        }
    }

    private static ResultStatus convertStatus(String s)
    {
        s = s.toUpperCase().replaceAll(" ", "_");
        try
        {
            return ResultStatus.valueOf(s);
        }
        catch (IllegalArgumentException e)
        {
            throw new PulseException("Expected result status, got '" + s + "'");
        }
    }
}
