package com.zutubi.android.droidscope;

import android.content.SharedPreferences;

/**
 * A settings implementation based on the default preferences.  This is the
 * implementation used in production.
 */
public class PreferencesSettings implements ISettings
{
    public static final String PROPERTY_PASSWORD = "password";
    public static final String PROPERTY_URL = "url";
    public static final String PROPERTY_USERNAME = "username";
    
    private SharedPreferences preferences;

    public PreferencesSettings(SharedPreferences preferences)
    {
        this.preferences = preferences;
    }

    @Override
    public String getURL()
    {
        return preferences.getString(PROPERTY_URL, "");
    }

    @Override
    public String getUsername()
    {
        return preferences.getString(PROPERTY_USERNAME, "");
    }

    @Override
    public String getPassword()
    {
        return preferences.getString(PROPERTY_PASSWORD, "");
    }
}
