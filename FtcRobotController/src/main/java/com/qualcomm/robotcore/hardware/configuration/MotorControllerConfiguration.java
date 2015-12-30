package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.util.SerialNumber;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MotorControllerConfiguration extends ControllerConfiguration implements Serializable {
    private static final ConfigurationType DEVICES_TYPE = ConfigurationType.MOTOR;

    public MotorControllerConfiguration() {
        super("", new ArrayList<DeviceConfiguration>(), new SerialNumber(ControllerConfiguration.NO_SERIAL_NUMBER.getSerialNumber()), ConfigurationType.MOTOR_CONTROLLER);
    }

    public MotorControllerConfiguration(String name, SerialNumber serialNumber) {
        this(name, null, serialNumber);
    }

    public MotorControllerConfiguration(String name, List<DeviceConfiguration> motors, SerialNumber serialNumber) {
        super(name, motors, serialNumber, ConfigurationType.MOTOR_CONTROLLER);
    }

    public List<DeviceConfiguration> getMotors() {
        return super.getDevices();
    }

    public void addMotors(List<DeviceConfiguration> motors) {
        super.addDevices(motors);
    }

    @Override
    public void addDevices(ConfigurationType type, List<DeviceConfiguration> devices) {
        if (type == DEVICES_TYPE) {
            this.addDevices(devices);
        }
    }

    @Override
    public List<ConfigurationType> getDevicesTypes() {
        return Collections.singletonList(DEVICES_TYPE);
    }

    @Override
    public ConfigurationType getDevicesType(ConfigurationType type) {
        if (type == DEVICES_TYPE) {
            return type;
        } else {
            return null;
        }
    }
}
