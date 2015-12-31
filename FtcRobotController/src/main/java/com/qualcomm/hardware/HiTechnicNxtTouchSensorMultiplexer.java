package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.TouchSensorMultiplexer;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteOrder;

public class HiTechnicNxtTouchSensorMultiplexer extends TouchSensorMultiplexer {
    public static final int INVALID = -1;
    public static final int[] MASK_MAP;
    public static final int MASK_TOUCH_SENSOR_1 = 1;
    public static final int MASK_TOUCH_SENSOR_2 = 2;
    public static final int MASK_TOUCH_SENSOR_3 = 4;
    public static final int MASK_TOUCH_SENSOR_4 = 8;
    int f75a;
    private final ModernRoboticsUsbLegacyModule f76b;
    private final int f77c;

    static {
        MASK_MAP = new int[]{INVALID, MASK_TOUCH_SENSOR_1, MASK_TOUCH_SENSOR_2, MASK_TOUCH_SENSOR_3, MASK_TOUCH_SENSOR_4};
    }

    public HiTechnicNxtTouchSensorMultiplexer(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        this.f75a = MASK_TOUCH_SENSOR_3;
        legacyModule.enableAnalogReadMode(physicalPort);
        this.f76b = legacyModule;
        this.f77c = physicalPort;
    }

    public String status() {
        Object[] objArr = new Object[MASK_TOUCH_SENSOR_2];
        objArr[0] = this.f76b.getSerialNumber().toString();
        objArr[MASK_TOUCH_SENSOR_1] = this.f77c;
        return String.format("NXT Touch Sensor Multiplexer, connected via device %s, port %d", objArr);
    }

    public String getDeviceName() {
        return "NXT Touch Sensor Multiplexer";
    }

    public String getConnectionInfo() {
        return this.f76b.getConnectionInfo() + "; port " + this.f77c;
    }

    public int getVersion() {
        return MASK_TOUCH_SENSOR_1;
    }

    public void close() {
    }

    public boolean isTouchSensorPressed(int channel) {
        m47a(channel);
        return (m46a() & MASK_MAP[channel]) > 0;
    }

    public int getSwitches() {
        return m46a();
    }

    private int m46a() {
        int byteArrayToShort = 1023 - TypeConversion.byteArrayToShort(this.f76b.readAnalog(3), ByteOrder.LITTLE_ENDIAN);
        return (((byteArrayToShort * 339) / (1023 - byteArrayToShort)) + 5) / 10;
    }

    private void m47a(int i) {
        if ((i <= 0) || (i > this.f75a)) {
            Object[] objArr = new Object[MASK_TOUCH_SENSOR_2];
            objArr[0] = i;
            objArr[MASK_TOUCH_SENSOR_1] = this.f75a;
            throw new IllegalArgumentException(String.format("Channel %d is invalid; valid channels are 1..%d", objArr));
        }
    }
}
