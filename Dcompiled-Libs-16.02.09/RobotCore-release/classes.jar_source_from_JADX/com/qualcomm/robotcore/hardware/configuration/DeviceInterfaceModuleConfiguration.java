package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import com.qualcomm.robotcore.util.SerialNumber;
import java.util.List;

public class DeviceInterfaceModuleConfiguration extends ControllerConfiguration {
    private List<DeviceConfiguration> f262a;
    private List<DeviceConfiguration> f263b;
    private List<DeviceConfiguration> f264c;
    private List<DeviceConfiguration> f265d;
    private List<DeviceConfiguration> f266e;

    public DeviceInterfaceModuleConfiguration(String name, SerialNumber serialNumber) {
        super(name, serialNumber, ConfigurationType.DEVICE_INTERFACE_MODULE);
    }

    public void setPwmDevices(List<DeviceConfiguration> pwmDevices) {
        this.f262a = pwmDevices;
    }

    public List<DeviceConfiguration> getPwmDevices() {
        return this.f262a;
    }

    public List<DeviceConfiguration> getI2cDevices() {
        return this.f263b;
    }

    public void setI2cDevices(List<DeviceConfiguration> i2cDevices) {
        this.f263b = i2cDevices;
    }

    public List<DeviceConfiguration> getAnalogInputDevices() {
        return this.f264c;
    }

    public void setAnalogInputDevices(List<DeviceConfiguration> analogInputDevices) {
        this.f264c = analogInputDevices;
    }

    public List<DeviceConfiguration> getDigitalDevices() {
        return this.f265d;
    }

    public void setDigitalDevices(List<DeviceConfiguration> digitalDevices) {
        this.f265d = digitalDevices;
    }

    public List<DeviceConfiguration> getAnalogOutputDevices() {
        return this.f266e;
    }

    public void setAnalogOutputDevices(List<DeviceConfiguration> analogOutputDevices) {
        this.f266e = analogOutputDevices;
    }
}
