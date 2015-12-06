package com.qualcomm.robotcore.hardware.configuration;

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

public class ReadXMLFileHandler {
    private static boolean DEBUG = false;
    private static final int pulseWidthPorts = 2;
    private static final int digitalPorts = 8;
    private static final int analogInputPorts = 8;
    private static final int analogOutputPorts = 2;
    private static final int i2cPorts = 6;
    private static final int legacyPorts = 6;
    private static final int servoPorts = 6;
    private static final int motorPorts = 2;
    private static final int f284k = 4;
    private static final int f285l = 4;
    List<ControllerConfiguration> configurations = new ArrayList<ControllerConfiguration>();
    private XmlPullParser parser;


    public List<ControllerConfiguration> getDeviceControllers() {
        return this.configurations;
    }

    public List<ControllerConfiguration> parse(InputStream is) throws RobotCoreException {
        this.parser = null;
        try {
            XmlPullParserFactory newInstance = XmlPullParserFactory.newInstance();
            newInstance.setNamespaceAware(true);
            this.parser = newInstance.newPullParser();
            this.parser.setInput(is, null);
            int eventType = this.parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tag = getTag(this.parser.getName());
                if (eventType == XmlPullParser.START_TAG) {
                    if (tag.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString())) {
                        this.configurations.add(parseMotorControllerConfiguration(true));
                    } else if (tag.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString())) {
                        this.configurations.add(parseServoControllerConfiguration(true));
                    } else if (tag.equalsIgnoreCase(ConfigurationType.LEGACY_MODULE_CONTROLLER.toString())) {
                        this.configurations.add(parseLegacyControllerConfiguration());
                    } else if (tag.equalsIgnoreCase(ConfigurationType.DEVICE_INTERFACE_MODULE.toString())) {
                        this.configurations.add(parseDeviceInterface());
                    }
                }
                eventType = this.parser.next();
            }
        } catch (XmlPullParserException e) {
            RobotLog.w("XmlPullParserException");
            e.printStackTrace();
        } catch (IOException e2) {
            RobotLog.w("IOException");
            e2.printStackTrace();
        }
        return this.configurations;
    }

    private ControllerConfiguration parseDeviceInterface() throws IOException, XmlPullParserException, RobotCoreException {
        String attributeValue = this.parser.getAttributeValue(null, "name");
        String serialNumber = this.parser.getAttributeValue(null, "serialNumber");
        ArrayList<DeviceConfiguration> pulseWidthDeviceConfigs = createDeviceConfigList(pulseWidthPorts, ConfigurationType.PULSE_WIDTH_DEVICE);
        ArrayList<DeviceConfiguration> i2cDeviceConfigs = createDeviceConfigList(i2cPorts, ConfigurationType.I2C_DEVICE);
        ArrayList<DeviceConfiguration> analogInputConfigs = createDeviceConfigList(analogInputPorts, ConfigurationType.ANALOG_INPUT);
        ArrayList<DeviceConfiguration> digitalDeviceConfigs = createDeviceConfigList(digitalPorts, ConfigurationType.DIGITAL_DEVICE);
        ArrayList<DeviceConfiguration> analogOutputConfigs = createDeviceConfigList(analogOutputPorts, ConfigurationType.ANALOG_OUTPUT);
        int next = this.parser.next();
        String tag = getTag(this.parser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (tag == null) {
                    continue;
                } else {
                    if (DEBUG) {
                        RobotLog.e("[handleDeviceInterfaceModule] tagname: " + tag);
                    }
                    if (tag.equalsIgnoreCase(ConfigurationType.DEVICE_INTERFACE_MODULE.toString())) {
                        DeviceInterfaceModuleConfiguration configuration = new DeviceInterfaceModuleConfiguration(attributeValue, new SerialNumber(serialNumber));
                        configuration.setPwmDevices(pulseWidthDeviceConfigs);
                        configuration.setI2cDevices(i2cDeviceConfigs);
                        configuration.setAnalogInputDevices(analogInputConfigs);
                        configuration.setDigitalDevices(digitalDeviceConfigs);
                        configuration.setAnalogOutputDevices(analogOutputConfigs);
                        configuration.setEnabled(true);
                        return configuration;
                    }
                }
            } else if (next == XmlPullParser.START_TAG) {
                DeviceConfiguration configuration;
                if (tag.equalsIgnoreCase(ConfigurationType.ANALOG_INPUT.toString()) ||
                        tag.equalsIgnoreCase(ConfigurationType.OPTICAL_DISTANCE_SENSOR.toString())) {
                    configuration = parseDeviceConfiguration();
                    analogInputConfigs.set(configuration.getPort(), configuration);
                }
                if (tag.equalsIgnoreCase(ConfigurationType.PULSE_WIDTH_DEVICE.toString())) {
                    configuration = parseDeviceConfiguration();
                    pulseWidthDeviceConfigs.set(configuration.getPort(), configuration);
                }
                if (tag.equalsIgnoreCase(ConfigurationType.I2C_DEVICE.toString()) ||
                        tag.equalsIgnoreCase(ConfigurationType.IR_SEEKER_V3.toString()) ||
                        tag.equalsIgnoreCase(ConfigurationType.ADAFRUIT_COLOR_SENSOR.toString()) ||
                        tag.equalsIgnoreCase(ConfigurationType.COLOR_SENSOR.toString()) ||
                        tag.equalsIgnoreCase(ConfigurationType.GYRO.toString())) {
                    configuration = parseDeviceConfiguration();
                    i2cDeviceConfigs.set(configuration.getPort(), configuration);
                }
                if (tag.equalsIgnoreCase(ConfigurationType.ANALOG_OUTPUT.toString())) {
                    configuration = parseDeviceConfiguration();
                    analogOutputConfigs.set(configuration.getPort(), configuration);
                }
                if (tag.equalsIgnoreCase(ConfigurationType.DIGITAL_DEVICE.toString()) ||
                        tag.equalsIgnoreCase(ConfigurationType.TOUCH_SENSOR.toString()) ||
                        tag.equalsIgnoreCase(ConfigurationType.LED.toString())) {
                    DeviceConfiguration c2 = parseDeviceConfiguration();
                    digitalDeviceConfigs.set(c2.getPort(), c2);
                }
            }
            next = this.parser.next();
            tag = getTag(this.parser.getName());
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }

    private ControllerConfiguration parseLegacyControllerConfiguration() throws IOException, XmlPullParserException, RobotCoreException {
        String controllerName = this.parser.getAttributeValue(null, "name");
        String serialNumber = this.parser.getAttributeValue(null, "serialNumber");
        List<DeviceConfiguration> deviceConfigs = createDeviceConfigList(legacyPorts, ConfigurationType.NOTHING);
        ControllerConfiguration controllerConfig = null;
        int next = this.parser.next();
        String tag = getTag(this.parser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (tag == null) {
                    continue;
                } else if (tag.equalsIgnoreCase(ConfigurationType.LEGACY_MODULE_CONTROLLER.toString())) {
                    controllerConfig = new LegacyModuleControllerConfiguration(controllerName, deviceConfigs, new SerialNumber(serialNumber));
                    controllerConfig.setEnabled(true);
                    return controllerConfig;
                }
            } else if (next == XmlPullParser.START_TAG) {
                if (DEBUG) {
                    RobotLog.e("[handleLegacyModule] tagname: " + tag);
                }
                if (tag.equalsIgnoreCase(ConfigurationType.COMPASS.toString()) ||
                        tag.equalsIgnoreCase(ConfigurationType.LIGHT_SENSOR.toString()) ||
                        tag.equalsIgnoreCase(ConfigurationType.IR_SEEKER.toString()) ||
                        tag.equalsIgnoreCase(ConfigurationType.ACCELEROMETER.toString()) ||
                        tag.equalsIgnoreCase(ConfigurationType.GYRO.toString()) ||
                        tag.equalsIgnoreCase(ConfigurationType.TOUCH_SENSOR.toString()) ||
                        tag.equalsIgnoreCase(ConfigurationType.TOUCH_SENSOR_MULTIPLEXER.toString()) ||
                        tag.equalsIgnoreCase(ConfigurationType.ULTRASONIC_SENSOR.toString()) ||
                        tag.equalsIgnoreCase(ConfigurationType.COLOR_SENSOR.toString()) ||
                        tag.equalsIgnoreCase(ConfigurationType.NOTHING.toString())) {
                    DeviceConfiguration deviceConfig = parseDeviceConfiguration();
                    deviceConfigs.set(deviceConfig.getPort(), deviceConfig);
                } else {
                    if (tag.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString())) {
                        controllerConfig = parseMotorControllerConfiguration(false);
                    } else if (tag.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString())) {
                        controllerConfig = parseServoControllerConfiguration(false);
                    } else if (tag.equalsIgnoreCase(ConfigurationType.MATRIX_CONTROLLER.toString())) {
                        controllerConfig = parseMatrixControllerConfiguration();
                    }
                    if(controllerConfig != null) {
                        deviceConfigs.set(controllerConfig.getPort(), controllerConfig);
                    }
                }
            }
            next = this.parser.next();
            tag = getTag(this.parser.getName());
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }

    private DeviceConfiguration parseDeviceConfiguration() {
        String tag = getTag(this.parser.getName());
        DeviceConfiguration deviceConfiguration = new DeviceConfiguration(Integer.parseInt(this.parser.getAttributeValue(null, "port")));
        deviceConfiguration.setType(deviceConfiguration.typeFromString(tag));
        deviceConfiguration.setName(this.parser.getAttributeValue(null, "name"));
        if (!deviceConfiguration.getName().equalsIgnoreCase(DeviceConfiguration.DISABLED_DEVICE_NAME)) {
            deviceConfiguration.setEnabled(true);
        }
        if (DEBUG) {
            RobotLog.e("[handleDevice] name: " + deviceConfiguration.getName() + ", port: " + deviceConfiguration.getPort() + ", type: " + deviceConfiguration.getType());
        }
        return deviceConfiguration;
    }

    private ArrayList<DeviceConfiguration> createDeviceConfigList(int ports, ConfigurationType configurationType) {
        ArrayList<DeviceConfiguration> deviceConfigs = new ArrayList<DeviceConfiguration>();
        for (int port = 0; port < ports; port++) {
            if (configurationType == ConfigurationType.SERVO) {
                deviceConfigs.add(new ServoConfiguration(port + 1, DeviceConfiguration.DISABLED_DEVICE_NAME, false));
            } else if (configurationType == ConfigurationType.MOTOR) {
                deviceConfigs.add(new MotorConfiguration(port + 1, DeviceConfiguration.DISABLED_DEVICE_NAME, false));
            } else {
                deviceConfigs.add(new DeviceConfiguration(port, configurationType, DeviceConfiguration.DISABLED_DEVICE_NAME, false));
            }
        }
        return deviceConfigs;
    }

    private ControllerConfiguration parseMatrixControllerConfiguration() throws IOException, XmlPullParserException, RobotCoreException {
        String controllerName = this.parser.getAttributeValue(null, "name");
        String serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER.toString();
        int controllerPort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
        List<DeviceConfiguration> servoConfigs = createDeviceConfigList(f285l, ConfigurationType.SERVO);
        List<DeviceConfiguration> motorConfigs = createDeviceConfigList(f284k, ConfigurationType.MOTOR);
        int next = this.parser.next();
        String tag = getTag(this.parser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (tag == null) {
                    continue;
                } else if (tag.equalsIgnoreCase(ConfigurationType.MATRIX_CONTROLLER.toString())) {
                    ControllerConfiguration matrixControllerConfiguration = new MatrixControllerConfiguration(controllerName, motorConfigs, servoConfigs, new SerialNumber(serialNumber));
                    matrixControllerConfiguration.setPort(controllerPort);
                    matrixControllerConfiguration.setEnabled(true);
                    return matrixControllerConfiguration;
                }
            } else if (next == XmlPullParser.START_TAG) {
                int devicePort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                String deviceName = this.parser.getAttributeValue(null, "name");
                if (tag.equalsIgnoreCase(ConfigurationType.SERVO.toString())) {
                    servoConfigs.set(devicePort - 1, new ServoConfiguration(devicePort, deviceName, true));
                } else if (tag.equalsIgnoreCase(ConfigurationType.MOTOR.toString())) {
                    motorConfigs.set(devicePort - 1, new MotorConfiguration(devicePort, deviceName, true));
                }
            }
            next = this.parser.next();
            tag = getTag(this.parser.getName());
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }

    private ControllerConfiguration parseServoControllerConfiguration(boolean enabled) throws IOException, XmlPullParserException, RobotCoreException {
        String controllerName = this.parser.getAttributeValue(null, "name");
        int controllerPort;
        String serialNumber;

        if (enabled) {
            controllerPort = -1;
            serialNumber = this.parser.getAttributeValue(null, "serialNumber");
        } else {
            controllerPort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
            serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER.toString();
        }

        ControllerConfiguration controllerConfig;
        List<DeviceConfiguration> deviceConfigs = createDeviceConfigList(servoPorts, ConfigurationType.SERVO);
        int next = this.parser.next();
        String tag = getTag(this.parser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (tag == null) {
                    continue;
                } else if (tag.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString())) {
                    controllerConfig = new ServoControllerConfiguration(controllerName, deviceConfigs, new SerialNumber(serialNumber));
                    controllerConfig.setPort(controllerPort);
                    controllerConfig.setEnabled(true);
                    return controllerConfig;
                }
            } else if (next == XmlPullParser.START_TAG && tag.equalsIgnoreCase(ConfigurationType.SERVO.toString())) {
                int devicePort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                String deviceName = this.parser.getAttributeValue(null, "name");
                deviceConfigs.set(devicePort - 1, new ServoConfiguration(devicePort, deviceName, true));
            }
            next = this.parser.next();
            tag = getTag(this.parser.getName());
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }

    private ControllerConfiguration parseMotorControllerConfiguration(boolean enabled) throws IOException, XmlPullParserException, RobotCoreException{
        String controllerName = this.parser.getAttributeValue(null, "name");
        int controllerPort;
        String serialNumber;

        if (enabled) {
            controllerPort = -1;
            serialNumber = this.parser.getAttributeValue(null, "serialNumber");
        } else {
            controllerPort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
            serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER.toString();
        }

        ControllerConfiguration controllerConfig;
        List<DeviceConfiguration> deviceConfigs = createDeviceConfigList(motorPorts, ConfigurationType.MOTOR);

        int next = this.parser.next();
        String tag = getTag(this.parser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (tag == null) {
                    continue;
                } else if (tag.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString())) {
                    controllerConfig = new MotorControllerConfiguration(controllerName, deviceConfigs, new SerialNumber(serialNumber));
                    controllerConfig.setPort(controllerPort);
                    controllerConfig.setEnabled(true);
                    return controllerConfig;
                }
            } else if (next == XmlPullParser.START_TAG && tag.equalsIgnoreCase(ConfigurationType.MOTOR.toString())) {
                int devicePort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                String deviceName = this.parser.getAttributeValue(null, "name");
                deviceConfigs.set(devicePort - 1, new MotorConfiguration(devicePort, deviceName, true));
            }
            next = this.parser.next();
            tag = getTag(this.parser.getName());
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }

    private String getTag(String str) {
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
