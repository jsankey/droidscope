package com.zutubi.android.droidscope;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * A trivial preferences activity, used for editing settings.
 */
public class Preferences extends PreferenceActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
