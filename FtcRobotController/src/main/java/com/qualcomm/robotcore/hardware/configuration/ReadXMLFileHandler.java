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

    List<ControllerConfiguration> controllerConfigurations = new ArrayList<ControllerConfiguration>();
    private XmlPullParser parser;

    public ReadXMLFileHandler(Context context) {
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

            int next;
            while ((next = this.parser.next()) != XmlPullParser.END_DOCUMENT) {
                ConfigurationType type = getConfigurationType(this.parser.getName());
                if (next == XmlPullParser.START_TAG) {
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
        DeviceInterfaceModuleConfiguration deviceInterfaceModuleConfiguration;
        DeviceConfigurationList pwdConfigs = new DeviceConfigurationList(PWM_PORTS, ConfigurationType.PULSE_WIDTH_DEVICE);
        DeviceConfigurationList i2CDeviceConfigs = new DeviceConfigurationList(I2C_PORTS, ConfigurationType.I2C_DEVICE);
        DeviceConfigurationList analogInputDeviceConfigs = new DeviceConfigurationList(ANALOG_INPUT_PORTS, ConfigurationType.ANALOG_INPUT);
        DeviceConfigurationList digitalDeviceConfigs = new DeviceConfigurationList(DIGITAL_PORTS, ConfigurationType.DIGITAL_DEVICE);
        DeviceConfigurationList analogOutputDeviceConfigs = new DeviceConfigurationList(ANALOG_OUTPUT_PORTS, ConfigurationType.ANALOG_OUTPUT);

        String name = this.parser.getAttributeValue(null, "name");
        String serialNumber = this.parser.getAttributeValue(null, "serialNumber");
        ConfigurationType type = getConfigurationType(this.parser.getName());

        int next;
        while ((next = this.parser.next()) != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (type != null) {
                    if (DEBUG) {
                        RobotLog.e("[handleDeviceInterfaceModule] tagname: " + type);
                    }
                    if (type == ConfigurationType.DEVICE_INTERFACE_MODULE) {
                        deviceInterfaceModuleConfiguration = new DeviceInterfaceModuleConfiguration(name, new SerialNumber(serialNumber));
                        deviceInterfaceModuleConfiguration.setPwmDevices(pwdConfigs);
                        deviceInterfaceModuleConfiguration.setI2cDevices(i2CDeviceConfigs);
                        deviceInterfaceModuleConfiguration.setAnalogInputDevices(analogInputDeviceConfigs);
                        deviceInterfaceModuleConfiguration.setDigitalDevices(digitalDeviceConfigs);
                        deviceInterfaceModuleConfiguration.setAnalogOutputDevices(analogOutputDeviceConfigs);
                        deviceInterfaceModuleConfiguration.setEnabled(true);
                        return deviceInterfaceModuleConfiguration;
                    }
                }
            } else if (next == XmlPullParser.START_TAG) {
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
            type = getConfigurationType(this.parser.getName());
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }

    private ControllerConfiguration parseLegacyModuleControllerConfig() throws IOException, XmlPullParserException, RobotCoreException {
        ControllerConfiguration controllerConfiguration;
        DeviceConfigurationList deviceConfigurationList = new DeviceConfigurationList(LEGACY_MODULE_PORTS, ConfigurationType.NOTHING);

        String name = this.parser.getAttributeValue(null, "name");
        String serialNumber = this.parser.getAttributeValue(null, "serialNumber");
        ConfigurationType type = getConfigurationType(this.parser.getName());

        int next;
        while ((next = this.parser.next()) != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (type == null) {
                    continue;
                } else if (type == ConfigurationType.LEGACY_MODULE_CONTROLLER) {
                    controllerConfiguration = new LegacyModuleControllerConfiguration(name, deviceConfigurationList, new SerialNumber(serialNumber));
                    controllerConfiguration.setEnabled(true);
                    return controllerConfiguration;
                }
            } else if (next == XmlPullParser.START_TAG) {
                if (DEBUG) {
                    RobotLog.e("[handleLegacyModule] tagname: " + type);
                }
                if (type == ConfigurationType.COMPASS || type == ConfigurationType.LIGHT_SENSOR || type == ConfigurationType.IR_SEEKER || type == ConfigurationType.ACCELEROMETER || type == ConfigurationType.GYRO || type == ConfigurationType.TOUCH_SENSOR || type == ConfigurationType.TOUCH_SENSOR_MULTIPLEXER || type == ConfigurationType.ULTRASONIC_SENSOR || type == ConfigurationType.COLOR_SENSOR || type == ConfigurationType.NOTHING) {
                    DeviceConfiguration c = parseDeviceConfig();
                    deviceConfigurationList.set(c.getPort(), c);
                } else if (type == ConfigurationType.MOTOR_CONTROLLER) {
                    controllerConfiguration = parseMotorControllerConfig(false);
                    deviceConfigurationList.set(controllerConfiguration.getPort(), controllerConfiguration);
                } else if (type == ConfigurationType.SERVO_CONTROLLER) {
                    controllerConfiguration = parseServoControllerConfig(false);
                    deviceConfigurationList.set(controllerConfiguration.getPort(), controllerConfiguration);
                } else if (type == ConfigurationType.MATRIX_CONTROLLER) {
                    controllerConfiguration = parseMatrixControllerConfig();
                    deviceConfigurationList.set(controllerConfiguration.getPort(), controllerConfiguration);
                }
            }
            type = getConfigurationType(this.parser.getName());
        }
        return new LegacyModuleControllerConfiguration(name, deviceConfigurationList, new SerialNumber(serialNumber));
    }

    private DeviceConfiguration parseDeviceConfig() {
        int port = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
        ConfigurationType type = getConfigurationType(this.parser.getName());
        String name = this.parser.getAttributeValue(null, "name");
        boolean enabled = !(name.equalsIgnoreCase(DeviceConfiguration.DISABLED_DEVICE_NAME));

        if (DEBUG) {
            RobotLog.e("[handleDevice] name: " + name + ", port: " + port + ", type: " + type);
        }

        return new DeviceConfiguration(port, type, name, enabled);
    }


    private ControllerConfiguration parseMatrixControllerConfig() throws IOException, XmlPullParserException, RobotCoreException {
        ControllerConfiguration controllerConfiguration;
        DeviceConfigurationList servoConfigurationList = new DeviceConfigurationList(MATRIX_SERVO_PORTS, ConfigurationType.SERVO);
        DeviceConfigurationList motorConfigurationList = new DeviceConfigurationList(MATRIX_MOTOR_PORTS, ConfigurationType.MOTOR);

        String name = this.parser.getAttributeValue(null, "name");
        String serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER.toString();
        int port = Integer.parseInt(this.parser.getAttributeValue(null, "port"));

        int next;
        ConfigurationType type = getConfigurationType(this.parser.getName());
        while ((next = this.parser.next()) != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (type == null) {
                    continue;
                } else if (type == ConfigurationType.MATRIX_CONTROLLER) {
                    ControllerConfiguration matrixControllerConfiguration = new MatrixControllerConfiguration(name, motorConfigurationList, servoConfigurationList, new SerialNumber(serialNumber));
                    matrixControllerConfiguration.setPort(port);
                    matrixControllerConfiguration.setEnabled(true);
                    return matrixControllerConfiguration;
                }
            } else if (next == XmlPullParser.START_TAG) {
                int parseInt2;
                if (type == ConfigurationType.SERVO) {
                    parseInt2 = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                    servoConfigurationList.set(parseInt2, new ServoConfiguration(parseInt2, this.parser.getAttributeValue(null, "name"), true));
                } else if (type == ConfigurationType.MOTOR) {
                    parseInt2 = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                    motorConfigurationList.set(parseInt2, new MotorConfiguration(parseInt2, this.parser.getAttributeValue(null, "name"), true));
                }
            }
            type = getConfigurationType(this.parser.getName());
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }

    private ControllerConfiguration parseServoControllerConfig(boolean useSerialNumber) throws IOException, XmlPullParserException {
        ControllerConfiguration controllerConfiguration;
        DeviceConfigurationList deviceConfigurationList = new DeviceConfigurationList(SERVO_PORTS, ConfigurationType.SERVO);

        String name = this.parser.getAttributeValue(null, "name");
        int port = -1;
        String serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER.toString();

        if (useSerialNumber) {
            serialNumber = this.parser.getAttributeValue(null, "serialNumber");
        } else {
            port = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
        }

        int next;
        ConfigurationType type = getConfigurationType(this.parser.getName());
        while ((next = this.parser.next()) != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (type == null) {
                    continue;
                } else if (type == ConfigurationType.SERVO_CONTROLLER) {
                    controllerConfiguration = new ServoControllerConfiguration(name, deviceConfigurationList, new SerialNumber(serialNumber));
                    controllerConfiguration.setPort(port);
                    controllerConfiguration.setEnabled(true);
                    return controllerConfiguration;
                }
            } else if (next == XmlPullParser.START_TAG && type == ConfigurationType.SERVO) {
                int parseInt = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                deviceConfigurationList.set(parseInt, new ServoConfiguration(parseInt, this.parser.getAttributeValue(null, "name"), true));
            }
            type = getConfigurationType(this.parser.getName());
        }
        controllerConfiguration = new ServoControllerConfiguration(name, deviceConfigurationList, new SerialNumber(serialNumber));
        controllerConfiguration.setPort(port);
        return controllerConfiguration;
    }

    private ControllerConfiguration parseMotorControllerConfig(boolean useSerialNumber) throws IOException, XmlPullParserException {
        ControllerConfiguration controllerConfiguration;
        DeviceConfigurationList deviceConfigurationList = new DeviceConfigurationList(MOTOR_PORTS, ConfigurationType.MOTOR);

        String attributeValue = this.parser.getAttributeValue(null, "name");
        int port = -1;
        String serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER.toString();

        if (useSerialNumber) {
            serialNumber = this.parser.getAttributeValue(null, "serialNumber");
        } else {
            port = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
        }

        int next;
        ConfigurationType type = getConfigurationType(this.parser.getName());
        while ((next = this.parser.next()) != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (type == null) {
                    continue;
                } else if (type == ConfigurationType.MOTOR_CONTROLLER) {
                    controllerConfiguration = new MotorControllerConfiguration(attributeValue, deviceConfigurationList, new SerialNumber(serialNumber));
                    controllerConfiguration.setPort(port);
                    controllerConfiguration.setEnabled(true);
                    return controllerConfiguration;
                }
            } else if (next == XmlPullParser.START_TAG && type == ConfigurationType.MOTOR) {
                int parseInt = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                deviceConfigurationList.set(parseInt, new MotorConfiguration(parseInt, this.parser.getAttributeValue(null, "name"), true));
            }
            type = getConfigurationType(this.parser.getName());
        }
        controllerConfiguration = new MotorControllerConfiguration(attributeValue, deviceConfigurationList, new SerialNumber(serialNumber));
        controllerConfiguration.setPort(port);
        return controllerConfiguration;
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

    private class DeviceConfigurationList extends ArrayList<DeviceConfiguration> {
        private int ports;
        private ConfigurationType type;
        private int portOffset;

        public DeviceConfigurationList(int ports, ConfigurationType type) {
            super();

            this.type = type;
            this.ports = ports;

            if((type == ConfigurationType.SERVO) || (type == ConfigurationType.MOTOR)) {
                portOffset = 1;
            } else {
                portOffset = 0;
            }

            for (int port = 0; port < ports; port++) {
                this.add(new DeviceConfiguration(port + portOffset, type, DeviceConfiguration.DISABLED_DEVICE_NAME, false));
            }
        }

        public DeviceConfiguration set(DeviceConfiguration deviceConfig) {
            return super.set(deviceConfig.getPort() - portOffset, deviceConfig);
        }
    }

}
