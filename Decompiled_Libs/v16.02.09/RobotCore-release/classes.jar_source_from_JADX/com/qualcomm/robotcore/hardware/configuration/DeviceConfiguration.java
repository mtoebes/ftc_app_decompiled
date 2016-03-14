package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.BuildConfig;
import java.io.Serializable;

public class DeviceConfiguration implements Serializable {
    public static final String DISABLED_DEVICE_NAME = "NO DEVICE ATTACHED";
    private ConfigurationType f251a;
    private int f252b;
    private boolean f253c;
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
        this.f251a = ConfigurationType.NOTHING;
        this.f253c = false;
        this.f252b = port;
        this.f251a = type;
        this.name = name;
        this.f253c = enabled;
    }

    public DeviceConfiguration(int port) {
        this.f251a = ConfigurationType.NOTHING;
        this.f253c = false;
        this.name = DISABLED_DEVICE_NAME;
        this.f251a = ConfigurationType.NOTHING;
        this.f252b = port;
        this.f253c = false;
    }

    public DeviceConfiguration(ConfigurationType type) {
        this.f251a = ConfigurationType.NOTHING;
        this.f253c = false;
        this.name = BuildConfig.VERSION_NAME;
        this.f251a = type;
        this.f253c = false;
    }

    public DeviceConfiguration(int port, ConfigurationType type) {
        this.f251a = ConfigurationType.NOTHING;
        this.f253c = false;
        this.name = DISABLED_DEVICE_NAME;
        this.f251a = type;
        this.f252b = port;
        this.f253c = false;
    }

    public boolean isEnabled() {
        return this.f253c;
    }

    public void setEnabled(boolean enabled) {
        this.f253c = enabled;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setType(ConfigurationType type) {
        this.f251a = type;
    }

    public ConfigurationType getType() {
        return this.f251a;
    }

    public int getPort() {
        return this.f252b;
    }

    public void setPort(int port) {
        this.f252b = port;
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
