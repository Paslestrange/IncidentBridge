package nodomain.freeyourgadget.gadgetbridge.incident.responder;

import nodomain.freeyourgadget.gadgetbridge.incident.IncidentAppConfig;
import nodomain.freeyourgadget.gadgetbridge.incident.ResponderResult;
import nodomain.freeyourgadget.gadgetbridge.incident.webhook.WebhookResponder;

public interface IncidentResponder {
    ResponderResult acknowledge(String incidentId);
    ResponderResult escalate(String incidentId);
    ResponderResult resolve(String incidentId);

    static IncidentResponder getResponder(IncidentAppConfig.IncidentProvider provider) {
        switch (provider) {
            case PAGERDUTY:
                return new PagerDutyResponder();
            case OPSGENIE:
                return new OpsgenieResponder();
            case ROOTLY:
                return new RootlyResponder();
            case WEBHOOK:
                return new WebhookResponder();
            default:
                return null;
        }
    }
}
