package nodomain.freeyourgadget.gadgetbridge.incident;

import android.app.NotificationManager;
import android.content.Context;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;

public class DndIntegration {

    public static boolean isDndActive() {
        Context context = GBApplication.getContext();
        if (context == null) return false;

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return false;

        int filter = nm.getCurrentInterruptionFilter();
        return filter == NotificationManager.INTERRUPTION_FILTER_PRIORITY
                || filter == NotificationManager.INTERRUPTION_FILTER_NONE
                || filter == NotificationManager.INTERRUPTION_FILTER_ALARMS;
    }

    public static boolean shouldBypassDnd(String severity) {
        if (!isDndActive()) {
            return true;
        }

        if (!OnCallSchedule.isEnabled()) {
            return true;
        }

        if (OnCallSchedule.isOnCallHoursNow()) {
            return true;
        }

        return isCriticalSeverity(severity);
    }

    private static boolean isCriticalSeverity(String severity) {
        if (severity == null) return false;
        String upper = severity.toUpperCase(java.util.Locale.ROOT);
        return upper.equals(IncidentConstants.SEV_P1)
                || upper.equals(IncidentConstants.SEV_SEV1)
                || upper.equals(IncidentConstants.SEV_CRITICAL);
    }
}
