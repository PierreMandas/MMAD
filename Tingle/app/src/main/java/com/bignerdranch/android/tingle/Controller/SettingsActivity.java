package com.bignerdranch.android.tingle.Controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.bignerdranch.android.tingle.R;

//Simple activity being used for the user to choose connection type (Wi-Fi or any connection).
//Uses deprecated method, as I couldn't find anything else.
public class SettingsActivity extends PreferenceActivity {

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, SettingsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
