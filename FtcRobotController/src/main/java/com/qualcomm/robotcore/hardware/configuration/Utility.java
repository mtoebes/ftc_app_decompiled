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

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DeviceManager.DeviceType;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import static com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants.ANALOG_INPUT_PORTS;
import static com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants.ANALOG_OUTPUT_PORTS;
import static com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants.DIGITAL_PORTS;
import static com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants.I2C_PORTS;
import static com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants.LEGACY_MODULE_PORTS;
import static com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants.MOTOR_PORTS;
import static com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants.PWD_PORTS;
import static com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants.SERVO_PORTS;

public class Utility {
    public static final String AUTOCONFIGURE_K9LEGACYBOT = "K9LegacyBot";
    public static final String AUTOCONFIGURE_K9USBBOT = "K9USBBot";
    public static final String CONFIG_FILES_DIR = Environment.getExternalStorageDirectory() + "/FIRST/";
    public static final String DEFAULT_ROBOT_CONFIG = "robot_config";
    public static final String DEFAULT_ROBOT_CONFIG_FILENAME = "robot_config.xml";
    public static final String FILE_EXT = ".xml";
    public static final String NO_FILE = "No current file!";
    public static final String UNSAVED = "Unsaved";
    private static int count = 1;
    private Activity activity;
    private SharedPreferences preferences;
    private WriteXMLFileHandler writeXMLFileHandler;
    private String xmlOutput;

    public Utility(Activity activity) {
        this.activity = activity;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        this.writeXMLFileHandler = new WriteXMLFileHandler(activity);
    }

    public void createConfigFolder() {
        File file = new File(CONFIG_FILES_DIR);
        if (!file.exists()) {
            if (!file.mkdir()) {
                RobotLog.e("Can't create the Robot Config Files directory!");
                complainToast("Can't create the Robot Config Files directory!", this.activity);
            }
        }
    }

    public ArrayList<String> getXMLFiles() {
        File[] listFiles = new File(CONFIG_FILES_DIR).listFiles();
        if (listFiles == null) {
            RobotLog.i("robotConfigFiles directory is empty");
            return new ArrayList<String>();
        }
        ArrayList<String> arrayList = new ArrayList<String>();
        for (File file : listFiles) {
            if (file.isFile()) {
                String name = file.getName();
                if (Pattern.compile("(?i).xml").matcher(name).find()) {
                    arrayList.add(name.replaceFirst("[.][^.]+$", ""));
                }
            }
        }
        return arrayList;
    }

    public boolean writeXML(Map<SerialNumber, ControllerConfiguration> deviceControllers) {
        ArrayList<ControllerConfiguration> arrayList = new ArrayList<ControllerConfiguration>();
        arrayList.addAll(deviceControllers.values());
        try {
            this.xmlOutput = this.writeXMLFileHandler.writeXml(arrayList);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Duplicate name")) {
                complainToast("Found " + e.getMessage(), this.activity);
                RobotLog.e("Found " + e.getMessage());
                return true;
            } else {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void writeToFile(String filename) throws RobotCoreException, IOException {
        this.writeXMLFileHandler.writeToFile(this.xmlOutput, CONFIG_FILES_DIR, filename);
    }

    public String getOutput() {
        return this.xmlOutput;
    }

    public void complainToast(String msg, Context context) {
        Toast makeText = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        makeText.setGravity(17, 0, 0);
        TextView textView = (TextView) makeText.getView().findViewById(android.R.id.message);
        textView.setTextColor(-1);
        textView.setTextSize(18.0f);
        makeText.show();
    }

    public void createLists(Set<Entry<SerialNumber, DeviceType>> entries, Map<SerialNumber, ControllerConfiguration> deviceControllers) {
        for (Entry<SerialNumber, DeviceType> entry : entries) {
            switch (entry.getValue()) {
                case MODERN_ROBOTICS_USB_DC_MOTOR_CONTROLLER:
                    deviceControllers.put(entry.getKey(), buildMotorController(entry.getKey()));
                    break;
                case MODERN_ROBOTICS_USB_SERVO_CONTROLLER:
                    deviceControllers.put(entry.getKey(), buildServoController(entry.getKey()));
                    break;
                case MODERN_ROBOTICS_USB_LEGACY_MODULE:
                    deviceControllers.put(entry.getKey(), buildLegacyModule(entry.getKey()));
                    break;
                case MODERN_ROBOTICS_USB_DEVICE_INTERFACE_MODULE:
                    deviceControllers.put(entry.getKey(), buildDeviceInterfaceModule(entry.getKey()));
                    break;
                default:
                    break;
            }
        }
    }

    public DeviceInterfaceModuleConfiguration buildDeviceInterfaceModule(SerialNumber serialNumber) {
        DeviceInterfaceModuleConfiguration deviceInterfaceModuleConfiguration = new DeviceInterfaceModuleConfiguration("Device Interface Module " + count, serialNumber);
        deviceInterfaceModuleConfiguration.setPwmDevices(createPWMList());
        deviceInterfaceModuleConfiguration.setI2cDevices(createI2CList());
        deviceInterfaceModuleConfiguration.setAnalogInputDevices(createAnalogInputList());
        deviceInterfaceModuleConfiguration.setDigitalDevices(createDigitalList());
        deviceInterfaceModuleConfiguration.setAnalogOutputDevices(createAnalogOutputList());
        count++;
        return deviceInterfaceModuleConfiguration;
    }

    public LegacyModuleControllerConfiguration buildLegacyModule(SerialNumber serialNumber) {
        LegacyModuleControllerConfiguration legacyModuleControllerConfiguration = new LegacyModuleControllerConfiguration("Legacy Module " + count, createLegacyModuleList(), serialNumber);
        count++;
        return legacyModuleControllerConfiguration;
    }

    public ServoControllerConfiguration buildServoController(SerialNumber serialNumber) {
        ServoControllerConfiguration servoControllerConfiguration = new ServoControllerConfiguration("Servo Controller " + count, createServoList(), serialNumber);
        count++;
        return servoControllerConfiguration;
    }

    public MotorControllerConfiguration buildMotorController(SerialNumber serialNumber) {
        MotorControllerConfiguration motorControllerConfiguration = new MotorControllerConfiguration("Motor Controller " + count, createMotorList(), serialNumber);
        count++;
        return motorControllerConfiguration;
    }

    public ArrayList<DeviceConfiguration> createMotorList() {
        ArrayList<DeviceConfiguration> arrayList = new ArrayList<DeviceConfiguration>();
        for (int i = 0; i < MOTOR_PORTS; i++) {
            arrayList.add(new MotorConfiguration(i));
        }
        return arrayList;
    }

    public ArrayList<DeviceConfiguration> createServoList() {
        ArrayList<DeviceConfiguration> arrayList = new ArrayList<DeviceConfiguration>();
        for (int i = 1; i <= SERVO_PORTS; i++) {
            arrayList.add(new ServoConfiguration(i));
        }
        return arrayList;
    }

    public ArrayList<DeviceConfiguration> createLegacyModuleList() {
        ArrayList<DeviceConfiguration> arrayList = new ArrayList<DeviceConfiguration>();
        for (int i = 0; i < LEGACY_MODULE_PORTS; i++) {
            arrayList.add(new DeviceConfiguration(i, ConfigurationType.NOTHING));
        }
        return arrayList;
    }

    public ArrayList<DeviceConfiguration> createPWMList() {
        ArrayList<DeviceConfiguration> arrayList = new ArrayList<DeviceConfiguration>();
        for (int i = 0; i < PWD_PORTS; i++) {
            arrayList.add(new DeviceConfiguration(i, ConfigurationType.PULSE_WIDTH_DEVICE));
        }
        return arrayList;
    }

    public ArrayList<DeviceConfiguration> createI2CList() {
        ArrayList<DeviceConfiguration> arrayList = new ArrayList<DeviceConfiguration>();
        for (int i = 0; i < I2C_PORTS; i++) {
            arrayList.add(new DeviceConfiguration(i, ConfigurationType.I2C_DEVICE));
        }
        return arrayList;
    }

    public ArrayList<DeviceConfiguration> createAnalogInputList() {
        ArrayList<DeviceConfiguration> arrayList = new ArrayList<DeviceConfiguration>();
        for (int i = 0; i < ANALOG_INPUT_PORTS; i++) {
            arrayList.add(new DeviceConfiguration(i, ConfigurationType.ANALOG_INPUT));
        }
        return arrayList;
    }

    public ArrayList<DeviceConfiguration> createDigitalList() {
        ArrayList<DeviceConfiguration> arrayList = new ArrayList<DeviceConfiguration>();
        for (int i = 0; i < DIGITAL_PORTS; i++) {
            arrayList.add(new DeviceConfiguration(i, ConfigurationType.DIGITAL_DEVICE));
        }
        return arrayList;
    }

    public ArrayList<DeviceConfiguration> createAnalogOutputList() {
        ArrayList<DeviceConfiguration> arrayList = new ArrayList<DeviceConfiguration>();
        for (int i = 0; i < ANALOG_OUTPUT_PORTS; i++) {
            arrayList.add(new DeviceConfiguration(i, ConfigurationType.ANALOG_OUTPUT));
        }
        return arrayList;
    }

    public void updateHeader(String default_name, int pref_hardware_config_filename_id, int fileTextView, int header_id) {
        String replaceFirst = this.preferences.getString(this.activity.getString(pref_hardware_config_filename_id), default_name).replaceFirst("[.][^.]+$", "");
        ((TextView) this.activity.findViewById(fileTextView)).setText(replaceFirst);
        if (replaceFirst.equalsIgnoreCase(NO_FILE)) {
            changeBackground(Color.parseColor("#bf0510"), header_id);
        } else if (replaceFirst.toLowerCase().contains(UNSAVED.toLowerCase())) {
            changeBackground(-12303292, header_id);
        } else {
            changeBackground(Color.parseColor("#790E15"), header_id);
        }
    }

    public void saveToPreferences(String filename, int pref_hardware_config_filename_id) {
        filename = filename.replaceFirst("[.][^.]+$", "");
        Editor edit = this.preferences.edit();
        edit.putString(this.activity.getString(pref_hardware_config_filename_id), filename);
        edit.apply();
    }

    public void changeBackground(int color, int header_id) {
        (this.activity.findViewById(header_id)).setBackgroundColor(color);
    }

    public String getFilenameFromPrefs(int pref_hardware_config_filename_id, String default_name) {
        return this.preferences.getString(this.activity.getString(pref_hardware_config_filename_id), default_name);
    }

    public void resetCount() {
        count = 1;
    }

    public void setOrangeText(String msg0, String msg1, int info_id, int layout_id, int orange0, int orange1) {
        LinearLayout linearLayout = (LinearLayout) this.activity.findViewById(info_id);
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout.removeAllViews();
        this.activity.getLayoutInflater().inflate(layout_id, linearLayout, true);
        TextView textView = (TextView) linearLayout.findViewById(orange0);
        TextView textView2 = (TextView) linearLayout.findViewById(orange1);
        textView2.setGravity(3);
        textView.setText(msg0);
        textView2.setText(msg1);
    }

    public void confirmSave() {
        Toast makeText = Toast.makeText(this.activity, "Saved", Toast.LENGTH_SHORT);
        makeText.setGravity(80, 0, 50);
        makeText.show();
    }

    public Builder buildBuilder(String title, String message) {
        Builder builder = new Builder(this.activity);
        builder.setTitle(title).setMessage(message);
        return builder;
    }

    public String prepareFilename(String currentFile) {
        if (currentFile.toLowerCase().contains(UNSAVED.toLowerCase())) {
            currentFile = currentFile.substring(UNSAVED.length() + 1).trim();
        }
        if (currentFile.equalsIgnoreCase(NO_FILE)) {
            return "";
        }
        return currentFile;
    }
}