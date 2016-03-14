package com.qualcomm.ftccommon.configuration;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.qualcomm.ftccommon.BuildConfig;
import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.ftccommon.R;
import com.qualcomm.hardware.HardwareDeviceManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DeviceManager;
import com.qualcomm.robotcore.hardware.DeviceManager.DeviceType;
import com.qualcomm.robotcore.hardware.configuration.ControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import com.qualcomm.robotcore.hardware.configuration.LegacyModuleControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.MotorConfiguration;
import com.qualcomm.robotcore.hardware.configuration.MotorControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.ServoConfiguration;
import com.qualcomm.robotcore.hardware.configuration.ServoControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.Utility;
import com.qualcomm.robotcore.util.SerialNumber;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AutoConfigureActivity extends Activity {
    private Context f97a;
    private Button f98b;
    private Button f99c;
    private DeviceManager f100d;
    private Map<SerialNumber, ControllerConfiguration> f101e;
    protected Set<Entry<SerialNumber, DeviceType>> entries;
    private Thread f102f;
    private Utility f103g;
    protected Map<SerialNumber, DeviceType> scannedDevices;

    /* renamed from: com.qualcomm.ftccommon.configuration.AutoConfigureActivity.1 */
    class C00341 implements OnClickListener {
        final /* synthetic */ AutoConfigureActivity f93a;

        /* renamed from: com.qualcomm.ftccommon.configuration.AutoConfigureActivity.1.1 */
        class C00331 implements Runnable {
            final /* synthetic */ C00341 f92a;

            /* renamed from: com.qualcomm.ftccommon.configuration.AutoConfigureActivity.1.1.1 */
            class C00321 implements Runnable {
                final /* synthetic */ C00331 f91a;

                C00321(C00331 c00331) {
                    this.f91a = c00331;
                }

                public void run() {
                    this.f91a.f92a.f93a.f103g.resetCount();
                    if (this.f91a.f92a.f93a.scannedDevices.size() == 0) {
                        this.f91a.f92a.f93a.f103g.saveToPreferences("No current file!", R.string.pref_hardware_config_filename);
                        this.f91a.f92a.f93a.f103g.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
                        this.f91a.f92a.f93a.m52a();
                    }
                    this.f91a.f92a.f93a.entries = this.f91a.f92a.f93a.scannedDevices.entrySet();
                    this.f91a.f92a.f93a.f101e = new HashMap();
                    this.f91a.f92a.f93a.f103g.createLists(this.f91a.f92a.f93a.entries, this.f91a.f92a.f93a.f101e);
                    if (this.f91a.f92a.f93a.m68g()) {
                        this.f91a.f92a.f93a.m55a("K9LegacyBot");
                        return;
                    }
                    this.f91a.f92a.f93a.f103g.saveToPreferences("No current file!", R.string.pref_hardware_config_filename);
                    this.f91a.f92a.f93a.f103g.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
                    this.f91a.f92a.f93a.m57b();
                }
            }

            C00331(C00341 c00341) {
                this.f92a = c00341;
            }

            public void run() {
                try {
                    DbgLog.msg("Scanning USB bus");
                    this.f92a.f93a.scannedDevices = this.f92a.f93a.f100d.scanForUsbDevices();
                } catch (RobotCoreException e) {
                    DbgLog.error("Device scan failed");
                }
                this.f92a.f93a.runOnUiThread(new C00321(this));
            }
        }

        C00341(AutoConfigureActivity autoConfigureActivity) {
            this.f93a = autoConfigureActivity;
        }

        public void onClick(View view) {
            this.f93a.f102f = new Thread(new C00331(this));
            this.f93a.f102f.start();
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.AutoConfigureActivity.2 */
    class C00372 implements OnClickListener {
        final /* synthetic */ AutoConfigureActivity f96a;

        /* renamed from: com.qualcomm.ftccommon.configuration.AutoConfigureActivity.2.1 */
        class C00361 implements Runnable {
            final /* synthetic */ C00372 f95a;

            /* renamed from: com.qualcomm.ftccommon.configuration.AutoConfigureActivity.2.1.1 */
            class C00351 implements Runnable {
                final /* synthetic */ C00361 f94a;

                C00351(C00361 c00361) {
                    this.f94a = c00361;
                }

                public void run() {
                    this.f94a.f95a.f96a.f103g.resetCount();
                    if (this.f94a.f95a.f96a.scannedDevices.size() == 0) {
                        this.f94a.f95a.f96a.f103g.saveToPreferences("No current file!", R.string.pref_hardware_config_filename);
                        this.f94a.f95a.f96a.f103g.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
                        this.f94a.f95a.f96a.m52a();
                    }
                    this.f94a.f95a.f96a.entries = this.f94a.f95a.f96a.scannedDevices.entrySet();
                    this.f94a.f95a.f96a.f101e = new HashMap();
                    this.f94a.f95a.f96a.f103g.createLists(this.f94a.f95a.f96a.entries, this.f94a.f95a.f96a.f101e);
                    if (this.f94a.f95a.f96a.m63e()) {
                        this.f94a.f95a.f96a.m55a("K9USBBot");
                        return;
                    }
                    this.f94a.f95a.f96a.f103g.saveToPreferences("No current file!", R.string.pref_hardware_config_filename);
                    this.f94a.f95a.f96a.f103g.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
                    this.f94a.f95a.f96a.m59c();
                }
            }

            C00361(C00372 c00372) {
                this.f95a = c00372;
            }

            public void run() {
                try {
                    DbgLog.msg("Scanning USB bus");
                    this.f95a.f96a.scannedDevices = this.f95a.f96a.f100d.scanForUsbDevices();
                } catch (RobotCoreException e) {
                    DbgLog.error("Device scan failed");
                }
                this.f95a.f96a.runOnUiThread(new C00351(this));
            }
        }

        C00372(AutoConfigureActivity autoConfigureActivity) {
            this.f96a = autoConfigureActivity;
        }

        public void onClick(View view) {
            this.f96a.f102f = new Thread(new C00361(this));
            this.f96a.f102f.start();
        }
    }

    public AutoConfigureActivity() {
        this.scannedDevices = new HashMap();
        this.entries = new HashSet();
        this.f101e = new HashMap();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.f97a = this;
        setContentView(R.layout.activity_autoconfigure);
        this.f103g = new Utility(this);
        this.f98b = (Button) findViewById(R.id.configureLegacy);
        this.f99c = (Button) findViewById(R.id.configureUSB);
        try {
            this.f100d = new HardwareDeviceManager(this.f97a, null);
        } catch (RobotCoreException e) {
            this.f103g.complainToast("Failed to open the Device Manager", this.f97a);
            DbgLog.error("Failed to open deviceManager: " + e.toString());
            DbgLog.logStacktrace(e);
        }
    }

    protected void onStart() {
        super.onStart();
        this.f103g.updateHeader("K9LegacyBot", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        String filenameFromPrefs = this.f103g.getFilenameFromPrefs(R.string.pref_hardware_config_filename, "No current file!");
        if (filenameFromPrefs.equals("K9LegacyBot") || filenameFromPrefs.equals("K9USBBot")) {
            m62d();
        } else {
            m52a();
        }
        this.f98b.setOnClickListener(new C00341(this));
        this.f99c.setOnClickListener(new C00372(this));
    }

    private void m55a(String str) {
        this.f103g.writeXML(this.f101e);
        try {
            this.f103g.writeToFile(str + ".xml");
            this.f103g.saveToPreferences(str, R.string.pref_hardware_config_filename);
            this.f103g.updateHeader(str, R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
            Toast.makeText(this.f97a, "AutoConfigure " + str + " Successful", 0).show();
        } catch (RobotCoreException e) {
            this.f103g.complainToast(e.getMessage(), this.f97a);
            DbgLog.error(e.getMessage());
        } catch (IOException e2) {
            this.f103g.complainToast("Found " + e2.getMessage() + "\n Please fix and re-save", this.f97a);
            DbgLog.error(e2.getMessage());
        }
    }

    private void m52a() {
        this.f103g.setOrangeText("No devices found!", "To configure K9LegacyBot, please: \n   1. Attach a LegacyModuleController, \n       with \n       a. MotorController in port 0, with a \n         motor in port 1 and port 2 \n       b. ServoController in port 1, with a \n         servo in port 1 and port 6 \n      c. IR seeker in port 2\n      d. Light sensor in port 3 \n   2. Press the K9LegacyBot button\n \nTo configure K9USBBot, please: \n   1. Attach a USBMotorController, with a \n       motor in port 1 and port 2 \n    2. USBServoController in port 1, with a \n      servo in port 1 and port 6 \n   3. LegacyModule, with \n      a. IR seeker in port 2\n      b. Light sensor in port 3 \n   4. Press the K9USBBot button", R.id.autoconfigure_info, R.layout.orange_warning, R.id.orangetext0, R.id.orangetext1);
    }

    private void m57b() {
        String str = "Found: \n" + this.scannedDevices.values() + "\n" + "Required: \n" + "   1. LegacyModuleController, with \n " + "      a. MotorController in port 0, with a \n" + "          motor in port 1 and port 2 \n " + "      b. ServoController in port 1, with a \n" + "          servo in port 1 and port 6 \n" + "       c. IR seeker in port 2\n" + "       d. Light sensor in port 3 ";
        this.f103g.setOrangeText("Wrong devices found!", str, R.id.autoconfigure_info, R.layout.orange_warning, R.id.orangetext0, R.id.orangetext1);
    }

    private void m59c() {
        String str = "Found: \n" + this.scannedDevices.values() + "\n" + "Required: \n" + "   1. USBMotorController with a \n" + "      motor in port 1 and port 2 \n " + "   2. USBServoController with a \n" + "      servo in port 1 and port 6 \n" + "   3. LegacyModuleController, with \n " + "       a. IR seeker in port 2\n" + "       b. Light sensor in port 3 ";
        this.f103g.setOrangeText("Wrong devices found!", str, R.id.autoconfigure_info, R.layout.orange_warning, R.id.orangetext0, R.id.orangetext1);
    }

    private void m62d() {
        String str = BuildConfig.VERSION_NAME;
        this.f103g.setOrangeText("Already configured!", str, R.id.autoconfigure_info, R.layout.orange_warning, R.id.orangetext0, R.id.orangetext1);
    }

    private boolean m63e() {
        boolean z = true;
        boolean z2 = true;
        boolean z3 = true;
        for (Entry entry : this.entries) {
            boolean z4;
            boolean z5;
            DeviceType deviceType = (DeviceType) entry.getValue();
            if (deviceType == DeviceType.MODERN_ROBOTICS_USB_LEGACY_MODULE && z3) {
                m54a((SerialNumber) entry.getKey(), "sensors");
                z4 = false;
            } else {
                z4 = z3;
            }
            if (deviceType == DeviceType.MODERN_ROBOTICS_USB_DC_MOTOR_CONTROLLER && z2) {
                m48a((SerialNumber) entry.getKey(), "motor_1", "motor_2", "wheels");
                z3 = false;
            } else {
                z3 = z2;
            }
            if (deviceType == DeviceType.MODERN_ROBOTICS_USB_SERVO_CONTROLLER && z) {
                m49a((SerialNumber) entry.getKey(), m65f(), "servos");
                z5 = false;
            } else {
                z5 = z;
            }
            z = z5;
            z2 = z3;
            z3 = z4;
        }
        if (z3 || z2 || z) {
            return false;
        }
        ((LinearLayout) findViewById(R.id.autoconfigure_info)).removeAllViews();
        return true;
    }

    private ArrayList<String> m65f() {
        ArrayList<String> arrayList = new ArrayList();
        arrayList.add("servo_1");
        arrayList.add("NO DEVICE ATTACHED");
        arrayList.add("NO DEVICE ATTACHED");
        arrayList.add("NO DEVICE ATTACHED");
        arrayList.add("NO DEVICE ATTACHED");
        arrayList.add("servo_6");
        return arrayList;
    }

    private void m54a(SerialNumber serialNumber, String str) {
        LegacyModuleControllerConfiguration legacyModuleControllerConfiguration = (LegacyModuleControllerConfiguration) this.f101e.get(serialNumber);
        legacyModuleControllerConfiguration.setName(str);
        DeviceConfiguration a = m47a(ConfigurationType.IR_SEEKER, "ir_seeker", 2);
        DeviceConfiguration a2 = m47a(ConfigurationType.LIGHT_SENSOR, "light_sensor", 3);
        List arrayList = new ArrayList();
        for (int i = 0; i < 6; i++) {
            if (i == 2) {
                arrayList.add(a);
            }
            if (i == 3) {
                arrayList.add(a2);
            } else {
                arrayList.add(new DeviceConfiguration(i));
            }
        }
        legacyModuleControllerConfiguration.addDevices(arrayList);
    }

    private boolean m68g() {
        boolean z = true;
        for (Entry entry : this.entries) {
            boolean z2;
            if (((DeviceType) entry.getValue()) == DeviceType.MODERN_ROBOTICS_USB_LEGACY_MODULE && z) {
                m58b((SerialNumber) entry.getKey(), "devices");
                z2 = false;
            } else {
                z2 = z;
            }
            z = z2;
        }
        if (z) {
            return false;
        }
        ((LinearLayout) findViewById(R.id.autoconfigure_info)).removeAllViews();
        return true;
    }

    private void m58b(SerialNumber serialNumber, String str) {
        LegacyModuleControllerConfiguration legacyModuleControllerConfiguration = (LegacyModuleControllerConfiguration) this.f101e.get(serialNumber);
        legacyModuleControllerConfiguration.setName(str);
        MotorControllerConfiguration a = m48a(ControllerConfiguration.NO_SERIAL_NUMBER, "motor_1", "motor_2", "wheels");
        a.setPort(0);
        ServoControllerConfiguration a2 = m49a(ControllerConfiguration.NO_SERIAL_NUMBER, m65f(), "servos");
        a2.setPort(1);
        DeviceConfiguration a3 = m47a(ConfigurationType.IR_SEEKER, "ir_seeker", 2);
        DeviceConfiguration a4 = m47a(ConfigurationType.LIGHT_SENSOR, "light_sensor", 3);
        List arrayList = new ArrayList();
        arrayList.add(a);
        arrayList.add(a2);
        arrayList.add(a3);
        arrayList.add(a4);
        for (int i = 4; i < 6; i++) {
            arrayList.add(new DeviceConfiguration(i));
        }
        legacyModuleControllerConfiguration.addDevices(arrayList);
    }

    private DeviceConfiguration m47a(ConfigurationType configurationType, String str, int i) {
        return new DeviceConfiguration(i, configurationType, str, true);
    }

    private MotorControllerConfiguration m48a(SerialNumber serialNumber, String str, String str2, String str3) {
        MotorControllerConfiguration motorControllerConfiguration;
        if (serialNumber.equals(ControllerConfiguration.NO_SERIAL_NUMBER)) {
            motorControllerConfiguration = new MotorControllerConfiguration();
        } else {
            motorControllerConfiguration = (MotorControllerConfiguration) this.f101e.get(serialNumber);
        }
        motorControllerConfiguration.setName(str3);
        List arrayList = new ArrayList();
        MotorConfiguration motorConfiguration = new MotorConfiguration(1, str, true);
        MotorConfiguration motorConfiguration2 = new MotorConfiguration(2, str2, true);
        arrayList.add(motorConfiguration);
        arrayList.add(motorConfiguration2);
        motorControllerConfiguration.addMotors(arrayList);
        return motorControllerConfiguration;
    }

    private ServoControllerConfiguration m49a(SerialNumber serialNumber, ArrayList<String> arrayList, String str) {
        ServoControllerConfiguration servoControllerConfiguration;
        if (serialNumber.equals(ControllerConfiguration.NO_SERIAL_NUMBER)) {
            servoControllerConfiguration = new ServoControllerConfiguration();
        } else {
            servoControllerConfiguration = (ServoControllerConfiguration) this.f101e.get(serialNumber);
        }
        servoControllerConfiguration.setName(str);
        ArrayList arrayList2 = new ArrayList();
        for (int i = 1; i <= 6; i++) {
            boolean z;
            String str2 = (String) arrayList.get(i - 1);
            if (str2.equals("NO DEVICE ATTACHED")) {
                z = false;
            } else {
                z = true;
            }
            arrayList2.add(new ServoConfiguration(i, str2, z));
        }
        servoControllerConfiguration.addServos(arrayList2);
        return servoControllerConfiguration;
    }
}
