package com.zutubi.android.droidscope.test;

import com.zutubi.android.droidscope.ISettings;

/**
 * Testing implementation of the {@link ISettings} interface.
 */
class FakeSettings implements ISettings
{
    private String url;
    private String username;
    private String password;
    private boolean refreshOnResume = false;
    private int staleAge;
    
    public FakeSettings(String url, String username, String password)
    {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public String getURL()
    {
        return url;
    }

    @Override
    public String getUsername()
    {
        return username;
    }

    @Override
    public String getPassword()
    {
        return password;
    }

    @Override
    public boolean isRefreshOnResume()
    {
        return refreshOnResume;
    }
    
    public void setRefreshOnResume(boolean refreshOnResume)
    {
        this.refreshOnResume = refreshOnResume;
    }

    @Override
    public int getStaleAge()
    {
        return staleAge;
    }
    
    public void setStaleAge(int staleAge)
    {
        this.staleAge = staleAge;
    }
}
