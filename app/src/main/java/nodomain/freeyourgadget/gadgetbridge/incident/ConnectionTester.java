package nodomain.freeyourgadget.gadgetbridge.incident;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.util.GBPrefs;

public class ConnectionTester {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionTester.class);

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    public static class TestResult {
        public final boolean success;
        public final String message;
        public final long latencyMs;

        public TestResult(boolean success, String message, long latencyMs) {
            this.success = success;
            this.message = message;
            this.latencyMs = latencyMs;
        }
    }

    public static TestResult testPagerDuty() {
        GBPrefs prefs = GBApplication.getPrefs();
        String apiToken = prefs.getString(IncidentConstants.PREF_PAGERDUTY_API_TOKEN, "");
        String userEmail = prefs.getString(IncidentConstants.PREF_PAGERDUTY_USER_EMAIL, "");

        if (apiToken.isEmpty() || userEmail.isEmpty()) {
            return new TestResult(false, "PagerDuty API token or user email not configured", 0);
        }

        long startTime = System.currentTimeMillis();

        Request request = new Request.Builder()
                .url("https://api.pagerduty.com/users/me")
                .addHeader("Authorization", "Token token=" + apiToken)
                .addHeader("Accept", "application/vnd.pagerduty+json;version=2")
                .get()
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            long latency = System.currentTimeMillis() - startTime;
            if (response.isSuccessful()) {
                return new TestResult(true, "PagerDuty connection successful (" + latency + "ms)", latency);
            } else if (response.code() == 401 || response.code() == 403) {
                return new TestResult(false, "PagerDuty authentication failed (" + response.code() + ")", latency);
            } else {
                return new TestResult(false, "PagerDuty connection failed (" + response.code() + ")", latency);
            }
        } catch (SocketTimeoutException e) {
            long latency = System.currentTimeMillis() - startTime;
            LOG.warn("PagerDuty connection timed out", e);
            return new TestResult(false, "PagerDuty connection timed out", latency);
        } catch (IOException e) {
            long latency = System.currentTimeMillis() - startTime;
            LOG.error("PagerDuty connection error", e);
            return new TestResult(false, "PagerDuty connection error: " + e.getMessage(), latency);
        }
    }

    public static TestResult testOpsgenie() {
        GBPrefs prefs = GBApplication.getPrefs();
        String apiKey = prefs.getString(IncidentConstants.PREF_OPSGENIE_API_KEY, "");

        if (apiKey.isEmpty()) {
            return new TestResult(false, "Opsgenie API key not configured", 0);
        }

        long startTime = System.currentTimeMillis();

        Request request = new Request.Builder()
                .url("https://api.opsgenie.com/v2/users")
                .addHeader("Authorization", "GenieKey " + apiKey)
                .get()
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            long latency = System.currentTimeMillis() - startTime;
            if (response.isSuccessful()) {
                return new TestResult(true, "Opsgenie connection successful (" + latency + "ms)", latency);
            } else if (response.code() == 401 || response.code() == 403) {
                return new TestResult(false, "Opsgenie authentication failed (" + response.code() + ")", latency);
            } else {
                return new TestResult(false, "Opsgenie connection failed (" + response.code() + ")", latency);
            }
        } catch (SocketTimeoutException e) {
            long latency = System.currentTimeMillis() - startTime;
            LOG.warn("Opsgenie connection timed out", e);
            return new TestResult(false, "Opsgenie connection timed out", latency);
        } catch (IOException e) {
            long latency = System.currentTimeMillis() - startTime;
            LOG.error("Opsgenie connection error", e);
            return new TestResult(false, "Opsgenie connection error: " + e.getMessage(), latency);
        }
    }

    public static TestResult testRootly() {
        GBPrefs prefs = GBApplication.getPrefs();
        String apiToken = prefs.getString(IncidentConstants.PREF_ROOTLY_API_TOKEN, "");

        if (apiToken.isEmpty()) {
            return new TestResult(false, "Rootly API token not configured", 0);
        }

        long startTime = System.currentTimeMillis();

        Request request = new Request.Builder()
                .url("https://api.rootly.com/v1/incidents?limit=1")
                .addHeader("Authorization", "Bearer " + apiToken)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            long latency = System.currentTimeMillis() - startTime;
            if (response.isSuccessful()) {
                return new TestResult(true, "Rootly connection successful (" + latency + "ms)", latency);
            } else if (response.code() == 401 || response.code() == 403) {
                return new TestResult(false, "Rootly authentication failed (" + response.code() + ")", latency);
            } else {
                return new TestResult(false, "Rootly connection failed (" + response.code() + ")", latency);
            }
        } catch (SocketTimeoutException e) {
            long latency = System.currentTimeMillis() - startTime;
            LOG.warn("Rootly connection timed out", e);
            return new TestResult(false, "Rootly connection timed out", latency);
        } catch (IOException e) {
            long latency = System.currentTimeMillis() - startTime;
            LOG.error("Rootly connection error", e);
            return new TestResult(false, "Rootly connection error: " + e.getMessage(), latency);
        }
    }
}
