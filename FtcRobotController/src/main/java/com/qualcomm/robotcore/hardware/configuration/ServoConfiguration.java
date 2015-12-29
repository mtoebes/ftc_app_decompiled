package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;

public class ServoConfiguration extends DeviceConfiguration {
    public ServoConfiguration(int port, String name, boolean enabled) {
        super(port, ConfigurationType.SERVO, name, enabled);
    }

    public ServoConfiguration(int port) {
        super(port, ConfigurationType.SERVO);
    }

    public ServoConfiguration(String name) {
        super(ConfigurationType.SERVO);
        super.setName(name);
    }
}
