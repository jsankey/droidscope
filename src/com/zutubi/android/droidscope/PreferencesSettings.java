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
    public static final String PROPERTY_REFRESH_ON_RESUME = "refreshOnResume";
    public static final String PROPERTY_STALE_AGE = "staleAge";
    
    private static final int DEFAULT_STALE_AGE = 60;
    
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

    @Override
    public boolean isRefreshOnResume()
    {
        return preferences.getBoolean(PROPERTY_REFRESH_ON_RESUME, true);
    }
    
    @Override
    public int getStaleAge()
    {
        String s = preferences.getString(PROPERTY_STALE_AGE, Integer.toString(DEFAULT_STALE_AGE));
        try
        {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException e)
        {
            return DEFAULT_STALE_AGE;
        }
    }
}
