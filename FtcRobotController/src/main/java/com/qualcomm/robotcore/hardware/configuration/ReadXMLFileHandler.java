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

            ConfigurationType type;
            int next;
            while ((next = this.parser.next()) != XmlPullParser.END_DOCUMENT) {
                type = getConfigurationType(this.parser.getName());
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
        ConfigurationType controllerType = ConfigurationType.DEVICE_INTERFACE_MODULE;

        DeviceInterfaceModuleConfiguration deviceInterfaceModuleConfiguration;
        DeviceConfigurationList pwdConfigs = new DeviceConfigurationList(ConfigurationType.PULSE_WIDTH_DEVICE, controllerType);
        DeviceConfigurationList i2CDeviceConfigs = new DeviceConfigurationList(ConfigurationType.I2C_DEVICE, controllerType);
        DeviceConfigurationList analogInputDeviceConfigs = new DeviceConfigurationList(ConfigurationType.ANALOG_INPUT, controllerType);
        DeviceConfigurationList digitalDeviceConfigs = new DeviceConfigurationList(ConfigurationType.DIGITAL_DEVICE, controllerType);
        DeviceConfigurationList analogOutputDeviceConfigs = new DeviceConfigurationList(ConfigurationType.ANALOG_OUTPUT, controllerType);

        String name = this.parser.getAttributeValue(null, "name");
        SerialNumber serialNumber = new SerialNumber(this.parser.getAttributeValue(null, "serialNumber"));

        ConfigurationType type;
        int next;
        while ((next = this.parser.next()) != XmlPullParser.END_DOCUMENT) {
            type = getConfigurationType(this.parser.getName());
            if (type != null) {
                if (next == XmlPullParser.END_TAG) {
                    if (type == ConfigurationType.DEVICE_INTERFACE_MODULE) {
                        deviceInterfaceModuleConfiguration = new DeviceInterfaceModuleConfiguration(name, serialNumber);
                        deviceInterfaceModuleConfiguration.setPwmDevices(pwdConfigs);
                        deviceInterfaceModuleConfiguration.setI2cDevices(i2CDeviceConfigs);
                        deviceInterfaceModuleConfiguration.setAnalogInputDevices(analogInputDeviceConfigs);
                        deviceInterfaceModuleConfiguration.setDigitalDevices(digitalDeviceConfigs);
                        deviceInterfaceModuleConfiguration.setAnalogOutputDevices(analogOutputDeviceConfigs);
                        deviceInterfaceModuleConfiguration.setEnabled(true);
                        return deviceInterfaceModuleConfiguration;
                    }
                } else if (next == XmlPullParser.START_TAG) {
                    DeviceConfiguration deviceConfiguration = null;
                    if (type == ConfigurationType.ANALOG_INPUT ||
                            type == ConfigurationType.OPTICAL_DISTANCE_SENSOR) {
                        deviceConfiguration = parseDeviceConfig();
                        analogInputDeviceConfigs.set(deviceConfiguration.getPort(), deviceConfiguration);
                    } else if (type == ConfigurationType.PULSE_WIDTH_DEVICE) {
                        deviceConfiguration = parseDeviceConfig();
                        pwdConfigs.set(deviceConfiguration.getPort(), deviceConfiguration);
                    } else if (type == ConfigurationType.I2C_DEVICE ||
                            type == ConfigurationType.IR_SEEKER_V3 ||
                            type == ConfigurationType.ADAFRUIT_COLOR_SENSOR ||
                            type == ConfigurationType.COLOR_SENSOR ||
                            type == ConfigurationType.GYRO) {
                        deviceConfiguration = parseDeviceConfig();
                        i2CDeviceConfigs.set(deviceConfiguration.getPort(), deviceConfiguration);
                    } else if (type == ConfigurationType.ANALOG_OUTPUT) {
                        deviceConfiguration = parseDeviceConfig();
                        analogOutputDeviceConfigs.set(deviceConfiguration.getPort(), deviceConfiguration);
                    } else if (type == ConfigurationType.DIGITAL_DEVICE ||
                            type == ConfigurationType.TOUCH_SENSOR ||
                            type == ConfigurationType.LED) {
                        deviceConfiguration = parseDeviceConfig();
                        digitalDeviceConfigs.set(deviceConfiguration.getPort(), deviceConfiguration);
                    }
                }
            }
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }

    private ControllerConfiguration parseLegacyModuleControllerConfig() throws IOException, XmlPullParserException, RobotCoreException {
        ConfigurationType controllerType = ConfigurationType.LEGACY_MODULE_CONTROLLER;

        ControllerConfiguration controllerConfiguration;
        DeviceConfigurationList deviceConfigurationList = new DeviceConfigurationList(ConfigurationType.NOTHING, controllerType);

        String name = this.parser.getAttributeValue(null, "name");
        SerialNumber serialNumber = new SerialNumber(this.parser.getAttributeValue(null, "serialNumber"));

        ConfigurationType type;
        int next;
        while ((next = this.parser.next()) != XmlPullParser.END_DOCUMENT) {
            type = getConfigurationType(this.parser.getName());
            if (type != null) {
                if (next == XmlPullParser.END_TAG) {
                    if (type == ConfigurationType.LEGACY_MODULE_CONTROLLER) {
                        controllerConfiguration = new LegacyModuleControllerConfiguration(name, deviceConfigurationList, serialNumber);
                        controllerConfiguration.setEnabled(true);
                        return controllerConfiguration;
                    }
                } else if (next == XmlPullParser.START_TAG) {
                    DeviceConfiguration deviceConfiguration = null;
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
                        deviceConfiguration = parseDeviceConfig();
                    } else if (type == ConfigurationType.MOTOR_CONTROLLER) {
                        deviceConfiguration = parseMotorControllerConfig(false);
                    } else if (type == ConfigurationType.SERVO_CONTROLLER) {
                        deviceConfiguration = parseServoControllerConfig(false);
                    } else if (type == ConfigurationType.MATRIX_CONTROLLER) {
                        deviceConfiguration = parseMatrixControllerConfig();
                    }

                    if(deviceConfiguration != null) {
                        deviceConfigurationList.set(deviceConfiguration.getPort(), deviceConfiguration);
                    }
                }
            }
        }
        return new LegacyModuleControllerConfiguration(name, deviceConfigurationList, serialNumber);
    }

    private ControllerConfiguration parseMatrixControllerConfig() throws IOException, XmlPullParserException, RobotCoreException {
        ConfigurationType controllerType = ConfigurationType.MATRIX_CONTROLLER;

        ControllerConfiguration controllerConfiguration;
        DeviceConfigurationList servoConfigurationList = new DeviceConfigurationList(ConfigurationType.SERVO, controllerType);
        DeviceConfigurationList motorConfigurationList = new DeviceConfigurationList(ConfigurationType.MOTOR, controllerType);

        String name = this.parser.getAttributeValue(null, "name");
        String serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER.toString();
        int port = Integer.parseInt(this.parser.getAttributeValue(null, "port"));

        int next;
        ConfigurationType type;
        while ((next = this.parser.next()) != XmlPullParser.END_DOCUMENT) {
            type = getConfigurationType(this.parser.getName());
            if (type != null) {
                if (next == XmlPullParser.END_TAG) {
                    if (type == ConfigurationType.MATRIX_CONTROLLER) {
                        ControllerConfiguration matrixControllerConfiguration = new MatrixControllerConfiguration(name, motorConfigurationList, servoConfigurationList, new SerialNumber(serialNumber));
                        matrixControllerConfiguration.setPort(port);
                        matrixControllerConfiguration.setEnabled(true);
                        return matrixControllerConfiguration;
                    }
                } else if (next == XmlPullParser.START_TAG) {
                    if ((type == ConfigurationType.SERVO) || (type == ConfigurationType.MOTOR)) {
                        int devicePort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                        String deviceName = this.parser.getAttributeValue(null, "name");

                        DeviceConfiguration deviceConfiguration = new DeviceConfiguration(devicePort, type, deviceName, true);

                        if (type == ConfigurationType.SERVO) {
                            servoConfigurationList.set(devicePort, deviceConfiguration);
                        } else {
                            motorConfigurationList.set(devicePort, deviceConfiguration);
                        }
                    }
                }
            }
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }

    private ControllerConfiguration parseServoControllerConfig(boolean useSerialNumber) throws IOException, XmlPullParserException {
        int controllerPorts = SERVO_PORTS;
        ConfigurationType controllerType = ConfigurationType.SERVO_CONTROLLER;
        ConfigurationType deviceType = ConfigurationType.SERVO;
        return parseControllerConfig(controllerPorts, controllerType, deviceType, useSerialNumber);
    }

    private ControllerConfiguration parseMotorControllerConfig(boolean useSerialNumber) throws IOException, XmlPullParserException {
        int controllerPorts = MOTOR_PORTS;
        ConfigurationType controllerType = ConfigurationType.MOTOR_CONTROLLER;
        ConfigurationType deviceType = ConfigurationType.MOTOR;
        return parseControllerConfig(controllerPorts, controllerType, deviceType, useSerialNumber);
    }

    private ControllerConfiguration parseControllerConfig(int controllerPorts, ConfigurationType controllerType, ConfigurationType deviceType, boolean useSerialNumber) throws IOException, XmlPullParserException {
        ControllerConfiguration controllerConfiguration;
        DeviceConfigurationList deviceConfigurationList = new DeviceConfigurationList(deviceType, controllerType);

        String attributeValue = this.parser.getAttributeValue(null, "name");
        int port = -1;
        SerialNumber serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER;

        if (useSerialNumber) {
            serialNumber = new SerialNumber(this.parser.getAttributeValue(null, "serialNumber"));
        } else {
            port = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
        }

        int next;
        ConfigurationType type;
        while ((next = this.parser.next()) != XmlPullParser.END_DOCUMENT) {
            type = getConfigurationType(this.parser.getName());
            if (type != null) {
                if (next == XmlPullParser.END_TAG) {
                    if (type == controllerType) {
                        controllerConfiguration = new ControllerConfiguration(attributeValue, deviceConfigurationList, serialNumber, controllerType);
                        controllerConfiguration.setPort(port);
                        controllerConfiguration.setEnabled(true);
                        return controllerConfiguration;
                    }
                } else if (next == XmlPullParser.START_TAG && type == deviceType) {
                    int devicePort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                    String deviceName = this.parser.getAttributeValue(null, "name");
                    DeviceConfiguration deviceConfiguration = new DeviceConfiguration(devicePort, type, deviceName, true);
                    deviceConfigurationList.set(devicePort, deviceConfiguration);
                }
            }
        }
        controllerConfiguration = new ControllerConfiguration(attributeValue, deviceConfigurationList, serialNumber, controllerType);
        controllerConfiguration.setPort(port);
        return controllerConfiguration;
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
        private ConfigurationType deviceType;
        private int portOffset;

        public DeviceConfigurationList(ConfigurationType deviceType, ConfigurationType controllerType) {
            super();
            this.deviceType = deviceType;
            this.ports = getPorts(controllerType);
            this.portOffset = getPortOffset();
            createList();
        }

        public DeviceConfiguration set(DeviceConfiguration deviceConfig) {
            return super.set(deviceConfig.getPort() - portOffset, deviceConfig);
        }

        private int getPorts(ConfigurationType controllerType) {
            switch (deviceType) {
                case PULSE_WIDTH_DEVICE:
                    return PWM_PORTS;
                case I2C_DEVICE:
                    return  I2C_PORTS;
                case ANALOG_INPUT:
                    return ANALOG_INPUT_PORTS;
                case DIGITAL_DEVICE:
                    return DIGITAL_PORTS;
                case ANALOG_OUTPUT:
                    return ANALOG_OUTPUT_PORTS;
                case NOTHING:
                    return LEGACY_MODULE_PORTS;
                case MOTOR:
                    if(controllerType == ConfigurationType.MATRIX_CONTROLLER) {
                        return MATRIX_MOTOR_PORTS;
                    } else {
                        return MOTOR_PORTS;
                    }
                case SERVO:
                    if(controllerType == ConfigurationType.MATRIX_CONTROLLER) {
                        return MATRIX_MOTOR_PORTS;
                    } else {
                        return MOTOR_PORTS;
                    }
                default:
                    return 0;
            }
        }

        private int getPortOffset() {
            if((deviceType == ConfigurationType.SERVO) || (deviceType == ConfigurationType.MOTOR)) {
                return 1;
            } else {
                return 0;
            }
        }

        private void createList() {
            for (int port = 0; port < ports; port++) {
                this.add(new DeviceConfiguration(port + portOffset, deviceType, DeviceConfiguration.DISABLED_DEVICE_NAME, false));
            }
        }
    }

}
