package com.lgcampos.flagquiz;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Fragment of {@link SettingsActivity}
 *
 * @author Lucas Gonçalves de Campos
 */
public class SettingsActivityFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.preferences);
    }

}
