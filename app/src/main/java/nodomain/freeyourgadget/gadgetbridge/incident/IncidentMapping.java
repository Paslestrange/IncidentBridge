package nodomain.freeyourgadget.gadgetbridge.incident;

import java.util.HashMap;
import java.util.Map;

public class IncidentMapping {
    private static final Map<String, IncidentInfo> mappings = new HashMap<>();
    private static String mostRecentKey = null;

    public static void put(String notificationKey, String incidentId, IncidentAppConfig.IncidentProvider provider) {
        mappings.put(notificationKey, new IncidentInfo(incidentId, provider));
        mostRecentKey = notificationKey;
    }

    public static IncidentInfo get(String notificationKey) {
        return mappings.get(notificationKey);
    }

    public static IncidentInfo getMostRecent() {
        if (mostRecentKey == null) {
            return mappings.isEmpty() ? null : mappings.values().iterator().next();
        }
        return mappings.get(mostRecentKey);
    }

    public static void remove(String notificationKey) {
        mappings.remove(notificationKey);
        if (notificationKey.equals(mostRecentKey)) {
            mostRecentKey = null;
        }
    }

    public static class IncidentInfo {
        public final String incidentId;
        public final IncidentAppConfig.IncidentProvider provider;

        public IncidentInfo(String incidentId, IncidentAppConfig.IncidentProvider provider) {
            this.incidentId = incidentId;
            this.provider = provider;
        }
    }
}
