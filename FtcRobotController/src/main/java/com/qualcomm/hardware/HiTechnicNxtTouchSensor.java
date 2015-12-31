package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteOrder;

public class HiTechnicNxtTouchSensor extends TouchSensor {
    private final ModernRoboticsUsbLegacyModule f73a;
    private final int f74b;

    public HiTechnicNxtTouchSensor(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        legacyModule.enableAnalogReadMode(physicalPort);
        this.f73a = legacyModule;
        this.f74b = physicalPort;
    }

    public String status() {
        return String.format("NXT Touch Sensor, connected via device %s, port %d", this.f73a.getSerialNumber().toString(), this.f74b);
    }

    public double getValue() {
        return (((double) TypeConversion.byteArrayToShort(this.f73a.readAnalog(this.f74b), ByteOrder.LITTLE_ENDIAN)) > 675.0d) ? 0.0d : 1.0d;
    }

    public boolean isPressed() {
        return getValue() > 0.0d;
    }

    public String getDeviceName() {
        return "NXT Touch Sensor";
    }

    public String getConnectionInfo() {
        return this.f73a.getConnectionInfo() + "; port " + this.f74b;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
