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
            while (next != XmlPullParser.END_DOCUMENT) {
                ConfigurationType type = getConfigurationType(this.parser.getName());
                if (next == XmlPullParser.START_TAG) {
                    if (type == ConfigurationType.MOTOR_CONTROLLER) {
                        this.controllerConfigurations.add(parseMotorController(true));
                    }
                    if (type == ConfigurationType.SERVO_CONTROLLER) {
                        this.controllerConfigurations.add(parseServoController(true));
                    }
                    if (type == ConfigurationType.LEGACY_MODULE_CONTROLLER) {
                        this.controllerConfigurations.add(parseLegacyModuleController());
                    }
                    if (type == ConfigurationType.DEVICE_INTERFACE_MODULE) {
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

    private ControllerConfiguration parseDeviceInterfaceModule()
            throws IOException, XmlPullParserException, RobotCoreException {
        String name = this.parser.getAttributeValue(null, "name");
        SerialNumber serialNumber = new SerialNumber(this.parser.getAttributeValue(null, "serialNumber"));
        ArrayList<DeviceConfiguration> pwdConfigurations =
                createDeviceConfigurationList(PWD_PORTS, ConfigurationType.PULSE_WIDTH_DEVICE);
        ArrayList<DeviceConfiguration> i2cConfigurations =
                createDeviceConfigurationList(I2C_PORTS, ConfigurationType.I2C_DEVICE);
        ArrayList<DeviceConfiguration> analogInputConfigurations =
                createDeviceConfigurationList(ANALOG_INPUT_PORTS, ConfigurationType.ANALOG_INPUT);
        ArrayList<DeviceConfiguration> digitalDeviceConfigurations =
                createDeviceConfigurationList(DIGITAL_PORTS, ConfigurationType.DIGITAL_DEVICE);
        ArrayList<DeviceConfiguration> analogOutputConfigurations =
                createDeviceConfigurationList(ANALOG_OUTPUT_PORTS, ConfigurationType.ANALOG_OUTPUT);
        int next = this.parser.next();
        ConfigurationType type = getConfigurationType(this.parser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG && type != null) {
                if (DEBUG) {
                    RobotLog.e("[handleDeviceInterfaceModule] tagname: " + type);
                }
                if (type == ConfigurationType.DEVICE_INTERFACE_MODULE) {
                    DeviceInterfaceModuleConfiguration deviceInterfaceModuleConfiguration =
                            new DeviceInterfaceModuleConfiguration(name, serialNumber);
                    deviceInterfaceModuleConfiguration.setPwmDevices(pwdConfigurations);
                    deviceInterfaceModuleConfiguration.setI2cDevices(i2cConfigurations);
                    deviceInterfaceModuleConfiguration.setAnalogInputDevices(analogInputConfigurations);
                    deviceInterfaceModuleConfiguration.setDigitalDevices(digitalDeviceConfigurations);
                    deviceInterfaceModuleConfiguration.setAnalogOutputDevices(analogOutputConfigurations);
                    deviceInterfaceModuleConfiguration.setEnabled(true);
                    return deviceInterfaceModuleConfiguration;
                }
            }
            if (next == XmlPullParser.START_TAG) {
                DeviceConfiguration deviceConfiguration;
                if (type == ConfigurationType.ANALOG_INPUT ||
                        type == ConfigurationType.OPTICAL_DISTANCE_SENSOR) {
                    deviceConfiguration = parseDeviceConfiguration();
                    analogInputConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                }
                if (type == ConfigurationType.PULSE_WIDTH_DEVICE) {
                    deviceConfiguration = parseDeviceConfiguration();
                    pwdConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                }
                if (type == ConfigurationType.I2C_DEVICE ||
                        type == ConfigurationType.IR_SEEKER_V3 ||
                        type == ConfigurationType.ADAFRUIT_COLOR_SENSOR ||
                        type == ConfigurationType.COLOR_SENSOR ||
                        type == ConfigurationType.GYRO) {
                    deviceConfiguration = parseDeviceConfiguration();
                    i2cConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                }
                if (type == ConfigurationType.ANALOG_OUTPUT) {
                    deviceConfiguration = parseDeviceConfiguration();
                    analogOutputConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                }
                if (type == ConfigurationType.DIGITAL_DEVICE ||
                        type == ConfigurationType.TOUCH_SENSOR ||
                        type == ConfigurationType.LED) {
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

    private ControllerConfiguration parseLegacyModuleController()
            throws IOException, XmlPullParserException, RobotCoreException {
        String name = this.parser.getAttributeValue(null, "name");
        SerialNumber serialNumber = new SerialNumber(this.parser.getAttributeValue(null, "serialNumber"));
        List deviceConfigurations =
                createDeviceConfigurationList(LEGACY_MODULE_PORTS, ConfigurationType.NOTHING);
        int next = this.parser.next();
        ConfigurationType type = getConfigurationType(this.parser.getName());
        ControllerConfiguration legacyModuleControllerConfiguration;
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG && type != null) {
                if (type == ConfigurationType.LEGACY_MODULE_CONTROLLER) {
                    legacyModuleControllerConfiguration =
                            new LegacyModuleControllerConfiguration(name, deviceConfigurations, serialNumber);
                    legacyModuleControllerConfiguration.setEnabled(true);
                    return legacyModuleControllerConfiguration;
                }
            }
            if (next == XmlPullParser.START_TAG) {
                if (DEBUG) {
                    RobotLog.e("[handleLegacyModule] tagname: " + type);
                }
                DeviceConfiguration deviceConfiguration;
                if (type == ConfigurationType.COMPASS ||
                        type == ConfigurationType.LIGHT_SENSOR ||
                        type == ConfigurationType.IR_SEEKER ||
                        type == ConfigurationType.ACCELEROMETER ||
                        type == ConfigurationType.GYRO ||
                        type == ConfigurationType.TOUCH_SENSOR ||
                        type == ConfigurationType.TOUCH_SENSOR_MULTIPLEXER ||
                        type == ConfigurationType.ULTRASONIC_SENSOR ||
                        type == ConfigurationType.COLOR_SENSOR ||
                        type == ConfigurationType.NOTHING) {
                    deviceConfiguration = parseDeviceConfiguration();
                    deviceConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                } else if (type == ConfigurationType.MOTOR_CONTROLLER) {
                    deviceConfiguration = parseMotorController(false);
                    deviceConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                } else if (type == ConfigurationType.SERVO_CONTROLLER) {
                    deviceConfiguration = parseServoController(false);
                    deviceConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                } else if (type == ConfigurationType.MATRIX_CONTROLLER) {
                    deviceConfiguration = parseMatrixController();
                    deviceConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                }
            }
            next = this.parser.next();
            type = getConfigurationType(this.parser.getName());
        }
        return new LegacyModuleControllerConfiguration(name, deviceConfigurations, serialNumber);
    }

    private DeviceConfiguration parseDeviceConfiguration() {
        ConfigurationType type = getConfigurationType(this.parser.getName());
        String name = this.parser.getAttributeValue(null, "name");
        int port = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
        boolean enabled = !(name.equalsIgnoreCase(DeviceConfiguration.DISABLED_DEVICE_NAME));
        DeviceConfiguration deviceConfiguration = new DeviceConfiguration(port, type, name, enabled);

        if (DEBUG) {
            RobotLog.e("[handleDevice] name: " + name + ", port: " + port + ", type: " + type);
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
        SerialNumber serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER;
        int port = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
        ArrayList<DeviceConfiguration> servoConfigurations =
                createDeviceConfigurationList(MATRIX_SERVO_PORTS, ConfigurationType.SERVO);
        ArrayList<DeviceConfiguration> motorConfigurations =
                createDeviceConfigurationList(MATRIX_MOTOR_PORTS, ConfigurationType.MOTOR);
        int next = this.parser.next();
        ConfigurationType type = getConfigurationType(this.parser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG && type != null) {
                if (type == ConfigurationType.MATRIX_CONTROLLER) {
                    ControllerConfiguration matrixControllerConfiguration =
                            new MatrixControllerConfiguration(name, motorConfigurations, servoConfigurations, serialNumber);
                    matrixControllerConfiguration.setPort(port);
                    matrixControllerConfiguration.setEnabled(true);
                    return matrixControllerConfiguration;
                }
            }
            if (next == XmlPullParser.START_TAG) {
                int devicePort;
                if (type == ConfigurationType.SERVO) {
                    devicePort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                    servoConfigurations.set(devicePort - 1, new ServoConfiguration(devicePort, this.parser.getAttributeValue(null, "name"), true));
                } else if (type == ConfigurationType.MOTOR) {
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

    private ControllerConfiguration parseServoController(boolean useSerialNumber)
            throws IOException, XmlPullParserException {
        ControllerConfiguration servoControllerConfiguration;
        String name = this.parser.getAttributeValue(null, "name");
        int port = -1;
        SerialNumber serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER;
        if (useSerialNumber) {
            serialNumber = new SerialNumber(this.parser.getAttributeValue(null, "serialNumber"));
        } else {
            port = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
        }
        List deviceConfigurations = createDeviceConfigurationList(SERVO_PORTS, ConfigurationType.SERVO);
        int next = this.parser.next();
        ConfigurationType type = getConfigurationType(this.parser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG && type != null) {
                if (type == ConfigurationType.SERVO_CONTROLLER) {
                    servoControllerConfiguration =
                            new ServoControllerConfiguration(name, deviceConfigurations, serialNumber);
                    servoControllerConfiguration.setPort(port);
                    servoControllerConfiguration.setEnabled(true);
                    return servoControllerConfiguration;
                }
            }
            if (next == XmlPullParser.START_TAG && type == ConfigurationType.SERVO) {
                int devicePort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                deviceConfigurations.set(devicePort - 1, new ServoConfiguration(devicePort, this.parser.getAttributeValue(null, "name"), true));
            }
            next = this.parser.next();
            type = getConfigurationType(this.parser.getName());
        }
        servoControllerConfiguration = new ServoControllerConfiguration(name, deviceConfigurations, serialNumber);
        servoControllerConfiguration.setPort(port);
        return servoControllerConfiguration;
    }

    private ControllerConfiguration parseMotorController(boolean useSerialNumber) throws IOException, XmlPullParserException {
        ControllerConfiguration motorControllerConfiguration;
        String name = this.parser.getAttributeValue(null, "name");
        int port = -1;
        SerialNumber serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER;
        if (useSerialNumber) {
            serialNumber = new SerialNumber(this.parser.getAttributeValue(null, "serialNumber"));
        } else {
            port = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
        }
        List deviceConfigurations = createDeviceConfigurationList(MOTOR_PORTS, ConfigurationType.MOTOR);
        int next = this.parser.next();
        ConfigurationType type = getConfigurationType(this.parser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG && type != null) {
                if (type == ConfigurationType.MOTOR_CONTROLLER) {
                    motorControllerConfiguration =
                            new MotorControllerConfiguration(name, deviceConfigurations, serialNumber);
                    motorControllerConfiguration.setPort(port);
                    motorControllerConfiguration.setEnabled(true);
                    return motorControllerConfiguration;
                }
            }
            if (next == XmlPullParser.START_TAG && type == ConfigurationType.MOTOR) {
                int devicePort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                deviceConfigurations.set(devicePort - 1, new MotorConfiguration(devicePort, this.parser.getAttributeValue(null, "name"), true));
            }
            next = this.parser.next();
            type = getConfigurationType(this.parser.getName());
        }
        motorControllerConfiguration =
                new MotorControllerConfiguration(name, deviceConfigurations, serialNumber);
        motorControllerConfiguration.setPort(port);
        return motorControllerConfiguration;
    }

    private static ConfigurationType getConfigurationType(String str) {
        if (str == null) {
            return null;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.MOTOR_CONTROLLER)) {
            return DeviceConfiguration.ConfigurationType.MOTOR_CONTROLLER;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.SERVO_CONTROLLER)) {
            return DeviceConfiguration.ConfigurationType.SERVO_CONTROLLER;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.LEGACY_MODULE_CONTROLLER)) {
            return DeviceConfiguration.ConfigurationType.LEGACY_MODULE_CONTROLLER;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.DEVICE_INTERFACE_MODULE)) {
            return DeviceConfiguration.ConfigurationType.DEVICE_INTERFACE_MODULE;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.ANALOG_INPUT)) {
            return DeviceConfiguration.ConfigurationType.ANALOG_INPUT;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.OPTICAL_DISTANCE_SENSOR)) {
            return DeviceConfiguration.ConfigurationType.OPTICAL_DISTANCE_SENSOR;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.IR_SEEKER)) {
            return DeviceConfiguration.ConfigurationType.IR_SEEKER;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.LIGHT_SENSOR)) {
            return DeviceConfiguration.ConfigurationType.LIGHT_SENSOR;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.DIGITAL_DEVICE)) {
            return DeviceConfiguration.ConfigurationType.DIGITAL_DEVICE;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.TOUCH_SENSOR)) {
            return DeviceConfiguration.ConfigurationType.TOUCH_SENSOR;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.IR_SEEKER_V3)) {
            return DeviceConfiguration.ConfigurationType.IR_SEEKER_V3;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.PULSE_WIDTH_DEVICE)) {
            return DeviceConfiguration.ConfigurationType.PULSE_WIDTH_DEVICE;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.I2C_DEVICE)) {
            return DeviceConfiguration.ConfigurationType.I2C_DEVICE;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.ANALOG_OUTPUT)) {
            return DeviceConfiguration.ConfigurationType.ANALOG_OUTPUT;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.TOUCH_SENSOR_MULTIPLEXER)) {
            return DeviceConfiguration.ConfigurationType.TOUCH_SENSOR_MULTIPLEXER;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.MATRIX_CONTROLLER)) {
            return DeviceConfiguration.ConfigurationType.MATRIX_CONTROLLER;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.ULTRASONIC_SENSOR)) {
            return DeviceConfiguration.ConfigurationType.ULTRASONIC_SENSOR;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.ADAFRUIT_COLOR_SENSOR)) {
            return DeviceConfiguration.ConfigurationType.ADAFRUIT_COLOR_SENSOR;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.COLOR_SENSOR)) {
            return DeviceConfiguration.ConfigurationType.COLOR_SENSOR;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.LED)) {
            return DeviceConfiguration.ConfigurationType.LED;
        }
        if (str.equalsIgnoreCase(XMLConfigurationConstants.GYRO)) {
            return DeviceConfiguration.ConfigurationType.GYRO;
        }
        return null;
    }
}
