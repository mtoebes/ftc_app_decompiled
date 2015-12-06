package com.qualcomm.robotcore.hardware.configuration;

import android.content.Context;
import android.util.Xml;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

public class WriteXMLFileHandler {
    private XmlSerializer serializer;
    private HashSet<String> names = new HashSet<String>();
    private ArrayList<String> duplicates = new ArrayList<String>();
    private String[] indentation = new String[]{"    ", "        ", "            "};
    private int indent = 0;

    public WriteXMLFileHandler(Context context) {
        this.serializer = Xml.newSerializer();
    }

    public String writeXml(ArrayList<ControllerConfiguration> deviceControllerConfigurations) {
        this.duplicates = new ArrayList<String>();
        this.names = new HashSet<String>();
        Writer stringWriter = new StringWriter();
        try {
            this.serializer.setOutput(stringWriter);
            this.serializer.startDocument("UTF-8", true);
            this.serializer.ignorableWhitespace("\n");
            this.serializer.startTag("", "Robot");
            this.serializer.ignorableWhitespace("\n");
            for(ControllerConfiguration controllerConfiguration : deviceControllerConfigurations) {
                String configurationType = controllerConfiguration.getType().toString();
                if (configurationType.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString()) ||
                        configurationType.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString())) {
                    writeMotorServoControllerXml(controllerConfiguration, true);
                }
                if (configurationType.equalsIgnoreCase(ConfigurationType.LEGACY_MODULE_CONTROLLER.toString())) {
                    writeLegacyControllerXml(controllerConfiguration);
                }
                if (configurationType.equalsIgnoreCase(ConfigurationType.DEVICE_INTERFACE_MODULE.toString())) {
                    writeInterfaceModuleXml(controllerConfiguration);
                }
            }
            this.serializer.endTag("", "Robot");
            this.serializer.ignorableWhitespace("\n");
            this.serializer.endDocument();
            return stringWriter.toString();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void checkForDuplicates(String str) {
        if (!str.equalsIgnoreCase(DeviceConfiguration.DISABLED_DEVICE_NAME)) {
            if (this.names.contains(str)) {
                this.duplicates.add(str);
            } else {
                this.names.add(str);
            }
        }
    }

    private void writeInterfaceModuleXml(ControllerConfiguration controllerConfiguration) throws IOException {
        this.serializer.ignorableWhitespace(this.indentation[this.indent]);
        this.serializer.startTag("", toUpperCamelCase(controllerConfiguration.getType().toString()));
        checkForDuplicates(controllerConfiguration.getName());
        this.serializer.attribute("", "name", controllerConfiguration.getName());
        this.serializer.attribute("", "serialNumber", controllerConfiguration.getSerialNumber().toString());
        this.serializer.ignorableWhitespace("\n");
        this.indent++;
        DeviceInterfaceModuleConfiguration deviceInterfaceModuleConfiguration = (DeviceInterfaceModuleConfiguration) controllerConfiguration;

        for(DeviceConfiguration pwmDevices : deviceInterfaceModuleConfiguration.getPwmDevices()) {
            writeDeviceConfiguration(pwmDevices);
        }

        for(DeviceConfiguration i2cDevices : deviceInterfaceModuleConfiguration.getI2cDevices()) {
            writeDeviceConfiguration(i2cDevices);
        }

        for(DeviceConfiguration inputDevices : deviceInterfaceModuleConfiguration.getAnalogInputDevices()) {
            writeDeviceConfiguration(inputDevices);
        }

        for(DeviceConfiguration digitalDevices : deviceInterfaceModuleConfiguration.getDigitalDevices()) {
            writeDeviceConfiguration(digitalDevices);
        }

        for(DeviceConfiguration outputDevices : deviceInterfaceModuleConfiguration.getAnalogOutputDevices()) {
            writeDeviceConfiguration(outputDevices);
        }

        this.indent--;
        this.serializer.ignorableWhitespace(this.indentation[this.indent]);
        this.serializer.endTag("", toUpperCamelCase(controllerConfiguration.getType().toString()));
        this.serializer.ignorableWhitespace("\n");
    }

    private void writeLegacyControllerXml(ControllerConfiguration controllerConfiguration) throws IOException {
        this.serializer.ignorableWhitespace(this.indentation[this.indent]);
        this.serializer.startTag("", toUpperCamelCase(controllerConfiguration.getType().toString()));
        checkForDuplicates(controllerConfiguration.getName());
        this.serializer.attribute("", "name", controllerConfiguration.getName());
        this.serializer.attribute("", "serialNumber", controllerConfiguration.getSerialNumber().toString());
        this.serializer.ignorableWhitespace("\n");
        this.indent++;
        for(DeviceConfiguration deviceConfiguration : controllerConfiguration.getDevices()) {
            String configurationType = deviceConfiguration.getType().toString();
            if (configurationType.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString()) || configurationType.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString()) || configurationType.equalsIgnoreCase(ConfigurationType.MATRIX_CONTROLLER.toString())) {
                writeMotorServoControllerXml((ControllerConfiguration) deviceConfiguration, false);
            } else if (deviceConfiguration.isEnabled()) {
                writeDeviceConfiguration(deviceConfiguration);
            }
        }
        this.indent--;
        this.serializer.ignorableWhitespace(this.indentation[this.indent]);
        this.serializer.endTag("", toUpperCamelCase(controllerConfiguration.getType().toString()));
        this.serializer.ignorableWhitespace("\n");
    }

    private void writeMotorServoControllerXml(ControllerConfiguration controllerConfiguration, boolean useSerialNumber) throws IOException {
        this.serializer.ignorableWhitespace(this.indentation[this.indent]);
        this.serializer.startTag("", toUpperCamelCase(controllerConfiguration.getType().toString()));
        checkForDuplicates(controllerConfiguration.getName());
        this.serializer.attribute("", "name", controllerConfiguration.getName());
        if (useSerialNumber) {
            this.serializer.attribute("", "serialNumber", controllerConfiguration.getSerialNumber().toString());
        } else {
            this.serializer.attribute("", "port", String.valueOf(controllerConfiguration.getPort()));
        }
        this.serializer.ignorableWhitespace("\n");
        this.indent++;

        List<DeviceConfiguration> deviceConfigurations = controllerConfiguration.getDevices();

        if (controllerConfiguration.getType() == ConfigurationType.MATRIX_CONTROLLER) {
            deviceConfigurations = ((MatrixControllerConfiguration) controllerConfiguration).getMotors();
            for(DeviceConfiguration deviceConfiguration : deviceConfigurations) {
                if (deviceConfiguration.isEnabled()) {
                    writeDeviceConfiguration(deviceConfiguration);
                }
            }

            deviceConfigurations = ((MatrixControllerConfiguration) controllerConfiguration).getServos();
            for(DeviceConfiguration deviceConfiguration : deviceConfigurations) {
                if (deviceConfiguration.isEnabled()) {
                    writeDeviceConfiguration(deviceConfiguration);
                }
            }
        } else {
            for (DeviceConfiguration deviceConfiguration : deviceConfigurations) {
                if (deviceConfiguration.isEnabled()) {
                    writeDeviceConfiguration(deviceConfiguration);
                }
            }
        }
        this.indent--;
        this.serializer.ignorableWhitespace(this.indentation[this.indent]);
        this.serializer.endTag("", toUpperCamelCase(controllerConfiguration.getType().toString()));
        this.serializer.ignorableWhitespace("\n");
    }

    private void writeDeviceConfiguration(DeviceConfiguration deviceConfiguration) {
        if (deviceConfiguration.isEnabled()) {
            try {
                this.serializer.ignorableWhitespace(this.indentation[this.indent]);
                this.serializer.startTag("", toUpperCamelCase(deviceConfiguration.getType().toString()));
                checkForDuplicates(deviceConfiguration.getName());
                this.serializer.attribute("", "name", deviceConfiguration.getName());
                this.serializer.attribute("", "port", String.valueOf(deviceConfiguration.getPort()));
                this.serializer.endTag("", toUpperCamelCase(deviceConfiguration.getType().toString()));
                this.serializer.ignorableWhitespace("\n");
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void writeToFile(String data, String folderName, String filename) throws RobotCoreException, IOException {
        if (this.duplicates.size() > 0) {
            throw new IOException("Duplicate names: " + this.duplicates);
        }

        filename = filename.replaceFirst("[.][^.]+$", "");
        File file = new File(folderName);

        if (!file.exists()) {
            if(!file.mkdir()) {
                throw new RobotCoreException("Unable to create directory");
            }
        }

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(folderName + filename + Utility.FILE_EXT));
            fileOutputStream.write(data.getBytes());
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                if(fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch(Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private String toUpperCamelCase(String sourceString) {
        String resultString = sourceString.substring(0, 1) + sourceString.substring(1).toLowerCase();
        int lastUnderscoreIndex = sourceString.lastIndexOf("_");
        while (lastUnderscoreIndex > 0) {
            int currentIndex = lastUnderscoreIndex + 1;
            String substring = resultString.substring(0, lastUnderscoreIndex);
            String toUpperCase = resultString.substring(currentIndex, currentIndex + 1).toUpperCase();
            resultString = substring + toUpperCase + resultString.substring(currentIndex + 1);
            lastUnderscoreIndex = resultString.lastIndexOf("_");
        }
        return resultString;
    }
}
