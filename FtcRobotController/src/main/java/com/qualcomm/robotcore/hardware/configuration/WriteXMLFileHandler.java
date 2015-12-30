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
import org.xmlpull.v1.XmlSerializer;

public class WriteXMLFileHandler {
    private XmlSerializer serializer = Xml.newSerializer();
    private HashSet<String> names = new HashSet<String>();
    private ArrayList<String> duplicates = new ArrayList<String>();
    private String[] indentation = {"    ", "        ", "            "};
    private int indent;

    public WriteXMLFileHandler(Context context) {
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
                ConfigurationType configurationType = controllerConfiguration.getType();
                if ((configurationType == ConfigurationType.MOTOR_CONTROLLER) ||
                        (configurationType == ConfigurationType.SERVO_CONTROLLER)) {
                    writeMotorServoConfigurationXml(controllerConfiguration, true);
                }
                if (configurationType == ConfigurationType.LEGACY_MODULE_CONTROLLER) {
                    writeLegacyControllerXml(controllerConfiguration);
                }
                if (configurationType == ConfigurationType.DEVICE_INTERFACE_MODULE) {
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

        for(DeviceConfiguration deviceConfiguration : deviceInterfaceModuleConfiguration.getPwmDevices()) {
            writeDeviceConfigurationXml(deviceConfiguration);
        }
        for(DeviceConfiguration deviceConfiguration : deviceInterfaceModuleConfiguration.getI2cDevices()) {
            writeDeviceConfigurationXml(deviceConfiguration);
        }
        for(DeviceConfiguration deviceConfiguration : deviceInterfaceModuleConfiguration.getAnalogInputDevices()) {
            writeDeviceConfigurationXml(deviceConfiguration);
        }
        for(DeviceConfiguration deviceConfiguration : deviceInterfaceModuleConfiguration.getDigitalDevices()) {
            writeDeviceConfigurationXml(deviceConfiguration);
        }
        for(DeviceConfiguration deviceConfiguration : deviceInterfaceModuleConfiguration.getAnalogOutputDevices()) {
            writeDeviceConfigurationXml(deviceConfiguration);
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
        for (DeviceConfiguration deviceConfiguration : controllerConfiguration.getDevices()) {
            String configurationType = deviceConfiguration.getType().toString();
            if (configurationType.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString()) ||
                    configurationType.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString()) ||
                    configurationType.equalsIgnoreCase(ConfigurationType.MATRIX_CONTROLLER.toString())) {
                writeMotorServoConfigurationXml((ControllerConfiguration) deviceConfiguration, false);
            } else {
                writeDeviceConfigurationXml(deviceConfiguration);
            }
        }
        this.indent--;
        this.serializer.ignorableWhitespace(this.indentation[this.indent]);
        this.serializer.endTag("", toUpperCamelCase(controllerConfiguration.getType().toString()));
        this.serializer.ignorableWhitespace("\n");
    }

    private void writeMotorServoConfigurationXml(ControllerConfiguration controllerConfiguration, boolean useSerialNumber) throws IOException {
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
        for(DeviceConfiguration deviceConfiguration : controllerConfiguration.getDevices()) {
            writeDeviceConfigurationXml(deviceConfiguration);
        }

        if (controllerConfiguration.getType() == ConfigurationType.MATRIX_CONTROLLER) {
            for(DeviceConfiguration deviceConfiguration : ((MatrixControllerConfiguration) controllerConfiguration).getMotors()) {
                writeDeviceConfigurationXml(deviceConfiguration);
            }

            for(DeviceConfiguration deviceConfiguration : ((MatrixControllerConfiguration) controllerConfiguration).getServos()) {
                writeDeviceConfigurationXml(deviceConfiguration);
            }
        }

        this.indent--;
        this.serializer.ignorableWhitespace(this.indentation[this.indent]);
        this.serializer.endTag("", toUpperCamelCase(controllerConfiguration.getType().toString()));
        this.serializer.ignorableWhitespace("\n");
    }

    private void writeDeviceConfigurationXml(DeviceConfiguration deviceConfiguration) throws IOException {
        if (deviceConfiguration.isEnabled()) {
            this.serializer.ignorableWhitespace(this.indentation[this.indent]);
            this.serializer.startTag("", toUpperCamelCase(deviceConfiguration.getType().toString()));
            checkForDuplicates(deviceConfiguration.getName());
            this.serializer.attribute("", "name", deviceConfiguration.getName());
            this.serializer.attribute("", "port", String.valueOf(deviceConfiguration.getPort()));
            this.serializer.endTag("", toUpperCamelCase(deviceConfiguration.getType().toString()));
            this.serializer.ignorableWhitespace("\n");
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

        FileOutputStream fileOutputStream = new FileOutputStream(new File(folderName + filename + Utility.FILE_EXT));
        fileOutputStream.write(data.getBytes());
        fileOutputStream.close();
    }

    private String toUpperCamelCase(String str) {
        String result = "";
        String[] words = str.split("_");

        for(String word : words) {
            word = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
            result += word;
        }

        return result;
    }
}