package nodomain.freeyourgadget.gadgetbridge.incident;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationType;

public class WristFeedback {
    private static final Logger LOG = LoggerFactory.getLogger(WristFeedback.class);
    private static final long FEEDBACK_DISMISS_DELAY_MS = 5000;

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

        // Auto-dismiss after a short delay
        new Thread(() -> {
            try {
                Thread.sleep(FEEDBACK_DISMISS_DELAY_MS);
                GBApplication.deviceService().onDeleteNotification(notificationSpec.getId());
                LOG.debug("Auto-dismissed wrist feedback notification {}", notificationSpec.getId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
