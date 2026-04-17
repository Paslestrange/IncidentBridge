package nodomain.freeyourgadget.gadgetbridge.activities;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import nodomain.freeyourgadget.gadgetbridge.R;

public class IncidentManagementPreferencesActivity extends AbstractGBActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_management_preferences);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.incident_management_preferences_container, new IncidentManagementPreferencesFragment())
                    .commit();
        }
    }

    public static class IncidentManagementPreferencesFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.prefs_incident_management, rootKey);
        }
    }
}
