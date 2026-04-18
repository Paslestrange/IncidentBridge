package nodomain.freeyourgadget.gadgetbridge.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import java.util.Arrays;
import java.util.List;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.incident.ConnectionTester;
import nodomain.freeyourgadget.gadgetbridge.incident.IncidentConstants;
import nodomain.freeyourgadget.gadgetbridge.incident.VibrationRule;
import nodomain.freeyourgadget.gadgetbridge.incident.VibrationRuleStore;
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

            setupVibrationRules();
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

        private void setupVibrationRules() {
            PreferenceCategory category = findPreference("vibration_rules_category");
            if (category == null) {
                return;
            }

            List<Preference> stalePreferences = new java.util.ArrayList<>();
            for (int i = 0; i < category.getPreferenceCount(); i++) {
                Preference pref = category.getPreference(i);
                String key = pref.getKey();
                if (key != null && key.startsWith("vibration_rule_")) {
                    stalePreferences.add(pref);
                }
            }
            for (Preference pref : stalePreferences) {
                category.removePreference(pref);
            }

            List<VibrationRule> rules = VibrationRuleStore.loadRules();
            for (int i = 0; i < rules.size(); i++) {
                final VibrationRule rule = rules.get(i);

                SwitchPreferenceCompat enabledSwitch = new SwitchPreferenceCompat(requireContext());
                enabledSwitch.setKey("vibration_rule_" + i + "_enabled");
                enabledSwitch.setTitle(rule.name);
                enabledSwitch.setSummary(getString(R.string.pref_summary_vibration_rule_keyword, rule.keyword));
                enabledSwitch.setChecked(rule.enabled);
                enabledSwitch.setOnPreferenceChangeListener((pref, newValue) -> {
                    rule.enabled = (Boolean) newValue;
                    saveRules(rules);
                    return true;
                });
                category.addPreference(enabledSwitch);

                Preference testPref = new Preference(requireContext());
                testPref.setKey("vibration_rule_" + i + "_test");
                testPref.setTitle(R.string.pref_title_test_vibration);
                testPref.setSummary(getString(R.string.pref_summary_test_vibration, Arrays.toString(rule.pattern)));
                testPref.setOnPreferenceClickListener(p -> {
                    testVibration(rule.pattern);
                    return true;
                });
                category.addPreference(testPref);
            }
        }

        private void saveRules(List<VibrationRule> rules) {
            VibrationRuleStore.saveRules(rules);
        }

        private void setupResetRules() {
            Preference resetPref = findPreference("reset_vibration_rules");
            if (resetPref != null) {
                resetPref.setOnPreferenceClickListener(p -> {
                    VibrationRuleStore.resetToDefaults();
                    setupVibrationRules();
                    GB.toast(getActivity(), R.string.pref_toast_rules_reset, Toast.LENGTH_SHORT, GB.INFO);
                    return true;
                });
            }
        }

        private void testVibration(int[] pattern) {
            Context context = requireContext();
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator == null || !vibrator.hasVibrator()) {
                GB.toast(context, R.string.pref_toast_no_vibrator, Toast.LENGTH_SHORT, GB.ERROR);
                return;
            }

            long[] timings = new long[pattern.length];
            for (int i = 0; i < pattern.length; i++) {
                timings[i] = pattern[i];
            }

            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, -1));
                } else {
                    vibrator.vibrate(timings, -1);
                }
                GB.toast(context, R.string.pref_toast_vibration_tested, Toast.LENGTH_SHORT, GB.INFO);
            } catch (Exception e) {
                GB.toast(context, R.string.pref_toast_vibration_failed, Toast.LENGTH_SHORT, GB.ERROR);
            }
        }

        private interface TestConnectionRunnable {
            ConnectionTester.TestResult runTest();
        }
    }
}
