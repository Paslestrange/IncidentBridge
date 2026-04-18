package nodomain.freeyourgadget.gadgetbridge.incident;

public class VibrationRule {
    public String keyword;
    public int[] pattern;
    public boolean repeatUntilAcked;
    public int repeatIntervalMs;
    public String severity;

    public VibrationRule(String keyword, int[] pattern, boolean repeatUntilAcked, int repeatIntervalMs) {
        this.keyword = keyword;
        this.pattern = pattern;
        this.repeatUntilAcked = repeatUntilAcked;
        this.repeatIntervalMs = repeatIntervalMs;
        this.severity = null;
    }

    public VibrationRule(String severity, int[] pattern, boolean repeatUntilAcked, int repeatIntervalMs, boolean isSeverityRule) {
        this.keyword = null;
        this.severity = severity;
        this.pattern = pattern;
        this.repeatUntilAcked = repeatUntilAcked;
        this.repeatIntervalMs = repeatIntervalMs;
    }

    public boolean matches(String text, String severity) {
        if (this.severity != null && this.severity.equalsIgnoreCase(severity)) {
            return true;
        }
        if (this.keyword != null && text != null && text.toUpperCase().contains(this.keyword.toUpperCase())) {
            return true;
        }
        return false;
    }
}
