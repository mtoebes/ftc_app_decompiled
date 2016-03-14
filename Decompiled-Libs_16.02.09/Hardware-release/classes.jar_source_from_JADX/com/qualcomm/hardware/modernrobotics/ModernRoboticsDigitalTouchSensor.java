package com.qualcomm.hardware.modernrobotics;

import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class ModernRoboticsDigitalTouchSensor implements TouchSensor {
    private DigitalChannelController f106a;
    private int f107b;

    public ModernRoboticsDigitalTouchSensor(DigitalChannelController module, int physicalPort) {
        this.f106a = null;
        this.f107b = -1;
        this.f106a = module;
        this.f107b = physicalPort;
    }

    public String toString() {
        return String.format("Touch Sensor: %1.2f", new Object[]{Double.valueOf(getValue())});
    }

    public double getValue() {
        return isPressed() ? 1.0d : 0.0d;
    }

    public boolean isPressed() {
        return this.f106a.getDigitalChannelState(this.f107b);
    }

    public String getDeviceName() {
        return "Modern Robotics Digital Touch Sensor";
    }

    public String getConnectionInfo() {
        return this.f106a.getConnectionInfo() + "; digital port " + this.f107b;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
