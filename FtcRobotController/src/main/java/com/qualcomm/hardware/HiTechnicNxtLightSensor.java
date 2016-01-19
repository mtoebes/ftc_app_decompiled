package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.TypeConversion;

class HiTechnicNxtLightSensor extends LightSensor {
    private static final int VERSION = 1;
    private static final byte LED_DIGITAL_LINE_NUMBER = (byte) 0;
    private static final double LIGHT_DETECTED_RAW_MAX = 127.0;
    private static final double LIGHT_DETECTED_RAW_MIN = -128.0;

    private final ModernRoboticsUsbLegacyModule legacyModule;
    private final int physicalPort;

    HiTechnicNxtLightSensor(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        legacyModule.enableAnalogReadMode(physicalPort);
        this.legacyModule = legacyModule;
        this.physicalPort = physicalPort;
    }

    public double getLightDetected() {
        double rawLightValue = this.legacyModule.readAnalog(this.physicalPort)[0];
        return Range.scale(rawLightValue, LIGHT_DETECTED_RAW_MIN, LIGHT_DETECTED_RAW_MAX, 0, 1);
    }

    public int getLightDetectedRaw() {
        return TypeConversion.unsignedByteToInt(this.legacyModule.readAnalog(this.physicalPort)[0]);
    }

    public void enableLed(boolean enable) {
        this.legacyModule.setDigitalLine(this.physicalPort, LED_DIGITAL_LINE_NUMBER, enable);
    }

    public String status() {
        return String.format("NXT Light Sensor, connected via device %s, port %d", this.legacyModule.getSerialNumber().toString(), this.physicalPort);
    }

    public String getDeviceName() {
        return "NXT Light Sensor";
    }

    public String getConnectionInfo() {
        return String.format("%s; port %d", this.legacyModule.getConnectionInfo(), this.physicalPort);
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }
}
