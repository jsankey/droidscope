package com.zutubi.android.droidscope;

/**
 * Simple indirection for access to settings, allowing swapping in of fake
 * settings in testing.  I'm yet to find a way to use normal DI with the
 * Android testing framework -- due to a chicken and egg problem (I need
 * the activity to get preferences, but can't wire the activity post-create
 * as it is too late).
 */
public class SettingsHolder
{
    private static ISettings settings;
    
    public static ISettings getSettings()
    {
        return settings;
    }
    
    public static void setSettings(ISettings settings)
    {
        SettingsHolder.settings = settings;
    }
}
