package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteOrder;

public class HiTechnicNxtGyroSensor extends GyroSensor {
    private static final int VERSION = 1;

    private final ModernRoboticsUsbLegacyModule legacyModule;
    private final int physicalPort;

    HiTechnicNxtGyroSensor(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        legacyModule.enableAnalogReadMode(physicalPort);
        this.legacyModule = legacyModule;
        this.physicalPort = physicalPort;
    }

    public void calibrate() {
        notSupported();
    }

    public boolean isCalibrating() {
        notSupported();
        return false;
    }

    public int getHeading() {
        notSupported();
        return 0;
    }

    public double getRotation() {
        return (double) TypeConversion.byteArrayToShort(this.legacyModule.readAnalog(this.physicalPort), ByteOrder.LITTLE_ENDIAN);
    }

    public int rawX() {
        notSupported();
        return 0;
    }

    public int rawY() {
        notSupported();
        return 0;
    }

    public int rawZ() {
        notSupported();
        return 0;
    }

    public void resetZAxisIntegrator() {
        notSupported();
    }

    public String status() {
        return String.format("NXT Gyro Sensor, connected via device %s, port %d", this.legacyModule.getSerialNumber().toString(), this.physicalPort);
    }

    public String getDeviceName() {
        return "NXT Gyro Sensor";
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
