package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.util.SerialNumber;
import java.util.ArrayList;
import java.util.List;

public class MatrixControllerConfiguration extends ControllerConfiguration {
    private List<DeviceConfiguration> servos;
    private List<DeviceConfiguration> motors;

    public MatrixControllerConfiguration(String name, List<DeviceConfiguration> motors, List<DeviceConfiguration> servos, SerialNumber serialNumber) {
        super(name, serialNumber, ConfigurationType.MATRIX_CONTROLLER);
        this.servos = servos;
        this.motors = motors;
    }

    public List<DeviceConfiguration> getServos() {
        return servos;
    }

    public void addServos(ArrayList<DeviceConfiguration> servos) {
        this.servos = servos;
    }

    public List<DeviceConfiguration> getMotors() {
        return motors;
    }

    public void addMotors(ArrayList<DeviceConfiguration> motors) {
        this.motors = motors;
    }
}
