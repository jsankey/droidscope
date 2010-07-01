package com.zutubi.android.droidscope;

import java.io.IOException;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

import com.zutubi.android.libpulse.IPulse;
import com.zutubi.android.libpulse.internal.Pulse;
import com.zutubi.android.libpulse.internal.PulseClient;

/**
 * Global state for DroidScope.  Serves as a static registry, but allows
 * implementations to be swapped in for testing.
 */
public class DroidScopeApplication extends Application implements OnSharedPreferenceChangeListener
{
    private static ProjectStatusCache projectStatusCache;
    private static IPulse pulse;
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
            preferences.registerOnSharedPreferenceChangeListener(this);
            settings = new PreferencesSettings(preferences);
        }
    }
    
    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key)
    {
        try
        {
            if (pulse != null)
            {
                pulse.close();
            }
        }
        catch (IOException e)
        {
        }

        pulse = null;
        settings = new PreferencesSettings(preferences);
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

    public static IPulse getPulse()
    {
        if (pulse == null)
        {
            pulse = new Pulse(new PulseClient(settings.getURL(), settings.getUsername(), settings.getPassword()));
        }
        
        return pulse;
    }
    
    public static void setPulse(IPulse pulse)
    {
        DroidScopeApplication.pulse = pulse;
    }
}
