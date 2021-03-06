package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;

public class MotorConfiguration extends DeviceConfiguration {
    public MotorConfiguration(int port, String name, boolean enabled) {
        super(port, ConfigurationType.MOTOR, name, enabled);
    }

    public MotorConfiguration(int port) {
        super(ConfigurationType.MOTOR);
        super.setName(DeviceConfiguration.DISABLED_DEVICE_NAME);
        super.setPort(port);
    }

    public MotorConfiguration(String name) {
        super(ConfigurationType.MOTOR);
        super.setName(name);
        super.setType(ConfigurationType.MOTOR);
    }
}
