package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.util.SerialNumber;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServoControllerConfiguration extends ControllerConfiguration {
    private static final ConfigurationType DEVICES_TYPE = ConfigurationType.SERVO;

    public ServoControllerConfiguration() {
        super("", new ArrayList<DeviceConfiguration>(), new SerialNumber(ControllerConfiguration.NO_SERIAL_NUMBER.getSerialNumber()), ConfigurationType.SERVO_CONTROLLER);
    }

    public ServoControllerConfiguration(String name, SerialNumber serialNumber) {
        this(name, null, serialNumber);
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
