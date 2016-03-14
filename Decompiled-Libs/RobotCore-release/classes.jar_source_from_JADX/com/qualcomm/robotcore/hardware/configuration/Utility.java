package com.qualcomm.robotcore.hardware.configuration;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;
import com.qualcomm.robotcore.BuildConfig;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DeviceManager.DeviceType;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import com.qualcomm.robotcore.robocol.RobocolConfig;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

public class Utility {
    public static final String AUTOCONFIGURE_K9LEGACYBOT = "K9LegacyBot";
    public static final String AUTOCONFIGURE_K9USBBOT = "K9USBBot";
    public static final String CONFIG_FILES_DIR;
    public static final String DEFAULT_ROBOT_CONFIG = "robot_config";
    public static final String DEFAULT_ROBOT_CONFIG_FILENAME = "robot_config.xml";
    public static final String FILE_EXT = ".xml";
    public static final String NO_FILE = "No current file!";
    public static final String UNSAVED = "Unsaved";
    private static int f289c;
    private Activity f290a;
    private SharedPreferences f291b;
    private WriteXMLFileHandler f292d;
    private String f293e;

    /* renamed from: com.qualcomm.robotcore.hardware.configuration.Utility.1 */
    static /* synthetic */ class C00361 {
        static final /* synthetic */ int[] f288a;

        static {
            f288a = new int[DeviceType.values().length];
            try {
                f288a[DeviceType.MODERN_ROBOTICS_USB_DC_MOTOR_CONTROLLER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f288a[DeviceType.MODERN_ROBOTICS_USB_SERVO_CONTROLLER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f288a[DeviceType.MODERN_ROBOTICS_USB_LEGACY_MODULE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f288a[DeviceType.MODERN_ROBOTICS_USB_DEVICE_INTERFACE_MODULE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    static {
        CONFIG_FILES_DIR = Environment.getExternalStorageDirectory() + "/FIRST/";
        f289c = 1;
    }

    public Utility(Activity activity) {
        this.f290a = activity;
        this.f291b = PreferenceManager.getDefaultSharedPreferences(activity);
        this.f292d = new WriteXMLFileHandler(activity);
    }

    public void createConfigFolder() {
        File file = new File(CONFIG_FILES_DIR);
        boolean z = true;
        if (!file.exists()) {
            z = file.mkdir();
        }
        if (!z) {
            RobotLog.m231e("Can't create the Robot Config Files directory!");
            complainToast("Can't create the Robot Config Files directory!", this.f290a);
        }
    }

    public ArrayList<String> getXMLFiles() {
        File[] listFiles = new File(CONFIG_FILES_DIR).listFiles();
        if (listFiles == null) {
            RobotLog.m232i("robotConfigFiles directory is empty");
            return new ArrayList();
        }
        ArrayList<String> arrayList = new ArrayList();
        for (File file : listFiles) {
            if (file.isFile()) {
                Object name = file.getName();
                if (Pattern.compile("(?i).xml").matcher(name).find()) {
                    arrayList.add(name.replaceFirst("[.][^.]+$", BuildConfig.VERSION_NAME));
                }
            }
        }
        return arrayList;
    }

    public boolean writeXML(Map<SerialNumber, ControllerConfiguration> deviceControllers) {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(deviceControllers.values());
        try {
            this.f293e = this.f292d.writeXml(arrayList);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Duplicate name")) {
                complainToast("Found " + e.getMessage(), this.f290a);
                RobotLog.m231e("Found " + e.getMessage());
                return true;
            }
        }
        return false;
    }

    public void writeToFile(String filename) throws RobotCoreException, IOException {
        this.f292d.writeToFile(this.f293e, CONFIG_FILES_DIR, filename);
    }

    public String getOutput() {
        return this.f293e;
    }

    public void complainToast(String msg, Context context) {
        Toast makeText = Toast.makeText(context, msg, 0);
        makeText.setGravity(17, 0, 0);
        TextView textView = (TextView) makeText.getView().findViewById(16908299);
        textView.setTextColor(-1);
        textView.setTextSize(18.0f);
        makeText.show();
    }

    public void createLists(Set<Entry<SerialNumber, DeviceType>> entries, Map<SerialNumber, ControllerConfiguration> deviceControllers) {
        for (Entry entry : entries) {
            switch (C00361.f288a[((DeviceType) entry.getValue()).ordinal()]) {
                case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
                    deviceControllers.put(entry.getKey(), buildMotorController((SerialNumber) entry.getKey()));
                    break;
                case SpiSlaveResponseEvent.IO_ERROR /*2*/:
                    deviceControllers.put(entry.getKey(), buildServoController((SerialNumber) entry.getKey()));
                    break;
                case RobocolConfig.TTL /*3*/:
                    deviceControllers.put(entry.getKey(), buildLegacyModule((SerialNumber) entry.getKey()));
                    break;
                case FT_4222_Defines.DEBUG_REQ_READ_SFR /*4*/:
                    deviceControllers.put(entry.getKey(), buildDeviceInterfaceModule((SerialNumber) entry.getKey()));
                    break;
                default:
                    break;
            }
        }
    }

    public DeviceInterfaceModuleConfiguration buildDeviceInterfaceModule(SerialNumber serialNumber) {
        DeviceInterfaceModuleConfiguration deviceInterfaceModuleConfiguration = new DeviceInterfaceModuleConfiguration("Device Interface Module " + f289c, serialNumber);
        deviceInterfaceModuleConfiguration.setPwmDevices(createPWMList());
        deviceInterfaceModuleConfiguration.setI2cDevices(createI2CList());
        deviceInterfaceModuleConfiguration.setAnalogInputDevices(createAnalogInputList());
        deviceInterfaceModuleConfiguration.setDigitalDevices(createDigitalList());
        deviceInterfaceModuleConfiguration.setAnalogOutputDevices(createAnalogOutputList());
        f289c++;
        return deviceInterfaceModuleConfiguration;
    }

    public LegacyModuleControllerConfiguration buildLegacyModule(SerialNumber serialNumber) {
        LegacyModuleControllerConfiguration legacyModuleControllerConfiguration = new LegacyModuleControllerConfiguration("Legacy Module " + f289c, createLegacyModuleList(), serialNumber);
        f289c++;
        return legacyModuleControllerConfiguration;
    }

    public ServoControllerConfiguration buildServoController(SerialNumber serialNumber) {
        ServoControllerConfiguration servoControllerConfiguration = new ServoControllerConfiguration("Servo Controller " + f289c, createServoList(), serialNumber);
        f289c++;
        return servoControllerConfiguration;
    }

    public MotorControllerConfiguration buildMotorController(SerialNumber serialNumber) {
        MotorControllerConfiguration motorControllerConfiguration = new MotorControllerConfiguration("Motor Controller " + f289c, createMotorList(), serialNumber);
        f289c++;
        return motorControllerConfiguration;
    }

    public ArrayList<DeviceConfiguration> createMotorList() {
        ArrayList<DeviceConfiguration> arrayList = new ArrayList();
        arrayList.add(new MotorConfiguration(1));
        arrayList.add(new MotorConfiguration(2));
        return arrayList;
    }

    public ArrayList<DeviceConfiguration> createServoList() {
        ArrayList<DeviceConfiguration> arrayList = new ArrayList();
        for (int i = 1; i <= 6; i++) {
            arrayList.add(new ServoConfiguration(i));
        }
        return arrayList;
    }

    public ArrayList<DeviceConfiguration> createLegacyModuleList() {
        ArrayList<DeviceConfiguration> arrayList = new ArrayList();
        for (int i = 0; i < 6; i++) {
            arrayList.add(new DeviceConfiguration(i, ConfigurationType.NOTHING));
        }
        return arrayList;
    }

    public ArrayList<DeviceConfiguration> createPWMList() {
        ArrayList<DeviceConfiguration> arrayList = new ArrayList();
        for (int i = 0; i < 2; i++) {
            arrayList.add(new DeviceConfiguration(i, ConfigurationType.PULSE_WIDTH_DEVICE));
        }
        return arrayList;
    }

    public ArrayList<DeviceConfiguration> createI2CList() {
        ArrayList<DeviceConfiguration> arrayList = new ArrayList();
        for (int i = 0; i < 6; i++) {
            arrayList.add(new DeviceConfiguration(i, ConfigurationType.I2C_DEVICE));
        }
        return arrayList;
    }

    public ArrayList<DeviceConfiguration> createAnalogInputList() {
        ArrayList<DeviceConfiguration> arrayList = new ArrayList();
        for (int i = 0; i < 8; i++) {
            arrayList.add(new DeviceConfiguration(i, ConfigurationType.ANALOG_INPUT));
        }
        return arrayList;
    }

    public ArrayList<DeviceConfiguration> createDigitalList() {
        ArrayList<DeviceConfiguration> arrayList = new ArrayList();
        for (int i = 0; i < 8; i++) {
            arrayList.add(new DeviceConfiguration(i, ConfigurationType.DIGITAL_DEVICE));
        }
        return arrayList;
    }

    public ArrayList<DeviceConfiguration> createAnalogOutputList() {
        ArrayList<DeviceConfiguration> arrayList = new ArrayList();
        for (int i = 0; i < 2; i++) {
            arrayList.add(new DeviceConfiguration(i, ConfigurationType.ANALOG_OUTPUT));
        }
        return arrayList;
    }

    public void updateHeader(String default_name, int pref_hardware_config_filename_id, int fileTextView, int header_id) {
        String replaceFirst = this.f291b.getString(this.f290a.getString(pref_hardware_config_filename_id), default_name).replaceFirst("[.][^.]+$", BuildConfig.VERSION_NAME);
        ((TextView) this.f290a.findViewById(fileTextView)).setText(replaceFirst);
        if (replaceFirst.equalsIgnoreCase(NO_FILE)) {
            changeBackground(Color.parseColor("#bf0510"), header_id);
        } else if (replaceFirst.toLowerCase().contains(UNSAVED.toLowerCase())) {
            changeBackground(-12303292, header_id);
        } else {
            changeBackground(Color.parseColor("#790E15"), header_id);
        }
    }

    public void saveToPreferences(String filename, int pref_hardware_config_filename_id) {
        filename = filename.replaceFirst("[.][^.]+$", BuildConfig.VERSION_NAME);
        Editor edit = this.f291b.edit();
        edit.putString(this.f290a.getString(pref_hardware_config_filename_id), filename);
        edit.apply();
    }

    public void changeBackground(int color, int header_id) {
        ((LinearLayout) this.f290a.findViewById(header_id)).setBackgroundColor(color);
    }

    public String getFilenameFromPrefs(int pref_hardware_config_filename_id, String default_name) {
        return this.f291b.getString(this.f290a.getString(pref_hardware_config_filename_id), default_name);
    }

    public void resetCount() {
        f289c = 1;
    }

    public void setOrangeText(String msg0, String msg1, int info_id, int layout_id, int orange0, int orange1) {
        LinearLayout linearLayout = (LinearLayout) this.f290a.findViewById(info_id);
        linearLayout.setVisibility(0);
        linearLayout.removeAllViews();
        this.f290a.getLayoutInflater().inflate(layout_id, linearLayout, true);
        TextView textView = (TextView) linearLayout.findViewById(orange0);
        TextView textView2 = (TextView) linearLayout.findViewById(orange1);
        textView2.setGravity(3);
        textView.setText(msg0);
        textView2.setText(msg1);
    }

    public void confirmSave() {
        Toast makeText = Toast.makeText(this.f290a, "Saved", 0);
        makeText.setGravity(80, 0, 50);
        makeText.show();
    }

    public Builder buildBuilder(String title, String message) {
        Builder builder = new Builder(this.f290a);
        builder.setTitle(title).setMessage(message);
        return builder;
    }

    public String prepareFilename(String currentFile) {
        if (currentFile.toLowerCase().contains(UNSAVED.toLowerCase())) {
            currentFile = currentFile.substring(7).trim();
        }
        if (currentFile.equalsIgnoreCase(NO_FILE)) {
            return BuildConfig.VERSION_NAME;
        }
        return currentFile;
    }
}
