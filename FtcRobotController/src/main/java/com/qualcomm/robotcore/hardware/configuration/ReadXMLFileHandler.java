package com.qualcomm.robotcore.hardware.configuration;

import android.content.Context;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import static com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants.*;

public class ReadXMLFileHandler {
    private static boolean DEBUG;

    List<ControllerConfiguration> controllerConfigurations;
    private XmlPullParser parser;

    public ReadXMLFileHandler(Context context) {
        this.controllerConfigurations = new ArrayList();
    }

    public List<ControllerConfiguration> getDeviceControllers() {
        return this.controllerConfigurations;
    }

    public List<ControllerConfiguration> parse(InputStream is) throws RobotCoreException {
        this.parser = null;
        try {
            XmlPullParserFactory newInstance = XmlPullParserFactory.newInstance();
            newInstance.setNamespaceAware(true);
            this.parser = newInstance.newPullParser();
            this.parser.setInput(is, null);
            int next = this.parser.getEventType();
            while (next != 1) {
                String type = getConfigurationType(this.parser.getName());
                if (next == 2) {
                    if (type.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString())) {
                        this.controllerConfigurations.add(parseMotorController(true));
                    }
                    if (type.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString())) {
                        this.controllerConfigurations.add(parseServoController(true));
                    }
                    if (type.equalsIgnoreCase(ConfigurationType.LEGACY_MODULE_CONTROLLER.toString())) {
                        this.controllerConfigurations.add(parseLegacyModuleController());
                    }
                    if (type.equalsIgnoreCase(ConfigurationType.DEVICE_INTERFACE_MODULE.toString())) {
                        this.controllerConfigurations.add(parseDeviceInterfaceModule());
                    }
                }
                next = this.parser.next();
            }
        } catch (XmlPullParserException e) {
            RobotLog.w("XmlPullParserException");
            e.printStackTrace();
        } catch (IOException e2) {
            RobotLog.w("IOException");
            e2.printStackTrace();
        }
        return this.controllerConfigurations;
    }

    private ControllerConfiguration parseDeviceInterfaceModule() throws IOException, XmlPullParserException, RobotCoreException {
        String name = this.parser.getAttributeValue(null, "name");
        String serialNumber = this.parser.getAttributeValue(null, "serialNumber");
        ArrayList<DeviceConfiguration> pwdConfigurations = createDeviceConfigurationList(PWD_PORTS, ConfigurationType.PULSE_WIDTH_DEVICE);
        ArrayList<DeviceConfiguration> i2cConfigurations = createDeviceConfigurationList(I2C_PORTS, ConfigurationType.I2C_DEVICE);
        ArrayList<DeviceConfiguration> analogInputConfigurations = createDeviceConfigurationList(ANALOG_INPUT_PORTS, ConfigurationType.ANALOG_INPUT);
        ArrayList<DeviceConfiguration> digitalDeviceConfigurations = createDeviceConfigurationList(DIGITAL_PORTS, ConfigurationType.DIGITAL_DEVICE);
        ArrayList<DeviceConfiguration> analogOutputConfigurations = createDeviceConfigurationList(ANALOG_OUTPUT_PORTS, ConfigurationType.ANALOG_OUTPUT);
        int next = this.parser.next();
        String type = getConfigurationType(this.parser.getName());
        while (next != 1) {
            if (next == 3) {
                if (type == null) {
                    continue;
                } else {
                    if (DEBUG) {
                        RobotLog.e("[handleDeviceInterfaceModule] tagname: " + type);
                    }
                    if (type.equalsIgnoreCase(ConfigurationType.DEVICE_INTERFACE_MODULE.toString())) {
                        DeviceInterfaceModuleConfiguration deviceInterfaceModuleConfiguration = new DeviceInterfaceModuleConfiguration(name, new SerialNumber(serialNumber));
                        deviceInterfaceModuleConfiguration.setPwmDevices(pwdConfigurations);
                        deviceInterfaceModuleConfiguration.setI2cDevices(i2cConfigurations);
                        deviceInterfaceModuleConfiguration.setAnalogInputDevices(analogInputConfigurations);
                        deviceInterfaceModuleConfiguration.setDigitalDevices(digitalDeviceConfigurations);
                        deviceInterfaceModuleConfiguration.setAnalogOutputDevices(analogOutputConfigurations);
                        deviceInterfaceModuleConfiguration.setEnabled(true);
                        return deviceInterfaceModuleConfiguration;
                    }
                }
            }
            if (next == 2) {
                DeviceConfiguration deviceConfiguration;
                if (type.equalsIgnoreCase(ConfigurationType.ANALOG_INPUT.toString()) || type.equalsIgnoreCase(ConfigurationType.OPTICAL_DISTANCE_SENSOR.toString())) {
                    deviceConfiguration = parseDeviceConfiguration();
                    analogInputConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                }
                if (type.equalsIgnoreCase(ConfigurationType.PULSE_WIDTH_DEVICE.toString())) {
                    deviceConfiguration = parseDeviceConfiguration();
                    pwdConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                }
                if (type.equalsIgnoreCase(ConfigurationType.I2C_DEVICE.toString()) || type.equalsIgnoreCase(ConfigurationType.IR_SEEKER_V3.toString()) || type.equalsIgnoreCase(ConfigurationType.ADAFRUIT_COLOR_SENSOR.toString()) || type.equalsIgnoreCase(ConfigurationType.COLOR_SENSOR.toString()) || type.equalsIgnoreCase(ConfigurationType.GYRO.toString())) {
                    deviceConfiguration = parseDeviceConfiguration();
                    i2cConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                }
                if (type.equalsIgnoreCase(ConfigurationType.ANALOG_OUTPUT.toString())) {
                    deviceConfiguration = parseDeviceConfiguration();
                    analogOutputConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                }
                if (type.equalsIgnoreCase(ConfigurationType.DIGITAL_DEVICE.toString()) || type.equalsIgnoreCase(ConfigurationType.TOUCH_SENSOR.toString()) || type.equalsIgnoreCase(ConfigurationType.LED.toString())) {
                    DeviceConfiguration c2 = parseDeviceConfiguration();
                    digitalDeviceConfigurations.set(c2.getPort(), c2);
                }
            }
            next = this.parser.next();
            type = getConfigurationType(this.parser.getName());
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }

    private ControllerConfiguration parseLegacyModuleController() throws IOException, XmlPullParserException, RobotCoreException {
        String name = this.parser.getAttributeValue(null, "name");
        String serialNumber = this.parser.getAttributeValue(null, "serialNumber");
        List deviceConfigurations = createDeviceConfigurationList(LEGACY_MODULE_PORTS, ConfigurationType.NOTHING);
        int next = this.parser.next();
        String type = getConfigurationType(this.parser.getName());
        ControllerConfiguration legacyModuleControllerConfiguration;
        while (next != 1) {
            if (next == 3) {
                if (type == null) {
                    continue;
                } else if (type.equalsIgnoreCase(ConfigurationType.LEGACY_MODULE_CONTROLLER.toString())) {
                    legacyModuleControllerConfiguration = new LegacyModuleControllerConfiguration(name, deviceConfigurations, new SerialNumber(serialNumber));
                    legacyModuleControllerConfiguration.setEnabled(true);
                    return legacyModuleControllerConfiguration;
                }
            }
            if (next == 2) {
                if (DEBUG) {
                    RobotLog.e("[handleLegacyModule] tagname: " + type);
                }
                DeviceConfiguration deviceConfiguration;
                if (type.equalsIgnoreCase(ConfigurationType.COMPASS.toString()) || type.equalsIgnoreCase(ConfigurationType.LIGHT_SENSOR.toString()) || type.equalsIgnoreCase(ConfigurationType.IR_SEEKER.toString()) || type.equalsIgnoreCase(ConfigurationType.ACCELEROMETER.toString()) || type.equalsIgnoreCase(ConfigurationType.GYRO.toString()) || type.equalsIgnoreCase(ConfigurationType.TOUCH_SENSOR.toString()) || type.equalsIgnoreCase(ConfigurationType.TOUCH_SENSOR_MULTIPLEXER.toString()) || type.equalsIgnoreCase(ConfigurationType.ULTRASONIC_SENSOR.toString()) || type.equalsIgnoreCase(ConfigurationType.COLOR_SENSOR.toString()) || type.equalsIgnoreCase(ConfigurationType.NOTHING.toString())) {
                    deviceConfiguration = parseDeviceConfiguration();
                    deviceConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                } else if (type.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString())) {
                    deviceConfiguration = parseMotorController(false);
                    deviceConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                } else if (type.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString())) {
                    deviceConfiguration = parseServoController(false);
                    deviceConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                } else if (type.equalsIgnoreCase(ConfigurationType.MATRIX_CONTROLLER.toString())) {
                    deviceConfiguration = parseMatrixController();
                    deviceConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                }
            }
            next = this.parser.next();
            type = getConfigurationType(this.parser.getName());
        }
        return new LegacyModuleControllerConfiguration(name, deviceConfigurations, new SerialNumber(serialNumber));
    }

    private DeviceConfiguration parseDeviceConfiguration() {
        String type = getConfigurationType(this.parser.getName());
        DeviceConfiguration deviceConfiguration = new DeviceConfiguration(Integer.parseInt(this.parser.getAttributeValue(null, "port")));
        deviceConfiguration.setType(deviceConfiguration.typeFromString(type));
        deviceConfiguration.setName(this.parser.getAttributeValue(null, "name"));
        if (!deviceConfiguration.getName().equalsIgnoreCase(DeviceConfiguration.DISABLED_DEVICE_NAME)) {
            deviceConfiguration.setEnabled(true);
        }
        if (DEBUG) {
            RobotLog.e("[handleDevice] name: " + deviceConfiguration.getName() + ", port: " + deviceConfiguration.getPort() + ", type: " + deviceConfiguration.getType());
        }
        return deviceConfiguration;
    }

    private ArrayList<DeviceConfiguration> createDeviceConfigurationList(int ports, ConfigurationType type) {
        ArrayList<DeviceConfiguration> deviceConfigurations = new ArrayList();
        for (int port = 0; port < ports; port++) {
            if (type == ConfigurationType.SERVO) {
                deviceConfigurations.add(new ServoConfiguration(port + 1, DeviceConfiguration.DISABLED_DEVICE_NAME, false));
            } else if (type == ConfigurationType.MOTOR) {
                deviceConfigurations.add(new MotorConfiguration(port + 1, DeviceConfiguration.DISABLED_DEVICE_NAME, false));
            } else {
                deviceConfigurations.add(new DeviceConfiguration(port, type, DeviceConfiguration.DISABLED_DEVICE_NAME, false));
            }
        }
        return deviceConfigurations;
    }

    private ControllerConfiguration parseMatrixController() throws IOException, XmlPullParserException, RobotCoreException {
        String name = this.parser.getAttributeValue(null, "name");
        String serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER.toString();
        int port = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
        ArrayList<DeviceConfiguration> servoConfigurations = createDeviceConfigurationList(MATRIX_SERVO_PORTS, ConfigurationType.SERVO);
        ArrayList<DeviceConfiguration> motorConfigurations = createDeviceConfigurationList(MATRIX_MOTOR_PORTS, ConfigurationType.MOTOR);
        int next = this.parser.next();
        String type = getConfigurationType(this.parser.getName());
        while (next != 1) {
            if (next == 3) {
                if (type == null) {
                    continue;
                } else if (type.equalsIgnoreCase(ConfigurationType.MATRIX_CONTROLLER.toString())) {
                    ControllerConfiguration matrixControllerConfiguration = new MatrixControllerConfiguration(name, motorConfigurations, servoConfigurations, new SerialNumber(serialNumber));
                    matrixControllerConfiguration.setPort(port);
                    matrixControllerConfiguration.setEnabled(true);
                    return matrixControllerConfiguration;
                }
            }
            if (next == 2) {
                int devicePort;
                if (type.equalsIgnoreCase(ConfigurationType.SERVO.toString())) {
                    devicePort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                    servoConfigurations.set(devicePort - 1, new ServoConfiguration(devicePort, this.parser.getAttributeValue(null, "name"), true));
                } else if (type.equalsIgnoreCase(ConfigurationType.MOTOR.toString())) {
                    devicePort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                    motorConfigurations.set(devicePort - 1, new MotorConfiguration(devicePort, this.parser.getAttributeValue(null, "name"), true));
                }
            }
            next = this.parser.next();
            type = getConfigurationType(this.parser.getName());
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }

    private ControllerConfiguration parseServoController(boolean useSerialNumber) throws IOException, XmlPullParserException {
        ControllerConfiguration servoControllerConfiguration;
        String name = this.parser.getAttributeValue(null, "name");
        int port = -1;
        String serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER.toString();
        if (useSerialNumber) {
            serialNumber = this.parser.getAttributeValue(null, "serialNumber");
        } else {
            port = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
        }
        List deviceConfigurations = createDeviceConfigurationList(SERVO_PORTS, ConfigurationType.SERVO);
        int next = this.parser.next();
        String type = getConfigurationType(this.parser.getName());
        while (next != 1) {
            if (next == 3) {
                if (type == null) {
                    continue;
                } else if (type.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString())) {
                    servoControllerConfiguration = new ServoControllerConfiguration(name, deviceConfigurations, new SerialNumber(serialNumber));
                    servoControllerConfiguration.setPort(port);
                    servoControllerConfiguration.setEnabled(true);
                    return servoControllerConfiguration;
                }
            }
            if (next == 2 && type.equalsIgnoreCase(ConfigurationType.SERVO.toString())) {
                int devicePort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                deviceConfigurations.set(devicePort - 1, new ServoConfiguration(devicePort, this.parser.getAttributeValue(null, "name"), true));
            }
            next = this.parser.next();
            type = getConfigurationType(this.parser.getName());
        }
        servoControllerConfiguration = new ServoControllerConfiguration(name, deviceConfigurations, new SerialNumber(serialNumber));
        servoControllerConfiguration.setPort(port);
        return servoControllerConfiguration;
    }

    private ControllerConfiguration parseMotorController(boolean useSerialNumber) throws IOException, XmlPullParserException {
        ControllerConfiguration motorControllerConfiguration;
        String name = this.parser.getAttributeValue(null, "name");
        int port = -1;
        String serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER.toString();
        if (useSerialNumber) {
            serialNumber = this.parser.getAttributeValue(null, "serialNumber");
        } else {
            port = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
        }
        List deviceConfigurations = createDeviceConfigurationList(MOTOR_PORTS, ConfigurationType.MOTOR);
        int next = this.parser.next();
        String type = getConfigurationType(this.parser.getName());
        while (next != 1) {
            if (next == 3) {
                if (type == null) {
                    continue;
                } else if (type.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString())) {
                    motorControllerConfiguration = new MotorControllerConfiguration(name, deviceConfigurations, new SerialNumber(serialNumber));
                    motorControllerConfiguration.setPort(port);
                    motorControllerConfiguration.setEnabled(true);
                    return motorControllerConfiguration;
                }
            }
            if (next == 2 && type.equalsIgnoreCase(ConfigurationType.MOTOR.toString())) {
                int devicePort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                deviceConfigurations.set(devicePort - 1, new MotorConfiguration(devicePort, this.parser.getAttributeValue(null, "name"), true));
            }
            next = this.parser.next();
            type = getConfigurationType(this.parser.getName());
        }
        motorControllerConfiguration = new MotorControllerConfiguration(name, deviceConfigurations, new SerialNumber(serialNumber));
        motorControllerConfiguration.setPort(port);
        return motorControllerConfiguration;
    }

    private String getConfigurationType(String str) {
        if (str == null) {
            return null;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.MOTOR_CONTROLLER)) {
            return ConfigurationType.MOTOR_CONTROLLER.toString();
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.SERVO_CONTROLLER)) {
            return ConfigurationType.SERVO_CONTROLLER.toString();
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.LEGACY_MODULE_CONTROLLER)) {
            return ConfigurationType.LEGACY_MODULE_CONTROLLER.toString();
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.DEVICE_INTERFACE_MODULE)) {
            return ConfigurationType.DEVICE_INTERFACE_MODULE.toString();
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.ANALOG_INPUT)) {
            return ConfigurationType.ANALOG_INPUT.toString();
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.OPTICAL_DISTANCE_SENSOR)) {
            return ConfigurationType.OPTICAL_DISTANCE_SENSOR.toString();
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.IR_SEEKER)) {
            return ConfigurationType.IR_SEEKER.toString();
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.LIGHT_SENSOR)) {
            return ConfigurationType.LIGHT_SENSOR.toString();
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.DIGITAL_DEVICE)) {
            return ConfigurationType.DIGITAL_DEVICE.toString();
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.TOUCH_SENSOR)) {
            return ConfigurationType.TOUCH_SENSOR.toString();
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.IR_SEEKER_V3)) {
            return ConfigurationType.IR_SEEKER_V3.toString();
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.PULSE_WIDTH_DEVICE)) {
            return ConfigurationType.PULSE_WIDTH_DEVICE.toString();
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.I2C_DEVICE)) {
            return ConfigurationType.I2C_DEVICE.toString();
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.ANALOG_OUTPUT)) {
            return ConfigurationType.ANALOG_OUTPUT.toString();
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.TOUCH_SENSOR_MULTIPLEXER)) {
            return ConfigurationType.TOUCH_SENSOR_MULTIPLEXER.toString();
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.MATRIX_CONTROLLER)) {
            return ConfigurationType.MATRIX_CONTROLLER.toString();
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.ULTRASONIC_SENSOR)) {
            return ConfigurationType.ULTRASONIC_SENSOR.toString();
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.ADAFRUIT_COLOR_SENSOR)) {
            return ConfigurationType.ADAFRUIT_COLOR_SENSOR.toString();
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.COLOR_SENSOR)) {
            return ConfigurationType.COLOR_SENSOR.toString();
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.LED)) {
            return ConfigurationType.LED.toString();
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.GYRO)) {
            return ConfigurationType.GYRO.toString();
        }
        return str;
    }
}
