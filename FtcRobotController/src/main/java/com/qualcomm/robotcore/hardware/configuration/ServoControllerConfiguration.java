package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.util.SerialNumber;
import java.util.ArrayList;
import java.util.List;

public class ServoControllerConfiguration extends ControllerConfiguration {
    public static final int SERVO_TOTAL_PORTS = 6;

    public ServoControllerConfiguration() {
        super("", new ArrayList(), new SerialNumber(ControllerConfiguration.NO_SERIAL_NUMBER.getSerialNumber()), ConfigurationType.SERVO_CONTROLLER);
    }

    public ServoControllerConfiguration(String name, List<DeviceConfiguration> servos, SerialNumber serialNumber) {
        super(name, servos, serialNumber, ConfigurationType.SERVO_CONTROLLER);
    }

    public List<DeviceConfiguration> getServos() {
        return super.getDevices();
    }

    public void addServos(ArrayList<DeviceConfiguration> servos) {
        super.addDevices(servos);
    }
}
