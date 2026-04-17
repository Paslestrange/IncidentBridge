package nodomain.freeyourgadget.gadgetbridge.incident;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class IncidentAppConfig {
    public static final String PAGERDUTY = "com.pagerduty.android";
    public static final String OPSGENIE = "com.opsgenie.app";
    public static final String ROOTLY = "com.rootly.mobile";
    public static final String SLACK = "com.Slack";

    private static final Set<String> INCIDENT_APPS = new HashSet<>(Arrays.asList(
            PAGERDUTY,
            OPSGENIE,
            ROOTLY
    ));

    private IncidentAppConfig() {
    }

    public static boolean isIncidentApp(String packageName) {
        return packageName != null && INCIDENT_APPS.contains(packageName);
    }

    public static IncidentProvider getProvider(String packageName) {
        if (PAGERDUTY.equals(packageName)) return IncidentProvider.PAGERDUTY;
        if (OPSGENIE.equals(packageName)) return IncidentProvider.OPSGENIE;
        if (ROOTLY.equals(packageName)) return IncidentProvider.ROOTLY;
        return IncidentProvider.UNKNOWN;
    }

    public enum IncidentProvider {
        PAGERDUTY,
        OPSGENIE,
        ROOTLY,
        UNKNOWN
    }
}
