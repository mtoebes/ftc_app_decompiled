package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteOrder;

public class HiTechnicNxtGyroSensor extends GyroSensor {
    private final ModernRoboticsUsbLegacyModule f54a;
    private final int f55b;

    HiTechnicNxtGyroSensor(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        legacyModule.enableAnalogReadMode(physicalPort);
        this.f54a = legacyModule;
        this.f55b = physicalPort;
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
        return (double) TypeConversion.byteArrayToShort(this.f54a.readAnalog(this.f55b), ByteOrder.LITTLE_ENDIAN);
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
        return String.format("NXT Gyro Sensor, connected via device %s, port %d", new Object[]{this.f54a.getSerialNumber().toString(), Integer.valueOf(this.f55b)});
    }

    public String getDeviceName() {
        return "NXT Gyro Sensor";
    }

    public String getConnectionInfo() {
        return this.f54a.getConnectionInfo() + "; port " + this.f55b;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
