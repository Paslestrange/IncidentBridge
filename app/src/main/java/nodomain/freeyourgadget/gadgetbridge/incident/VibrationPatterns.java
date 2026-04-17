package nodomain.freeyourgadget.gadgetbridge.incident;

public class VibrationPatterns {
    public static final int[] P1_SOS = {200, 100, 200, 100, 200, 100, 600, 100, 600, 100, 600};
    public static final int[] P2_R = {400, 100, 200, 100, 400};
    public static final int[] P3_SINGLE = {100};
    public static final int[] P4_GENTLE = {50, 200, 50};
    public static final int[] SUCCESS_PATTERN = {100, 50, 100};
    public static final int[] FAILURE_PATTERN = {400, 100, 400, 100, 400};

    private VibrationPatterns() {
    }

    public static int[] getPattern(String severity) {
        if (severity == null) {
            return P4_GENTLE;
        }
        switch (severity.toUpperCase(java.util.Locale.ROOT)) {
            case "P1":
            case "SEV1":
            case "CRITICAL":
                return P1_SOS;
            case "P2":
            case "SEV2":
            case "HIGH":
                return P2_R;
            case "P3":
            case "SEV3":
            case "MEDIUM":
                return P3_SINGLE;
            case "SUCCESS":
                return SUCCESS_PATTERN;
            case "FAILURE":
                return FAILURE_PATTERN;
            default:
                return P4_GENTLE;
        }
    }
}
