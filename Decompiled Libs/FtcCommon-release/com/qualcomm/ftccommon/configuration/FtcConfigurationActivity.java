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
    OnClickListener f235a;
    OnClickListener f236b;
    OnClickListener f237c;
    private Thread f238d;
    private Map<SerialNumber, ControllerConfiguration> f239e;
    private Context f240f;
    private DeviceManager f241g;
    private Button f242h;
    private String f243i;
    private Utility f244j;
    protected SharedPreferences preferences;
    protected Map<SerialNumber, DeviceType> scannedDevices;
    protected Set<Entry<SerialNumber, DeviceType>> scannedEntries;

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.11 */
    class AnonymousClass11 implements OnClickListener {
        final /* synthetic */ EditText f221a;
        final /* synthetic */ FtcConfigurationActivity f222b;

        AnonymousClass11(FtcConfigurationActivity ftcConfigurationActivity, EditText editText) {
            this.f222b = ftcConfigurationActivity;
            this.f221a = editText;
        }

        public void onClick(DialogInterface dialog, int button) {
            String trim = (this.f221a.getText().toString() + ".xml").trim();
            if (trim.equals(".xml")) {
                this.f222b.f244j.complainToast("File not saved: Please entire a filename.", this.f222b.f240f);
                return;
            }
            try {
                this.f222b.f244j.writeToFile(trim);
                this.f222b.m171g();
                this.f222b.f244j.saveToPreferences(this.f221a.getText().toString(), R.string.pref_hardware_config_filename);
                this.f222b.f243i = this.f221a.getText().toString();
                this.f222b.f244j.updateHeader("robot_config", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
                this.f222b.f244j.confirmSave();
            } catch (RobotCoreException e) {
                this.f222b.f244j.complainToast(e.getMessage(), this.f222b.f240f);
                DbgLog.error(e.getMessage());
            } catch (IOException e2) {
                this.f222b.m158a(e2.getMessage());
                DbgLog.error(e2.getMessage());
            }
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.1 */
    class C00511 implements OnClickListener {
        final /* synthetic */ FtcConfigurationActivity f223a;

        C00511(FtcConfigurationActivity ftcConfigurationActivity) {
            this.f223a = ftcConfigurationActivity;
        }

        public void onClick(DialogInterface dialog, int button) {
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.2 */
    class C00522 implements OnClickListener {
        final /* synthetic */ FtcConfigurationActivity f224a;

        C00522(FtcConfigurationActivity ftcConfigurationActivity) {
            this.f224a = ftcConfigurationActivity;
        }

        public void onClick(DialogInterface dialog, int button) {
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.3 */
    static /* synthetic */ class C00533 {
        static final /* synthetic */ int[] f225a;

        static {
            f225a = new int[DeviceType.values().length];
            try {
                f225a[DeviceType.MODERN_ROBOTICS_USB_DC_MOTOR_CONTROLLER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f225a[DeviceType.MODERN_ROBOTICS_USB_SERVO_CONTROLLER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f225a[DeviceType.MODERN_ROBOTICS_USB_LEGACY_MODULE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f225a[DeviceType.MODERN_ROBOTICS_USB_DEVICE_INTERFACE_MODULE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.4 */
    class C00544 implements View.OnClickListener {
        final /* synthetic */ FtcConfigurationActivity f226a;

        C00544(FtcConfigurationActivity ftcConfigurationActivity) {
            this.f226a = ftcConfigurationActivity;
        }

        public void onClick(View view) {
            Builder buildBuilder = this.f226a.f244j.buildBuilder("Devices", "These are the devices discovered by the Hardware Wizard. You can click on the name of each device to edit the information relating to that device. Make sure each device has a unique name. The names should match what is in the Op mode code. Scroll down to see more devices if there are any.");
            buildBuilder.setPositiveButton("Ok", this.f226a.f235a);
            AlertDialog create = buildBuilder.create();
            create.show();
            ((TextView) create.findViewById(16908299)).setTextSize(14.0f);
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.5 */
    class C00555 implements View.OnClickListener {
        final /* synthetic */ FtcConfigurationActivity f227a;

        C00555(FtcConfigurationActivity ftcConfigurationActivity) {
            this.f227a = ftcConfigurationActivity;
        }

        public void onClick(View view) {
            Builder buildBuilder = this.f227a.f244j.buildBuilder("Save Configuration", "Clicking the save button will create an xml file in: \n      /sdcard/FIRST/  \nThis file will be used to initialize the robot.");
            buildBuilder.setPositiveButton("Ok", this.f227a.f235a);
            AlertDialog create = buildBuilder.create();
            create.show();
            ((TextView) create.findViewById(16908299)).setTextSize(14.0f);
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.6 */
    class C00586 implements View.OnClickListener {
        final /* synthetic */ FtcConfigurationActivity f230a;

        /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.6.1 */
        class C00571 implements Runnable {
            final /* synthetic */ C00586 f229a;

            /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.6.1.1 */
            class C00561 implements Runnable {
                final /* synthetic */ C00571 f228a;

                C00561(C00571 c00571) {
                    this.f228a = c00571;
                }

                public void run() {
                    this.f228a.f229a.f230a.f244j.resetCount();
                    this.f228a.f229a.f230a.m171g();
                    this.f228a.f229a.f230a.m175i();
                    this.f228a.f229a.f230a.f244j.updateHeader(this.f228a.f229a.f230a.f243i, R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
                    this.f228a.f229a.f230a.scannedEntries = this.f228a.f229a.f230a.scannedDevices.entrySet();
                    this.f228a.f229a.f230a.f239e = this.f228a.f229a.f230a.m162b();
                    this.f228a.f229a.f230a.m173h();
                    this.f228a.f229a.f230a.m170f();
                }
            }

            C00571(C00586 c00586) {
                this.f229a = c00586;
            }

            public void run() {
                try {
                    DbgLog.msg("Scanning USB bus");
                    this.f229a.f230a.scannedDevices = this.f229a.f230a.f241g.scanForUsbDevices();
                } catch (RobotCoreException e) {
                    DbgLog.error("Device scan failed: " + e.toString());
                }
                this.f229a.f230a.runOnUiThread(new C00561(this));
            }
        }

        C00586(FtcConfigurationActivity ftcConfigurationActivity) {
            this.f230a = ftcConfigurationActivity;
        }

        public void onClick(View view) {
            this.f230a.f238d = new Thread(new C00571(this));
            this.f230a.m163c();
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.7 */
    class C00597 implements OnClickListener {
        final /* synthetic */ FtcConfigurationActivity f231a;

        C00597(FtcConfigurationActivity ftcConfigurationActivity) {
            this.f231a = ftcConfigurationActivity;
        }

        public void onClick(DialogInterface dialog, int button) {
            this.f231a.f238d.start();
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.8 */
    class C00608 implements OnItemClickListener {
        final /* synthetic */ FtcConfigurationActivity f232a;

        C00608(FtcConfigurationActivity ftcConfigurationActivity) {
            this.f232a = ftcConfigurationActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View v, int pos, long arg3) {
            ControllerConfiguration controllerConfiguration = (ControllerConfiguration) adapterView.getItemAtPosition(pos);
            ConfigurationType type = controllerConfiguration.getType();
            if (type.equals(ConfigurationType.MOTOR_CONTROLLER)) {
                Intent intent = new Intent(this.f232a.f240f, EditMotorControllerActivity.class);
                intent.putExtra(EditMotorControllerActivity.EDIT_MOTOR_CONTROLLER_CONFIG, controllerConfiguration);
                intent.putExtra("requestCode", 1);
                this.f232a.setResult(-1, intent);
                this.f232a.startActivityForResult(intent, 1);
            }
            if (type.equals(ConfigurationType.SERVO_CONTROLLER)) {
                intent = new Intent(this.f232a.f240f, EditServoControllerActivity.class);
                intent.putExtra(EditServoControllerActivity.EDIT_SERVO_ACTIVITY, controllerConfiguration);
                intent.putExtra("requestCode", 2);
                this.f232a.setResult(-1, intent);
                this.f232a.startActivityForResult(intent, 2);
            }
            if (type.equals(ConfigurationType.LEGACY_MODULE_CONTROLLER)) {
                intent = new Intent(this.f232a.f240f, EditLegacyModuleControllerActivity.class);
                intent.putExtra(EditLegacyModuleControllerActivity.EDIT_LEGACY_CONFIG, controllerConfiguration);
                intent.putExtra("requestCode", 3);
                this.f232a.setResult(-1, intent);
                this.f232a.startActivityForResult(intent, 3);
            }
            if (type.equals(ConfigurationType.DEVICE_INTERFACE_MODULE)) {
                Intent intent2 = new Intent(this.f232a.f240f, EditDeviceInterfaceModuleActivity.class);
                intent2.putExtra(EditDeviceInterfaceModuleActivity.EDIT_DEVICE_INTERFACE_MODULE_CONFIG, controllerConfiguration);
                intent2.putExtra("requestCode", 4);
                this.f232a.setResult(-1, intent2);
                this.f232a.startActivityForResult(intent2, 4);
            }
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcConfigurationActivity.9 */
    class C00619 implements OnClickListener {
        final /* synthetic */ EditText f233a;
        final /* synthetic */ FtcConfigurationActivity f234b;

        C00619(FtcConfigurationActivity ftcConfigurationActivity, EditText editText) {
            this.f234b = ftcConfigurationActivity;
            this.f233a = editText;
        }

        public void onClick(DialogInterface dialog, int button) {
            String trim = (this.f233a.getText().toString() + ".xml").trim();
            if (trim.equals(".xml")) {
                this.f234b.f244j.complainToast("File not saved: Please entire a filename.", this.f234b.f240f);
                return;
            }
            try {
                this.f234b.f244j.writeToFile(trim);
                this.f234b.m171g();
                this.f234b.f244j.saveToPreferences(this.f233a.getText().toString(), R.string.pref_hardware_config_filename);
                this.f234b.f243i = this.f233a.getText().toString();
                this.f234b.f244j.updateHeader("robot_config", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
                this.f234b.f244j.confirmSave();
                this.f234b.finish();
            } catch (RobotCoreException e) {
                this.f234b.f244j.complainToast(e.getMessage(), this.f234b.f240f);
                DbgLog.error(e.getMessage());
            } catch (IOException e2) {
                this.f234b.m158a(e2.getMessage());
                DbgLog.error(e2.getMessage());
            }
        }
    }

    public FtcConfigurationActivity() {
        this.f239e = new HashMap();
        this.f243i = "No current file!";
        this.scannedDevices = new HashMap();
        this.scannedEntries = new HashSet();
        this.f235a = new C00511(this);
        this.f236b = new OnClickListener() {
            final /* synthetic */ FtcConfigurationActivity f220a;

            {
                this.f220a = r1;
            }

            public void onClick(DialogInterface dialog, int button) {
                this.f220a.f244j.saveToPreferences(this.f220a.f243i.substring(7).trim(), R.string.pref_hardware_config_filename);
                this.f220a.finish();
            }
        };
        this.f237c = new C00522(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftc_configuration);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f240f = this;
        this.f244j = new Utility(this);
        this.f242h = (Button) findViewById(R.id.scanButton);
        m156a();
        try {
            this.f241g = new HardwareDeviceManager(this.f240f, null);
        } catch (RobotCoreException e) {
            this.f244j.complainToast("Failed to open the Device Manager", this.f240f);
            DbgLog.error("Failed to open deviceManager: " + e.toString());
            DbgLog.logStacktrace(e);
        }
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void m156a() {
        ((Button) findViewById(R.id.devices_holder).findViewById(R.id.info_btn)).setOnClickListener(new C00544(this));
        ((Button) findViewById(R.id.save_holder).findViewById(R.id.info_btn)).setOnClickListener(new C00555(this));
    }

    protected void onStart() {
        super.onStart();
        this.f243i = this.f244j.getFilenameFromPrefs(R.string.pref_hardware_config_filename, "No current file!");
        if (this.f243i.equalsIgnoreCase("No current file!")) {
            this.f244j.createConfigFolder();
        }
        this.f244j.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        m168e();
        if (!this.f243i.toLowerCase().contains("Unsaved".toLowerCase())) {
            m165d();
        }
        this.f242h.setOnClickListener(new C00586(this));
    }

    private HashMap<SerialNumber, ControllerConfiguration> m162b() {
        HashMap<SerialNumber, ControllerConfiguration> hashMap = new HashMap();
        for (Entry entry : this.scannedEntries) {
            SerialNumber serialNumber = (SerialNumber) entry.getKey();
            if (!this.f239e.containsKey(serialNumber)) {
                switch (C00533.f225a[((DeviceType) entry.getValue()).ordinal()]) {
                    case BuildConfig.VERSION_CODE /*1*/:
                        hashMap.put(serialNumber, this.f244j.buildMotorController(serialNumber));
                        break;
                    case 2:
                        hashMap.put(serialNumber, this.f244j.buildServoController(serialNumber));
                        break;
                    case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                        hashMap.put(serialNumber, this.f244j.buildLegacyModule(serialNumber));
                        break;
                    case 4:
                        hashMap.put(serialNumber, this.f244j.buildDeviceInterfaceModule(serialNumber));
                        break;
                    default:
                        break;
                }
            }
            hashMap.put(serialNumber, this.f239e.get(serialNumber));
        }
        return hashMap;
    }

    private void m163c() {
        if (this.f243i.toLowerCase().contains("Unsaved".toLowerCase())) {
            View editText = new EditText(this.f240f);
            editText.setEnabled(false);
            editText.setText(BuildConfig.VERSION_NAME);
            Builder buildBuilder = this.f244j.buildBuilder("Unsaved changes", "If you scan, your current unsaved changes will be lost.");
            buildBuilder.setView(editText);
            buildBuilder.setPositiveButton("Ok", new C00597(this));
            buildBuilder.setNegativeButton("Cancel", this.f237c);
            buildBuilder.show();
            return;
        }
        this.f238d.start();
    }

    private void m165d() {
        ReadXMLFileHandler readXMLFileHandler = new ReadXMLFileHandler(this.f240f);
        if (!this.f243i.equalsIgnoreCase("No current file!")) {
            try {
                m159a((ArrayList) readXMLFileHandler.parse(new FileInputStream(Utility.CONFIG_FILES_DIR + this.f243i + ".xml")));
                m173h();
                m170f();
            } catch (RobotCoreException e) {
                RobotLog.e("Error parsing XML file");
                RobotLog.logStacktrace(e);
                this.f244j.complainToast("Error parsing XML file: " + this.f243i, this.f240f);
            } catch (Exception e2) {
                DbgLog.error("File was not found: " + this.f243i);
                DbgLog.logStacktrace(e2);
                this.f244j.complainToast("That file was not found: " + this.f243i, this.f240f);
            }
        }
    }

    private void m168e() {
        if (this.f239e.size() == 0) {
            this.f244j.setOrangeText("Scan to find devices.", "In order to find devices: \n    1. Attach a robot \n    2. Press the 'Scan' button", R.id.empty_devicelist, R.layout.orange_warning, R.id.orangetext0, R.id.orangetext1);
            m171g();
            return;
        }
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.empty_devicelist);
        linearLayout.removeAllViews();
        linearLayout.setVisibility(8);
    }

    private void m170f() {
        if (this.f239e.size() == 0) {
            this.f244j.setOrangeText("No devices found!", "In order to find devices: \n    1. Attach a robot \n    2. Press the 'Scan' button", R.id.empty_devicelist, R.layout.orange_warning, R.id.orangetext0, R.id.orangetext1);
            m171g();
            return;
        }
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.empty_devicelist);
        linearLayout.removeAllViews();
        linearLayout.setVisibility(8);
    }

    private void m158a(String str) {
        this.f244j.setOrangeText("Found " + str, "Please fix and re-save.", R.id.warning_layout, R.layout.orange_warning, R.id.orangetext0, R.id.orangetext1);
    }

    private void m171g() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.warning_layout);
        linearLayout.removeAllViews();
        linearLayout.setVisibility(8);
    }

    private void m173h() {
        ListView listView = (ListView) findViewById(R.id.controllersList);
        listView.setAdapter(new DeviceInfoAdapter(this, 17367044, this.f239e));
        listView.setOnItemClickListener(new C00608(this));
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
                this.f239e.put(controllerConfiguration.getSerialNumber(), controllerConfiguration);
                m173h();
                m175i();
                return;
            }
            DbgLog.error("Received Result with an incorrect request code: " + String.valueOf(requestCode));
        }
    }

    private void m175i() {
        if (!this.f243i.toLowerCase().contains("Unsaved".toLowerCase())) {
            String str = "Unsaved " + this.f243i;
            this.f244j.saveToPreferences(str, R.string.pref_hardware_config_filename);
            this.f243i = str;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        this.f244j.resetCount();
    }

    public void onBackPressed() {
        if (!this.f243i.toLowerCase().contains("Unsaved".toLowerCase())) {
            super.onBackPressed();
        } else if (!this.f244j.writeXML(this.f239e)) {
            View editText = new EditText(this);
            editText.setText(this.f244j.prepareFilename(this.f243i));
            Builder buildBuilder = this.f244j.buildBuilder("Unsaved changes", "Please save your file before exiting the Hardware Wizard! \n If you click 'Cancel' your changes will be lost.");
            buildBuilder.setView(editText);
            buildBuilder.setPositiveButton("Ok", new C00619(this, editText));
            buildBuilder.setNegativeButton("Cancel", this.f236b);
            buildBuilder.show();
        }
    }

    public void saveConfiguration(View v) {
        if (!this.f244j.writeXML(this.f239e)) {
            View editText = new EditText(this);
            editText.setText(this.f244j.prepareFilename(this.f243i));
            Builder buildBuilder = this.f244j.buildBuilder("Enter file name", "Please enter the file name");
            buildBuilder.setView(editText);
            buildBuilder.setPositiveButton("Ok", new AnonymousClass11(this, editText));
            buildBuilder.setNegativeButton("Cancel", this.f237c);
            buildBuilder.show();
        }
    }

    private void m159a(ArrayList<ControllerConfiguration> arrayList) {
        this.f239e = new HashMap();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ControllerConfiguration controllerConfiguration = (ControllerConfiguration) it.next();
            this.f239e.put(controllerConfiguration.getSerialNumber(), controllerConfiguration);
        }
    }
}
