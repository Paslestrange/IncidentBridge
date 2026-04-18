package nodomain.freeyourgadget.gadgetbridge.incident;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;

public class VibrationRuleStore {
    private static final Logger LOG = LoggerFactory.getLogger(VibrationRuleStore.class);
    private static final String PREF_VIBRATION_RULES = "vibration_rules";

    public static List<VibrationRule> loadRules() {
        List<VibrationRule> rules = new ArrayList<>();
        String json = GBApplication.getPrefs().getString(PREF_VIBRATION_RULES, "");
        if (json.isEmpty()) {
            return getDefaultRules();
        }
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String keyword = obj.optString("keyword", null);
                String severity = obj.optString("severity", null);
                JSONArray patternArr = obj.getJSONArray("pattern");
                int[] pattern = new int[patternArr.length()];
                for (int j = 0; j < patternArr.length(); j++) {
                    pattern[j] = patternArr.getInt(j);
                }
                boolean repeat = obj.optBoolean("repeat", false);
                int interval = obj.optInt("interval", 30000);
                
                if (severity != null && !severity.isEmpty()) {
                    rules.add(new VibrationRule(severity, pattern, repeat, interval, true));
                } else {
                    rules.add(new VibrationRule(keyword, pattern, repeat, interval));
                }
            }
        } catch (JSONException e) {
            LOG.error("Failed to parse vibration rules", e);
            return getDefaultRules();
        }
        return rules;
    }

    public static void saveRules(List<VibrationRule> rules) {
        try {
            JSONArray array = new JSONArray();
            for (VibrationRule rule : rules) {
                JSONObject obj = new JSONObject();
                if (rule.keyword != null) obj.put("keyword", rule.keyword);
                if (rule.severity != null) obj.put("severity", rule.severity);
                JSONArray patternArr = new JSONArray();
                for (int p : rule.pattern) {
                    patternArr.put(p);
                }
                obj.put("pattern", patternArr);
                obj.put("repeat", rule.repeatUntilAcked);
                obj.put("interval", rule.repeatIntervalMs);
                array.put(obj);
            }
            GBApplication.getPrefs().getPreferences().edit().putString(PREF_VIBRATION_RULES, array.toString()).apply();
        } catch (JSONException e) {
            LOG.error("Failed to save vibration rules", e);
        }
    }

    public static List<VibrationRule> getDefaultRules() {
        List<VibrationRule> rules = new ArrayList<>();
        boolean p1Repeat = GBApplication.getPrefs().getBoolean("p1_repeat", true);
        int p1Interval = parseInterval(GBApplication.getPrefs().getString("p1_interval", "15000"));
        boolean p2Repeat = GBApplication.getPrefs().getBoolean("p2_repeat", false);
        rules.add(new VibrationRule(IncidentConstants.SEV_P1, new int[]{400, 100, 400, 100, 400}, p1Repeat, p1Interval, true));
        rules.add(new VibrationRule(IncidentConstants.SEV_P2, new int[]{400, 100, 400}, p2Repeat, p1Interval, true));
        rules.add(new VibrationRule(IncidentConstants.SEV_P3, new int[]{200, 100, 200}, false, 0, true));
        rules.add(new VibrationRule(IncidentConstants.SEV_P4, new int[]{100}, false, 0, true));
        return rules;
    }

    private static int parseInterval(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 15000;
        }
    }

    public static void resetToDefaults() {
        saveRules(getDefaultRules());
    }
}
