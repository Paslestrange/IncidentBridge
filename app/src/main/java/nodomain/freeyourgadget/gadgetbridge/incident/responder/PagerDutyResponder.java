package nodomain.freeyourgadget.gadgetbridge.incident.responder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.incident.ResponderResult;

public class PagerDutyResponder implements IncidentResponder {
    private static final Logger LOG = LoggerFactory.getLogger(PagerDutyResponder.class);
    private static final String API_BASE = "https://api.pagerduty.com/incidents";

    @Override
    public ResponderResult acknowledge(String incidentId) {
        return sendAction(incidentId, "acknowledged");
    }

    @Override
    public ResponderResult escalate(String incidentId) {
        LOG.info("Escalate not directly supported via PagerDuty v2 API for incident {}", incidentId);
        return ResponderResult.FAILED;
    }

    @Override
    public ResponderResult resolve(String incidentId) {
        return sendAction(incidentId, "resolved");
    }

    private ResponderResult sendAction(String incidentId, String status) {
        if (incidentId == null || incidentId.isEmpty()) {
            LOG.warn("Invalid incidentId for PagerDuty {}", status);
            return ResponderResult.FAILED;
        }
        String token = getApiToken();
        String email = getUserEmail();
        if (token.isEmpty() || email.isEmpty()) {
            LOG.warn("PagerDuty credentials not configured");
            return ResponderResult.NO_CREDENTIALS;
        }
        HttpURLConnection conn = null;
        try {
            URL url = new URL(API_BASE + "/" + incidentId);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", "Token token=" + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/vnd.pagerduty+json;version=2");
            conn.setRequestProperty("From", email);
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
                return ResponderResult.SUCCESS;
            } else {
                LOG.warn("PagerDuty incident {} {} failed with code {}", incidentId, status, responseCode);
                return ResponderResult.FAILED;
            }
        } catch (SocketTimeoutException e) {
            LOG.error("Timeout while {} PagerDuty incident {}", status, incidentId, e);
            return ResponderResult.TIMEOUT;
        } catch (Exception e) {
            LOG.error("Failed to {} PagerDuty incident {}", status, incidentId, e);
            return ResponderResult.FAILED;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private String getApiToken() {
        return GBApplication.getPrefs().getString("pagerduty_api_token", "");
    }

    private String getUserEmail() {
        return GBApplication.getPrefs().getString("pagerduty_user_email", "");
    }
}
