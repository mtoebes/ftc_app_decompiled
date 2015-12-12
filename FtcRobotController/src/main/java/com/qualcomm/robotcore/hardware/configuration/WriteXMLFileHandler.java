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
    private XmlSerializer f294a;
    private HashSet<String> f295b;
    private ArrayList<String> f296c;
    private String[] f297d;
    private int f298e;

    public WriteXMLFileHandler(Context context) {
        this.f295b = new HashSet();
        this.f296c = new ArrayList();
        this.f297d = new String[]{"    ", "        ", "            "};
        this.f298e = 0;
        this.f294a = Xml.newSerializer();
    }

    public String writeXml(ArrayList<ControllerConfiguration> deviceControllerConfigurations) {
        this.f296c = new ArrayList();
        this.f295b = new HashSet();
        Writer stringWriter = new StringWriter();
        try {
            this.f294a.setOutput(stringWriter);
            this.f294a.startDocument("UTF-8", Boolean.valueOf(true));
            this.f294a.ignorableWhitespace("\n");
            this.f294a.startTag(BuildConfig.VERSION_NAME, "Robot");
            this.f294a.ignorableWhitespace("\n");
            Iterator it = deviceControllerConfigurations.iterator();
            while (it.hasNext()) {
                ControllerConfiguration controllerConfiguration = (ControllerConfiguration) it.next();
                String configurationType = controllerConfiguration.getType().toString();
                if (configurationType.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString()) || configurationType.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString())) {
                    m201a(controllerConfiguration, true);
                }
                if (configurationType.equalsIgnoreCase(ConfigurationType.LEGACY_MODULE_CONTROLLER.toString())) {
                    m205b(controllerConfiguration);
                }
                if (configurationType.equalsIgnoreCase(ConfigurationType.DEVICE_INTERFACE_MODULE.toString())) {
                    m200a(controllerConfiguration);
                }
            }
            this.f294a.endTag(BuildConfig.VERSION_NAME, "Robot");
            this.f294a.ignorableWhitespace("\n");
            this.f294a.endDocument();
            return stringWriter.toString();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void m203a(String str) {
        if (!str.equalsIgnoreCase(DeviceConfiguration.DISABLED_DEVICE_NAME)) {
            if (this.f295b.contains(str)) {
                this.f296c.add(str);
            } else {
                this.f295b.add(str);
            }
        }
    }

    private void m200a(ControllerConfiguration controllerConfiguration) throws IOException {
        this.f294a.ignorableWhitespace(this.f297d[this.f298e]);
        this.f294a.startTag(BuildConfig.VERSION_NAME, m204b(controllerConfiguration.getType().toString()));
        m203a(controllerConfiguration.getName());
        this.f294a.attribute(BuildConfig.VERSION_NAME, "name", controllerConfiguration.getName());
        this.f294a.attribute(BuildConfig.VERSION_NAME, "serialNumber", controllerConfiguration.getSerialNumber().toString());
        this.f294a.ignorableWhitespace("\n");
        this.f298e++;
        DeviceInterfaceModuleConfiguration deviceInterfaceModuleConfiguration = (DeviceInterfaceModuleConfiguration) controllerConfiguration;
        Iterator it = ((ArrayList) deviceInterfaceModuleConfiguration.getPwmDevices()).iterator();
        while (it.hasNext()) {
            m202a((DeviceConfiguration) it.next());
        }
        it = ((ArrayList) deviceInterfaceModuleConfiguration.getI2cDevices()).iterator();
        while (it.hasNext()) {
            m202a((DeviceConfiguration) it.next());
        }
        it = ((ArrayList) deviceInterfaceModuleConfiguration.getAnalogInputDevices()).iterator();
        while (it.hasNext()) {
            m202a((DeviceConfiguration) it.next());
        }
        it = ((ArrayList) deviceInterfaceModuleConfiguration.getDigitalDevices()).iterator();
        while (it.hasNext()) {
            m202a((DeviceConfiguration) it.next());
        }
        Iterator it2 = ((ArrayList) deviceInterfaceModuleConfiguration.getAnalogOutputDevices()).iterator();
        while (it2.hasNext()) {
            m202a((DeviceConfiguration) it2.next());
        }
        this.f298e--;
        this.f294a.ignorableWhitespace(this.f297d[this.f298e]);
        this.f294a.endTag(BuildConfig.VERSION_NAME, m204b(controllerConfiguration.getType().toString()));
        this.f294a.ignorableWhitespace("\n");
    }

    private void m205b(ControllerConfiguration controllerConfiguration) throws IOException {
        this.f294a.ignorableWhitespace(this.f297d[this.f298e]);
        this.f294a.startTag(BuildConfig.VERSION_NAME, m204b(controllerConfiguration.getType().toString()));
        m203a(controllerConfiguration.getName());
        this.f294a.attribute(BuildConfig.VERSION_NAME, "name", controllerConfiguration.getName());
        this.f294a.attribute(BuildConfig.VERSION_NAME, "serialNumber", controllerConfiguration.getSerialNumber().toString());
        this.f294a.ignorableWhitespace("\n");
        this.f298e++;
        Iterator it = ((ArrayList) controllerConfiguration.getDevices()).iterator();
        while (it.hasNext()) {
            DeviceConfiguration deviceConfiguration = (DeviceConfiguration) it.next();
            String configurationType = deviceConfiguration.getType().toString();
            if (configurationType.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString()) || configurationType.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString()) || configurationType.equalsIgnoreCase(ConfigurationType.MATRIX_CONTROLLER.toString())) {
                m201a((ControllerConfiguration) deviceConfiguration, false);
            } else if (deviceConfiguration.isEnabled()) {
                m202a(deviceConfiguration);
            }
        }
        this.f298e--;
        this.f294a.ignorableWhitespace(this.f297d[this.f298e]);
        this.f294a.endTag(BuildConfig.VERSION_NAME, m204b(controllerConfiguration.getType().toString()));
        this.f294a.ignorableWhitespace("\n");
    }

    private void m201a(ControllerConfiguration controllerConfiguration, boolean z) throws IOException {
        this.f294a.ignorableWhitespace(this.f297d[this.f298e]);
        this.f294a.startTag(BuildConfig.VERSION_NAME, m204b(controllerConfiguration.getType().toString()));
        m203a(controllerConfiguration.getName());
        this.f294a.attribute(BuildConfig.VERSION_NAME, "name", controllerConfiguration.getName());
        if (z) {
            this.f294a.attribute(BuildConfig.VERSION_NAME, "serialNumber", controllerConfiguration.getSerialNumber().toString());
        } else {
            this.f294a.attribute(BuildConfig.VERSION_NAME, "port", String.valueOf(controllerConfiguration.getPort()));
        }
        this.f294a.ignorableWhitespace("\n");
        this.f298e++;
        Iterator it = ((ArrayList) controllerConfiguration.getDevices()).iterator();
        while (it.hasNext()) {
            DeviceConfiguration deviceConfiguration = (DeviceConfiguration) it.next();
            if (deviceConfiguration.isEnabled()) {
                m202a(deviceConfiguration);
            }
        }
        if (controllerConfiguration.getType() == ConfigurationType.MATRIX_CONTROLLER) {
            it = ((ArrayList) ((MatrixControllerConfiguration) controllerConfiguration).getMotors()).iterator();
            DeviceConfiguration deviceConfiguration;
            while (it.hasNext()) {
                deviceConfiguration = (DeviceConfiguration) it.next();
                if (deviceConfiguration.isEnabled()) {
                    m202a(deviceConfiguration);
                }
            }
            it = ((ArrayList) ((MatrixControllerConfiguration) controllerConfiguration).getServos()).iterator();
            while (it.hasNext()) {
                deviceConfiguration = (DeviceConfiguration) it.next();
                if (deviceConfiguration.isEnabled()) {
                    m202a(deviceConfiguration);
                }
            }
        }
        this.f298e--;
        this.f294a.ignorableWhitespace(this.f297d[this.f298e]);
        this.f294a.endTag(BuildConfig.VERSION_NAME, m204b(controllerConfiguration.getType().toString()));
        this.f294a.ignorableWhitespace("\n");
    }

    private void m202a(DeviceConfiguration deviceConfiguration) {
        if (deviceConfiguration.isEnabled()) {
            try {
                this.f294a.ignorableWhitespace(this.f297d[this.f298e]);
                this.f294a.startTag(BuildConfig.VERSION_NAME, m204b(deviceConfiguration.getType().toString()));
                m203a(deviceConfiguration.getName());
                this.f294a.attribute(BuildConfig.VERSION_NAME, "name", deviceConfiguration.getName());
                this.f294a.attribute(BuildConfig.VERSION_NAME, "port", String.valueOf(deviceConfiguration.getPort()));
                this.f294a.endTag(BuildConfig.VERSION_NAME, m204b(deviceConfiguration.getType().toString()));
                this.f294a.ignorableWhitespace("\n");
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void writeToFile(String data, String folderName, String filename) throws RobotCoreException, IOException {
        Exception e;
        Throwable th;
        if (this.f296c.size() > 0) {
            throw new IOException("Duplicate names: " + this.f296c);
        }
        filename = filename.replaceFirst("[.][^.]+$", BuildConfig.VERSION_NAME);
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
                fileOutputStream.close();
                return;
            }
        }
        throw new RobotCoreException("Unable to create directory");
    }

    private String m204b(String str) {
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
