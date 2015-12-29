package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.util.SerialNumber;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MotorControllerConfiguration extends ControllerConfiguration implements Serializable {
    List<ConfigurationType> SUPPORTED_DEVICE_TYPES = Collections.singletonList(ConfigurationType.MOTOR);

    public MotorControllerConfiguration() {
        super("", new ArrayList<DeviceConfiguration>(), new SerialNumber(ControllerConfiguration.NO_SERIAL_NUMBER.getSerialNumber()), ConfigurationType.MOTOR_CONTROLLER);
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

    protected List<ConfigurationType> getSupportedDeviceTypes() {
        return SUPPORTED_DEVICE_TYPES;
    }
}
