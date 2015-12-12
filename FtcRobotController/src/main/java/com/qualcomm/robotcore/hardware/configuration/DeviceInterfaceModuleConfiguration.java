package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import com.qualcomm.robotcore.util.SerialNumber;
import java.util.List;

public class DeviceInterfaceModuleConfiguration extends ControllerConfiguration {
    private List<DeviceConfiguration> f268a;
    private List<DeviceConfiguration> f269b;
    private List<DeviceConfiguration> f270c;
    private List<DeviceConfiguration> f271d;
    private List<DeviceConfiguration> f272e;

    public DeviceInterfaceModuleConfiguration(String name, SerialNumber serialNumber) {
        super(name, serialNumber, ConfigurationType.DEVICE_INTERFACE_MODULE);
    }

    public void setPwmDevices(List<DeviceConfiguration> pwmDevices) {
        this.f268a = pwmDevices;
    }

    public List<DeviceConfiguration> getPwmDevices() {
        return this.f268a;
    }

    public List<DeviceConfiguration> getI2cDevices() {
        return this.f269b;
    }

    public void setI2cDevices(List<DeviceConfiguration> i2cDevices) {
        this.f269b = i2cDevices;
    }

    public List<DeviceConfiguration> getAnalogInputDevices() {
        return this.f270c;
    }

    public void setAnalogInputDevices(List<DeviceConfiguration> analogInputDevices) {
        this.f270c = analogInputDevices;
    }

    public List<DeviceConfiguration> getDigitalDevices() {
        return this.f271d;
    }

    public void setDigitalDevices(List<DeviceConfiguration> digitalDevices) {
        this.f271d = digitalDevices;
    }

    public List<DeviceConfiguration> getAnalogOutputDevices() {
        return this.f272e;
    }

    public void setAnalogOutputDevices(List<DeviceConfiguration> analogOutputDevices) {
        this.f272e = analogOutputDevices;
    }
}
