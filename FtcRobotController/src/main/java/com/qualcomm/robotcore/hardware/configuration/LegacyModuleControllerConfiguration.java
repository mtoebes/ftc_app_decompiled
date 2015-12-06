package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import com.qualcomm.robotcore.util.SerialNumber;
import java.util.List;

public class LegacyModuleControllerConfiguration extends ControllerConfiguration {
    public static final int LEGACY_TOTAL_PORTS = 6;

    public LegacyModuleControllerConfiguration(String name, List<DeviceConfiguration> modules, SerialNumber serialNumber) {
        super(name, modules, serialNumber, ConfigurationType.LEGACY_MODULE_CONTROLLER);
    }
}
