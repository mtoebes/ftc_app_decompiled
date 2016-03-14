package com.qualcomm.hardware.hitechnic;

import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.LegacyModulePortDeviceImpl;
import com.qualcomm.robotcore.hardware.TouchSensorMultiplexer;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteOrder;

public class HiTechnicNxtTouchSensorMultiplexer extends LegacyModulePortDeviceImpl implements TouchSensorMultiplexer {
    public static final int INVALID = -1;
    public static final int[] MASK_MAP;
    public static final int MASK_TOUCH_SENSOR_1 = 1;
    public static final int MASK_TOUCH_SENSOR_2 = 2;
    public static final int MASK_TOUCH_SENSOR_3 = 4;
    public static final int MASK_TOUCH_SENSOR_4 = 8;
    int f64a;

    static {
        MASK_MAP = new int[]{INVALID, MASK_TOUCH_SENSOR_1, MASK_TOUCH_SENSOR_2, MASK_TOUCH_SENSOR_3, MASK_TOUCH_SENSOR_4};
    }

    public HiTechnicNxtTouchSensorMultiplexer(LegacyModule legacyModule, int physicalPort) {
        super(legacyModule, physicalPort);
        this.f64a = MASK_TOUCH_SENSOR_3;
        finishConstruction();
    }

    protected void moduleNowArmedOrPretending() {
        this.module.enableAnalogReadMode(this.physicalPort);
    }

    public String status() {
        Object[] objArr = new Object[MASK_TOUCH_SENSOR_2];
        objArr[0] = this.module.getSerialNumber().toString();
        objArr[MASK_TOUCH_SENSOR_1] = Integer.valueOf(this.physicalPort);
        return String.format("NXT Touch Sensor Multiplexer, connected via device %s, port %d", objArr);
    }

    public String getDeviceName() {
        return "NXT Touch Sensor Multiplexer";
    }

    public String getConnectionInfo() {
        return this.module.getConnectionInfo() + "; port " + this.physicalPort;
    }

    public int getVersion() {
        return MASK_TOUCH_SENSOR_1;
    }

    public void close() {
    }

    public boolean isTouchSensorPressed(int channel) {
        m49a(channel);
        return (m48a() & MASK_MAP[channel]) > 0;
    }

    public int getSwitches() {
        return m48a();
    }

    private int m48a() {
        int byteArrayToShort = 1023 - TypeConversion.byteArrayToShort(this.module.readAnalog(3), ByteOrder.LITTLE_ENDIAN);
        return (((byteArrayToShort * 339) / (1023 - byteArrayToShort)) + 5) / 10;
    }

    private void m49a(int i) {
        if (i <= 0 || i > this.f64a) {
            Object[] objArr = new Object[MASK_TOUCH_SENSOR_2];
            objArr[0] = Integer.valueOf(i);
            objArr[MASK_TOUCH_SENSOR_1] = Integer.valueOf(this.f64a);
            throw new IllegalArgumentException(String.format("Channel %d is invalid; valid channels are 1..%d", objArr));
        }
    }
}
