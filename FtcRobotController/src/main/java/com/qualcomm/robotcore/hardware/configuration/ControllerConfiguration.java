package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.hardware.DeviceManager.DeviceType;
import com.qualcomm.robotcore.util.SerialNumber;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ControllerConfiguration extends DeviceConfiguration implements Serializable {
    public static final SerialNumber NO_SERIAL_NUMBER;
    private List<DeviceConfiguration> devices;
    private SerialNumber serialNumber;

    static {
        NO_SERIAL_NUMBER = new SerialNumber("-1");
    }

    public ControllerConfiguration(String name, SerialNumber serialNumber, ConfigurationType type) {
        this(name, new ArrayList<DeviceConfiguration>(), serialNumber, type);
    }

    public ControllerConfiguration(String name, List<DeviceConfiguration> devices, SerialNumber serialNumber, ConfigurationType type) {
        super(type);
        super.setName(name);
        this.devices = devices;
        this.serialNumber = serialNumber;
    }

    public List<DeviceConfiguration> getDevices() {
        return this.devices;
    }

    public SerialNumber getSerialNumber() {
        return this.serialNumber;
    }

    public void addDevices(List<DeviceConfiguration> devices) {
        this.devices = devices;
    }

    public ConfigurationType deviceTypeToConfigType(DeviceType type) {
        if (type == DeviceType.MODERN_ROBOTICS_USB_DC_MOTOR_CONTROLLER) {
            return ConfigurationType.MOTOR_CONTROLLER;
        }
        if (type == DeviceType.MODERN_ROBOTICS_USB_SERVO_CONTROLLER) {
            return ConfigurationType.SERVO_CONTROLLER;
        }
        if (type == DeviceType.MODERN_ROBOTICS_USB_LEGACY_MODULE) {
            return ConfigurationType.LEGACY_MODULE_CONTROLLER;
        }
        return ConfigurationType.NOTHING;
    }

    public DeviceType configTypeToDeviceType(ConfigurationType type) {
        if (type == ConfigurationType.MOTOR_CONTROLLER) {
            return DeviceType.MODERN_ROBOTICS_USB_DC_MOTOR_CONTROLLER;
        }
        if (type == ConfigurationType.SERVO_CONTROLLER) {
            return DeviceType.MODERN_ROBOTICS_USB_SERVO_CONTROLLER;
        }
        if (type == ConfigurationType.LEGACY_MODULE_CONTROLLER) {
            return DeviceType.MODERN_ROBOTICS_USB_LEGACY_MODULE;
        }
        return DeviceType.FTDI_USB_UNKNOWN_DEVICE;
    }
}
