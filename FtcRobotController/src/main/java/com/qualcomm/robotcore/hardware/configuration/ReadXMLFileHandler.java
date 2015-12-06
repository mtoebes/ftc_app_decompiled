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
        DeviceConfigurations pulseWidthDeviceConfigs = new DeviceConfigurations(ConfigurationType.PULSE_WIDTH_DEVICE, false);
        DeviceConfigurations i2cDeviceConfigs = new DeviceConfigurations(ConfigurationType.I2C_DEVICE, false);
        DeviceConfigurations analogInputConfigs = new DeviceConfigurations(ConfigurationType.ANALOG_INPUT, false);
        DeviceConfigurations digitalDeviceConfigs = new DeviceConfigurations(ConfigurationType.DIGITAL_DEVICE, false);
        DeviceConfigurations analogOutputConfigs = new DeviceConfigurations(ConfigurationType.ANALOG_OUTPUT, false);
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
                    analogInputConfigs.set(configuration);
                }
                if (type == ConfigurationType.PULSE_WIDTH_DEVICE) {
                    configuration = parseDeviceConfiguration();
                    pulseWidthDeviceConfigs.set(configuration);
                }
                if (type == ConfigurationType.I2C_DEVICE ||
                        type == ConfigurationType.IR_SEEKER_V3 ||
                        type == ConfigurationType.ADAFRUIT_COLOR_SENSOR ||
                        type == ConfigurationType.COLOR_SENSOR ||
                        type == ConfigurationType.GYRO) {
                    configuration = parseDeviceConfiguration();
                    i2cDeviceConfigs.set(configuration);
                }
                if (type == ConfigurationType.ANALOG_OUTPUT) {
                    configuration = parseDeviceConfiguration();
                    analogOutputConfigs.set(configuration);
                }
                if (type == ConfigurationType.DIGITAL_DEVICE ||
                        type == ConfigurationType.TOUCH_SENSOR ||
                        type == ConfigurationType.LED) {
                    DeviceConfiguration c2 = parseDeviceConfiguration();
                    digitalDeviceConfigs.set(c2);
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
        DeviceConfigurations deviceConfigs = new DeviceConfigurations(ConfigurationType.NOTHING, false);
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
                    deviceConfigs.set(deviceConfig);
                } else if(DeviceConfiguration.isControllerConfiguration(type)){
                    if (type == ConfigurationType.MOTOR_CONTROLLER) {
                        controllerConfig = parseMotorControllerConfiguration(false);
                    } else if (type == ConfigurationType.SERVO_CONTROLLER) {
                        controllerConfig = parseServoControllerConfiguration(false);
                    } else if (type == ConfigurationType.MATRIX_CONTROLLER) {
                        controllerConfig = parseMatrixControllerConfiguration();
                    }
                    if(controllerConfig != null) {
                        deviceConfigs.set(controllerConfig);
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

    private ControllerConfiguration parseMatrixControllerConfiguration() throws IOException, XmlPullParserException, RobotCoreException {
        String controllerName = this.parser.getAttributeValue(null, "name");
        String serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER.toString();
        int controllerPort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
        DeviceConfigurations servoConfigs = new DeviceConfigurations(ConfigurationType.SERVO, true);
        DeviceConfigurations motorConfigs = new DeviceConfigurations(ConfigurationType.MOTOR, true);
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
                    servoConfigs.set(new ServoConfiguration(devicePort, deviceName, true));
                } else if (type == ConfigurationType.MOTOR) {
                    motorConfigs.set(new MotorConfiguration(devicePort, deviceName, true));
                }
            }
            next = this.parser.next();
            type = getConfigurationType(this.parser.getName());
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }

    private ControllerConfiguration parseServoControllerConfiguration(boolean enabled) throws IOException, XmlPullParserException, RobotCoreException {
        return parseControllerConfiguration(ConfigurationType.SERVO_CONTROLLER, ConfigurationType.SERVO, enabled);
    }

    private ControllerConfiguration parseMotorControllerConfiguration(boolean enabled) throws IOException, XmlPullParserException, RobotCoreException {
        return parseControllerConfiguration(ConfigurationType.MOTOR_CONTROLLER, ConfigurationType.MOTOR, enabled);
    }

    private ControllerConfiguration parseControllerConfiguration(ConfigurationType controllerType, ConfigurationType deviceType, boolean enabled) throws IOException, XmlPullParserException, RobotCoreException{
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
        DeviceConfigurations deviceConfigs = new DeviceConfigurations(deviceType, false);

        int next = this.parser.next();
        ConfigurationType type = getConfigurationType(this.parser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if (next == XmlPullParser.END_TAG) {
                if (type == null) {
                    continue;
                } else if (type == controllerType) {
                    controllerConfig = new MotorControllerConfiguration(controllerName, deviceConfigs, new SerialNumber(serialNumber));
                    controllerConfig.setPort(controllerPort);
                    controllerConfig.setEnabled(true);
                    return controllerConfig;
                }
            } else if (next == XmlPullParser.START_TAG && type == deviceType) {
                int devicePort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                String deviceName = this.parser.getAttributeValue(null, "name");
                deviceConfigs.set(new MotorConfiguration(devicePort, deviceName, true));
            }
            next = this.parser.next();
            type = getConfigurationType(this.parser.getName());
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }

    private class DeviceConfigurations extends ArrayList<DeviceConfiguration> {
        private int ports;
        private ConfigurationType deviceType;
        private int portOffset;

        public DeviceConfigurations(ConfigurationType deviceType, boolean isMatrix) {
            super();

            this.deviceType = deviceType;
            if(deviceType == ConfigurationType.SERVO || deviceType == ConfigurationType.MOTOR) {
                portOffset = 1;
            } else {
                portOffset = 0;
            }

            this.ports = DeviceConfiguration.getTotalPorts(deviceType, isMatrix);
            initPorts();
        }

        private void initPorts() {
            for (int port = 0; port < ports; port++) {
                this.add(new DeviceConfiguration(port + portOffset, deviceType, DeviceConfiguration.DISABLED_DEVICE_NAME, false));
            }
        }

        public DeviceConfiguration set(DeviceConfiguration deviceConfig) {
            return super.set(deviceConfig.getPort() - portOffset, deviceConfig);
        }
    }
}
