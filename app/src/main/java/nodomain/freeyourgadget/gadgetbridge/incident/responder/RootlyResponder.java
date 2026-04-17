package nodomain.freeyourgadget.gadgetbridge.incident.responder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.incident.IncidentConstants;
import nodomain.freeyourgadget.gadgetbridge.incident.ResponderResult;

public class RootlyResponder implements IncidentResponder {
    private static final Logger LOG = LoggerFactory.getLogger(RootlyResponder.class);
    private static final String API_BASE = IncidentConstants.URL_ROOTLY_API;

    @Override
    public ResponderResult acknowledge(String incidentId) {
        return sendPatch(incidentId, "{\"data\":{\"attributes\":{\"status\":\"mitigated\"}}}");
    }

    @Override
    public ResponderResult escalate(String incidentId) {
        LOG.info("Escalate not yet implemented for Rootly incident {}", incidentId);
        return ResponderResult.FAILED;
    }

    @Override
    public ResponderResult resolve(String incidentId) {
        return sendPatch(incidentId, "{\"data\":{\"attributes\":{\"status\":\"resolved\"}}}");
    }

    private ResponderResult sendPatch(String incidentId, String payload) {
        String token = getApiToken();
        if (token.isEmpty()) {
            LOG.warn("Rootly API token not configured");
            return ResponderResult.NO_CREDENTIALS;
        }
        HttpURLConnection conn = null;
        try {
            URL url = new URL(API_BASE + "/" + incidentId);
            conn = (HttpURLConnection) url.openConnection();
            // HttpURLConnection doesn't support PATCH natively, use POST with override header
            conn.setRequestMethod("POST");
            conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            conn.setRequestProperty("Authorization", "Bearer " + token);
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
                return ResponderResult.SUCCESS;
            } else {
                LOG.warn("Rootly incident {} update failed with code {}", incidentId, responseCode);
                return ResponderResult.FAILED;
            }
        } catch (SocketTimeoutException e) {
            LOG.error("Timeout while updating Rootly incident {}", incidentId, e);
            return ResponderResult.TIMEOUT;
        } catch (Exception e) {
            LOG.error("Failed to update Rootly incident {}", incidentId, e);
            return ResponderResult.FAILED;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private String getApiToken() {
        return GBApplication.getPrefs().getString(IncidentConstants.PREF_ROOTLY_API_TOKEN, "");
    }
}
