package nodomain.freeyourgadget.gadgetbridge.incident.responder;

import nodomain.freeyourgadget.gadgetbridge.incident.IncidentAppConfig;

public interface IncidentResponder {
    void acknowledge(String incidentId);
    void escalate(String incidentId);
    void resolve(String incidentId);

    static IncidentResponder getResponder(IncidentAppConfig.IncidentProvider provider) {
        switch (provider) {
            case PAGERDUTY:
                return new PagerDutyResponder();
            case OPSGENIE:
                return new OpsgenieResponder();
            case ROOTLY:
                return new RootlyResponder();
            default:
                return null;
        }
    }
}
