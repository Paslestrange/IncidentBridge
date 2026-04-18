package nodomain.freeyourgadget.gadgetbridge.incident;

import java.util.UUID;

public class VibrationRule {
    public String id;
    public String name;
    public String keyword;
    public int[] pattern;
    public boolean repeatUntilAcked;
    public int repeatIntervalMs;
    public boolean enabled;

    public VibrationRule(String name, String keyword, int[] pattern, boolean repeatUntilAcked, int repeatIntervalMs, boolean enabled) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.keyword = keyword;
        this.pattern = pattern;
        this.repeatUntilAcked = repeatUntilAcked;
        this.repeatIntervalMs = repeatIntervalMs;
        this.enabled = enabled;
    }

    public VibrationRule(String id, String name, String keyword, int[] pattern, boolean repeatUntilAcked, int repeatIntervalMs, boolean enabled) {
        this.id = id;
        this.name = name;
        this.keyword = keyword;
        this.pattern = pattern;
        this.repeatUntilAcked = repeatUntilAcked;
        this.repeatIntervalMs = repeatIntervalMs;
        this.enabled = enabled;
    }

    public boolean matches(String text) {
        if (!enabled || keyword == null || text == null) {
            return false;
        }
        String upperText = text.toUpperCase();
        String[] keywords = keyword.toUpperCase().split(",");
        for (String k : keywords) {
            k = k.trim();
            if (!k.isEmpty() && upperText.contains(k)) {
                return true;
            }
        }
        return false;
    }
}
