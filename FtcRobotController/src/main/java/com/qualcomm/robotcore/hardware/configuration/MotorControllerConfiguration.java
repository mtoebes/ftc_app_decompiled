package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.BuildConfig;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import com.qualcomm.robotcore.util.SerialNumber;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MotorControllerConfiguration extends ControllerConfiguration implements Serializable {
    public MotorControllerConfiguration() {
        super(BuildConfig.VERSION_NAME, new ArrayList(), new SerialNumber(ControllerConfiguration.NO_SERIAL_NUMBER.getSerialNumber()), ConfigurationType.MOTOR_CONTROLLER);
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
}
