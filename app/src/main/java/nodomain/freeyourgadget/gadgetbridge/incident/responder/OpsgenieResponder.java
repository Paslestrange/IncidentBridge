package nodomain.freeyourgadget.gadgetbridge.incident.responder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;

public class OpsgenieResponder implements IncidentResponder {
    private static final Logger LOG = LoggerFactory.getLogger(OpsgenieResponder.class);
    private static final String API_BASE = "https://api.opsgenie.com/v2/alerts";

    @Override
    public void acknowledge(String incidentId) {
        sendPost(incidentId + "/acknowledge");
    }

    @Override
    public void escalate(String incidentId) {
        sendPost(incidentId + "/escalate");
    }

    @Override
    public void resolve(String incidentId) {
        sendPost(incidentId + "/close");
    }

    private void sendPost(String path) {
        if (path == null || path.isEmpty()) {
            LOG.warn("Invalid path for Opsgenie");
            return;
        }
        String apiKey = getApiKey();
        if (apiKey.isEmpty()) {
            LOG.warn("Opsgenie API key not configured");
            return;
        }
        HttpURLConnection conn = null;
        try {
            URL url = new URL(API_BASE + "/" + path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "GenieKey " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write("{}".getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                LOG.info("Opsgenie {} successful", path);
            } else {
                LOG.warn("Opsgenie {} failed with code {}", path, responseCode);
            }
        } catch (Exception e) {
            LOG.error("Failed to call Opsgenie {}", path, e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private String getApiKey() {
        return GBApplication.getPrefs().getString("opsgenie_api_key", "");
    }
}
