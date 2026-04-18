package nodomain.freeyourgadget.gadgetbridge.incident;

import android.os.Handler;
import android.os.Looper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import nodomain.freeyourgadget.gadgetbridge.service.devices.xiaomi.XiaomiSupport;

public class RepeatingVibrationManager {
    private static final Logger LOG = LoggerFactory.getLogger(RepeatingVibrationManager.class);
    private static final Map<String, Timer> activeTimers = new HashMap<>();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static void startRepeatingVibration(String notificationKey, int[] pattern, int intervalMs, XiaomiSupport support) {
        if (notificationKey == null || pattern == null || pattern.length == 0 || intervalMs <= 0) {
            return;
        }

        stopRepeatingVibration(notificationKey);

        Timer timer = new Timer("Vibration-" + notificationKey);
        activeTimers.put(notificationKey, timer);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mainHandler.post(() -> {
                    if (activeTimers.containsKey(notificationKey)) {
                        support.triggerIncidentVibration(pattern);
                        LOG.debug("Repeated vibration for {}", notificationKey);
                    }
                });
            }
        }, intervalMs, intervalMs);

        LOG.info("Started repeating vibration for {} every {}ms", notificationKey, intervalMs);
    }

    public static void stopRepeatingVibration(String notificationKey) {
        if (notificationKey == null) return;
        Timer timer = activeTimers.remove(notificationKey);
        if (timer != null) {
            timer.cancel();
            timer.purge();
            LOG.info("Stopped repeating vibration for {}", notificationKey);
        }
    }

    public static void stopAll() {
        for (Timer timer : activeTimers.values()) {
            timer.cancel();
        }
        activeTimers.clear();
    }

    public static boolean isActive(String notificationKey) {
        return notificationKey != null && activeTimers.containsKey(notificationKey);
    }
}
