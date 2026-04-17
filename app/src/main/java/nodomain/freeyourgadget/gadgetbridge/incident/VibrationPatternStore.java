package nodomain.freeyourgadget.gadgetbridge.incident;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;

public class VibrationPatternStore {

    public static int[] loadPattern(String severity) {
        String key = getKeyForSeverity(severity);
        if (key == null) return null;

        String patternStr = GBApplication.getPrefs().getString(key, "");
        if (patternStr.isEmpty()) {
            return null;
        }

        try {
            String[] parts = patternStr.split(",");
            int[] pattern = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                pattern[i] = Integer.parseInt(parts[i].trim());
            }
            return pattern;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static void savePattern(String severity, int[] pattern) {
        String key = getKeyForSeverity(severity);
        if (key == null || pattern == null) return;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pattern.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(pattern[i]);
        }
        GBApplication.getPrefs().getPreferences().edit().putString(key, sb.toString()).apply();
    }

    public static void resetPattern(String severity) {
        String key = getKeyForSeverity(severity);
        if (key != null) {
            GBApplication.getPrefs().getPreferences().edit().remove(key).apply();
        }
    }

    private static String getKeyForSeverity(String severity) {
        if (severity == null) return null;
        switch (severity.toUpperCase(java.util.Locale.ROOT)) {
            case "P1":
            case "SEV1":
            case "CRITICAL":
                return IncidentConstants.PREF_VIBRATION_P1;
            case "P2":
            case "SEV2":
            case "HIGH":
                return IncidentConstants.PREF_VIBRATION_P2;
            case "P3":
            case "SEV3":
            case "MEDIUM":
                return IncidentConstants.PREF_VIBRATION_P3;
            case "P4":
            case "LOW":
                return IncidentConstants.PREF_VIBRATION_P4;
            default:
                return null;
        }
    }
}
