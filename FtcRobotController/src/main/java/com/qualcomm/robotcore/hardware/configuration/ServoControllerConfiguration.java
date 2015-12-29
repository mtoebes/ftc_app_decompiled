package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.util.SerialNumber;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServoControllerConfiguration extends ControllerConfiguration {
    private static final List<ConfigurationType> SUPPORTED_DEVICE_TYPES = Collections.singletonList(ConfigurationType.SERVO);

    public ServoControllerConfiguration() {
        super("", new ArrayList<DeviceConfiguration>(), new SerialNumber(ControllerConfiguration.NO_SERIAL_NUMBER.getSerialNumber()), ConfigurationType.SERVO_CONTROLLER);
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

    protected static List<ConfigurationType> getSupportedDeviceTypes() {
        return SUPPORTED_DEVICE_TYPES;
    }
}
