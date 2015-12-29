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
    private XmlPullParser xmlPullParser;

    public ReadXMLFileHandler(Context context) {
        this.controllerConfigurations = new ArrayList();
    }

    public List<ControllerConfiguration> getDeviceControllers() {
        return this.controllerConfigurations;
    }

    public List<ControllerConfiguration> parse(InputStream is) throws RobotCoreException {
        this.xmlPullParser = null;
        try {
            XmlPullParserFactory newInstance = XmlPullParserFactory.newInstance();
            newInstance.setNamespaceAware(true);
            this.xmlPullParser = newInstance.newPullParser();
            this.xmlPullParser.setInput(is, null);
            int eventType = this.xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                ConfigurationType type = getConfigurationType(this.xmlPullParser.getName());
                if (eventType == XmlPullParser.START_TAG) {
                    if (type == ConfigurationType.MOTOR_CONTROLLER) {
                        this.controllerConfigurations.add(parseMotorControllerConfig(true));
                    } else if (type == ConfigurationType.SERVO_CONTROLLER) {
                        this.controllerConfigurations.add(parseServoControllerConfig(true));
                    } else if (type == ConfigurationType.LEGACY_MODULE_CONTROLLER) {
                        this.controllerConfigurations.add(parseLegacyModuleControllerConfig());
                    } else if (type == ConfigurationType.DEVICE_INTERFACE_MODULE) {
                        this.controllerConfigurations.add(parseDeviceInterfaceModuleConfig());
                    }
                }
                eventType = this.xmlPullParser.next();
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

    private ControllerConfiguration parseDeviceInterfaceModuleConfig() throws IOException, XmlPullParserException, RobotCoreException {
        String name = this.xmlPullParser.getAttributeValue(null, "name");
        String serialNumber = this.xmlPullParser.getAttributeValue(null, "serialNumber");
        ArrayList<DeviceConfiguration> pwdConfigs = createConfigList(PWM_PORTS, ConfigurationType.PULSE_WIDTH_DEVICE);
        ArrayList<DeviceConfiguration> i2CDeviceConfigs = createConfigList(I2C_PORTS, ConfigurationType.I2C_DEVICE);
        ArrayList<DeviceConfiguration> analogInputDeviceConfigs = createConfigList(ANALOG_INPUT_PORTS, ConfigurationType.ANALOG_INPUT);
        ArrayList<DeviceConfiguration> digitalDeviceConfigs = createConfigList(DIGITAL_PORTS, ConfigurationType.DIGITAL_DEVICE);
        ArrayList<DeviceConfiguration> analogOutputDeviceConfigs = createConfigList(ANALOG_OUTPUT_PORTS, ConfigurationType.ANALOG_OUTPUT);
        int next = this.xmlPullParser.next();
        ConfigurationType type = getConfigurationType(this.xmlPullParser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (type == null) {
                    continue;
                } else {
                    if (DEBUG) {
                        RobotLog.e("[handleDeviceInterfaceModule] tagname: " + type);
                    }
                    if (type == ConfigurationType.DEVICE_INTERFACE_MODULE) {
                        DeviceInterfaceModuleConfiguration deviceInterfaceModuleConfiguration = new DeviceInterfaceModuleConfiguration(name, new SerialNumber(serialNumber));
                        deviceInterfaceModuleConfiguration.setPwmDevices(pwdConfigs);
                        deviceInterfaceModuleConfiguration.setI2cDevices(i2CDeviceConfigs);
                        deviceInterfaceModuleConfiguration.setAnalogInputDevices(analogInputDeviceConfigs);
                        deviceInterfaceModuleConfiguration.setDigitalDevices(digitalDeviceConfigs);
                        deviceInterfaceModuleConfiguration.setAnalogOutputDevices(analogOutputDeviceConfigs);
                        deviceInterfaceModuleConfiguration.setEnabled(true);
                        return deviceInterfaceModuleConfiguration;
                    }
                }
            }
            if (next == XmlPullParser.START_TAG) {
                DeviceConfiguration c;
                if (type == ConfigurationType.ANALOG_INPUT || type == ConfigurationType.OPTICAL_DISTANCE_SENSOR) {
                    c = parseDeviceConfig();
                    analogInputDeviceConfigs.set(c.getPort(), c);
                } else if (type == ConfigurationType.PULSE_WIDTH_DEVICE) {
                    c = parseDeviceConfig();
                    pwdConfigs.set(c.getPort(), c);
                } else if (type == ConfigurationType.I2C_DEVICE || type == ConfigurationType.IR_SEEKER_V3 || type == ConfigurationType.ADAFRUIT_COLOR_SENSOR || type == ConfigurationType.COLOR_SENSOR || type == ConfigurationType.GYRO) {
                    c = parseDeviceConfig();
                    i2CDeviceConfigs.set(c.getPort(), c);
                } else if (type == ConfigurationType.ANALOG_OUTPUT) {
                    c = parseDeviceConfig();
                    analogOutputDeviceConfigs.set(c.getPort(), c);
                } else if (type == ConfigurationType.DIGITAL_DEVICE || type == ConfigurationType.TOUCH_SENSOR || type == ConfigurationType.LED) {
                    DeviceConfiguration c2 = parseDeviceConfig();
                    digitalDeviceConfigs.set(c2.getPort(), c2);
                }
            }
            next = this.xmlPullParser.next();
            type = getConfigurationType(this.xmlPullParser.getName());
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }

    private ControllerConfiguration parseLegacyModuleControllerConfig() throws IOException, XmlPullParserException, RobotCoreException {
        String name = this.xmlPullParser.getAttributeValue(null, "name");
        String serialNumber = this.xmlPullParser.getAttributeValue(null, "serialNumber");
        List a = createConfigList(LEGACY_MODULE_PORTS, ConfigurationType.NOTHING);
        int next = this.xmlPullParser.next();
        ConfigurationType type = getConfigurationType(this.xmlPullParser.getName());
        ControllerConfiguration legacyModuleControllerConfiguration;
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (type == null) {
                    continue;
                } else if (type == ConfigurationType.LEGACY_MODULE_CONTROLLER) {
                    legacyModuleControllerConfiguration = new LegacyModuleControllerConfiguration(name, a, new SerialNumber(serialNumber));
                    legacyModuleControllerConfiguration.setEnabled(true);
                    return legacyModuleControllerConfiguration;
                }
            } else if (next == XmlPullParser.START_TAG) {
                if (DEBUG) {
                    RobotLog.e("[handleLegacyModule] tagname: " + type);
                }
                if (type == ConfigurationType.COMPASS || type == ConfigurationType.LIGHT_SENSOR || type == ConfigurationType.IR_SEEKER || type == ConfigurationType.ACCELEROMETER || type == ConfigurationType.GYRO || type == ConfigurationType.TOUCH_SENSOR || type == ConfigurationType.TOUCH_SENSOR_MULTIPLEXER || type == ConfigurationType.ULTRASONIC_SENSOR || type == ConfigurationType.COLOR_SENSOR || type == ConfigurationType.NOTHING) {
                    DeviceConfiguration c = parseDeviceConfig();
                    a.set(c.getPort(), c);
                } else if (type == ConfigurationType.MOTOR_CONTROLLER) {
                    legacyModuleControllerConfiguration = parseMotorControllerConfig(false);
                    a.set(legacyModuleControllerConfiguration.getPort(), legacyModuleControllerConfiguration);
                } else if (type == ConfigurationType.SERVO_CONTROLLER) {
                    legacyModuleControllerConfiguration = parseServoControllerConfig(false);
                    a.set(legacyModuleControllerConfiguration.getPort(), legacyModuleControllerConfiguration);
                } else if (type == ConfigurationType.MATRIX_CONTROLLER) {
                    legacyModuleControllerConfiguration = parseMatrixControllerConfig();
                    a.set(legacyModuleControllerConfiguration.getPort(), legacyModuleControllerConfiguration);
                }
            }
            next = this.xmlPullParser.next();
            type = getConfigurationType(this.xmlPullParser.getName());
        }
        return new LegacyModuleControllerConfiguration(name, a, new SerialNumber(serialNumber));
    }

    private DeviceConfiguration parseDeviceConfig() {
        ConfigurationType type = getConfigurationType(this.xmlPullParser.getName());
        DeviceConfiguration deviceConfiguration = new DeviceConfiguration(Integer.parseInt(this.xmlPullParser.getAttributeValue(null, "port")));
        deviceConfiguration.setType(type);
        deviceConfiguration.setName(this.xmlPullParser.getAttributeValue(null, "name"));
        if (!deviceConfiguration.getName().equalsIgnoreCase(DeviceConfiguration.DISABLED_DEVICE_NAME)) {
            deviceConfiguration.setEnabled(true);
        }
        if (DEBUG) {
            RobotLog.e("[handleDevice] name: " + deviceConfiguration.getName() + ", port: " + deviceConfiguration.getPort() + ", type: " + deviceConfiguration.getType());
        }
        return deviceConfiguration;
    }

    private ArrayList<DeviceConfiguration> createConfigList(int ports, ConfigurationType configurationType) {
        ArrayList<DeviceConfiguration> arrayList = new ArrayList();
        for (int i2 = 0; i2 < ports; i2++) {
            if (configurationType == ConfigurationType.SERVO) {
                arrayList.add(new ServoConfiguration(i2 + 1, DeviceConfiguration.DISABLED_DEVICE_NAME, false));
            } else if (configurationType == ConfigurationType.MOTOR) {
                arrayList.add(new MotorConfiguration(i2 + 1, DeviceConfiguration.DISABLED_DEVICE_NAME, false));
            } else {
                arrayList.add(new DeviceConfiguration(i2, configurationType, DeviceConfiguration.DISABLED_DEVICE_NAME, false));
            }
        }
        return arrayList;
    }

    private ControllerConfiguration parseMatrixControllerConfig() throws IOException, XmlPullParserException, RobotCoreException {
        String attributeValue = this.xmlPullParser.getAttributeValue(null, "name");
        String serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER.toString();
        int parseInt = Integer.parseInt(this.xmlPullParser.getAttributeValue(null, "port"));
        ArrayList<DeviceConfiguration> a = createConfigList(MATRIX_SERVO_PORTS, ConfigurationType.SERVO);
        ArrayList<DeviceConfiguration> a2 = createConfigList(MATRIX_MOTOR_PORTS, ConfigurationType.MOTOR);
        int next = this.xmlPullParser.next();
        ConfigurationType type = getConfigurationType(this.xmlPullParser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (type == null) {
                    continue;
                } else if (type == ConfigurationType.MATRIX_CONTROLLER) {
                    ControllerConfiguration matrixControllerConfiguration = new MatrixControllerConfiguration(attributeValue, a2, a, new SerialNumber(serialNumber));
                    matrixControllerConfiguration.setPort(parseInt);
                    matrixControllerConfiguration.setEnabled(true);
                    return matrixControllerConfiguration;
                }
            } else if (next == XmlPullParser.START_TAG) {
                int parseInt2;
                if (type == ConfigurationType.SERVO) {
                    parseInt2 = Integer.parseInt(this.xmlPullParser.getAttributeValue(null, "port"));
                    a.set(parseInt2 - 1, new ServoConfiguration(parseInt2, this.xmlPullParser.getAttributeValue(null, "name"), true));
                } else if (type == ConfigurationType.MOTOR) {
                    parseInt2 = Integer.parseInt(this.xmlPullParser.getAttributeValue(null, "port"));
                    a2.set(parseInt2 - 1, new MotorConfiguration(parseInt2, this.xmlPullParser.getAttributeValue(null, "name"), true));
                }
            }
            next = this.xmlPullParser.next();
            type = getConfigurationType(this.xmlPullParser.getName());
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }

    private ControllerConfiguration parseServoControllerConfig(boolean useSerialNumber) throws IOException, XmlPullParserException {
        ControllerConfiguration servoControllerConfiguration;
        String attributeValue = this.xmlPullParser.getAttributeValue(null, "name");

        int port = -1;
        String serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER.toString();

        if (useSerialNumber) {
            serialNumber = this.xmlPullParser.getAttributeValue(null, "serialNumber");
        } else {
            port = Integer.parseInt(this.xmlPullParser.getAttributeValue(null, "port"));
        }
        List a = createConfigList(SERVO_PORTS, ConfigurationType.SERVO);
        int next = this.xmlPullParser.next();
        ConfigurationType type = getConfigurationType(this.xmlPullParser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (type == null) {
                    continue;
                } else if (type == ConfigurationType.SERVO_CONTROLLER) {
                    servoControllerConfiguration = new ServoControllerConfiguration(attributeValue, a, new SerialNumber(serialNumber));
                    servoControllerConfiguration.setPort(port);
                    servoControllerConfiguration.setEnabled(true);
                    return servoControllerConfiguration;
                }
            } else if (next == XmlPullParser.START_TAG && type == ConfigurationType.SERVO) {
                int parseInt = Integer.parseInt(this.xmlPullParser.getAttributeValue(null, "port"));
                a.set(parseInt - 1, new ServoConfiguration(parseInt, this.xmlPullParser.getAttributeValue(null, "name"), true));
            }
            next = this.xmlPullParser.next();
            type = getConfigurationType(this.xmlPullParser.getName());
        }
        servoControllerConfiguration = new ServoControllerConfiguration(attributeValue, a, new SerialNumber(serialNumber));
        servoControllerConfiguration.setPort(port);
        return servoControllerConfiguration;
    }

    private ControllerConfiguration parseMotorControllerConfig(boolean useSerialNumber) throws IOException, XmlPullParserException {
        ControllerConfiguration motorControllerConfiguration;
        String attributeValue = this.xmlPullParser.getAttributeValue(null, "name");

        int port = -1;
        String serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER.toString();

        if (useSerialNumber) {
            serialNumber = this.xmlPullParser.getAttributeValue(null, "serialNumber");
        } else {
            port = Integer.parseInt(this.xmlPullParser.getAttributeValue(null, "port"));
        }

        List a = createConfigList(MOTOR_PORTS, ConfigurationType.MOTOR);
        int next = this.xmlPullParser.next();
        ConfigurationType type = getConfigurationType(this.xmlPullParser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (type == null) {
                    continue;
                } else if (type == ConfigurationType.MOTOR_CONTROLLER) {
                    motorControllerConfiguration = new MotorControllerConfiguration(attributeValue, a, new SerialNumber(serialNumber));
                    motorControllerConfiguration.setPort(port);
                    motorControllerConfiguration.setEnabled(true);
                    return motorControllerConfiguration;
                }
            } else if (next == XmlPullParser.START_TAG && type == ConfigurationType.MOTOR) {
                int parseInt = Integer.parseInt(this.xmlPullParser.getAttributeValue(null, "port"));
                a.set(parseInt - 1, new MotorConfiguration(parseInt, this.xmlPullParser.getAttributeValue(null, "name"), true));
            }
            next = this.xmlPullParser.next();
            type = getConfigurationType(this.xmlPullParser.getName());
        }
        motorControllerConfiguration = new MotorControllerConfiguration(attributeValue, a, new SerialNumber(serialNumber));
        motorControllerConfiguration.setPort(port);
        return motorControllerConfiguration;
    }

    private ConfigurationType getConfigurationType(String str) {
        if (str == null) {
            return null;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.MOTOR_CONTROLLER)) {
            return ConfigurationType.MOTOR_CONTROLLER;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.SERVO_CONTROLLER)) {
            return ConfigurationType.SERVO_CONTROLLER;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.LEGACY_MODULE_CONTROLLER)) {
            return ConfigurationType.LEGACY_MODULE_CONTROLLER;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.DEVICE_INTERFACE_MODULE)) {
            return ConfigurationType.DEVICE_INTERFACE_MODULE;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.ANALOG_INPUT)) {
            return ConfigurationType.ANALOG_INPUT;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.OPTICAL_DISTANCE_SENSOR)) {
            return ConfigurationType.OPTICAL_DISTANCE_SENSOR;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.IR_SEEKER)) {
            return ConfigurationType.IR_SEEKER;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.LIGHT_SENSOR)) {
            return ConfigurationType.LIGHT_SENSOR;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.DIGITAL_DEVICE)) {
            return ConfigurationType.DIGITAL_DEVICE;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.TOUCH_SENSOR)) {
            return ConfigurationType.TOUCH_SENSOR;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.IR_SEEKER_V3)) {
            return ConfigurationType.IR_SEEKER_V3;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.PULSE_WIDTH_DEVICE)) {
            return ConfigurationType.PULSE_WIDTH_DEVICE;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.I2C_DEVICE)) {
            return ConfigurationType.I2C_DEVICE;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.ANALOG_OUTPUT)) {
            return ConfigurationType.ANALOG_OUTPUT;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.TOUCH_SENSOR_MULTIPLEXER)) {
            return ConfigurationType.TOUCH_SENSOR_MULTIPLEXER;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.MATRIX_CONTROLLER)) {
            return ConfigurationType.MATRIX_CONTROLLER;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.ULTRASONIC_SENSOR)) {
            return ConfigurationType.ULTRASONIC_SENSOR;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.ADAFRUIT_COLOR_SENSOR)) {
            return ConfigurationType.ADAFRUIT_COLOR_SENSOR;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.COLOR_SENSOR)) {
            return ConfigurationType.COLOR_SENSOR;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.LED)) {
            return ConfigurationType.LED;
        } else if (str.equalsIgnoreCase(XMLConfigurationConstants.GYRO)) {
            return ConfigurationType.GYRO;
        } else {
            return null;
        }
    }
}
