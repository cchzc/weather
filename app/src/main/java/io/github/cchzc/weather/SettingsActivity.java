package io.github.cchzc.weather;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.DropDownPreference;
import androidx.preference.PreferenceFragmentCompat;

import java.io.IOException;

import io.github.cchzc.weather.setting.CountyRepository;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            DropDownPreference county = findPreference(getString(R.string.preferences_county));
            DropDownPreference town = findPreference(getString(R.string.preferences_town));
            try {
                CountyRepository countyRepo = new CountyRepository(getContext());

                county.setEntries(countyRepo.getCountyNames());
                county.setEntryValues(countyRepo.getCountyIds());
                county.setOnPreferenceChangeListener((preference, newValue) -> {
                    town.setEntries(countyRepo.getTownNames(Integer.parseInt((String) newValue)));
                    town.setEntryValues(countyRepo.getTownIds(Integer.parseInt((String) newValue)));
                    town.setValueIndex(0);
                    return true;
                });

                if(county.getValue() == null)
                    county.setValueIndex(0);
                town.setEntries(countyRepo.getTownNames(Integer.parseInt(county.getValue())));
                town.setEntryValues(countyRepo.getTownIds(Integer.parseInt(county.getValue())));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}