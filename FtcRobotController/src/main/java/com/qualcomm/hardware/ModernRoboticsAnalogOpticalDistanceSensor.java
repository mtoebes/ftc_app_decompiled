package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

class ModernRoboticsAnalogOpticalDistanceSensor extends OpticalDistanceSensor {
    private final static double LIGHT_VALUE_MAX = 1023.0d;
    private final static int VERSION = 0;
    private final ModernRoboticsUsbDeviceInterfaceModule deviceInterfaceModule;
    private final int physicalPort;

    public ModernRoboticsAnalogOpticalDistanceSensor(ModernRoboticsUsbDeviceInterfaceModule deviceInterfaceModule, int physicalPort) {
        this.deviceInterfaceModule = deviceInterfaceModule;
        this.physicalPort = physicalPort;
    }

    public double getLightDetected() {
        return getLightDetectedRaw()/ LIGHT_VALUE_MAX;
    }

    public int getLightDetectedRaw() {
        return this.deviceInterfaceModule.getAnalogInputValue(this.physicalPort);
    }

    public void enableLed(boolean enable) {
    }

    public String status() {
        return String.format("Optical Distance Sensor, connected via device %s, port %d", this.deviceInterfaceModule.getSerialNumber().toString(), this.physicalPort);
    }

    public String getDeviceName() {
        return "Modern Robotics Analog Optical Distance Sensor";
    }

    public String getConnectionInfo() {
        return String.format("%s; analog port %d", this.deviceInterfaceModule.getConnectionInfo(), this.physicalPort);
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }
}
