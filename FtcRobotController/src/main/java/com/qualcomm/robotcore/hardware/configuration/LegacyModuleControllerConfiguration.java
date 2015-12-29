package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import com.qualcomm.robotcore.util.SerialNumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LegacyModuleControllerConfiguration extends ControllerConfiguration {
    private static final List<ConfigurationType> CONTROLLER_TYPES = Arrays.asList(ConfigurationType.MOTOR_CONTROLLER, ConfigurationType.SERVO_CONTROLLER, ConfigurationType.MATRIX_CONTROLLER);
    private static final List<ConfigurationType> SENSOR_TYPES = Arrays.asList(ConfigurationType.COMPASS, ConfigurationType.LIGHT_SENSOR, ConfigurationType.IR_SEEKER, ConfigurationType.ACCELEROMETER, ConfigurationType.GYRO, ConfigurationType.TOUCH_SENSOR, ConfigurationType.TOUCH_SENSOR_MULTIPLEXER, ConfigurationType.ULTRASONIC_SENSOR, ConfigurationType.COLOR_SENSOR, ConfigurationType.NOTHING);
    private static final List<ConfigurationType> SUPPORTED_DEVICE_TYPES = new ArrayList<ConfigurationType>();

    static {
        SUPPORTED_DEVICE_TYPES.addAll(CONTROLLER_TYPES);
        SUPPORTED_DEVICE_TYPES.addAll(SENSOR_TYPES);
    }

    public LegacyModuleControllerConfiguration(String name, List<DeviceConfiguration> modules, SerialNumber serialNumber) {
        super(name, modules, serialNumber, ConfigurationType.LEGACY_MODULE_CONTROLLER);
    }

    protected static List<ConfigurationType> getSupportedDeviceTypes() {
        return SUPPORTED_DEVICE_TYPES;
    }
}
