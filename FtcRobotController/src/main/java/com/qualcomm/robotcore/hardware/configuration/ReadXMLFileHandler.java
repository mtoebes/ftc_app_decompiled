package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import static com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants.getConfigurationType;

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
                ConfigurationType type = getConfigurationType(this.parser.getName());
                if (eventType == XmlPullParser.START_TAG) {
                    if (type == ConfigurationType.MOTOR_CONTROLLER) {
                        this.configurations.add(parseMotorControllerConfiguration(true));
                    } else if (type == ConfigurationType.SERVO_CONTROLLER) {
                        this.configurations.add(parseServoControllerConfiguration(true));
                    } else if (type == ConfigurationType.LEGACY_MODULE_CONTROLLER) {
                        this.configurations.add(parseLegacyControllerConfiguration());
                    } else if (type == ConfigurationType.DEVICE_INTERFACE_MODULE) {
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
        ConfigurationType type = getConfigurationType(this.parser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (type == null) {
                    continue;
                } else {
                    if (DEBUG) {
                        RobotLog.e("[handleDeviceInterfaceModule] tagname: " + type.toString());
                    }
                    if (type == ConfigurationType.DEVICE_INTERFACE_MODULE) {
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
                if (type == ConfigurationType.ANALOG_INPUT ||
                        type == ConfigurationType.OPTICAL_DISTANCE_SENSOR) {
                    configuration = parseDeviceConfiguration();
                    analogInputConfigs.set(configuration.getPort(), configuration);
                }
                if (type == ConfigurationType.PULSE_WIDTH_DEVICE) {
                    configuration = parseDeviceConfiguration();
                    pulseWidthDeviceConfigs.set(configuration.getPort(), configuration);
                }
                if (type == ConfigurationType.I2C_DEVICE ||
                        type == ConfigurationType.IR_SEEKER_V3 ||
                        type == ConfigurationType.ADAFRUIT_COLOR_SENSOR ||
                        type == ConfigurationType.COLOR_SENSOR ||
                        type == ConfigurationType.GYRO) {
                    configuration = parseDeviceConfiguration();
                    i2cDeviceConfigs.set(configuration.getPort(), configuration);
                }
                if (type == ConfigurationType.ANALOG_OUTPUT) {
                    configuration = parseDeviceConfiguration();
                    analogOutputConfigs.set(configuration.getPort(), configuration);
                }
                if (type == ConfigurationType.DIGITAL_DEVICE ||
                        type == ConfigurationType.TOUCH_SENSOR ||
                        type == ConfigurationType.LED) {
                    DeviceConfiguration c2 = parseDeviceConfiguration();
                    digitalDeviceConfigs.set(c2.getPort(), c2);
                }
            }
            next = this.parser.next();
            type = getConfigurationType(this.parser.getName());
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
        ConfigurationType type = getConfigurationType(this.parser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (type == null) {
                    continue;
                } else if (type == ConfigurationType.LEGACY_MODULE_CONTROLLER) {
                    controllerConfig = new LegacyModuleControllerConfiguration(controllerName, deviceConfigs, new SerialNumber(serialNumber));
                    controllerConfig.setEnabled(true);
                    return controllerConfig;
                }
            } else if (next == XmlPullParser.START_TAG) {
                if (DEBUG) {
                    RobotLog.e("[handleLegacyModule] tagname: " + type.toString());
                }
                if (DeviceConfiguration.isDeviceConfiguration(type)) {
                    DeviceConfiguration deviceConfig = parseDeviceConfiguration();
                    deviceConfigs.set(deviceConfig.getPort(), deviceConfig);
                } else if(DeviceConfiguration.isControllerConfiguration(type)){
                    if (type == ConfigurationType.MOTOR_CONTROLLER) {
                        controllerConfig = parseMotorControllerConfiguration(false);
                    } else if (type == ConfigurationType.SERVO_CONTROLLER) {
                        controllerConfig = parseServoControllerConfiguration(false);
                    } else if (type == ConfigurationType.MATRIX_CONTROLLER) {
                        controllerConfig = parseMatrixControllerConfiguration();
                    }
                    if(controllerConfig != null) {
                        deviceConfigs.set(controllerConfig.getPort(), controllerConfig);
                    }
                }
            }
            next = this.parser.next();
            type = getConfigurationType(this.parser.getName());
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }

    private DeviceConfiguration parseDeviceConfiguration() {
        ConfigurationType type = getConfigurationType(this.parser.getName());

        DeviceConfiguration deviceConfiguration = new DeviceConfiguration(Integer.parseInt(this.parser.getAttributeValue(null, "port")));
        deviceConfiguration.setType(type);
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
        ConfigurationType type = getConfigurationType(this.parser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (type == null) {
                    continue;
                } else if (type == ConfigurationType.MATRIX_CONTROLLER) {
                    ControllerConfiguration matrixControllerConfiguration = new MatrixControllerConfiguration(controllerName, motorConfigs, servoConfigs, new SerialNumber(serialNumber));
                    matrixControllerConfiguration.setPort(controllerPort);
                    matrixControllerConfiguration.setEnabled(true);
                    return matrixControllerConfiguration;
                }
            } else if (next == XmlPullParser.START_TAG) {
                int devicePort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                String deviceName = this.parser.getAttributeValue(null, "name");
                if (type == ConfigurationType.SERVO) {
                    servoConfigs.set(devicePort - 1, new ServoConfiguration(devicePort, deviceName, true));
                } else if (type == ConfigurationType.MOTOR) {
                    motorConfigs.set(devicePort - 1, new MotorConfiguration(devicePort, deviceName, true));
                }
            }
            next = this.parser.next();
            type = getConfigurationType(this.parser.getName());
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
        ConfigurationType type = getConfigurationType(this.parser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (type == null) {
                    continue;
                } else if (type == ConfigurationType.SERVO_CONTROLLER) {
                    controllerConfig = new ServoControllerConfiguration(controllerName, deviceConfigs, new SerialNumber(serialNumber));
                    controllerConfig.setPort(controllerPort);
                    controllerConfig.setEnabled(true);
                    return controllerConfig;
                }
            } else if (next == XmlPullParser.START_TAG && type == ConfigurationType.SERVO) {
                int devicePort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                String deviceName = this.parser.getAttributeValue(null, "name");
                deviceConfigs.set(devicePort - 1, new ServoConfiguration(devicePort, deviceName, true));
            }
            next = this.parser.next();
            type = getConfigurationType(this.parser.getName());
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
        ConfigurationType type = getConfigurationType(this.parser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (type == null) {
                    continue;
                } else if (type == ConfigurationType.MOTOR_CONTROLLER) {
                    controllerConfig = new MotorControllerConfiguration(controllerName, deviceConfigs, new SerialNumber(serialNumber));
                    controllerConfig.setPort(controllerPort);
                    controllerConfig.setEnabled(true);
                    return controllerConfig;
                }
            } else if (next == XmlPullParser.START_TAG && type == ConfigurationType.MOTOR) {
                int devicePort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                String deviceName = this.parser.getAttributeValue(null, "name");
                deviceConfigs.set(devicePort - 1, new MotorConfiguration(devicePort, deviceName, true));
            }
            next = this.parser.next();
            type = getConfigurationType(this.parser.getName());
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }
}
