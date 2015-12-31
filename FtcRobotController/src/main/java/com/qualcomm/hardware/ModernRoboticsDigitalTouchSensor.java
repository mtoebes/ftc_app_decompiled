package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class ModernRoboticsDigitalTouchSensor extends TouchSensor {
    private DeviceInterfaceModule f121a;
    private int f122b;

    public ModernRoboticsDigitalTouchSensor(DeviceInterfaceModule module, int physicalPort) {
        this.f121a = null;
        this.f122b = -1;
        this.f121a = module;
        this.f122b = physicalPort;
    }

    public double getValue() {
        return isPressed() ? 1.0d : 0.0d;
    }

    public boolean isPressed() {
        return this.f121a.getDigitalChannelState(this.f122b);
    }

    public String getDeviceName() {
        return "Modern Robotics Digital Touch Sensor";
    }

    public String getConnectionInfo() {
        return this.f121a.getConnectionInfo() + "; digital port " + this.f122b;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
