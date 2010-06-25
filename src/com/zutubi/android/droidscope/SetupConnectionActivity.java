package com.zutubi.android.droidscope;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SetupConnectionActivity extends Activity implements OnClickListener
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection);
        setTitle(R.string.connection);
    
        Button okButton = (Button) findViewById(R.id.connection_ok);
        okButton.setOnClickListener(this);
    }
    
    @Override
    protected void onStart()
    {
        super.onStart();
        
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        populateField(preferences, PreferencesSettings.PROPERTY_URL, R.id.connection_pulse_url);
        populateField(preferences, PreferencesSettings.PROPERTY_USERNAME, R.id.connection_username);
        populateField(preferences, PreferencesSettings.PROPERTY_PASSWORD, R.id.connection_password);
    }
    
    private void populateField(final SharedPreferences preferences, String property, int viewId)
    {
        String value = preferences.getString(property, null);
        if (value != null)
        {
            ((TextView) findViewById(viewId)).setText(value);
        }
    }


    @Override
    public void onClick(View v)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        CharSequence url = ((TextView) findViewById(R.id.connection_pulse_url)).getText();
        CharSequence username = ((TextView) findViewById(R.id.connection_username))
                        .getText();
        CharSequence password = ((TextView) findViewById(R.id.connection_password))
                        .getText();

        Editor editor = preferences.edit();
        editor.putString(PreferencesSettings.PROPERTY_URL, url.toString());
        editor.putString(PreferencesSettings.PROPERTY_USERNAME, username.toString());
        editor.putString(PreferencesSettings.PROPERTY_PASSWORD, password.toString());
        editor.commit();
        finish();
    }
}
