package nodomain.freeyourgadget.gadgetbridge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

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

            setupCustomRulesLink();
            setupDeviceSelectionLink();
            setupResetRules();
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

        private void setupCustomRulesLink() {
            Preference rulesPref = findPreference("custom_vibration_rules");
            if (rulesPref != null) {
                rulesPref.setOnPreferenceClickListener(p -> {
                    Intent intent = new Intent(getActivity(), CustomVibrationRulesActivity.class);
                    startActivity(intent);
                    return true;
                });
            }
        }

        private void setupDeviceSelectionLink() {
            Preference devicePref = findPreference("select_incident_device");
            if (devicePref != null) {
                devicePref.setOnPreferenceClickListener(p -> {
                    Intent intent = new Intent(getActivity(), DeviceSelectionActivity.class);
                    startActivity(intent);
                    return true;
                });
            }
        }

        private void setupResetRules() {
            Preference resetPref = findPreference("reset_vibration_rules");
            if (resetPref != null) {
                resetPref.setOnPreferenceClickListener(p -> {
                    nodomain.freeyourgadget.gadgetbridge.incident.VibrationRuleStore.resetToDefaults();
                    GB.toast(getActivity(), R.string.pref_toast_rules_reset, Toast.LENGTH_SHORT, GB.INFO);
                    return true;
                });
            }
        }

        private interface TestConnectionRunnable {
            ConnectionTester.TestResult runTest();
        }
    }
}
