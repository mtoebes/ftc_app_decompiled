package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.util.SerialNumber;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MotorControllerConfiguration extends ControllerConfiguration implements Serializable {
    public static final int MOTOR_TOTAL_PORTS = 2;

    public MotorControllerConfiguration() {
        super("", new ArrayList(), new SerialNumber(ControllerConfiguration.NO_SERIAL_NUMBER.getSerialNumber()), ConfigurationType.MOTOR_CONTROLLER);
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
