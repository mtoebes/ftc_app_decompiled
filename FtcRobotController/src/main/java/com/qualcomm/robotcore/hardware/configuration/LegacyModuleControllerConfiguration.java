package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import com.qualcomm.robotcore.util.SerialNumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LegacyModuleControllerConfiguration extends ControllerConfiguration {
    private static final ConfigurationType DEVICES_TYPE = ConfigurationType.NOTHING;
    private static final List<ConfigurationType> CONTROLLER_TYPES = Arrays.asList(ConfigurationType.MOTOR_CONTROLLER, ConfigurationType.SERVO_CONTROLLER, ConfigurationType.MATRIX_CONTROLLER);
    private static final List<ConfigurationType> SENSOR_TYPES = Arrays.asList(ConfigurationType.COMPASS, ConfigurationType.LIGHT_SENSOR, ConfigurationType.IR_SEEKER, ConfigurationType.ACCELEROMETER, ConfigurationType.GYRO, ConfigurationType.TOUCH_SENSOR, ConfigurationType.TOUCH_SENSOR_MULTIPLEXER, ConfigurationType.ULTRASONIC_SENSOR, ConfigurationType.COLOR_SENSOR, ConfigurationType.NOTHING);

    public LegacyModuleControllerConfiguration(String name, SerialNumber serialNumber) {
        this(name, null, serialNumber);
    }

    public LegacyModuleControllerConfiguration(String name, List<DeviceConfiguration> modules, SerialNumber serialNumber) {
        super(name, modules, serialNumber, ConfigurationType.LEGACY_MODULE_CONTROLLER);
    }


    @Override
    public void addDevices(ConfigurationType type, List<DeviceConfiguration> devices) {
        if (type == DEVICES_TYPE) {
            this.addDevices(devices);
        }
    }

    @Override
    public List<ConfigurationType> getDevicesTypes() {
        return Collections.singletonList(DEVICES_TYPE);
    }

    @Override
    public ConfigurationType getDevicesType(ConfigurationType type) {
        if (CONTROLLER_TYPES.contains(type) || SENSOR_TYPES.contains(type)) {
            return DEVICES_TYPE;
        } else {
            return null;
        }
    }
}
