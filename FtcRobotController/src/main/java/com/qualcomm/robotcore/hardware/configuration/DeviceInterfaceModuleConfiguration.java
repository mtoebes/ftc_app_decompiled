package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.util.SerialNumber;

import java.util.List;

public class DeviceInterfaceModuleConfiguration extends ControllerConfiguration {
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
}
