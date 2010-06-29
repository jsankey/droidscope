package com.zutubi.android.droidscope;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Global state for DroidScope.  Serves as a static registry, but allows
 * implementations to be swapped in for testing.
 */
public class DroidScopeApplication extends Application
{
    private static ProjectStatusCache projectStatusCache;
    private static ISettings settings;

    @Override
    public void onCreate()
    {
        super.onCreate();
        
        if (projectStatusCache == null)
        {
            projectStatusCache = new ProjectStatusCache();
        }
        
        if (settings == null)
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            settings = new PreferencesSettings(preferences);
        }
    }
    
    public static ProjectStatusCache getProjectStatusCache()
    {
        return projectStatusCache;
    }

    public static void setProjectStatusCache(ProjectStatusCache projectStatusCache)
    {
        DroidScopeApplication.projectStatusCache = projectStatusCache;
    }
    
    public static ISettings getSettings()
    {
        return settings;
    }
    
    public static void setSettings(ISettings settings)
    {
        DroidScopeApplication.settings = settings;
    }
}
