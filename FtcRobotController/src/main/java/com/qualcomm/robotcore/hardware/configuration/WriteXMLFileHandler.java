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
import java.util.Iterator;
import org.xmlpull.v1.XmlSerializer;

public class WriteXMLFileHandler {
    private XmlSerializer serializer = Xml.newSerializer();;
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
            Iterator it = deviceControllerConfigurations.iterator();
            while (it.hasNext()) {
                ControllerConfiguration controllerConfiguration = (ControllerConfiguration) it.next();
                String configurationType = controllerConfiguration.getType().toString();
                if (configurationType.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString()) || configurationType.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString())) {
                    writeMotorServoConfigurationXml(controllerConfiguration, true);
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
        Iterator it = ((ArrayList) deviceInterfaceModuleConfiguration.getPwmDevices()).iterator();
        while (it.hasNext()) {
            writeDeviceConfigurationXml((DeviceConfiguration) it.next());
        }
        it = ((ArrayList) deviceInterfaceModuleConfiguration.getI2cDevices()).iterator();
        while (it.hasNext()) {
            writeDeviceConfigurationXml((DeviceConfiguration) it.next());
        }
        it = ((ArrayList) deviceInterfaceModuleConfiguration.getAnalogInputDevices()).iterator();
        while (it.hasNext()) {
            writeDeviceConfigurationXml((DeviceConfiguration) it.next());
        }
        it = ((ArrayList) deviceInterfaceModuleConfiguration.getDigitalDevices()).iterator();
        while (it.hasNext()) {
            writeDeviceConfigurationXml((DeviceConfiguration) it.next());
        }
        Iterator it2 = ((ArrayList) deviceInterfaceModuleConfiguration.getAnalogOutputDevices()).iterator();
        while (it2.hasNext()) {
            writeDeviceConfigurationXml((DeviceConfiguration) it2.next());
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
        Iterator it = ((ArrayList) controllerConfiguration.getDevices()).iterator();
        while (it.hasNext()) {
            DeviceConfiguration deviceConfiguration = (DeviceConfiguration) it.next();
            String configurationType = deviceConfiguration.getType().toString();
            if (configurationType.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString()) || configurationType.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString()) || configurationType.equalsIgnoreCase(ConfigurationType.MATRIX_CONTROLLER.toString())) {
                writeMotorServoConfigurationXml((ControllerConfiguration) deviceConfiguration, false);
            } else if (deviceConfiguration.isEnabled()) {
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
        Iterator it = ((ArrayList) controllerConfiguration.getDevices()).iterator();
        while (it.hasNext()) {
            DeviceConfiguration deviceConfiguration = (DeviceConfiguration) it.next();
            if (deviceConfiguration.isEnabled()) {
                writeDeviceConfigurationXml(deviceConfiguration);
            }
        }
        if (controllerConfiguration.getType() == ConfigurationType.MATRIX_CONTROLLER) {
            it = ((ArrayList) ((MatrixControllerConfiguration) controllerConfiguration).getMotors()).iterator();
            DeviceConfiguration deviceConfiguration;
            while (it.hasNext()) {
                deviceConfiguration = (DeviceConfiguration) it.next();
                if (deviceConfiguration.isEnabled()) {
                    writeDeviceConfigurationXml(deviceConfiguration);
                }
            }
            it = ((ArrayList) ((MatrixControllerConfiguration) controllerConfiguration).getServos()).iterator();
            while (it.hasNext()) {
                deviceConfiguration = (DeviceConfiguration) it.next();
                if (deviceConfiguration.isEnabled()) {
                    writeDeviceConfigurationXml(deviceConfiguration);
                }
            }
        }
        this.indent--;
        this.serializer.ignorableWhitespace(this.indentation[this.indent]);
        this.serializer.endTag("", toUpperCamelCase(controllerConfiguration.getType().toString()));
        this.serializer.ignorableWhitespace("\n");
    }

    private void writeDeviceConfigurationXml(DeviceConfiguration deviceConfiguration) {
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
        Exception e;
        if (this.duplicates.size() > 0) {
            throw new IOException("Duplicate names: " + this.duplicates);
        }
        filename = filename.replaceFirst("[.][^.]+$", "");
        File file = new File(folderName);
        boolean z = true;
        if (!file.exists()) {
            z = file.mkdir();
        }
        if (z) {
            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = new FileOutputStream(new File(folderName + filename + Utility.FILE_EXT));
                try {
                    fileOutputStream.write(data.getBytes());
                    try {
                        fileOutputStream.close();
                        return;
                    } catch (IOException e2) {
                        e2.printStackTrace();
                        return;
                    }
                } catch (Exception e3) {
                    e = e3;
                        e.printStackTrace();
                        try {
                            fileOutputStream.close();
                            return;
                        } catch (IOException e22) {
                            e22.printStackTrace();
                            return;
                        }
                }
            } catch (Exception e5) {
                e = e5;
                fileOutputStream = null;
                e.printStackTrace();
                assert fileOutputStream != null;
                fileOutputStream.close();
                return;
            }
        }
        throw new RobotCoreException("Unable to create directory");
    }

    private String toUpperCamelCase(String str) {
        String str2 = str.substring(0, 1) + str.substring(1).toLowerCase();
        int lastIndexOf = str.lastIndexOf("_");
        while (lastIndexOf > 0) {
            int i = lastIndexOf + 1;
            String substring = str2.substring(0, lastIndexOf);
            String toUpperCase = str2.substring(i, i + 1).toUpperCase();
            str2 = substring + toUpperCase + str2.substring(i + 1);
            lastIndexOf = str2.lastIndexOf("_");
        }
        return str2;
    }
}
