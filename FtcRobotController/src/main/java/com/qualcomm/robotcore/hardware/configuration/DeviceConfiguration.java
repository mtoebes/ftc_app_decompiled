package com.qualcomm.robotcore.hardware.configuration;

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

    public ConfigurationType typeFromString(String type) {
        for (ConfigurationType configurationType : ConfigurationType.values()) {
            if (type.equalsIgnoreCase(configurationType.toString())) {
                return configurationType;
            }
        }
        return ConfigurationType.NOTHING;
    }
}
