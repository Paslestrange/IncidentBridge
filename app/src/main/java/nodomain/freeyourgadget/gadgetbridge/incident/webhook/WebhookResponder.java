package nodomain.freeyourgadget.gadgetbridge.incident.webhook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.incident.IncidentConstants;
import nodomain.freeyourgadget.gadgetbridge.incident.responder.IncidentResponder;
import nodomain.freeyourgadget.gadgetbridge.incident.ResponderResult;

public class WebhookResponder implements IncidentResponder {
    private static final Logger LOG = LoggerFactory.getLogger(WebhookResponder.class);

    @Override
    public ResponderResult acknowledge(String incidentId) {
        return sendWebhook(incidentId, "acknowledge");
    }

    @Override
    public ResponderResult escalate(String incidentId) {
        return sendWebhook(incidentId, "escalate");
    }

    @Override
    public ResponderResult resolve(String incidentId) {
        return sendWebhook(incidentId, "resolve");
    }

    private ResponderResult sendWebhook(String incidentId, String action) {
        String urlStr = GBApplication.getPrefs().getString("webhook_url", "");
        if (urlStr.isEmpty()) {
            return ResponderResult.NO_CREDENTIALS;
        }

        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            String payload = "{\"incident_id\":\"" + incidentId + "\",\"action\":\"" + action + "\"}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                LOG.info("Webhook {} successful for incident {}", action, incidentId);
                return ResponderResult.SUCCESS;
            } else {
                LOG.warn("Webhook {} failed with code {}", action, responseCode);
                return ResponderResult.FAILED;
            }
        } catch (Exception e) {
            LOG.error("Webhook failed for incident {}", incidentId, e);
            return ResponderResult.FAILED;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
