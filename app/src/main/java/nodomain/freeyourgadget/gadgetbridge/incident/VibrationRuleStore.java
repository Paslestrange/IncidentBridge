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
                String id = obj.optString("id", "");
                String name = obj.optString("name", "Rule " + (i + 1));
                String keyword = obj.optString("keyword", "");
                JSONArray patternArr = obj.getJSONArray("pattern");
                int[] pattern = new int[patternArr.length()];
                for (int j = 0; j < patternArr.length(); j++) {
                    pattern[j] = patternArr.getInt(j);
                }
                boolean repeat = obj.optBoolean("repeat", false);
                int interval = obj.optInt("interval", 15000);
                boolean enabled = obj.optBoolean("enabled", true);
                rules.add(new VibrationRule(id, name, keyword, pattern, repeat, interval, enabled));
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
                obj.put("id", rule.id);
                obj.put("name", rule.name);
                obj.put("keyword", rule.keyword);
                JSONArray patternArr = new JSONArray();
                for (int p : rule.pattern) {
                    patternArr.put(p);
                }
                obj.put("pattern", patternArr);
                obj.put("repeat", rule.repeatUntilAcked);
                obj.put("interval", rule.repeatIntervalMs);
                obj.put("enabled", rule.enabled);
                array.put(obj);
            }
            GBApplication.getPrefs().getPreferences().edit().putString(PREF_VIBRATION_RULES, array.toString()).apply();
        } catch (JSONException e) {
            LOG.error("Failed to save vibration rules", e);
        }
    }

    public static List<VibrationRule> getDefaultRules() {
        List<VibrationRule> rules = new ArrayList<>();
        rules.add(new VibrationRule(
            "P1 / Critical",
            "P1,CRITICAL,SEV1",
            new int[]{400, 100, 400, 100, 400},
            true,
            15000,
            true
        ));
        rules.add(new VibrationRule(
            "P2 / High",
            "P2,HIGH,SEV2",
            new int[]{200, 50, 200, 50, 200},
            false,
            0,
            true
        ));
        rules.add(new VibrationRule(
            "P3 / Medium",
            "P3,MEDIUM,SEV3",
            new int[]{200, 50, 200},
            false,
            0,
            true
        ));
        rules.add(new VibrationRule(
            "P4 / Low",
            "P4,LOW,SEV4",
            new int[]{100},
            false,
            0,
            false
        ));
        return rules;
    }

    public static void resetToDefaults() {
        saveRules(getDefaultRules());
    }

    public static VibrationRule findMatchingRule(String text) {
        List<VibrationRule> rules = loadRules();
        for (VibrationRule rule : rules) {
            if (rule.matches(text)) {
                return rule;
            }
        }
        return null;
    }

    public static void addRule(VibrationRule rule) {
        List<VibrationRule> rules = loadRules();
        rules.add(rule);
        saveRules(rules);
    }

    public static void updateRule(VibrationRule updatedRule) {
        List<VibrationRule> rules = loadRules();
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).id.equals(updatedRule.id)) {
                rules.set(i, updatedRule);
                saveRules(rules);
                return;
            }
        }
    }

    public static void deleteRule(String ruleId) {
        List<VibrationRule> rules = loadRules();
        rules.removeIf(rule -> rule.id.equals(ruleId));
        saveRules(rules);
    }

    public static void deleteRule(int index) {
        List<VibrationRule> rules = loadRules();
        if (index >= 0 && index < rules.size()) {
            rules.remove(index);
            saveRules(rules);
        }
    }
}
