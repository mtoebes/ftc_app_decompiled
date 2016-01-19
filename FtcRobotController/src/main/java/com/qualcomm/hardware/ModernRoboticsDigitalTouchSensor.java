package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.TouchSensor;

class ModernRoboticsDigitalTouchSensor extends TouchSensor {
    private static final int VERSION = 1;
    private final DeviceInterfaceModule deviceInterfaceModule;
    private final int physicalPort;

    public ModernRoboticsDigitalTouchSensor(DeviceInterfaceModule module, int physicalPort) {
        this.deviceInterfaceModule = module;
        this.physicalPort = physicalPort;
    }

    public double getValue() {
        return isPressed() ? 1.0d : 0.0d;
    }

    public boolean isPressed() {
        return this.deviceInterfaceModule.getDigitalChannelState(this.physicalPort);
    }

    public String getDeviceName() {
        return "Modern Robotics Digital Touch Sensor";
    }

    public String getConnectionInfo() {
        return this.deviceInterfaceModule.getConnectionInfo() + "; digital port: " + this.physicalPort;
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }
}
