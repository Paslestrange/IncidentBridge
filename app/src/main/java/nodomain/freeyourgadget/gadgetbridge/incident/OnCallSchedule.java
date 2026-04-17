package nodomain.freeyourgadget.gadgetbridge.incident;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;

public class OnCallSchedule {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final LocalTime DEFAULT_START = LocalTime.of(9, 0);
    private static final LocalTime DEFAULT_END = LocalTime.of(18, 0);

    public static boolean isEnabled() {
        return GBApplication.getPrefs().getBoolean(IncidentConstants.PREF_ONCALL_SCHEDULE_ENABLED, false);
    }

    public static boolean isOnCallHoursNow() {
        LocalTime now = LocalTime.now();
        LocalTime start = getStartTime();
        LocalTime end = getEndTime();

        if (start.isBefore(end)) {
            return !now.isBefore(start) && !now.isAfter(end);
        } else {
            return !now.isBefore(start) || !now.isAfter(end);
        }
    }

    public static boolean shouldForwardNotification(String severity) {
        if (!isEnabled()) {
            return true;
        }

        if (isOnCallHoursNow()) {
            return true;
        }

        return isCriticalSeverity(severity);
    }

    private static boolean isCriticalSeverity(String severity) {
        if (severity == null) {
            return false;
        }
        String upper = severity.toUpperCase(java.util.Locale.ROOT);
        return upper.equals(IncidentConstants.SEV_P1)
                || upper.equals(IncidentConstants.SEV_SEV1)
                || upper.equals(IncidentConstants.SEV_CRITICAL);
    }

    private static LocalTime getStartTime() {
        return parseTime(GBApplication.getPrefs().getString(IncidentConstants.PREF_ONCALL_START_TIME, "09:00"));
    }

    private static LocalTime getEndTime() {
        return parseTime(GBApplication.getPrefs().getString(IncidentConstants.PREF_ONCALL_END_TIME, "18:00"));
    }

    private static LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return DEFAULT_START;
        }
        try {
            return LocalTime.parse(timeStr, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return DEFAULT_START;
        }
    }
}
