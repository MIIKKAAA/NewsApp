package com.example.miikka.newsapp;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Fragment luokka preferences näkymälle. Ladataan XML:stä (preferences.xml).
 */
@SuppressWarnings("deprecation")
public class SettingsActivityFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.preferences);

    }
}