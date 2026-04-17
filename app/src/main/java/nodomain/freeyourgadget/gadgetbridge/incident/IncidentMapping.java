package nodomain.freeyourgadget.gadgetbridge.incident;

import java.util.HashMap;
import java.util.Map;

public class IncidentMapping {
    private static final Map<String, IncidentInfo> mappings = new HashMap<>();

    public static void put(String notificationKey, String incidentId, IncidentAppConfig.IncidentProvider provider) {
        mappings.put(notificationKey, new IncidentInfo(incidentId, provider));
    }

    public static IncidentInfo get(String notificationKey) {
        return mappings.get(notificationKey);
    }

    public static void remove(String notificationKey) {
        mappings.remove(notificationKey);
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
