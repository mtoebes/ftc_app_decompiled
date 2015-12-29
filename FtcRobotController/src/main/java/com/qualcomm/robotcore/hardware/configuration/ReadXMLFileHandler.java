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
            while (eventType != 1) {
                String a = getConfigurationType(this.xmlPullParser.getName());
                if (eventType == 2) {
                    if (a.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString())) {
                        this.controllerConfigurations.add(parseMotorControllerConfig(true));
                    }
                    if (a.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString())) {
                        this.controllerConfigurations.add(parseServoControllerConfig(true));
                    }
                    if (a.equalsIgnoreCase(ConfigurationType.LEGACY_MODULE_CONTROLLER.toString())) {
                        this.controllerConfigurations.add(parseLegacyModuleControllerConfig());
                    }
                    if (a.equalsIgnoreCase(ConfigurationType.DEVICE_INTERFACE_MODULE.toString())) {
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
        String attributeValue = this.xmlPullParser.getAttributeValue(null, "name");
        String attributeValue2 = this.xmlPullParser.getAttributeValue(null, "serialNumber");
        ArrayList<DeviceConfiguration> pwdConfigs = createConfigList(PWM_PORTS, ConfigurationType.PULSE_WIDTH_DEVICE);
        ArrayList<DeviceConfiguration> i2CDeviceConfigs = createConfigList(I2C_PORTS, ConfigurationType.I2C_DEVICE);
        ArrayList<DeviceConfiguration> analogInputDeviceConfigs = createConfigList(ANALOG_INPUT_PORTS, ConfigurationType.ANALOG_INPUT);
        ArrayList<DeviceConfiguration> digitalDeviceConfigs = createConfigList(DIGITAL_PORTS, ConfigurationType.DIGITAL_DEVICE);
        ArrayList<DeviceConfiguration> analogOutputDeviceConfigs = createConfigList(ANALOG_OUTPUT_PORTS, ConfigurationType.ANALOG_OUTPUT);
        int next = this.xmlPullParser.next();
        String a6 = getConfigurationType(this.xmlPullParser.getName());
        while (next != 1) {
            if (next == 3) {
                if (a6 == null) {
                    continue;
                } else {
                    if (DEBUG) {
                        RobotLog.e("[handleDeviceInterfaceModule] tagname: " + a6);
                    }
                    if (a6.equalsIgnoreCase(ConfigurationType.DEVICE_INTERFACE_MODULE.toString())) {
                        DeviceInterfaceModuleConfiguration deviceInterfaceModuleConfiguration = new DeviceInterfaceModuleConfiguration(attributeValue, new SerialNumber(attributeValue2));
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
            if (next == 2) {
                DeviceConfiguration c;
                if (a6.equalsIgnoreCase(ConfigurationType.ANALOG_INPUT.toString()) || a6.equalsIgnoreCase(ConfigurationType.OPTICAL_DISTANCE_SENSOR.toString())) {
                    c = parseDeviceConfig();
                    analogInputDeviceConfigs.set(c.getPort(), c);
                }
                if (a6.equalsIgnoreCase(ConfigurationType.PULSE_WIDTH_DEVICE.toString())) {
                    c = parseDeviceConfig();
                    pwdConfigs.set(c.getPort(), c);
                }
                if (a6.equalsIgnoreCase(ConfigurationType.I2C_DEVICE.toString()) || a6.equalsIgnoreCase(ConfigurationType.IR_SEEKER_V3.toString()) || a6.equalsIgnoreCase(ConfigurationType.ADAFRUIT_COLOR_SENSOR.toString()) || a6.equalsIgnoreCase(ConfigurationType.COLOR_SENSOR.toString()) || a6.equalsIgnoreCase(ConfigurationType.GYRO.toString())) {
                    c = parseDeviceConfig();
                    i2CDeviceConfigs.set(c.getPort(), c);
                }
                if (a6.equalsIgnoreCase(ConfigurationType.ANALOG_OUTPUT.toString())) {
                    c = parseDeviceConfig();
                    analogOutputDeviceConfigs.set(c.getPort(), c);
                }
                if (a6.equalsIgnoreCase(ConfigurationType.DIGITAL_DEVICE.toString()) || a6.equalsIgnoreCase(ConfigurationType.TOUCH_SENSOR.toString()) || a6.equalsIgnoreCase(ConfigurationType.LED.toString())) {
                    DeviceConfiguration c2 = parseDeviceConfig();
                    digitalDeviceConfigs.set(c2.getPort(), c2);
                }
            }
            next = this.xmlPullParser.next();
            a6 = getConfigurationType(this.xmlPullParser.getName());
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }

    private ControllerConfiguration parseLegacyModuleControllerConfig() throws IOException, XmlPullParserException, RobotCoreException {
        String attributeValue = this.xmlPullParser.getAttributeValue(null, "name");
        String attributeValue2 = this.xmlPullParser.getAttributeValue(null, "serialNumber");
        List a = createConfigList(LEGACY_MODULE_PORTS, ConfigurationType.NOTHING);
        int next = this.xmlPullParser.next();
        String a2 = getConfigurationType(this.xmlPullParser.getName());
        ControllerConfiguration legacyModuleControllerConfiguration;
        while (next != 1) {
            if (next == 3) {
                if (a2 == null) {
                    continue;
                } else if (a2.equalsIgnoreCase(ConfigurationType.LEGACY_MODULE_CONTROLLER.toString())) {
                    legacyModuleControllerConfiguration = new LegacyModuleControllerConfiguration(attributeValue, a, new SerialNumber(attributeValue2));
                    legacyModuleControllerConfiguration.setEnabled(true);
                    return legacyModuleControllerConfiguration;
                }
            }
            if (next == 2) {
                if (DEBUG) {
                    RobotLog.e("[handleLegacyModule] tagname: " + a2);
                }
                if (a2.equalsIgnoreCase(ConfigurationType.COMPASS.toString()) || a2.equalsIgnoreCase(ConfigurationType.LIGHT_SENSOR.toString()) || a2.equalsIgnoreCase(ConfigurationType.IR_SEEKER.toString()) || a2.equalsIgnoreCase(ConfigurationType.ACCELEROMETER.toString()) || a2.equalsIgnoreCase(ConfigurationType.GYRO.toString()) || a2.equalsIgnoreCase(ConfigurationType.TOUCH_SENSOR.toString()) || a2.equalsIgnoreCase(ConfigurationType.TOUCH_SENSOR_MULTIPLEXER.toString()) || a2.equalsIgnoreCase(ConfigurationType.ULTRASONIC_SENSOR.toString()) || a2.equalsIgnoreCase(ConfigurationType.COLOR_SENSOR.toString()) || a2.equalsIgnoreCase(ConfigurationType.NOTHING.toString())) {
                    DeviceConfiguration c = parseDeviceConfig();
                    a.set(c.getPort(), c);
                } else if (a2.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString())) {
                    legacyModuleControllerConfiguration = parseMotorControllerConfig(false);
                    a.set(legacyModuleControllerConfiguration.getPort(), legacyModuleControllerConfiguration);
                } else if (a2.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString())) {
                    legacyModuleControllerConfiguration = parseServoControllerConfig(false);
                    a.set(legacyModuleControllerConfiguration.getPort(), legacyModuleControllerConfiguration);
                } else if (a2.equalsIgnoreCase(ConfigurationType.MATRIX_CONTROLLER.toString())) {
                    legacyModuleControllerConfiguration = parseMatrixControllerConfig();
                    a.set(legacyModuleControllerConfiguration.getPort(), legacyModuleControllerConfiguration);
                }
            }
            next = this.xmlPullParser.next();
            a2 = getConfigurationType(this.xmlPullParser.getName());
        }
        return new LegacyModuleControllerConfiguration(attributeValue, a, new SerialNumber(attributeValue2));
    }

    private DeviceConfiguration parseDeviceConfig() {
        String a = getConfigurationType(this.xmlPullParser.getName());
        DeviceConfiguration deviceConfiguration = new DeviceConfiguration(Integer.parseInt(this.xmlPullParser.getAttributeValue(null, "port")));
        deviceConfiguration.setType(deviceConfiguration.typeFromString(a));
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
        String a3 = getConfigurationType(this.xmlPullParser.getName());
        while (next != 1) {
            if (next == 3) {
                if (a3 == null) {
                    continue;
                } else if (a3.equalsIgnoreCase(ConfigurationType.MATRIX_CONTROLLER.toString())) {
                    ControllerConfiguration matrixControllerConfiguration = new MatrixControllerConfiguration(attributeValue, a2, a, new SerialNumber(serialNumber));
                    matrixControllerConfiguration.setPort(parseInt);
                    matrixControllerConfiguration.setEnabled(true);
                    return matrixControllerConfiguration;
                }
            }
            if (next == 2) {
                int parseInt2;
                if (a3.equalsIgnoreCase(ConfigurationType.SERVO.toString())) {
                    parseInt2 = Integer.parseInt(this.xmlPullParser.getAttributeValue(null, "port"));
                    a.set(parseInt2 - 1, new ServoConfiguration(parseInt2, this.xmlPullParser.getAttributeValue(null, "name"), true));
                } else if (a3.equalsIgnoreCase(ConfigurationType.MOTOR.toString())) {
                    parseInt2 = Integer.parseInt(this.xmlPullParser.getAttributeValue(null, "port"));
                    a2.set(parseInt2 - 1, new MotorConfiguration(parseInt2, this.xmlPullParser.getAttributeValue(null, "name"), true));
                }
            }
            next = this.xmlPullParser.next();
            a3 = getConfigurationType(this.xmlPullParser.getName());
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }

    private ControllerConfiguration parseServoControllerConfig(boolean useSerialNumber) throws IOException, XmlPullParserException {
        ControllerConfiguration servoControllerConfiguration;
        String attributeValue = this.xmlPullParser.getAttributeValue(null, "name");
        int i = -1;
        String serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER.toString();
        if (useSerialNumber) {
            serialNumber = this.xmlPullParser.getAttributeValue(null, "serialNumber");
        } else {
            i = Integer.parseInt(this.xmlPullParser.getAttributeValue(null, "port"));
        }
        List a = createConfigList(SERVO_PORTS, ConfigurationType.SERVO);
        int next = this.xmlPullParser.next();
        String a2 = getConfigurationType(this.xmlPullParser.getName());
        while (next != 1) {
            if (next == 3) {
                if (a2 == null) {
                    continue;
                } else if (a2.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString())) {
                    servoControllerConfiguration = new ServoControllerConfiguration(attributeValue, a, new SerialNumber(serialNumber));
                    servoControllerConfiguration.setPort(i);
                    servoControllerConfiguration.setEnabled(true);
                    return servoControllerConfiguration;
                }
            }
            if (next == 2 && a2.equalsIgnoreCase(ConfigurationType.SERVO.toString())) {
                int parseInt = Integer.parseInt(this.xmlPullParser.getAttributeValue(null, "port"));
                a.set(parseInt - 1, new ServoConfiguration(parseInt, this.xmlPullParser.getAttributeValue(null, "name"), true));
            }
            next = this.xmlPullParser.next();
            a2 = getConfigurationType(this.xmlPullParser.getName());
        }
        servoControllerConfiguration = new ServoControllerConfiguration(attributeValue, a, new SerialNumber(serialNumber));
        servoControllerConfiguration.setPort(i);
        return servoControllerConfiguration;
    }

    private ControllerConfiguration parseMotorControllerConfig(boolean useSerialNumber) throws IOException, XmlPullParserException {
        ControllerConfiguration motorControllerConfiguration;
        String attributeValue = this.xmlPullParser.getAttributeValue(null, "name");
        int i = -1;
        String serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER.toString();
        if (useSerialNumber) {
            serialNumber = this.xmlPullParser.getAttributeValue(null, "serialNumber");
        } else {
            i = Integer.parseInt(this.xmlPullParser.getAttributeValue(null, "port"));
        }
        List a = createConfigList(MOTOR_PORTS, ConfigurationType.MOTOR);
        int next = this.xmlPullParser.next();
        String a2 = getConfigurationType(this.xmlPullParser.getName());
        while (next != 1) {
            if (next == 3) {
                if (a2 == null) {
                    continue;
                } else if (a2.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString())) {
                    motorControllerConfiguration = new MotorControllerConfiguration(attributeValue, a, new SerialNumber(serialNumber));
                    motorControllerConfiguration.setPort(i);
                    motorControllerConfiguration.setEnabled(true);
                    return motorControllerConfiguration;
                }
            }
            if (next == 2 && a2.equalsIgnoreCase(ConfigurationType.MOTOR.toString())) {
                int parseInt = Integer.parseInt(this.xmlPullParser.getAttributeValue(null, "port"));
                a.set(parseInt - 1, new MotorConfiguration(parseInt, this.xmlPullParser.getAttributeValue(null, "name"), true));
            }
            next = this.xmlPullParser.next();
            a2 = getConfigurationType(this.xmlPullParser.getName());
        }
        motorControllerConfiguration = new MotorControllerConfiguration(attributeValue, a, new SerialNumber(serialNumber));
        motorControllerConfiguration.setPort(i);
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
