package nodomain.freeyourgadget.gadgetbridge.incident;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class IncidentAppConfig {
    private static final Set<String> INCIDENT_APPS = new HashSet<>(Arrays.asList(
            IncidentConstants.PKG_PAGERDUTY,
            IncidentConstants.PKG_OPSGENIE,
            IncidentConstants.PKG_ROOTLY
    ));

    private IncidentAppConfig() {
    }

    public static boolean isIncidentApp(String packageName) {
        return packageName != null && INCIDENT_APPS.contains(packageName);
    }

    public static IncidentProvider getProvider(String packageName) {
        if (IncidentConstants.PKG_PAGERDUTY.equals(packageName)) return IncidentProvider.PAGERDUTY;
        if (IncidentConstants.PKG_OPSGENIE.equals(packageName)) return IncidentProvider.OPSGENIE;
        if (IncidentConstants.PKG_ROOTLY.equals(packageName)) return IncidentProvider.ROOTLY;
        return IncidentProvider.UNKNOWN;
    }

    public enum IncidentProvider {
        PAGERDUTY,
        OPSGENIE,
        ROOTLY,
        WEBHOOK,
        UNKNOWN
    }
}
