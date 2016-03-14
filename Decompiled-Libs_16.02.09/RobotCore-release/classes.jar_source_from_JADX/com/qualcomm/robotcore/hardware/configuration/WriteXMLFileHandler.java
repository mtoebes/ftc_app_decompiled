package com.qualcomm.robotcore.hardware.configuration;

import android.content.Context;
import android.util.Xml;
import com.qualcomm.robotcore.BuildConfig;
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
    private XmlSerializer f288a;
    private HashSet<String> f289b;
    private ArrayList<String> f290c;
    private String[] f291d;
    private int f292e;

    public WriteXMLFileHandler(Context context) {
        this.f289b = new HashSet();
        this.f290c = new ArrayList();
        this.f291d = new String[]{"    ", "        ", "            "};
        this.f292e = 0;
        this.f288a = Xml.newSerializer();
    }

    public String writeXml(ArrayList<ControllerConfiguration> deviceControllerConfigurations) {
        this.f290c = new ArrayList();
        this.f289b = new HashSet();
        Writer stringWriter = new StringWriter();
        try {
            this.f288a.setOutput(stringWriter);
            this.f288a.startDocument("UTF-8", Boolean.valueOf(true));
            this.f288a.ignorableWhitespace("\n");
            this.f288a.startTag(BuildConfig.VERSION_NAME, "Robot");
            this.f288a.ignorableWhitespace("\n");
            Iterator it = deviceControllerConfigurations.iterator();
            while (it.hasNext()) {
                ControllerConfiguration controllerConfiguration = (ControllerConfiguration) it.next();
                String configurationType = controllerConfiguration.getType().toString();
                if (configurationType.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString()) || configurationType.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString())) {
                    m212a(controllerConfiguration, true);
                }
                if (configurationType.equalsIgnoreCase(ConfigurationType.LEGACY_MODULE_CONTROLLER.toString())) {
                    m216b(controllerConfiguration);
                }
                if (configurationType.equalsIgnoreCase(ConfigurationType.DEVICE_INTERFACE_MODULE.toString())) {
                    m211a(controllerConfiguration);
                }
            }
            this.f288a.endTag(BuildConfig.VERSION_NAME, "Robot");
            this.f288a.ignorableWhitespace("\n");
            this.f288a.endDocument();
            return stringWriter.toString();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void m214a(String str) {
        if (!str.equalsIgnoreCase(DeviceConfiguration.DISABLED_DEVICE_NAME)) {
            if (this.f289b.contains(str)) {
                this.f290c.add(str);
            } else {
                this.f289b.add(str);
            }
        }
    }

    private void m211a(ControllerConfiguration controllerConfiguration) throws IOException {
        this.f288a.ignorableWhitespace(this.f291d[this.f292e]);
        this.f288a.startTag(BuildConfig.VERSION_NAME, m215b(controllerConfiguration.getType().toString()));
        m214a(controllerConfiguration.getName());
        this.f288a.attribute(BuildConfig.VERSION_NAME, "name", controllerConfiguration.getName());
        this.f288a.attribute(BuildConfig.VERSION_NAME, "serialNumber", controllerConfiguration.getSerialNumber().toString());
        this.f288a.ignorableWhitespace("\n");
        this.f292e++;
        DeviceInterfaceModuleConfiguration deviceInterfaceModuleConfiguration = (DeviceInterfaceModuleConfiguration) controllerConfiguration;
        Iterator it = ((ArrayList) deviceInterfaceModuleConfiguration.getPwmDevices()).iterator();
        while (it.hasNext()) {
            m213a((DeviceConfiguration) it.next());
        }
        it = ((ArrayList) deviceInterfaceModuleConfiguration.getI2cDevices()).iterator();
        while (it.hasNext()) {
            m213a((DeviceConfiguration) it.next());
        }
        it = ((ArrayList) deviceInterfaceModuleConfiguration.getAnalogInputDevices()).iterator();
        while (it.hasNext()) {
            m213a((DeviceConfiguration) it.next());
        }
        it = ((ArrayList) deviceInterfaceModuleConfiguration.getDigitalDevices()).iterator();
        while (it.hasNext()) {
            m213a((DeviceConfiguration) it.next());
        }
        Iterator it2 = ((ArrayList) deviceInterfaceModuleConfiguration.getAnalogOutputDevices()).iterator();
        while (it2.hasNext()) {
            m213a((DeviceConfiguration) it2.next());
        }
        this.f292e--;
        this.f288a.ignorableWhitespace(this.f291d[this.f292e]);
        this.f288a.endTag(BuildConfig.VERSION_NAME, m215b(controllerConfiguration.getType().toString()));
        this.f288a.ignorableWhitespace("\n");
    }

    private void m216b(ControllerConfiguration controllerConfiguration) throws IOException {
        this.f288a.ignorableWhitespace(this.f291d[this.f292e]);
        this.f288a.startTag(BuildConfig.VERSION_NAME, m215b(controllerConfiguration.getType().toString()));
        m214a(controllerConfiguration.getName());
        this.f288a.attribute(BuildConfig.VERSION_NAME, "name", controllerConfiguration.getName());
        this.f288a.attribute(BuildConfig.VERSION_NAME, "serialNumber", controllerConfiguration.getSerialNumber().toString());
        this.f288a.ignorableWhitespace("\n");
        this.f292e++;
        Iterator it = ((ArrayList) controllerConfiguration.getDevices()).iterator();
        while (it.hasNext()) {
            DeviceConfiguration deviceConfiguration = (DeviceConfiguration) it.next();
            String configurationType = deviceConfiguration.getType().toString();
            if (configurationType.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString()) || configurationType.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString()) || configurationType.equalsIgnoreCase(ConfigurationType.MATRIX_CONTROLLER.toString())) {
                m212a((ControllerConfiguration) deviceConfiguration, false);
            } else if (deviceConfiguration.isEnabled()) {
                m213a(deviceConfiguration);
            }
        }
        this.f292e--;
        this.f288a.ignorableWhitespace(this.f291d[this.f292e]);
        this.f288a.endTag(BuildConfig.VERSION_NAME, m215b(controllerConfiguration.getType().toString()));
        this.f288a.ignorableWhitespace("\n");
    }

    private void m212a(ControllerConfiguration controllerConfiguration, boolean z) throws IOException {
        this.f288a.ignorableWhitespace(this.f291d[this.f292e]);
        this.f288a.startTag(BuildConfig.VERSION_NAME, m215b(controllerConfiguration.getType().toString()));
        m214a(controllerConfiguration.getName());
        this.f288a.attribute(BuildConfig.VERSION_NAME, "name", controllerConfiguration.getName());
        if (z) {
            this.f288a.attribute(BuildConfig.VERSION_NAME, "serialNumber", controllerConfiguration.getSerialNumber().toString());
        } else {
            this.f288a.attribute(BuildConfig.VERSION_NAME, "port", String.valueOf(controllerConfiguration.getPort()));
        }
        this.f288a.ignorableWhitespace("\n");
        this.f292e++;
        Iterator it = ((ArrayList) controllerConfiguration.getDevices()).iterator();
        while (it.hasNext()) {
            DeviceConfiguration deviceConfiguration = (DeviceConfiguration) it.next();
            if (deviceConfiguration.isEnabled()) {
                m213a(deviceConfiguration);
            }
        }
        if (controllerConfiguration.getType() == ConfigurationType.MATRIX_CONTROLLER) {
            it = ((ArrayList) ((MatrixControllerConfiguration) controllerConfiguration).getMotors()).iterator();
            while (it.hasNext()) {
                deviceConfiguration = (DeviceConfiguration) it.next();
                if (deviceConfiguration.isEnabled()) {
                    m213a(deviceConfiguration);
                }
            }
            it = ((ArrayList) ((MatrixControllerConfiguration) controllerConfiguration).getServos()).iterator();
            while (it.hasNext()) {
                deviceConfiguration = (DeviceConfiguration) it.next();
                if (deviceConfiguration.isEnabled()) {
                    m213a(deviceConfiguration);
                }
            }
        }
        this.f292e--;
        this.f288a.ignorableWhitespace(this.f291d[this.f292e]);
        this.f288a.endTag(BuildConfig.VERSION_NAME, m215b(controllerConfiguration.getType().toString()));
        this.f288a.ignorableWhitespace("\n");
    }

    private void m213a(DeviceConfiguration deviceConfiguration) {
        if (deviceConfiguration.isEnabled()) {
            try {
                this.f288a.ignorableWhitespace(this.f291d[this.f292e]);
                this.f288a.startTag(BuildConfig.VERSION_NAME, m215b(deviceConfiguration.getType().toString()));
                m214a(deviceConfiguration.getName());
                this.f288a.attribute(BuildConfig.VERSION_NAME, "name", deviceConfiguration.getName());
                this.f288a.attribute(BuildConfig.VERSION_NAME, "port", String.valueOf(deviceConfiguration.getPort()));
                this.f288a.endTag(BuildConfig.VERSION_NAME, m215b(deviceConfiguration.getType().toString()));
                this.f288a.ignorableWhitespace("\n");
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void writeToFile(String data, String folderName, String filename) throws RobotCoreException, IOException {
        FileOutputStream fileOutputStream;
        Exception e;
        Throwable th;
        if (this.f290c.size() > 0) {
            throw new IOException("Duplicate names: " + this.f290c);
        }
        filename = filename.replaceFirst("[.][^.]+$", BuildConfig.VERSION_NAME);
        File file = new File(folderName);
        boolean z = true;
        if (!file.exists()) {
            z = file.mkdir();
        }
        if (z) {
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
                    try {
                        e.printStackTrace();
                        try {
                            fileOutputStream.close();
                            return;
                        } catch (IOException e22) {
                            e22.printStackTrace();
                            return;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        try {
                            fileOutputStream.close();
                        } catch (IOException e4) {
                            e4.printStackTrace();
                        }
                        throw th;
                    }
                }
            } catch (Exception e5) {
                e = e5;
                fileOutputStream = null;
                e.printStackTrace();
                fileOutputStream.close();
                return;
            } catch (Throwable th3) {
                th = th3;
                fileOutputStream = null;
                fileOutputStream.close();
                throw th;
            }
        }
        throw new RobotCoreException("Unable to create directory");
    }

    private String m215b(String str) {
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
