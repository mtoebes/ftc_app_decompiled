package com.qualcomm.robotcore.hardware.configuration;

import android.content.res.Configuration;

import java.io.Serializable;

public class DeviceConfiguration implements Serializable {
    public static final String DISABLED_DEVICE_NAME = "NO DEVICE ATTACHED";
    private ConfigurationType type;
    private int port;
    private boolean enabled;
    protected String name;

    public enum ConfigurationType {
        MOTOR,
        SERVO,
        GYRO,
        COMPASS,
        IR_SEEKER,
        LIGHT_SENSOR,
        ACCELEROMETER,
        MOTOR_CONTROLLER,
        SERVO_CONTROLLER,
        LEGACY_MODULE_CONTROLLER,
        DEVICE_INTERFACE_MODULE,
        I2C_DEVICE,
        ANALOG_INPUT,
        TOUCH_SENSOR,
        OPTICAL_DISTANCE_SENSOR,
        ANALOG_OUTPUT,
        DIGITAL_DEVICE,
        PULSE_WIDTH_DEVICE,
        IR_SEEKER_V3,
        TOUCH_SENSOR_MULTIPLEXER,
        MATRIX_CONTROLLER,
        ULTRASONIC_SENSOR,
        ADAFRUIT_COLOR_SENSOR,
        COLOR_SENSOR,
        LED,
        OTHER,
        NOTHING
    }

    public DeviceConfiguration(int port, ConfigurationType type, String name, boolean enabled) {
        this.port = port;
        this.type = type;
        this.name = name;
        this.enabled = enabled;
    }

    public DeviceConfiguration(int port) {
        this.type = ConfigurationType.NOTHING;
        this.enabled = false;
        this.name = DISABLED_DEVICE_NAME;
        this.type = ConfigurationType.NOTHING;
        this.port = port;
        this.enabled = false;
    }

    public DeviceConfiguration(ConfigurationType type) {
        this.type = ConfigurationType.NOTHING;
        this.enabled = false;
        this.name = "";
        this.type = type;
        this.enabled = false;
    }

    public DeviceConfiguration(int port, ConfigurationType type) {
        this.type = ConfigurationType.NOTHING;
        this.enabled = false;
        this.name = DISABLED_DEVICE_NAME;
        this.type = type;
        this.port = port;
        this.enabled = false;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setType(ConfigurationType type) {
        this.type = type;
    }

    public ConfigurationType getType() {
        return this.type;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static ConfigurationType typeFromString(String type) {
        for (ConfigurationType configurationType : ConfigurationType.values()) {
            if (type.equalsIgnoreCase(configurationType.toString())) {
                return configurationType;
            }
        }
        return ConfigurationType.NOTHING;
    }

    public static boolean isDeviceConfiguration(ConfigurationType type) {
        return (type == ConfigurationType.COMPASS ||
                type == ConfigurationType.LIGHT_SENSOR ||
                type == ConfigurationType.IR_SEEKER ||
                type == ConfigurationType.ACCELEROMETER ||
                type == ConfigurationType.GYRO ||
                type == ConfigurationType.TOUCH_SENSOR ||
                type == ConfigurationType.TOUCH_SENSOR_MULTIPLEXER ||
                type == ConfigurationType.ULTRASONIC_SENSOR ||
                type == ConfigurationType.COLOR_SENSOR ||
                type == ConfigurationType.NOTHING);
    }

    public static boolean isControllerConfiguration(ConfigurationType type) {
        return (type == ConfigurationType.MOTOR_CONTROLLER ||
                type == ConfigurationType.SERVO_CONTROLLER ||
                type == ConfigurationType.MATRIX_CONTROLLER);
    }

    public static int getTotalPorts(ConfigurationType type, boolean isMatrix) {
        if(type == ConfigurationType.PULSE_WIDTH_DEVICE) {
            return DeviceInterfaceModuleConfiguration.PWD_TOTAL_PORTS;
        } else if(type == ConfigurationType.I2C_DEVICE) {
            return DeviceInterfaceModuleConfiguration.I2C_TOTAL_PORTS;
        } else if(type == ConfigurationType.ANALOG_INPUT) {
            return DeviceInterfaceModuleConfiguration.ANALOG_INPUT_TOTAL_PORTS;
        } else if(type == ConfigurationType.ANALOG_OUTPUT) {
            return DeviceInterfaceModuleConfiguration.ANALOG_OUTPUT_TOTAL_PORTS;
        } else if(type == ConfigurationType.DIGITAL_DEVICE) {
            return DeviceInterfaceModuleConfiguration.DIGITAL_TOTAL_PORTS;
        } else if(type == ConfigurationType.NOTHING) {
            return LegacyModuleControllerConfiguration.LEGACY_TOTAL_PORTS;
        } else if(type == ConfigurationType.MOTOR) {
            if (isMatrix) {
                return MatrixControllerConfiguration.MATRIX_MOTOR_TOTAL_PORTS;
            } else {
                return MotorControllerConfiguration.MOTOR_TOTAL_PORTS;
            }
        } else if(type == ConfigurationType.SERVO) {
            if (isMatrix) {
                return MatrixControllerConfiguration.MATRIX_SERVO_TOTAL_PORTS;
            } else {
                return ServoControllerConfiguration.SERVO_TOTAL_PORTS;
            }
        } else {
            return -1;
        }
    }
}
