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

public class ReadXMLFileHandler {
    private static boolean f275b;
    private static int f276c;
    private static int f277d;
    private static int f278e;
    private static int f279f;
    private static int f280g;
    private static int f281h;
    private static int f282i;
    private static int f283j;
    private static int f284k;
    private static int f285l;
    List<ControllerConfiguration> f286a;
    private XmlPullParser f287m;

    static {
        f275b = false;
        f276c = 2;
        f277d = 8;
        f278e = 8;
        f279f = 2;
        f280g = 6;
        f281h = 6;
        f282i = 6;
        f283j = 2;
        f284k = 4;
        f285l = 4;
    }

    public ReadXMLFileHandler(Context context) {
        this.f286a = new ArrayList();
    }

    public List<ControllerConfiguration> getDeviceControllers() {
        return this.f286a;
    }

    public List<ControllerConfiguration> parse(InputStream is) throws RobotCoreException {
        this.f287m = null;
        try {
            XmlPullParserFactory newInstance = XmlPullParserFactory.newInstance();
            newInstance.setNamespaceAware(true);
            this.f287m = newInstance.newPullParser();
            this.f287m.setInput(is, null);
            int eventType = this.f287m.getEventType();
            while (eventType != 1) {
                String a = m194a(this.f287m.getName());
                if (eventType == 2) {
                    if (a.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString())) {
                        this.f286a.add(m197b(true));
                    }
                    if (a.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString())) {
                        this.f286a.add(m193a(true));
                    }
                    if (a.equalsIgnoreCase(ConfigurationType.LEGACY_MODULE_CONTROLLER.toString())) {
                        this.f286a.add(m196b());
                    }
                    if (a.equalsIgnoreCase(ConfigurationType.DEVICE_INTERFACE_MODULE.toString())) {
                        this.f286a.add(m192a());
                    }
                }
                eventType = this.f287m.next();
            }
        } catch (XmlPullParserException e) {
            RobotLog.w("XmlPullParserException");
            e.printStackTrace();
        } catch (IOException e2) {
            RobotLog.w("IOException");
            e2.printStackTrace();
        }
        return this.f286a;
    }

    private ControllerConfiguration m192a() throws IOException, XmlPullParserException, RobotCoreException {
        String attributeValue = this.f287m.getAttributeValue(null, "name");
        String attributeValue2 = this.f287m.getAttributeValue(null, "serialNumber");
        Object a = m195a(f276c, ConfigurationType.PULSE_WIDTH_DEVICE);
        Object a2 = m195a(f280g, ConfigurationType.I2C_DEVICE);
        Object a3 = m195a(f278e, ConfigurationType.ANALOG_INPUT);
        Object a4 = m195a(f277d, ConfigurationType.DIGITAL_DEVICE);
        Object a5 = m195a(f279f, ConfigurationType.ANALOG_OUTPUT);
        int next = this.f287m.next();
        String a6 = m194a(this.f287m.getName());
        while (next != 1) {
            if (next == 3) {
                if (a6 == null) {
                    continue;
                } else {
                    if (f275b) {
                        RobotLog.e("[handleDeviceInterfaceModule] tagname: " + a6);
                    }
                    if (a6.equalsIgnoreCase(ConfigurationType.DEVICE_INTERFACE_MODULE.toString())) {
                        ControllerConfiguration deviceInterfaceModuleConfiguration = new DeviceInterfaceModuleConfiguration(attributeValue, new SerialNumber(attributeValue2));
                        deviceInterfaceModuleConfiguration.setPwmDevices(a);
                        deviceInterfaceModuleConfiguration.setI2cDevices(a2);
                        deviceInterfaceModuleConfiguration.setAnalogInputDevices(a3);
                        deviceInterfaceModuleConfiguration.setDigitalDevices(a4);
                        deviceInterfaceModuleConfiguration.setAnalogOutputDevices(a5);
                        deviceInterfaceModuleConfiguration.setEnabled(true);
                        return deviceInterfaceModuleConfiguration;
                    }
                }
            }
            if (next == 2) {
                DeviceConfiguration c;
                if (a6.equalsIgnoreCase(ConfigurationType.ANALOG_INPUT.toString()) || a6.equalsIgnoreCase(ConfigurationType.OPTICAL_DISTANCE_SENSOR.toString())) {
                    c = m198c();
                    a3.set(c.getPort(), c);
                }
                if (a6.equalsIgnoreCase(ConfigurationType.PULSE_WIDTH_DEVICE.toString())) {
                    c = m198c();
                    a.set(c.getPort(), c);
                }
                if (a6.equalsIgnoreCase(ConfigurationType.I2C_DEVICE.toString()) || a6.equalsIgnoreCase(ConfigurationType.IR_SEEKER_V3.toString()) || a6.equalsIgnoreCase(ConfigurationType.ADAFRUIT_COLOR_SENSOR.toString()) || a6.equalsIgnoreCase(ConfigurationType.COLOR_SENSOR.toString()) || a6.equalsIgnoreCase(ConfigurationType.GYRO.toString())) {
                    c = m198c();
                    a2.set(c.getPort(), c);
                }
                if (a6.equalsIgnoreCase(ConfigurationType.ANALOG_OUTPUT.toString())) {
                    c = m198c();
                    a5.set(c.getPort(), c);
                }
                if (a6.equalsIgnoreCase(ConfigurationType.DIGITAL_DEVICE.toString()) || a6.equalsIgnoreCase(ConfigurationType.TOUCH_SENSOR.toString()) || a6.equalsIgnoreCase(ConfigurationType.LED.toString())) {
                    DeviceConfiguration c2 = m198c();
                    a4.set(c2.getPort(), c2);
                }
            }
            next = this.f287m.next();
            a6 = m194a(this.f287m.getName());
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }

    private ControllerConfiguration m196b() throws IOException, XmlPullParserException, RobotCoreException {
        String attributeValue = this.f287m.getAttributeValue(null, "name");
        String attributeValue2 = this.f287m.getAttributeValue(null, "serialNumber");
        List a = m195a(f281h, ConfigurationType.NOTHING);
        int next = this.f287m.next();
        String a2 = m194a(this.f287m.getName());
        while (next != 1) {
            if (next == 3) {
                if (a2 == null) {
                    continue;
                } else if (a2.equalsIgnoreCase(ConfigurationType.LEGACY_MODULE_CONTROLLER.toString())) {
                    ControllerConfiguration legacyModuleControllerConfiguration = new LegacyModuleControllerConfiguration(attributeValue, a, new SerialNumber(attributeValue2));
                    legacyModuleControllerConfiguration.setEnabled(true);
                    return legacyModuleControllerConfiguration;
                }
            }
            if (next == 2) {
                if (f275b) {
                    RobotLog.e("[handleLegacyModule] tagname: " + a2);
                }
                if (a2.equalsIgnoreCase(ConfigurationType.COMPASS.toString()) || a2.equalsIgnoreCase(ConfigurationType.LIGHT_SENSOR.toString()) || a2.equalsIgnoreCase(ConfigurationType.IR_SEEKER.toString()) || a2.equalsIgnoreCase(ConfigurationType.ACCELEROMETER.toString()) || a2.equalsIgnoreCase(ConfigurationType.GYRO.toString()) || a2.equalsIgnoreCase(ConfigurationType.TOUCH_SENSOR.toString()) || a2.equalsIgnoreCase(ConfigurationType.TOUCH_SENSOR_MULTIPLEXER.toString()) || a2.equalsIgnoreCase(ConfigurationType.ULTRASONIC_SENSOR.toString()) || a2.equalsIgnoreCase(ConfigurationType.COLOR_SENSOR.toString()) || a2.equalsIgnoreCase(ConfigurationType.NOTHING.toString())) {
                    DeviceConfiguration c = m198c();
                    a.set(c.getPort(), c);
                } else if (a2.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString())) {
                    legacyModuleControllerConfiguration = m197b(false);
                    a.set(legacyModuleControllerConfiguration.getPort(), legacyModuleControllerConfiguration);
                } else if (a2.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString())) {
                    legacyModuleControllerConfiguration = m193a(false);
                    a.set(legacyModuleControllerConfiguration.getPort(), legacyModuleControllerConfiguration);
                } else if (a2.equalsIgnoreCase(ConfigurationType.MATRIX_CONTROLLER.toString())) {
                    legacyModuleControllerConfiguration = m199d();
                    a.set(legacyModuleControllerConfiguration.getPort(), legacyModuleControllerConfiguration);
                }
            }
            next = this.f287m.next();
            a2 = m194a(this.f287m.getName());
        }
        return new LegacyModuleControllerConfiguration(attributeValue, a, new SerialNumber(attributeValue2));
    }

    private DeviceConfiguration m198c() {
        String a = m194a(this.f287m.getName());
        DeviceConfiguration deviceConfiguration = new DeviceConfiguration(Integer.parseInt(this.f287m.getAttributeValue(null, "port")));
        deviceConfiguration.setType(deviceConfiguration.typeFromString(a));
        deviceConfiguration.setName(this.f287m.getAttributeValue(null, "name"));
        if (!deviceConfiguration.getName().equalsIgnoreCase(DeviceConfiguration.DISABLED_DEVICE_NAME)) {
            deviceConfiguration.setEnabled(true);
        }
        if (f275b) {
            RobotLog.e("[handleDevice] name: " + deviceConfiguration.getName() + ", port: " + deviceConfiguration.getPort() + ", type: " + deviceConfiguration.getType());
        }
        return deviceConfiguration;
    }

    private ArrayList<DeviceConfiguration> m195a(int i, ConfigurationType configurationType) {
        ArrayList<DeviceConfiguration> arrayList = new ArrayList();
        for (int i2 = 0; i2 < i; i2++) {
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

    private ControllerConfiguration m199d() throws IOException, XmlPullParserException, RobotCoreException {
        String attributeValue = this.f287m.getAttributeValue(null, "name");
        String serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER.toString();
        int parseInt = Integer.parseInt(this.f287m.getAttributeValue(null, "port"));
        Object a = m195a(f285l, ConfigurationType.SERVO);
        Object a2 = m195a(f284k, ConfigurationType.MOTOR);
        int next = this.f287m.next();
        String a3 = m194a(this.f287m.getName());
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
                    parseInt2 = Integer.parseInt(this.f287m.getAttributeValue(null, "port"));
                    a.set(parseInt2 - 1, new ServoConfiguration(parseInt2, this.f287m.getAttributeValue(null, "name"), true));
                } else if (a3.equalsIgnoreCase(ConfigurationType.MOTOR.toString())) {
                    parseInt2 = Integer.parseInt(this.f287m.getAttributeValue(null, "port"));
                    a2.set(parseInt2 - 1, new MotorConfiguration(parseInt2, this.f287m.getAttributeValue(null, "name"), true));
                }
            }
            next = this.f287m.next();
            a3 = m194a(this.f287m.getName());
        }
        RobotLog.logAndThrow("Reached the end of the XML file while parsing.");
        return null;
    }

    private ControllerConfiguration m193a(boolean z) throws IOException, XmlPullParserException {
        ControllerConfiguration servoControllerConfiguration;
        String attributeValue = this.f287m.getAttributeValue(null, "name");
        int i = -1;
        String serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER.toString();
        if (z) {
            serialNumber = this.f287m.getAttributeValue(null, "serialNumber");
        } else {
            i = Integer.parseInt(this.f287m.getAttributeValue(null, "port"));
        }
        List a = m195a(f282i, ConfigurationType.SERVO);
        int next = this.f287m.next();
        String a2 = m194a(this.f287m.getName());
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
                int parseInt = Integer.parseInt(this.f287m.getAttributeValue(null, "port"));
                a.set(parseInt - 1, new ServoConfiguration(parseInt, this.f287m.getAttributeValue(null, "name"), true));
            }
            next = this.f287m.next();
            a2 = m194a(this.f287m.getName());
        }
        servoControllerConfiguration = new ServoControllerConfiguration(attributeValue, a, new SerialNumber(serialNumber));
        servoControllerConfiguration.setPort(i);
        return servoControllerConfiguration;
    }

    private ControllerConfiguration m197b(boolean z) throws IOException, XmlPullParserException {
        ControllerConfiguration motorControllerConfiguration;
        String attributeValue = this.f287m.getAttributeValue(null, "name");
        int i = -1;
        String serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER.toString();
        if (z) {
            serialNumber = this.f287m.getAttributeValue(null, "serialNumber");
        } else {
            i = Integer.parseInt(this.f287m.getAttributeValue(null, "port"));
        }
        List a = m195a(f283j, ConfigurationType.MOTOR);
        int next = this.f287m.next();
        String a2 = m194a(this.f287m.getName());
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
                int parseInt = Integer.parseInt(this.f287m.getAttributeValue(null, "port"));
                a.set(parseInt - 1, new MotorConfiguration(parseInt, this.f287m.getAttributeValue(null, "name"), true));
            }
            next = this.f287m.next();
            a2 = m194a(this.f287m.getName());
        }
        motorControllerConfiguration = new MotorControllerConfiguration(attributeValue, a, new SerialNumber(serialNumber));
        motorControllerConfiguration.setPort(i);
        return motorControllerConfiguration;
    }

    private String m194a(String str) {
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
