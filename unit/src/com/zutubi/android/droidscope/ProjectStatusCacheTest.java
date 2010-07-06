package com.zutubi.android.droidscope;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.zutubi.android.libpulse.ProjectStatus;

public class ProjectStatusCacheTest
{
    private ProjectStatusCache cache;

    @Before
    public void setUp()
    {
        cache = new ProjectStatusCache();
    }

    @Test
    public void testPutFind()
    {
        cache.put(status("p1", 0));
        
        assertEquals(status("p1", 0), cache.findByProjectName("p1"));
        assertNull(cache.findByProjectName("unknown"));
    }

    @Test
    public void testPutGetAll()
    {
        cache.putAll(Arrays.asList(status("p1", 0), status("p2", 0)));
        
        assertEquals(Arrays.asList(status("p1", 0), status("p2", 0)), cache.getAll());
        assertNull(cache.findByProjectName("unknown"));
    }

    @Test
    public void testClear()
    {
        cache.put(status("p1", 0));
        cache.put(status("p2", 0));
        assertEquals(2, cache.getAll().size());
        
        cache.clear();
        
        assertEquals(0, cache.getAll().size());
    }

    @Test
    public void testGetOldestTimestampEmpty()
    {
        assertEquals(-1, cache.getOldestTimestamp());
    }

    @Test
    public void testGetOldestTimestampSingleEntry()
    {
        cache.put(status("name", 101));
        assertEquals(101, cache.getOldestTimestamp());
    }

    @Test
    public void testGetOldestTimestampMultipleEntries()
    {
        cache.put(status("1", 101));
        cache.put(status("2", 9));
        cache.put(status("3", 13456));
        cache.put(status("4", 8080));
        cache.put(status("5", 11));
        assertEquals(9, cache.getOldestTimestamp());
    }

    private ProjectStatus status(String name, long timestamp)
    {
        return new ProjectStatus(name, null, null, timestamp);
    }
}
