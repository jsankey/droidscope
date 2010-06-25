package com.zutubi.android.droidscope.test;

import com.zutubi.android.droidscope.ISettings;

/**
 * Testing implementation of the {@link ISettings} interface.
 */
class FakeSettings implements ISettings
{
    String url;
    String username;
    String password;

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
}