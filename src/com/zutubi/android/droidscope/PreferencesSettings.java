package com.zutubi.android.droidscope;

import android.content.SharedPreferences;

/**
 * A settings implementation based on the default preferences.  This is the
 * implementation used in production.
 */
public class PreferencesSettings implements ISettings
{
    private SharedPreferences preferences;

    public PreferencesSettings(SharedPreferences preferences)
    {
        this.preferences = preferences;
    }

    @Override
    public String getURL()
    {
        return preferences.getString("url", "");
    }

    @Override
    public String getUsername()
    {
        return preferences.getString("username", "");
    }

    @Override
    public String getPassword()
    {
        return preferences.getString("password", "");
    }
}
