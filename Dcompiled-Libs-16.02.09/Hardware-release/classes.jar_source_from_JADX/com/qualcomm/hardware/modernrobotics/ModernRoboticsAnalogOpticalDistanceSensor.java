package com.qualcomm.hardware.modernrobotics;

import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

public class ModernRoboticsAnalogOpticalDistanceSensor implements OpticalDistanceSensor {
    private final DeviceInterfaceModule f104a;
    private final int f105b;

    public ModernRoboticsAnalogOpticalDistanceSensor(DeviceInterfaceModule deviceInterfaceModule, int physicalPort) {
        this.f104a = deviceInterfaceModule;
        this.f105b = physicalPort;
    }

    public String toString() {
        return String.format("OpticalDistanceSensor: %1.2f", new Object[]{Double.valueOf(getLightDetected())});
    }

    public double getLightDetected() {
        return ((double) this.f104a.getAnalogInputValue(this.f105b)) / 1023.0d;
    }

    public int getLightDetectedRaw() {
        return this.f104a.getAnalogInputValue(this.f105b);
    }

    public void enableLed(boolean enable) {
    }

    public String status() {
        return String.format("Optical Distance Sensor, connected via device %s, port %d", new Object[]{this.f104a.getSerialNumber().toString(), Integer.valueOf(this.f105b)});
    }

    public String getDeviceName() {
        return "Modern Robotics Analog Optical Distance Sensor";
    }

    public String getConnectionInfo() {
        return this.f104a.getConnectionInfo() + "; analog port " + this.f105b;
    }

    public int getVersion() {
        return 0;
    }

    public void close() {
    }
}
