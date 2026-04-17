package nodomain.freeyourgadget.gadgetbridge.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.incident.ConnectionTester;
import nodomain.freeyourgadget.gadgetbridge.incident.IncidentConstants;
import nodomain.freeyourgadget.gadgetbridge.util.GB;

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

            setupTestConnectionPreference(IncidentConstants.PREF_TEST_PAGERDUTY_CONNECTION, ConnectionTester::testPagerDuty);
            setupTestConnectionPreference(IncidentConstants.PREF_TEST_OPSGENIE_CONNECTION, ConnectionTester::testOpsgenie);
            setupTestConnectionPreference(IncidentConstants.PREF_TEST_ROOTLY_CONNECTION, ConnectionTester::testRootly);
        }

        private void setupTestConnectionPreference(String key, TestConnectionRunnable testRunnable) {
            Preference preference = findPreference(key);
            if (preference != null) {
                preference.setOnPreferenceClickListener(p -> {
                    new Thread(() -> {
                        ConnectionTester.TestResult result = testRunnable.runTest();
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                GB.toast(getActivity(), result.message, Toast.LENGTH_LONG, result.success ? GB.INFO : GB.ERROR);
                            });
                        }
                    }).start();
                    return true;
                });
            }
        }

        private interface TestConnectionRunnable {
            ConnectionTester.TestResult runTest();
        }
    }
}
