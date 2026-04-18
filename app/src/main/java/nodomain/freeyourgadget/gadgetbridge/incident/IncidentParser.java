package nodomain.freeyourgadget.gadgetbridge.incident;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;

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

    public static String parseSeverity(String title, String body, StatusBarNotification sbn) {
        String combined = (title + " " + body).toUpperCase();

        String customRegex = GBApplication.getPrefs().getString(IncidentConstants.PREF_CUSTOM_REGEX, "");
        if (!customRegex.isEmpty()) {
            try {
                Pattern customPattern = Pattern.compile(customRegex, Pattern.CASE_INSENSITIVE);
                Matcher customMatcher = customPattern.matcher(title + " " + body);
                if (customMatcher.find()) {
                    String match = customMatcher.group(1);
                    if (match != null) {
                        return normalizeSeverity(match);
                    }
                }
            } catch (Exception e) {
            }
        }

        if (combined.contains("P1") || combined.contains("SEV1") || combined.contains("CRITICAL")) {
            return IncidentConstants.SEV_P1;
        }
        if (combined.contains("P2") || combined.contains("SEV2") || combined.contains("HIGH")) {
            return IncidentConstants.SEV_P2;
        }
        if (combined.contains("P3") || combined.contains("SEV3") || combined.contains("MEDIUM")) {
            return IncidentConstants.SEV_P3;
        }
        if (combined.contains("P4") || combined.contains("SEV4") || combined.contains("P5") || combined.contains("LOW")) {
            return IncidentConstants.SEV_P4;
        }

        Matcher matcher = SEVERITY_PATTERN.matcher(combined);
        if (matcher.find()) {
            return normalizeSeverity(matcher.group(1));
        }

        if (sbn != null && sbn.getNotification() != null) {
            int priority = sbn.getNotification().priority;
            switch (priority) {
                case Notification.PRIORITY_HIGH:
                case Notification.PRIORITY_MAX:
                    return IncidentConstants.SEV_P2;
                case Notification.PRIORITY_LOW:
                case Notification.PRIORITY_MIN:
                    return IncidentConstants.SEV_P4;
                default:
                    return IncidentConstants.SEV_P3;
            }
        }

        String defaultSev = GBApplication.getPrefs().getString(IncidentConstants.PREF_DEFAULT_SEVERITY, "");
        if (!defaultSev.isEmpty()) {
            return normalizeSeverity(defaultSev);
        }

        return null;
    }

    private static String normalizeSeverity(String severity) {
        if (severity == null) return null;
        String upper = severity.toUpperCase(java.util.Locale.ROOT);
        switch (upper) {
            case "P1": case "SEV1": case "CRITICAL": return IncidentConstants.SEV_P1;
            case "P2": case "SEV2": case "HIGH": return IncidentConstants.SEV_P2;
            case "P3": case "SEV3": case "MEDIUM": return IncidentConstants.SEV_P3;
            case "P4": case "SEV4": case "P5": case "LOW": return IncidentConstants.SEV_P4;
            default: return upper;
        }
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
