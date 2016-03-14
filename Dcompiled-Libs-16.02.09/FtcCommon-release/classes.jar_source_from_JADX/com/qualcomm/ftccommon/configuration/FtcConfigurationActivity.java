package com.qualcomm.ftccommon.configuration;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.qualcomm.ftccommon.BuildConfig;
import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.ftccommon.LaunchActivityConstantsList;
import com.qualcomm.ftccommon.R;
import com.qualcomm.hardware.HardwareDeviceManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DeviceManager;
import com.qualcomm.robotcore.hardware.DeviceManager.DeviceType;
import com.qualcomm.robotcore.hardware.configuration.ControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import com.qualcomm.robotcore.hardware.configuration.DeviceInfoAdapter;
import com.qualcomm.robotcore.hardware.configuration.ReadXMLFileHandler;
import com.qualcomm.robotcore.hardware.configuration.Utility;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;
import com.qualcomm.robotcore.util.Util;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class FtcConfigurationActivity extends Activity {
    OnClickListener f251a;
    OnClickListener f252b;
    OnClickListener f253c;
    private Thread f254d;
    private Map<SerialNumber, ControllerConfiguration> f255e;
    private Context f256f;
    private DeviceManager f257g;
    private Button f258h;
    private String f259i;
    private Utility f260j;
    protected SharedPreferences preferences;
    protected Map<SerialNumber, DeviceType> scannedDevices;
    protected Set<Entry<SerialNumber, DeviceType>> scannedEntries;

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.11 */
    class AnonymousClass11 implements OnClickListener {
        final /* synthetic */ EditText f236a;
        final /* synthetic */ FtcConfigurationActivity f237b;

        AnonymousClass11(FtcConfigurationActivity ftcConfigurationActivity, EditText editText) {
            this.f237b = ftcConfigurationActivity;
            this.f236a = editText;
        }

        public void onClick(DialogInterface dialog, int button) {
            String trim = (this.f236a.getText().toString() + ".xml").trim();
            if (trim.equals(".xml")) {
                this.f237b.f260j.complainToast("File not saved: Please entire a filename.", this.f237b.f256f);
                return;
            }
            try {
                this.f237b.f260j.writeToFile(trim);
                this.f237b.m182g();
                this.f237b.f260j.saveToPreferences(this.f236a.getText().toString(), R.string.pref_hardware_config_filename);
                this.f237b.f259i = this.f236a.getText().toString();
                this.f237b.f260j.updateHeader("robot_config", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
                this.f237b.f260j.confirmSave();
            } catch (RobotCoreException e) {
                this.f237b.f260j.complainToast(e.getMessage(), this.f237b.f256f);
                DbgLog.error(e.getMessage());
            } catch (IOException e2) {
                this.f237b.m169a(e2.getMessage());
                DbgLog.error(e2.getMessage());
            }
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.1 */
    class C00581 implements OnClickListener {
        final /* synthetic */ FtcConfigurationActivity f238a;

        C00581(FtcConfigurationActivity ftcConfigurationActivity) {
            this.f238a = ftcConfigurationActivity;
        }

        public void onClick(DialogInterface dialog, int button) {
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.2 */
    class C00592 implements OnClickListener {
        final /* synthetic */ FtcConfigurationActivity f239a;

        C00592(FtcConfigurationActivity ftcConfigurationActivity) {
            this.f239a = ftcConfigurationActivity;
        }

        public void onClick(DialogInterface dialog, int button) {
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.3 */
    static /* synthetic */ class C00603 {
        static final /* synthetic */ int[] f240a;

        static {
            f240a = new int[DeviceType.values().length];
            try {
                f240a[DeviceType.MODERN_ROBOTICS_USB_DC_MOTOR_CONTROLLER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f240a[DeviceType.MODERN_ROBOTICS_USB_SERVO_CONTROLLER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f240a[DeviceType.MODERN_ROBOTICS_USB_LEGACY_MODULE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f240a[DeviceType.MODERN_ROBOTICS_USB_DEVICE_INTERFACE_MODULE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.4 */
    class C00614 implements View.OnClickListener {
        final /* synthetic */ FtcConfigurationActivity f241a;

        C00614(FtcConfigurationActivity ftcConfigurationActivity) {
            this.f241a = ftcConfigurationActivity;
        }

        public void onClick(View view) {
            Builder buildBuilder = this.f241a.f260j.buildBuilder("Devices", "These are the devices discovered by the Hardware Wizard. You can click on the name of each device to edit the information relating to that device. Make sure each device has a unique name. The names should match what is in the Op mode code. Scroll down to see more devices if there are any.");
            buildBuilder.setPositiveButton("Ok", this.f241a.f251a);
            AlertDialog create = buildBuilder.create();
            create.show();
            ((TextView) create.findViewById(16908299)).setTextSize(14.0f);
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.5 */
    class C00625 implements View.OnClickListener {
        final /* synthetic */ FtcConfigurationActivity f242a;

        C00625(FtcConfigurationActivity ftcConfigurationActivity) {
            this.f242a = ftcConfigurationActivity;
        }

        public void onClick(View view) {
            Builder buildBuilder = this.f242a.f260j.buildBuilder("Save Configuration", "Clicking the save button will create an xml file in: \n      /sdcard/FIRST/  \nThis file will be used to initialize the robot.");
            buildBuilder.setPositiveButton("Ok", this.f242a.f251a);
            AlertDialog create = buildBuilder.create();
            create.show();
            ((TextView) create.findViewById(16908299)).setTextSize(14.0f);
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.6 */
    class C00666 implements View.OnClickListener {
        final /* synthetic */ FtcConfigurationActivity f246a;

        /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.6.1 */
        class C00651 implements Runnable {
            final /* synthetic */ C00666 f245a;

            /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.6.1.1 */
            class C00641 implements Runnable {
                final /* synthetic */ C00651 f244a;

                /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.6.1.1.1 */
                class C00631 implements Runnable {
                    final /* synthetic */ C00641 f243a;

                    C00631(C00641 c00641) {
                        this.f243a = c00641;
                    }

                    public void run() {
                        this.f243a.f244a.f245a.f246a.f260j.resetCount();
                        this.f243a.f244a.f245a.f246a.m182g();
                        this.f243a.f244a.f245a.f246a.m186i();
                        this.f243a.f244a.f245a.f246a.f260j.updateHeader(this.f243a.f244a.f245a.f246a.f259i, R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
                        this.f243a.f244a.f245a.f246a.scannedEntries = this.f243a.f244a.f245a.f246a.scannedDevices.entrySet();
                        this.f243a.f244a.f245a.f246a.f255e = this.f243a.f244a.f245a.f246a.m173b();
                        this.f243a.f244a.f245a.f246a.m184h();
                        this.f243a.f244a.f245a.f246a.m181f();
                    }
                }

                C00641(C00651 c00651) {
                    this.f244a = c00651;
                }

                public void run() {
                    try {
                        DbgLog.msg("Scanning USB bus");
                        this.f244a.f245a.f246a.scannedDevices = this.f244a.f245a.f246a.f257g.scanForUsbDevices();
                    } catch (RobotCoreException e) {
                        DbgLog.error("Device scan failed: " + e.toString());
                    }
                    this.f244a.f245a.f246a.runOnUiThread(new C00631(this));
                }
            }

            C00651(C00666 c00666) {
                this.f245a = c00666;
            }

            public void run() {
                Util.logThreadLifeCycle("Scanning USB Bus", new C00641(this));
            }
        }

        C00666(FtcConfigurationActivity ftcConfigurationActivity) {
            this.f246a = ftcConfigurationActivity;
        }

        public void onClick(View view) {
            this.f246a.f254d = new Thread(new C00651(this));
            this.f246a.m174c();
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.7 */
    class C00677 implements OnClickListener {
        final /* synthetic */ FtcConfigurationActivity f247a;

        C00677(FtcConfigurationActivity ftcConfigurationActivity) {
            this.f247a = ftcConfigurationActivity;
        }

        public void onClick(DialogInterface dialog, int button) {
            this.f247a.f254d.start();
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.8 */
    class C00688 implements OnItemClickListener {
        final /* synthetic */ FtcConfigurationActivity f248a;

        C00688(FtcConfigurationActivity ftcConfigurationActivity) {
            this.f248a = ftcConfigurationActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View v, int pos, long arg3) {
            ControllerConfiguration controllerConfiguration = (ControllerConfiguration) adapterView.getItemAtPosition(pos);
            ConfigurationType type = controllerConfiguration.getType();
            if (type.equals(ConfigurationType.MOTOR_CONTROLLER)) {
                Intent intent = new Intent(this.f248a.f256f, EditMotorControllerActivity.class);
                intent.putExtra(EditMotorControllerActivity.EDIT_MOTOR_CONTROLLER_CONFIG, controllerConfiguration);
                intent.putExtra("requestCode", 1);
                this.f248a.setResult(-1, intent);
                this.f248a.startActivityForResult(intent, 1);
            }
            if (type.equals(ConfigurationType.SERVO_CONTROLLER)) {
                intent = new Intent(this.f248a.f256f, EditServoControllerActivity.class);
                intent.putExtra(EditServoControllerActivity.EDIT_SERVO_ACTIVITY, controllerConfiguration);
                intent.putExtra("requestCode", 2);
                this.f248a.setResult(-1, intent);
                this.f248a.startActivityForResult(intent, 2);
            }
            if (type.equals(ConfigurationType.LEGACY_MODULE_CONTROLLER)) {
                intent = new Intent(this.f248a.f256f, EditLegacyModuleControllerActivity.class);
                intent.putExtra(EditLegacyModuleControllerActivity.EDIT_LEGACY_CONFIG, controllerConfiguration);
                intent.putExtra("requestCode", 3);
                this.f248a.setResult(-1, intent);
                this.f248a.startActivityForResult(intent, 3);
            }
            if (type.equals(ConfigurationType.DEVICE_INTERFACE_MODULE)) {
                Intent intent2 = new Intent(this.f248a.f256f, EditDeviceInterfaceModuleActivity.class);
                intent2.putExtra(EditDeviceInterfaceModuleActivity.EDIT_DEVICE_INTERFACE_MODULE_CONFIG, controllerConfiguration);
                intent2.putExtra("requestCode", 4);
                this.f248a.setResult(-1, intent2);
                this.f248a.startActivityForResult(intent2, 4);
            }
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.9 */
    class C00699 implements OnClickListener {
        final /* synthetic */ EditText f249a;
        final /* synthetic */ FtcConfigurationActivity f250b;

        C00699(FtcConfigurationActivity ftcConfigurationActivity, EditText editText) {
            this.f250b = ftcConfigurationActivity;
            this.f249a = editText;
        }

        public void onClick(DialogInterface dialog, int button) {
            String trim = (this.f249a.getText().toString() + ".xml").trim();
            if (trim.equals(".xml")) {
                this.f250b.f260j.complainToast("File not saved: Please entire a filename.", this.f250b.f256f);
                return;
            }
            try {
                this.f250b.f260j.writeToFile(trim);
                this.f250b.m182g();
                this.f250b.f260j.saveToPreferences(this.f249a.getText().toString(), R.string.pref_hardware_config_filename);
                this.f250b.f259i = this.f249a.getText().toString();
                this.f250b.f260j.updateHeader("robot_config", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
                this.f250b.f260j.confirmSave();
                this.f250b.finish();
            } catch (RobotCoreException e) {
                this.f250b.f260j.complainToast(e.getMessage(), this.f250b.f256f);
                DbgLog.error(e.getMessage());
            } catch (IOException e2) {
                this.f250b.m169a(e2.getMessage());
                DbgLog.error(e2.getMessage());
            }
        }
    }

    public FtcConfigurationActivity() {
        this.f255e = new HashMap();
        this.f259i = "No current file!";
        this.scannedDevices = new HashMap();
        this.scannedEntries = new HashSet();
        this.f251a = new C00581(this);
        this.f252b = new OnClickListener() {
            final /* synthetic */ FtcConfigurationActivity f235a;

            {
                this.f235a = r1;
            }

            public void onClick(DialogInterface dialog, int button) {
                this.f235a.f260j.saveToPreferences(this.f235a.f259i.substring(7).trim(), R.string.pref_hardware_config_filename);
                this.f235a.finish();
            }
        };
        this.f253c = new C00592(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftc_configuration);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f256f = this;
        this.f260j = new Utility(this);
        this.f258h = (Button) findViewById(R.id.scanButton);
        m167a();
        try {
            this.f257g = new HardwareDeviceManager(this.f256f, null);
        } catch (RobotCoreException e) {
            this.f260j.complainToast("Failed to open the Device Manager", this.f256f);
            DbgLog.error("Failed to open deviceManager: " + e.toString());
            DbgLog.logStacktrace(e);
        }
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void m167a() {
        ((Button) findViewById(R.id.devices_holder).findViewById(R.id.info_btn)).setOnClickListener(new C00614(this));
        ((Button) findViewById(R.id.save_holder).findViewById(R.id.info_btn)).setOnClickListener(new C00625(this));
    }

    protected void onStart() {
        super.onStart();
        this.f259i = this.f260j.getFilenameFromPrefs(R.string.pref_hardware_config_filename, "No current file!");
        if (this.f259i.equalsIgnoreCase("No current file!")) {
            this.f260j.createConfigFolder();
        }
        this.f260j.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        m179e();
        if (!this.f259i.toLowerCase().contains("Unsaved".toLowerCase())) {
            m176d();
        }
        this.f258h.setOnClickListener(new C00666(this));
    }

    private HashMap<SerialNumber, ControllerConfiguration> m173b() {
        HashMap<SerialNumber, ControllerConfiguration> hashMap = new HashMap();
        for (Entry entry : this.scannedEntries) {
            SerialNumber serialNumber = (SerialNumber) entry.getKey();
            if (!this.f255e.containsKey(serialNumber)) {
                switch (C00603.f240a[((DeviceType) entry.getValue()).ordinal()]) {
                    case BuildConfig.VERSION_CODE /*1*/:
                        hashMap.put(serialNumber, this.f260j.buildMotorController(serialNumber));
                        break;
                    case 2:
                        hashMap.put(serialNumber, this.f260j.buildServoController(serialNumber));
                        break;
                    case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                        hashMap.put(serialNumber, this.f260j.buildLegacyModule(serialNumber));
                        break;
                    case 4:
                        hashMap.put(serialNumber, this.f260j.buildDeviceInterfaceModule(serialNumber));
                        break;
                    default:
                        break;
                }
            }
            hashMap.put(serialNumber, this.f255e.get(serialNumber));
        }
        return hashMap;
    }

    private void m174c() {
        if (this.f259i.toLowerCase().contains("Unsaved".toLowerCase())) {
            View editText = new EditText(this.f256f);
            editText.setEnabled(false);
            editText.setText(BuildConfig.VERSION_NAME);
            Builder buildBuilder = this.f260j.buildBuilder("Unsaved changes", "If you scan, your current unsaved changes will be lost.");
            buildBuilder.setView(editText);
            buildBuilder.setPositiveButton("Ok", new C00677(this));
            buildBuilder.setNegativeButton("Cancel", this.f253c);
            buildBuilder.show();
            return;
        }
        this.f254d.start();
    }

    private void m176d() {
        ReadXMLFileHandler readXMLFileHandler = new ReadXMLFileHandler(this.f256f);
        if (!this.f259i.equalsIgnoreCase("No current file!")) {
            try {
                m170a((ArrayList) readXMLFileHandler.parse(new FileInputStream(Utility.CONFIG_FILES_DIR + this.f259i + ".xml")));
                m184h();
                m181f();
            } catch (RobotCoreException e) {
                RobotLog.e("Error parsing XML file");
                RobotLog.logStacktrace(e);
                this.f260j.complainToast("Error parsing XML file: " + this.f259i, this.f256f);
            } catch (Exception e2) {
                DbgLog.error("File was not found: " + this.f259i);
                DbgLog.logStacktrace(e2);
                this.f260j.complainToast("That file was not found: " + this.f259i, this.f256f);
            }
        }
    }

    private void m179e() {
        if (this.f255e.size() == 0) {
            this.f260j.setOrangeText("Scan to find devices.", "In order to find devices: \n    1. Attach a robot \n    2. Press the 'Scan' button", R.id.empty_devicelist, R.layout.orange_warning, R.id.orangetext0, R.id.orangetext1);
            m182g();
            return;
        }
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.empty_devicelist);
        linearLayout.removeAllViews();
        linearLayout.setVisibility(8);
    }

    private void m181f() {
        if (this.f255e.size() == 0) {
            this.f260j.setOrangeText("No devices found!", "In order to find devices: \n    1. Attach a robot \n    2. Press the 'Scan' button", R.id.empty_devicelist, R.layout.orange_warning, R.id.orangetext0, R.id.orangetext1);
            m182g();
            return;
        }
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.empty_devicelist);
        linearLayout.removeAllViews();
        linearLayout.setVisibility(8);
    }

    private void m169a(String str) {
        this.f260j.setOrangeText("Found " + str, "Please fix and re-save.", R.id.warning_layout, R.layout.orange_warning, R.id.orangetext0, R.id.orangetext1);
    }

    private void m182g() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.warning_layout);
        linearLayout.removeAllViews();
        linearLayout.setVisibility(8);
    }

    private void m184h() {
        ListView listView = (ListView) findViewById(R.id.controllersList);
        listView.setAdapter(new DeviceInfoAdapter(this, 17367044, this.f255e));
        listView.setOnItemClickListener(new C00688(this));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != 0) {
            Serializable serializable = null;
            if (requestCode == 1) {
                serializable = data.getSerializableExtra(EditMotorControllerActivity.EDIT_MOTOR_CONTROLLER_CONFIG);
            } else if (requestCode == 2) {
                serializable = data.getSerializableExtra(EditServoControllerActivity.EDIT_SERVO_ACTIVITY);
            } else if (requestCode == 3) {
                serializable = data.getSerializableExtra(EditLegacyModuleControllerActivity.EDIT_LEGACY_CONFIG);
            } else if (requestCode == 4) {
                serializable = data.getSerializableExtra(EditDeviceInterfaceModuleActivity.EDIT_DEVICE_INTERFACE_MODULE_CONFIG);
            }
            if (serializable != null) {
                ControllerConfiguration controllerConfiguration = (ControllerConfiguration) serializable;
                this.scannedDevices.put(controllerConfiguration.getSerialNumber(), controllerConfiguration.configTypeToDeviceType(controllerConfiguration.getType()));
                this.f255e.put(controllerConfiguration.getSerialNumber(), controllerConfiguration);
                m184h();
                m186i();
                return;
            }
            DbgLog.error("Received Result with an incorrect request code: " + String.valueOf(requestCode));
        }
    }

    private void m186i() {
        if (!this.f259i.toLowerCase().contains("Unsaved".toLowerCase())) {
            String str = "Unsaved " + this.f259i;
            this.f260j.saveToPreferences(str, R.string.pref_hardware_config_filename);
            this.f259i = str;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        this.f260j.resetCount();
    }

    public void onBackPressed() {
        if (!this.f259i.toLowerCase().contains("Unsaved".toLowerCase())) {
            super.onBackPressed();
        } else if (!this.f260j.writeXML(this.f255e)) {
            View editText = new EditText(this);
            editText.setText(this.f260j.prepareFilename(this.f259i));
            Builder buildBuilder = this.f260j.buildBuilder("Unsaved changes", "Please save your file before exiting the Hardware Wizard! \n If you click 'Cancel' your changes will be lost.");
            buildBuilder.setView(editText);
            buildBuilder.setPositiveButton("Ok", new C00699(this, editText));
            buildBuilder.setNegativeButton("Cancel", this.f252b);
            buildBuilder.show();
        }
    }

    public void saveConfiguration(View v) {
        if (!this.f260j.writeXML(this.f255e)) {
            View editText = new EditText(this);
            editText.setText(this.f260j.prepareFilename(this.f259i));
            Builder buildBuilder = this.f260j.buildBuilder("Enter file name", "Please enter the file name");
            buildBuilder.setView(editText);
            buildBuilder.setPositiveButton("Ok", new AnonymousClass11(this, editText));
            buildBuilder.setNegativeButton("Cancel", this.f253c);
            buildBuilder.show();
        }
    }

    private void m170a(ArrayList<ControllerConfiguration> arrayList) {
        this.f255e = new HashMap();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ControllerConfiguration controllerConfiguration = (ControllerConfiguration) it.next();
            this.f255e.put(controllerConfiguration.getSerialNumber(), controllerConfiguration);
        }
    }
}
