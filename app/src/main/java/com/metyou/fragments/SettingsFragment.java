package com.metyou.fragments;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.metyou.R;

/**
 * Created by mihai on 8/12/14.
 */
public class SettingsFragment extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
