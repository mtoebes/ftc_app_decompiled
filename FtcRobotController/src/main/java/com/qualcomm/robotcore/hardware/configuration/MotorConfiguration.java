package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;

public class MotorConfiguration extends DeviceConfiguration {
    public MotorConfiguration(int port, String name, boolean enabled) {
        super(port, ConfigurationType.MOTOR, name, enabled);
    }

    public MotorConfiguration(int port) {
        super(port, ConfigurationType.MOTOR);
    }

    public MotorConfiguration(String name) {
        super(ConfigurationType.MOTOR);
        super.setName(name);
    }
}
