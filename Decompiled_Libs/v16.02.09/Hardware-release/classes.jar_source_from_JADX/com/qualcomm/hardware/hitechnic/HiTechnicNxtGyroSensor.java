package com.qualcomm.hardware.hitechnic;

import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.LegacyModulePortDeviceImpl;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteOrder;

public class HiTechnicNxtGyroSensor extends LegacyModulePortDeviceImpl implements GyroSensor {
    public HiTechnicNxtGyroSensor(LegacyModule legacyModule, int physicalPort) {
        super(legacyModule, physicalPort);
        finishConstruction();
    }

    protected void moduleNowArmedOrPretending() {
        this.module.enableAnalogReadMode(this.physicalPort);
    }

    public String toString() {
        return String.format("Gyro: %3.1f", new Object[]{Double.valueOf(getRotation())});
    }

    public void calibrate() {
    }

    public boolean isCalibrating() {
        return false;
    }

    public int getHeading() {
        notSupported();
        return 0;
    }

    public double getRotation() {
        return (double) TypeConversion.byteArrayToShort(this.module.readAnalog(this.physicalPort), ByteOrder.LITTLE_ENDIAN);
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
    }

    public String status() {
        return String.format("NXT Gyro Sensor, connected via device %s, port %d", new Object[]{this.module.getSerialNumber().toString(), Integer.valueOf(this.physicalPort)});
    }

    public String getDeviceName() {
        return "NXT Gyro Sensor";
    }

    public String getConnectionInfo() {
        return this.module.getConnectionInfo() + "; port " + this.physicalPort;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }

    protected void notSupported() {
        throw new UnsupportedOperationException("This method is not supported for " + getDeviceName());
    }
}
