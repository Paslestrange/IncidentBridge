package nodomain.freeyourgadget.gadgetbridge.incident;

import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class IncidentParser {
    private static final Pattern SEVERITY_PATTERN = Pattern.compile(
            "(P[0-9]|SEV[0-9]|CRITICAL|HIGH|MEDIUM|LOW)",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern INCIDENT_ID_PATTERN = Pattern.compile(
            "#([A-Za-z0-9-]+)"
    );

    private IncidentParser() {
    }

    public static String parseSeverity(String title, String body) {
        String combined = (title + " " + body).toUpperCase();

        if (combined.contains("P1") || combined.contains("SEV1") || combined.contains("CRITICAL")) {
            return "P1";
        }
        if (combined.contains("P2") || combined.contains("SEV2") || combined.contains("HIGH")) {
            return "P2";
        }
        if (combined.contains("P3") || combined.contains("SEV3") || combined.contains("MEDIUM")) {
            return "P3";
        }
        if (combined.contains("P4") || combined.contains("SEV4") || combined.contains("P5") || combined.contains("LOW")) {
            return "P4";
        }

        Matcher matcher = SEVERITY_PATTERN.matcher(combined);
        if (matcher.find()) {
            return matcher.group(1).toUpperCase();
        }

        return null;
    }

    public static String parseIncidentId(StatusBarNotification sbn) {
        if (sbn == null || sbn.getNotification() == null) {
            return null;
        }
        Bundle extras = sbn.getNotification().extras;
        String incidentId = extras.getString("incident_id");
        if (incidentId != null && !incidentId.isEmpty()) {
            return incidentId;
        }

        String title = extras.getString("android.title", "");
        String text = extras.getString("android.text", "");
        String combined = title + " " + text;

        Matcher matcher = INCIDENT_ID_PATTERN.matcher(combined);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}
