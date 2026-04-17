package nodomain.freeyourgadget.gadgetbridge.incident.responder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;

public class PagerDutyResponder implements IncidentResponder {
    private static final Logger LOG = LoggerFactory.getLogger(PagerDutyResponder.class);
    private static final String API_BASE = "https://api.pagerduty.com/incidents";

    @Override
    public void acknowledge(String incidentId) {
        sendAction(incidentId, "acknowledged");
    }

    @Override
    public void escalate(String incidentId) {
        LOG.info("Escalate not directly supported via PagerDuty v2 API for incident {}", incidentId);
    }

    @Override
    public void resolve(String incidentId) {
        sendAction(incidentId, "resolved");
    }

    private void sendAction(String incidentId, String status) {
        try {
            URL url = new URL(API_BASE + "/" + incidentId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", "Token token=" + getApiToken());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("From", getUserEmail());
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            String payload = "{\"incident\":{\"type\":\"incident_reference\",\"status\":\"" + status + "\"}}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                LOG.info("PagerDuty incident {} {} successful", incidentId, status);
            } else {
                LOG.warn("PagerDuty incident {} {} failed with code {}", incidentId, status, responseCode);
            }
            conn.disconnect();
        } catch (Exception e) {
            LOG.error("Failed to {} PagerDuty incident {}", status, incidentId, e);
        }
    }

    private String getApiToken() {
        return GBApplication.getDeviceSpecificSharedPrefs(null)
                .getString("pagerduty_api_token", "");
    }

    private String getUserEmail() {
        return GBApplication.getDeviceSpecificSharedPrefs(null)
                .getString("pagerduty_user_email", "");
    }
}
