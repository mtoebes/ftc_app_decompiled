package com.qualcomm.robotcore.hardware.configuration;

import java.io.Serializable;

public class DeviceConfiguration implements Serializable {
    public static final String DISABLED_DEVICE_NAME = "NO DEVICE ATTACHED";
    private ConfigurationType f257a;
    private int f258b;
    private boolean f259c;
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
        this.f257a = ConfigurationType.NOTHING;
        this.f259c = false;
        this.f258b = port;
        this.f257a = type;
        this.name = name;
        this.f259c = enabled;
    }

    public DeviceConfiguration(int port) {
        this.f257a = ConfigurationType.NOTHING;
        this.f259c = false;
        this.name = DISABLED_DEVICE_NAME;
        this.f257a = ConfigurationType.NOTHING;
        this.f258b = port;
        this.f259c = false;
    }

    public DeviceConfiguration(ConfigurationType type) {
        this.f257a = ConfigurationType.NOTHING;
        this.f259c = false;
        this.name = "";
        this.f257a = type;
        this.f259c = false;
    }

    public DeviceConfiguration(int port, ConfigurationType type) {
        this.f257a = ConfigurationType.NOTHING;
        this.f259c = false;
        this.name = DISABLED_DEVICE_NAME;
        this.f257a = type;
        this.f258b = port;
        this.f259c = false;
    }

    public boolean isEnabled() {
        return this.f259c;
    }

    public void setEnabled(boolean enabled) {
        this.f259c = enabled;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setType(ConfigurationType type) {
        this.f257a = type;
    }

    public ConfigurationType getType() {
        return this.f257a;
    }

    public int getPort() {
        return this.f258b;
    }

    public void setPort(int port) {
        this.f258b = port;
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
