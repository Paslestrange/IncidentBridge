package nodomain.freeyourgadget.gadgetbridge.service.devices.xiaomi;

import java.util.ArrayList;
import java.util.List;

import nodomain.freeyourgadget.gadgetbridge.proto.xiaomi.XiaomiProto;

public final class XiaomiVibrationPatternConverter {
    private XiaomiVibrationPatternConverter() {}
    
    public static List<XiaomiProto.Vibration> convertPattern(int[] pattern) {
        List<XiaomiProto.Vibration> vibrations = new ArrayList<>();
        for (int i = 0; i < pattern.length; i++) {
            vibrations.add(XiaomiProto.Vibration.newBuilder()
                    .setVibrate(i % 2 == 0 ? 1 : 0)
                    .setMs(pattern[i])
                    .build());
        }
        return vibrations;
    }
}
