package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import com.qualcomm.robotcore.util.SerialNumber;
import java.util.ArrayList;
import java.util.List;

public class MatrixControllerConfiguration extends ControllerConfiguration {
    private List<DeviceConfiguration> f267a;
    private List<DeviceConfiguration> f268b;

    public MatrixControllerConfiguration(String name, List<DeviceConfiguration> motors, List<DeviceConfiguration> servos, SerialNumber serialNumber) {
        super(name, serialNumber, ConfigurationType.MATRIX_CONTROLLER);
        this.f267a = servos;
        this.f268b = motors;
    }

    public List<DeviceConfiguration> getServos() {
        return this.f267a;
    }

    public void addServos(ArrayList<DeviceConfiguration> servos) {
        this.f267a = servos;
    }

    public List<DeviceConfiguration> getMotors() {
        return this.f268b;
    }

    public void addMotors(ArrayList<DeviceConfiguration> motors) {
        this.f268b = motors;
    }
}
