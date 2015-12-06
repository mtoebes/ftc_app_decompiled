package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;

public class XMLConfigurationConstants {
    public static final String ADAFRUIT_COLOR_SENSOR = "AdafruitColorSensor";
    public static final String ANALOG_INPUT = "AnalogInput";
    public static final String ANALOG_OUTPUT = "AnalogOutput";
    public static final String COLOR_SENSOR = "ColorSensor";
    public static final String DEVICE_INTERFACE_MODULE = "DeviceInterfaceModule";
    public static final String DIGITAL_DEVICE = "DigitalDevice";
    public static final String GYRO = "Gyro";
    public static final String I2C_DEVICE = "I2cDevice";
    public static final String IR_SEEKER = "IrSeeker";
    public static final String IR_SEEKER_V3 = "IrSeekerV3";
    public static final String LED = "Led";
    public static final String LEGACY_MODULE_CONTROLLER = "LegacyModuleController";
    public static final String LIGHT_SENSOR = "LightSensor";
    public static final String MATRIX_CONTROLLER = "MatrixController";
    public static final String MOTOR_CONTROLLER = "MotorController";
    public static final String OPTICAL_DISTANCE_SENSOR = "OpticalDistanceSensor";
    public static final String PULSE_WIDTH_DEVICE = "PulseWidthDevice";
    public static final String SERVO_CONTROLLER = "ServoController";
    public static final String TOUCH_SENSOR = "TouchSensor";
    public static final String TOUCH_SENSOR_MULTIPLEXER = "TouchSensorMultiplexer";
    public static final String ULTRASONIC_SENSOR = "UltrasonicSensor";

    public static ConfigurationType getConfigurationType(String str) {
        if (str == null) {
            return null;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.MOTOR_CONTROLLER)) {
            return DeviceConfiguration.ConfigurationType.MOTOR_CONTROLLER;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.SERVO_CONTROLLER)) {
            return DeviceConfiguration.ConfigurationType.SERVO_CONTROLLER;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.LEGACY_MODULE_CONTROLLER)) {
            return DeviceConfiguration.ConfigurationType.LEGACY_MODULE_CONTROLLER;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.DEVICE_INTERFACE_MODULE)) {
            return DeviceConfiguration.ConfigurationType.DEVICE_INTERFACE_MODULE;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.ANALOG_INPUT)) {
            return DeviceConfiguration.ConfigurationType.ANALOG_INPUT;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.OPTICAL_DISTANCE_SENSOR)) {
            return DeviceConfiguration.ConfigurationType.OPTICAL_DISTANCE_SENSOR;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.IR_SEEKER)) {
            return DeviceConfiguration.ConfigurationType.IR_SEEKER;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.LIGHT_SENSOR)) {
            return DeviceConfiguration.ConfigurationType.LIGHT_SENSOR;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.DIGITAL_DEVICE)) {
            return DeviceConfiguration.ConfigurationType.DIGITAL_DEVICE;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.TOUCH_SENSOR)) {
            return DeviceConfiguration.ConfigurationType.TOUCH_SENSOR;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.IR_SEEKER_V3)) {
            return DeviceConfiguration.ConfigurationType.IR_SEEKER_V3;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.PULSE_WIDTH_DEVICE)) {
            return DeviceConfiguration.ConfigurationType.PULSE_WIDTH_DEVICE;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.I2C_DEVICE)) {
            return DeviceConfiguration.ConfigurationType.I2C_DEVICE;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.ANALOG_OUTPUT)) {
            return DeviceConfiguration.ConfigurationType.ANALOG_OUTPUT;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.TOUCH_SENSOR_MULTIPLEXER)) {
            return DeviceConfiguration.ConfigurationType.TOUCH_SENSOR_MULTIPLEXER;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.MATRIX_CONTROLLER)) {
            return DeviceConfiguration.ConfigurationType.MATRIX_CONTROLLER;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.ULTRASONIC_SENSOR)) {
            return DeviceConfiguration.ConfigurationType.ULTRASONIC_SENSOR;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.ADAFRUIT_COLOR_SENSOR)) {
            return DeviceConfiguration.ConfigurationType.ADAFRUIT_COLOR_SENSOR;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.COLOR_SENSOR)) {
            return DeviceConfiguration.ConfigurationType.COLOR_SENSOR;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.LED)) {
            return DeviceConfiguration.ConfigurationType.LED;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.GYRO)) {
            return DeviceConfiguration.ConfigurationType.GYRO;
        }
        return null;
    }
}
