package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import com.qualcomm.robotcore.util.SerialNumber;
import java.util.ArrayList;
import java.util.List;

public class MatrixControllerConfiguration extends ControllerConfiguration {
    private List<DeviceConfiguration> f273a;
    private List<DeviceConfiguration> f274b;

    public MatrixControllerConfiguration(String name, List<DeviceConfiguration> motors, List<DeviceConfiguration> servos, SerialNumber serialNumber) {
        super(name, serialNumber, ConfigurationType.MATRIX_CONTROLLER);
        this.f273a = servos;
        this.f274b = motors;
    }

    public List<DeviceConfiguration> getServos() {
        return this.f273a;
    }

    public void addServos(ArrayList<DeviceConfiguration> servos) {
        this.f273a = servos;
    }

    public List<DeviceConfiguration> getMotors() {
        return this.f274b;
    }

    public void addMotors(ArrayList<DeviceConfiguration> motors) {
        this.f274b = motors;
    }
}
