package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

public class ModernRoboticsAnalogOpticalDistanceSensor extends OpticalDistanceSensor {
    private final ModernRoboticsUsbDeviceInterfaceModule f119a;
    private final int f120b;

    public ModernRoboticsAnalogOpticalDistanceSensor(ModernRoboticsUsbDeviceInterfaceModule deviceInterfaceModule, int physicalPort) {
        this.f119a = deviceInterfaceModule;
        this.f120b = physicalPort;
    }

    public double getLightDetected() {
        return ((double) this.f119a.getAnalogInputValue(this.f120b)) / 1023.0d;
    }

    public int getLightDetectedRaw() {
        return this.f119a.getAnalogInputValue(this.f120b);
    }

    public void enableLed(boolean enable) {
    }

    public String status() {
        return String.format("Optical Distance Sensor, connected via device %s, port %d", new Object[]{this.f119a.getSerialNumber().toString(), Integer.valueOf(this.f120b)});
    }

    public String getDeviceName() {
        return "Modern Robotics Analog Optical Distance Sensor";
    }

    public String getConnectionInfo() {
        return this.f119a.getConnectionInfo() + "; analog port " + this.f120b;
    }

    public int getVersion() {
        return 0;
    }

    public void close() {
    }
}
