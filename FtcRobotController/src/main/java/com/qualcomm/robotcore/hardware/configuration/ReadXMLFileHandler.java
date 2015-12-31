/*
 * Copyright (c) 2014, 2015 Qualcomm Technologies Inc
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * (subject to the limitations in the disclaimer below) provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Qualcomm Technologies Inc nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.qualcomm.robotcore.hardware.configuration;

import android.content.Context;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants.ANALOG_INPUT_PORTS;
import static com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants.ANALOG_OUTPUT_PORTS;
import static com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants.DIGITAL_PORTS;
import static com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants.I2C_PORTS;
import static com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants.LEGACY_MODULE_PORTS;
import static com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants.MATRIX_MOTOR_PORTS;
import static com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants.MATRIX_SERVO_PORTS;
import static com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants.MOTOR_PORTS;
import static com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants.PWD_PORTS;
import static com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants.SERVO_PORTS;

public class ReadXMLFileHandler {
    private static boolean DEBUG;

    List<ControllerConfiguration> controllerConfigurations;
    private XmlPullParser parser;

    public ReadXMLFileHandler(Context context) {
        this.controllerConfigurations = new ArrayList<ControllerConfiguration>();
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
            if ((next == XmlPullParser.END_TAG) && (type != null)) {
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
                if ((type == ConfigurationType.ANALOG_INPUT) ||
                        (type == ConfigurationType.OPTICAL_DISTANCE_SENSOR)) {
                    deviceConfiguration = parseDeviceConfiguration();
                    analogInputConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                }
                if (type == ConfigurationType.PULSE_WIDTH_DEVICE) {
                    deviceConfiguration = parseDeviceConfiguration();
                    pwdConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                }
                if ((type == ConfigurationType.I2C_DEVICE) ||
                        (type == ConfigurationType.IR_SEEKER_V3) ||
                        (type == ConfigurationType.ADAFRUIT_COLOR_SENSOR) ||
                        (type == ConfigurationType.COLOR_SENSOR) ||
                        (type == ConfigurationType.GYRO)) {
                    deviceConfiguration = parseDeviceConfiguration();
                    i2cConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                }
                if (type == ConfigurationType.ANALOG_OUTPUT) {
                    deviceConfiguration = parseDeviceConfiguration();
                    analogOutputConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                }
                if ((type == ConfigurationType.DIGITAL_DEVICE) ||
                        (type == ConfigurationType.TOUCH_SENSOR) ||
                        (type == ConfigurationType.LED)) {
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
        List<DeviceConfiguration> deviceConfigurations =
                createDeviceConfigurationList(LEGACY_MODULE_PORTS, ConfigurationType.NOTHING);
        int next = this.parser.next();
        ConfigurationType type = getConfigurationType(this.parser.getName());
        ControllerConfiguration legacyModuleControllerConfiguration;
        while (next != XmlPullParser.END_DOCUMENT) {
            if ((next == XmlPullParser.END_TAG) && (type == ConfigurationType.LEGACY_MODULE_CONTROLLER)) {
                legacyModuleControllerConfiguration =
                        new LegacyModuleControllerConfiguration(name, deviceConfigurations, serialNumber);
                legacyModuleControllerConfiguration.setEnabled(true);
                return legacyModuleControllerConfiguration;
            } else if (next == XmlPullParser.START_TAG) {
                if (DEBUG) {
                    RobotLog.e("[handleLegacyModule] tagname: " + type);
                }
                DeviceConfiguration deviceConfiguration = null;
                if ((type == ConfigurationType.COMPASS) ||
                        (type == ConfigurationType.LIGHT_SENSOR) ||
                        (type == ConfigurationType.IR_SEEKER) ||
                        (type == ConfigurationType.ACCELEROMETER) ||
                        (type == ConfigurationType.GYRO) ||
                        (type == ConfigurationType.TOUCH_SENSOR) ||
                        (type == ConfigurationType.TOUCH_SENSOR_MULTIPLEXER) ||
                        (type == ConfigurationType.ULTRASONIC_SENSOR) ||
                        (type == ConfigurationType.COLOR_SENSOR) ||
                        (type == ConfigurationType.NOTHING)) {
                    deviceConfiguration = parseDeviceConfiguration();
                } else if (type == ConfigurationType.MOTOR_CONTROLLER) {
                    deviceConfiguration = parseMotorController(false);
                } else if (type == ConfigurationType.SERVO_CONTROLLER) {
                    deviceConfiguration = parseServoController(false);
                } else if (type == ConfigurationType.MATRIX_CONTROLLER) {
                    deviceConfiguration = parseMatrixController();
                }

                if (deviceConfiguration != null) {
                    deviceConfigurations.set(deviceConfiguration.getPort(), deviceConfiguration);
                }

            }
            next = this.parser.next();
            type = getConfigurationType(this.parser.getName());
        }
        return new LegacyModuleControllerConfiguration(name, deviceConfigurations, serialNumber);
    }

    private ControllerConfiguration parseMatrixController()
            throws IOException, XmlPullParserException, RobotCoreException {
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
            if ((next == XmlPullParser.END_TAG) && (type == ConfigurationType.MATRIX_CONTROLLER)) {
                ControllerConfiguration matrixControllerConfiguration =
                        new MatrixControllerConfiguration(name, motorConfigurations, servoConfigurations, serialNumber);
                matrixControllerConfiguration.setPort(port);
                matrixControllerConfiguration.setEnabled(true);
                return matrixControllerConfiguration;

            } else if (next == XmlPullParser.START_TAG) {
                if ((type == ConfigurationType.SERVO) || (type == ConfigurationType.MOTOR)) {
                    int devicePort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                    String deviceName = this.parser.getAttributeValue(null, "name");

                    DeviceConfiguration deviceConfiguration =
                            new DeviceConfiguration(devicePort, type, deviceName, true);

                    if (type == ConfigurationType.SERVO) {
                        servoConfigurations.set(devicePort - 1, deviceConfiguration);
                    } else {
                        motorConfigurations.set(devicePort - 1, deviceConfiguration);
                    }
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
        int port;
        SerialNumber serialNumber;
        if (useSerialNumber) {
            port = -1;
            serialNumber = new SerialNumber(this.parser.getAttributeValue(null, "serialNumber"));
        } else {
            port = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
            serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER;
        }
        List<DeviceConfiguration> deviceConfigurations = createDeviceConfigurationList(SERVO_PORTS, ConfigurationType.SERVO);
        int next = this.parser.next();
        ConfigurationType type = getConfigurationType(this.parser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if ((next == XmlPullParser.END_TAG) && (type == ConfigurationType.SERVO_CONTROLLER)) {
                servoControllerConfiguration =
                        new ServoControllerConfiguration(name, deviceConfigurations, serialNumber);
                servoControllerConfiguration.setPort(port);
                servoControllerConfiguration.setEnabled(true);
                return servoControllerConfiguration;
            } else if ((next == XmlPullParser.START_TAG) && (type == ConfigurationType.SERVO)) {
                int devicePort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                String deviceName = this.parser.getAttributeValue(null, "name");
                DeviceConfiguration deviceConfiguration =
                        new MotorConfiguration(devicePort, deviceName, true);
                deviceConfigurations.set(devicePort - 1, deviceConfiguration);
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
        int port;
        SerialNumber serialNumber;
        if (useSerialNumber) {
            port = -1;
            serialNumber = new SerialNumber(this.parser.getAttributeValue(null, "serialNumber"));
        } else {
            port = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
            serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER;
        }

        List<DeviceConfiguration> deviceConfigurations = createDeviceConfigurationList(MOTOR_PORTS, ConfigurationType.MOTOR);
        int next = this.parser.next();
        ConfigurationType type = getConfigurationType(this.parser.getName());
        while (next != XmlPullParser.END_DOCUMENT) {
            if ((next == XmlPullParser.END_TAG) && (type == ConfigurationType.MOTOR_CONTROLLER)) {
                motorControllerConfiguration =
                        new MotorControllerConfiguration(name, deviceConfigurations, serialNumber);
                motorControllerConfiguration.setPort(port);
                motorControllerConfiguration.setEnabled(true);
                return motorControllerConfiguration;
            } else if ((next == XmlPullParser.START_TAG) && (type == ConfigurationType.MOTOR)) {
                int devicePort = Integer.parseInt(this.parser.getAttributeValue(null, "port"));
                String deviceName = this.parser.getAttributeValue(null, "name");
                DeviceConfiguration deviceConfiguration =
                        new MotorConfiguration(devicePort, deviceName, true);
                deviceConfigurations.set(devicePort - 1, deviceConfiguration);
            }
            next = this.parser.next();
            type = getConfigurationType(this.parser.getName());
        }
        motorControllerConfiguration =
                new MotorControllerConfiguration(name, deviceConfigurations, serialNumber);
        motorControllerConfiguration.setPort(port);
        return motorControllerConfiguration;
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
        ArrayList<DeviceConfiguration> deviceConfigurations = new ArrayList<DeviceConfiguration>();
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
