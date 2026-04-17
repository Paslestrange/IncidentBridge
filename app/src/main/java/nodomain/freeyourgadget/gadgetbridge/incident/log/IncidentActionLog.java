package nodomain.freeyourgadget.gadgetbridge.incident.log;

public class IncidentActionLog {
    public final long timestamp;
    public final String provider;
    public final String incidentId;
    public final String action;
    public final String status;
    public final long responseTimeMs;

    public IncidentActionLog(long timestamp, String provider, String incidentId, String action, String status, long responseTimeMs) {
        this.timestamp = timestamp;
        this.provider = provider;
        this.incidentId = incidentId;
        this.action = action;
        this.status = status;
        this.responseTimeMs = responseTimeMs;
    }
}
