package nodomain.freeyourgadget.gadgetbridge.incident;

import android.os.Handler;
import android.os.Looper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationType;

public class WristFeedback {
    private static final Logger LOG = LoggerFactory.getLogger(WristFeedback.class);
    private static final long FEEDBACK_DISMISS_DELAY_MS = 5000;
    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    private WristFeedback() {
    }

    public static void sendFeedback(String message, boolean success) {
        NotificationSpec notificationSpec = new NotificationSpec();
        notificationSpec.title = success ? "IncidentBridge" : "IncidentBridge Failed";
        notificationSpec.body = message;
        notificationSpec.sourceAppId = "nodomain.freeyourgadget.gadgetbridge";
        notificationSpec.sourceName = "IncidentBridge";
        notificationSpec.type = NotificationType.GADGETBRIDGE_TEXT_RECEIVER;

        GBApplication.deviceService().onNotification(notificationSpec);
        LOG.info("Sent wrist feedback: {}", message);

        final int notificationId = notificationSpec.getId();
        executor.schedule(() -> {
            mainHandler.post(() -> {
                GBApplication.deviceService().onDeleteNotification(notificationId);
                LOG.debug("Auto-dismissed wrist feedback notification {}", notificationId);
            });
        }, FEEDBACK_DISMISS_DELAY_MS, TimeUnit.MILLISECONDS);
    }
}
