package nodomain.freeyourgadget.gadgetbridge.incident.responder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;

public class RootlyResponder implements IncidentResponder {
    private static final Logger LOG = LoggerFactory.getLogger(RootlyResponder.class);
    private static final String API_BASE = "https://api.rootly.com/v1/incidents";

    @Override
    public void acknowledge(String incidentId) {
        sendPatch(incidentId, "{\"data\":{\"attributes\":{\"status\":\"mitigated\"}}}");
    }

    @Override
    public void escalate(String incidentId) {
        LOG.info("Escalate not yet implemented for Rootly incident {}", incidentId);
    }

    @Override
    public void resolve(String incidentId) {
        sendPatch(incidentId, "{\"data\":{\"attributes\":{\"status\":\"resolved\"}}}");
    }

    private void sendPatch(String incidentId, String payload) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(API_BASE + "/" + incidentId);
            conn = (HttpURLConnection) url.openConnection();
            // HttpURLConnection doesn't support PATCH natively, use POST with override header
            conn.setRequestMethod("POST");
            conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            conn.setRequestProperty("Authorization", "Bearer " + getApiToken());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                LOG.info("Rootly incident {} updated successfully", incidentId);
            } else {
                LOG.warn("Rootly incident {} update failed with code {}", incidentId, responseCode);
            }
        } catch (Exception e) {
            LOG.error("Failed to update Rootly incident {}", incidentId, e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private String getApiToken() {
        return GBApplication.getPrefs().getString("rootly_api_token", "");
    }
}
