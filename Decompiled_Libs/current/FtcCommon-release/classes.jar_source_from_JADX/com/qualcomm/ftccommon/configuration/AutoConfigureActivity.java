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
    private Context f82a;
    private Button f83b;
    private Button f84c;
    private DeviceManager f85d;
    private Map<SerialNumber, ControllerConfiguration> f86e;
    protected Set<Entry<SerialNumber, DeviceType>> entries;
    private Thread f87f;
    private Utility f88g;
    protected Map<SerialNumber, DeviceType> scannedDevices;

    /* renamed from: com.qualcomm.ftccommon.configuration.AutoConfigureActivity.1 */
    class C00271 implements OnClickListener {
        final /* synthetic */ AutoConfigureActivity f78a;

        /* renamed from: com.qualcomm.ftccommon.configuration.AutoConfigureActivity.1.1 */
        class C00261 implements Runnable {
            final /* synthetic */ C00271 f77a;

            /* renamed from: com.qualcomm.ftccommon.configuration.AutoConfigureActivity.1.1.1 */
            class C00251 implements Runnable {
                final /* synthetic */ C00261 f76a;

                C00251(C00261 c00261) {
                    this.f76a = c00261;
                }

                public void run() {
                    this.f76a.f77a.f78a.f88g.resetCount();
                    if (this.f76a.f77a.f78a.scannedDevices.size() == 0) {
                        this.f76a.f77a.f78a.f88g.saveToPreferences("No current file!", R.string.pref_hardware_config_filename);
                        this.f76a.f77a.f78a.f88g.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
                        this.f76a.f77a.f78a.m41a();
                    }
                    this.f76a.f77a.f78a.entries = this.f76a.f77a.f78a.scannedDevices.entrySet();
                    this.f76a.f77a.f78a.f86e = new HashMap();
                    this.f76a.f77a.f78a.f88g.createLists(this.f76a.f77a.f78a.entries, this.f76a.f77a.f78a.f86e);
                    if (this.f76a.f77a.f78a.m57g()) {
                        this.f76a.f77a.f78a.m44a("K9LegacyBot");
                        return;
                    }
                    this.f76a.f77a.f78a.f88g.saveToPreferences("No current file!", R.string.pref_hardware_config_filename);
                    this.f76a.f77a.f78a.f88g.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
                    this.f76a.f77a.f78a.m46b();
                }
            }

            C00261(C00271 c00271) {
                this.f77a = c00271;
            }

            public void run() {
                try {
                    DbgLog.msg("Scanning USB bus");
                    this.f77a.f78a.scannedDevices = this.f77a.f78a.f85d.scanForUsbDevices();
                } catch (RobotCoreException e) {
                    DbgLog.error("Device scan failed");
                }
                this.f77a.f78a.runOnUiThread(new C00251(this));
            }
        }

        C00271(AutoConfigureActivity autoConfigureActivity) {
            this.f78a = autoConfigureActivity;
        }

        public void onClick(View view) {
            this.f78a.f87f = new Thread(new C00261(this));
            this.f78a.f87f.start();
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.AutoConfigureActivity.2 */
    class C00302 implements OnClickListener {
        final /* synthetic */ AutoConfigureActivity f81a;

        /* renamed from: com.qualcomm.ftccommon.configuration.AutoConfigureActivity.2.1 */
        class C00291 implements Runnable {
            final /* synthetic */ C00302 f80a;

            /* renamed from: com.qualcomm.ftccommon.configuration.AutoConfigureActivity.2.1.1 */
            class C00281 implements Runnable {
                final /* synthetic */ C00291 f79a;

                C00281(C00291 c00291) {
                    this.f79a = c00291;
                }

                public void run() {
                    this.f79a.f80a.f81a.f88g.resetCount();
                    if (this.f79a.f80a.f81a.scannedDevices.size() == 0) {
                        this.f79a.f80a.f81a.f88g.saveToPreferences("No current file!", R.string.pref_hardware_config_filename);
                        this.f79a.f80a.f81a.f88g.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
                        this.f79a.f80a.f81a.m41a();
                    }
                    this.f79a.f80a.f81a.entries = this.f79a.f80a.f81a.scannedDevices.entrySet();
                    this.f79a.f80a.f81a.f86e = new HashMap();
                    this.f79a.f80a.f81a.f88g.createLists(this.f79a.f80a.f81a.entries, this.f79a.f80a.f81a.f86e);
                    if (this.f79a.f80a.f81a.m52e()) {
                        this.f79a.f80a.f81a.m44a("K9USBBot");
                        return;
                    }
                    this.f79a.f80a.f81a.f88g.saveToPreferences("No current file!", R.string.pref_hardware_config_filename);
                    this.f79a.f80a.f81a.f88g.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
                    this.f79a.f80a.f81a.m48c();
                }
            }

            C00291(C00302 c00302) {
                this.f80a = c00302;
            }

            public void run() {
                try {
                    DbgLog.msg("Scanning USB bus");
                    this.f80a.f81a.scannedDevices = this.f80a.f81a.f85d.scanForUsbDevices();
                } catch (RobotCoreException e) {
                    DbgLog.error("Device scan failed");
                }
                this.f80a.f81a.runOnUiThread(new C00281(this));
            }
        }

        C00302(AutoConfigureActivity autoConfigureActivity) {
            this.f81a = autoConfigureActivity;
        }

        public void onClick(View view) {
            this.f81a.f87f = new Thread(new C00291(this));
            this.f81a.f87f.start();
        }
    }

    public AutoConfigureActivity() {
        this.scannedDevices = new HashMap();
        this.entries = new HashSet();
        this.f86e = new HashMap();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.f82a = this;
        setContentView(R.layout.activity_autoconfigure);
        this.f88g = new Utility(this);
        this.f83b = (Button) findViewById(R.id.configureLegacy);
        this.f84c = (Button) findViewById(R.id.configureUSB);
        try {
            this.f85d = new HardwareDeviceManager(this.f82a, null);
        } catch (RobotCoreException e) {
            this.f88g.complainToast("Failed to open the Device Manager", this.f82a);
            DbgLog.error("Failed to open deviceManager: " + e.toString());
            DbgLog.logStacktrace(e);
        }
    }

    protected void onStart() {
        super.onStart();
        this.f88g.updateHeader("K9LegacyBot", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        String filenameFromPrefs = this.f88g.getFilenameFromPrefs(R.string.pref_hardware_config_filename, "No current file!");
        if (filenameFromPrefs.equals("K9LegacyBot") || filenameFromPrefs.equals("K9USBBot")) {
            m51d();
        } else {
            m41a();
        }
        this.f83b.setOnClickListener(new C00271(this));
        this.f84c.setOnClickListener(new C00302(this));
    }

    private void m44a(String str) {
        this.f88g.writeXML(this.f86e);
        try {
            this.f88g.writeToFile(str + ".xml");
            this.f88g.saveToPreferences(str, R.string.pref_hardware_config_filename);
            this.f88g.updateHeader(str, R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
            Toast.makeText(this.f82a, "AutoConfigure " + str + " Successful", 0).show();
        } catch (RobotCoreException e) {
            this.f88g.complainToast(e.getMessage(), this.f82a);
            DbgLog.error(e.getMessage());
        } catch (IOException e2) {
            this.f88g.complainToast("Found " + e2.getMessage() + "\n Please fix and re-save", this.f82a);
            DbgLog.error(e2.getMessage());
        }
    }

    private void m41a() {
        this.f88g.setOrangeText("No devices found!", "To configure K9LegacyBot, please: \n   1. Attach a LegacyModuleController, \n       with \n       a. MotorController in port 0, with a \n         motor in port 1 and port 2 \n       b. ServoController in port 1, with a \n         servo in port 1 and port 6 \n      c. IR seeker in port 2\n      d. Light sensor in port 3 \n   2. Press the K9LegacyBot button\n \nTo configure K9USBBot, please: \n   1. Attach a USBMotorController, with a \n       motor in port 1 and port 2 \n    2. USBServoController in port 1, with a \n      servo in port 1 and port 6 \n   3. LegacyModule, with \n      a. IR seeker in port 2\n      b. Light sensor in port 3 \n   4. Press the K9USBBot button", R.id.autoconfigure_info, R.layout.orange_warning, R.id.orangetext0, R.id.orangetext1);
    }

    private void m46b() {
        String str = "Found: \n" + this.scannedDevices.values() + "\n" + "Required: \n" + "   1. LegacyModuleController, with \n " + "      a. MotorController in port 0, with a \n" + "          motor in port 1 and port 2 \n " + "      b. ServoController in port 1, with a \n" + "          servo in port 1 and port 6 \n" + "       c. IR seeker in port 2\n" + "       d. Light sensor in port 3 ";
        this.f88g.setOrangeText("Wrong devices found!", str, R.id.autoconfigure_info, R.layout.orange_warning, R.id.orangetext0, R.id.orangetext1);
    }

    private void m48c() {
        String str = "Found: \n" + this.scannedDevices.values() + "\n" + "Required: \n" + "   1. USBMotorController with a \n" + "      motor in port 1 and port 2 \n " + "   2. USBServoController with a \n" + "      servo in port 1 and port 6 \n" + "   3. LegacyModuleController, with \n " + "       a. IR seeker in port 2\n" + "       b. Light sensor in port 3 ";
        this.f88g.setOrangeText("Wrong devices found!", str, R.id.autoconfigure_info, R.layout.orange_warning, R.id.orangetext0, R.id.orangetext1);
    }

    private void m51d() {
        String str = BuildConfig.VERSION_NAME;
        this.f88g.setOrangeText("Already configured!", str, R.id.autoconfigure_info, R.layout.orange_warning, R.id.orangetext0, R.id.orangetext1);
    }

    private boolean m52e() {
        boolean z = true;
        boolean z2 = true;
        boolean z3 = true;
        for (Entry entry : this.entries) {
            boolean z4;
            boolean z5;
            DeviceType deviceType = (DeviceType) entry.getValue();
            if (deviceType == DeviceType.MODERN_ROBOTICS_USB_LEGACY_MODULE && z3) {
                m43a((SerialNumber) entry.getKey(), "sensors");
                z4 = false;
            } else {
                z4 = z3;
            }
            if (deviceType == DeviceType.MODERN_ROBOTICS_USB_DC_MOTOR_CONTROLLER && z2) {
                m37a((SerialNumber) entry.getKey(), "motor_1", "motor_2", "wheels");
                z3 = false;
            } else {
                z3 = z2;
            }
            if (deviceType == DeviceType.MODERN_ROBOTICS_USB_SERVO_CONTROLLER && z) {
                m38a((SerialNumber) entry.getKey(), m54f(), "servos");
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

    private ArrayList<String> m54f() {
        ArrayList<String> arrayList = new ArrayList();
        arrayList.add("servo_1");
        arrayList.add("NO DEVICE ATTACHED");
        arrayList.add("NO DEVICE ATTACHED");
        arrayList.add("NO DEVICE ATTACHED");
        arrayList.add("NO DEVICE ATTACHED");
        arrayList.add("servo_6");
        return arrayList;
    }

    private void m43a(SerialNumber serialNumber, String str) {
        LegacyModuleControllerConfiguration legacyModuleControllerConfiguration = (LegacyModuleControllerConfiguration) this.f86e.get(serialNumber);
        legacyModuleControllerConfiguration.setName(str);
        DeviceConfiguration a = m36a(ConfigurationType.IR_SEEKER, "ir_seeker", 2);
        DeviceConfiguration a2 = m36a(ConfigurationType.LIGHT_SENSOR, "light_sensor", 3);
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

    private boolean m57g() {
        boolean z = true;
        for (Entry entry : this.entries) {
            boolean z2;
            if (((DeviceType) entry.getValue()) == DeviceType.MODERN_ROBOTICS_USB_LEGACY_MODULE && z) {
                m47b((SerialNumber) entry.getKey(), "devices");
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

    private void m47b(SerialNumber serialNumber, String str) {
        LegacyModuleControllerConfiguration legacyModuleControllerConfiguration = (LegacyModuleControllerConfiguration) this.f86e.get(serialNumber);
        legacyModuleControllerConfiguration.setName(str);
        MotorControllerConfiguration a = m37a(ControllerConfiguration.NO_SERIAL_NUMBER, "motor_1", "motor_2", "wheels");
        a.setPort(0);
        ServoControllerConfiguration a2 = m38a(ControllerConfiguration.NO_SERIAL_NUMBER, m54f(), "servos");
        a2.setPort(1);
        DeviceConfiguration a3 = m36a(ConfigurationType.IR_SEEKER, "ir_seeker", 2);
        DeviceConfiguration a4 = m36a(ConfigurationType.LIGHT_SENSOR, "light_sensor", 3);
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

    private DeviceConfiguration m36a(ConfigurationType configurationType, String str, int i) {
        return new DeviceConfiguration(i, configurationType, str, true);
    }

    private MotorControllerConfiguration m37a(SerialNumber serialNumber, String str, String str2, String str3) {
        MotorControllerConfiguration motorControllerConfiguration;
        if (serialNumber.equals(ControllerConfiguration.NO_SERIAL_NUMBER)) {
            motorControllerConfiguration = new MotorControllerConfiguration();
        } else {
            motorControllerConfiguration = (MotorControllerConfiguration) this.f86e.get(serialNumber);
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

    private ServoControllerConfiguration m38a(SerialNumber serialNumber, ArrayList<String> arrayList, String str) {
        ServoControllerConfiguration servoControllerConfiguration;
        if (serialNumber.equals(ControllerConfiguration.NO_SERIAL_NUMBER)) {
            servoControllerConfiguration = new ServoControllerConfiguration();
        } else {
            servoControllerConfiguration = (ServoControllerConfiguration) this.f86e.get(serialNumber);
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
