package nodomain.freeyourgadget.gadgetbridge.incident;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class IncidentMapping {
    private static final ConcurrentHashMap<String, IncidentInfo> mappings = new ConcurrentHashMap<>();
    private static final AtomicReference<String> mostRecentKey = new AtomicReference<>();

    public static void put(String notificationKey, String incidentId, IncidentAppConfig.IncidentProvider provider) {
        mappings.put(notificationKey, new IncidentInfo(incidentId, provider));
        mostRecentKey.set(notificationKey);
    }

    public static IncidentInfo get(String notificationKey) {
        return mappings.get(notificationKey);
    }

    public static IncidentInfo getMostRecent() {
        String key = mostRecentKey.get();
        if (key != null) {
            return mappings.get(key);
        }
        return mappings.isEmpty() ? null : mappings.values().iterator().next();
    }

    public static void remove(String notificationKey) {
        mappings.remove(notificationKey);
        mostRecentKey.compareAndSet(notificationKey, null);
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
