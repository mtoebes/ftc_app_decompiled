package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.util.SerialNumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DeviceInterfaceModuleConfiguration extends ControllerConfiguration {
    private static final List<ConfigurationType> DEVICES_TYPES = Arrays.asList(ConfigurationType.PULSE_WIDTH_DEVICE, ConfigurationType.ANALOG_OUTPUT, ConfigurationType.DIGITAL_DEVICE, ConfigurationType.ANALOG_INPUT, ConfigurationType.I2C_DEVICE);
    private static final List<ConfigurationType> PWM_DEVICE_TYPES = Collections.singletonList(ConfigurationType.PULSE_WIDTH_DEVICE);
    private static final List<ConfigurationType> ANALOG_OUTPUT_DEVICE_TYPES = Collections.singletonList(ConfigurationType.ANALOG_OUTPUT);
    private static final List<ConfigurationType> DIGITAL_DEVICE_TYPES = Arrays.asList(ConfigurationType.DIGITAL_DEVICE, ConfigurationType.TOUCH_SENSOR, ConfigurationType.LED);
    private static final List<ConfigurationType> ANALOG_INPUT_DEVICE_TYPES = Arrays.asList(ConfigurationType.ANALOG_INPUT, ConfigurationType.OPTICAL_DISTANCE_SENSOR);
    private static final List<ConfigurationType> I2C_DEVICE_TYPES = Arrays.asList(ConfigurationType.I2C_DEVICE, ConfigurationType.IR_SEEKER_V3, ConfigurationType.ADAFRUIT_COLOR_SENSOR, ConfigurationType.COLOR_SENSOR, ConfigurationType.GYRO);

    private List<DeviceConfiguration> pwnDevices;
    private List<DeviceConfiguration> i2cDevices;
    private List<DeviceConfiguration> analogInputDevices;
    private List<DeviceConfiguration> digitalDevices;
    private List<DeviceConfiguration> analogOutputDevices;

    public DeviceInterfaceModuleConfiguration(String name, SerialNumber serialNumber) {
        super(name, serialNumber, ConfigurationType.DEVICE_INTERFACE_MODULE);
    }

    public void setPwmDevices(List<DeviceConfiguration> pwmDevices) {
        this.pwnDevices = pwmDevices;
    }

    public List<DeviceConfiguration> getPwmDevices() {
        return this.pwnDevices;
    }

    public List<DeviceConfiguration> getI2cDevices() {
        return this.i2cDevices;
    }

    public void setI2cDevices(List<DeviceConfiguration> i2cDevices) {
        this.i2cDevices = i2cDevices;
    }

    public List<DeviceConfiguration> getAnalogInputDevices() {
        return this.analogInputDevices;
    }

    public void setAnalogInputDevices(List<DeviceConfiguration> analogInputDevices) {
        this.analogInputDevices = analogInputDevices;
    }

    public List<DeviceConfiguration> getDigitalDevices() {
        return this.digitalDevices;
    }

    public void setDigitalDevices(List<DeviceConfiguration> digitalDevices) {
        this.digitalDevices = digitalDevices;
    }

    public List<DeviceConfiguration> getAnalogOutputDevices() {
        return this.analogOutputDevices;
    }

    public void setAnalogOutputDevices(List<DeviceConfiguration> analogOutputDevices) {
        this.analogOutputDevices = analogOutputDevices;
    }

    @Override
    public void addDevices(ConfigurationType type, List<DeviceConfiguration> devices) {
        switch (type) {
            case PULSE_WIDTH_DEVICE:
                setPwmDevices(devices);
                break;
            case ANALOG_OUTPUT:
                setAnalogOutputDevices(devices);
                break;
            case ANALOG_INPUT:
                setAnalogInputDevices(devices);
                break;
            case I2C_DEVICE:
                setI2cDevices(devices);
                break;
            case DIGITAL_DEVICE:
                setDigitalDevices(devices);
                break;
        }
    }

    @Override
    public List<ConfigurationType> getDevicesTypes() {
        return DEVICES_TYPES;
    }

    public ConfigurationType getDevicesType(ConfigurationType type) {
        if (PWM_DEVICE_TYPES.contains(type)) {
            return ConfigurationType.PULSE_WIDTH_DEVICE;
        } else if (ANALOG_INPUT_DEVICE_TYPES.contains(type)) {
            return ConfigurationType.ANALOG_INPUT;
        } else if (ANALOG_OUTPUT_DEVICE_TYPES.contains(type)) {
            return ConfigurationType.ANALOG_OUTPUT;
        } else if (I2C_DEVICE_TYPES.contains(type)) {
            return ConfigurationType.I2C_DEVICE;
        } else if (DIGITAL_DEVICE_TYPES.contains(type)) {
            return ConfigurationType.DIGITAL_DEVICE;
        } else {
            return null;
        }
    }
}
