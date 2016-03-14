package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.TypeConversion;

public class HiTechnicNxtLightSensor extends LightSensor {
    public static final byte LED_DIGITAL_LINE_NUMBER = (byte) 0;
    private final ModernRoboticsUsbLegacyModule f65a;
    private final int f66b;

    HiTechnicNxtLightSensor(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        legacyModule.enableAnalogReadMode(physicalPort);
        this.f65a = legacyModule;
        this.f66b = physicalPort;
    }

    public double getLightDetected() {
        return Range.scale((double) this.f65a.readAnalog(this.f66b)[0], -128.0d, 127.0d, 0.0d, 1.0d);
    }

    public int getLightDetectedRaw() {
        return TypeConversion.unsignedByteToInt(this.f65a.readAnalog(this.f66b)[0]);
    }

    public void enableLed(boolean enable) {
        this.f65a.setDigitalLine(this.f66b, 0, enable);
    }

    public String status() {
        return String.format("NXT Light Sensor, connected via device %s, port %d", new Object[]{this.f65a.getSerialNumber().toString(), Integer.valueOf(this.f66b)});
    }

    public String getDeviceName() {
        return "NXT Light Sensor";
    }

    public String getConnectionInfo() {
        return this.f65a.getConnectionInfo() + "; port " + this.f66b;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
