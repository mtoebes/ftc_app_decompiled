package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.util.SerialNumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MatrixControllerConfiguration extends ControllerConfiguration {
    private static final List<ConfigurationType> DEVICES_TYPES = Arrays.asList(ConfigurationType.MOTOR, ConfigurationType.SERVO);

    private List<DeviceConfiguration> servos;
    private List<DeviceConfiguration> motors;

    public MatrixControllerConfiguration(String name, SerialNumber serialNumber) {
        this(name, null, null, serialNumber);
    }

    public MatrixControllerConfiguration(String name, List<DeviceConfiguration> motors, List<DeviceConfiguration> servos, SerialNumber serialNumber) {
        super(name, serialNumber, ConfigurationType.MATRIX_CONTROLLER);
        this.servos = servos;
        this.motors = motors;
    }

    public List<DeviceConfiguration> getServos() {
        return this.servos;
    }

    public void addServos(List<DeviceConfiguration> servos) {
        this.servos = servos;
    }

    public List<DeviceConfiguration> getMotors() {
        return this.motors;
    }

    public void addMotors(List<DeviceConfiguration> motors) {
        this.motors = motors;
    }

    @Override
    public void addDevices(ConfigurationType type, List<DeviceConfiguration> devices) {
        switch (type) {
            case SERVO:
                addServos(devices);
                break;
            case MOTOR:
                addMotors(devices);
                break;
        }
    }

    @Override
    public List<ConfigurationType> getDevicesTypes() {
        return DEVICES_TYPES;
    }

    @Override
    public ConfigurationType getDevicesType(ConfigurationType type) {
        if (DEVICES_TYPES.contains(type)) {
            return type;
        } else {
            return null;
        }
    }
}
