package com.qualcomm.ftccommon.configuration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.qualcomm.ftccommon.BuildConfig;
import com.qualcomm.ftccommon.LaunchActivityConstantsList;
import com.qualcomm.ftccommon.R;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration;
import com.qualcomm.robotcore.hardware.configuration.DeviceInterfaceModuleConfiguration;
import com.qualcomm.robotcore.hardware.configuration.Utility;
import com.qualcomm.robotcore.util.RobotLog;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EditDeviceInterfaceModuleActivity extends Activity {
    public static final int EDIT_ANALOG_INPUT_REQUEST_CODE = 203;
    public static final int EDIT_ANALOG_OUTPUT_REQUEST_CODE = 205;
    public static final String EDIT_DEVICE_INTERFACE_MODULE_CONFIG = "EDIT_DEVICE_INTERFACE_MODULE_CONFIG";
    public static final int EDIT_DIGITAL_REQUEST_CODE = 204;
    public static final int EDIT_I2C_PORT_REQUEST_CODE = 202;
    public static final int EDIT_PWM_PORT_REQUEST_CODE = 201;
    private Utility f128a;
    private String f129b;
    private Context f130c;
    private DeviceInterfaceModuleConfiguration f131d;
    private EditText f132e;
    private ArrayList<DeviceConfiguration> f133f;
    private OnItemClickListener f134g;

    /* renamed from: com.qualcomm.ftccommon.configuration.EditDeviceInterfaceModuleActivity.1 */
    class C00421 implements OnItemClickListener {
        final /* synthetic */ EditDeviceInterfaceModuleActivity f126a;

        C00421(EditDeviceInterfaceModuleActivity editDeviceInterfaceModuleActivity) {
            this.f126a = editDeviceInterfaceModuleActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            switch (position) {
                case 0:
                    this.f126a.m96a(EditDeviceInterfaceModuleActivity.EDIT_PWM_PORT_REQUEST_CODE, this.f126a.f131d.getPwmDevices(), EditPWMDevicesActivity.class);
                case BuildConfig.VERSION_CODE /*1*/:
                    this.f126a.m96a(EditDeviceInterfaceModuleActivity.EDIT_I2C_PORT_REQUEST_CODE, this.f126a.f131d.getI2cDevices(), EditI2cDevicesActivity.class);
                case 2:
                    this.f126a.m96a(EditDeviceInterfaceModuleActivity.EDIT_ANALOG_INPUT_REQUEST_CODE, this.f126a.f131d.getAnalogInputDevices(), EditAnalogInputDevicesActivity.class);
                case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                    this.f126a.m96a(EditDeviceInterfaceModuleActivity.EDIT_DIGITAL_REQUEST_CODE, this.f126a.f131d.getDigitalDevices(), EditDigitalDevicesActivity.class);
                case 4:
                    this.f126a.m96a(EditDeviceInterfaceModuleActivity.EDIT_ANALOG_OUTPUT_REQUEST_CODE, this.f126a.f131d.getAnalogOutputDevices(), EditAnalogOutputDevicesActivity.class);
                default:
            }
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.EditDeviceInterfaceModuleActivity.a */
    private class C0043a implements TextWatcher {
        final /* synthetic */ EditDeviceInterfaceModuleActivity f127a;

        private C0043a(EditDeviceInterfaceModuleActivity editDeviceInterfaceModuleActivity) {
            this.f127a = editDeviceInterfaceModuleActivity;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            this.f127a.f131d.setName(editable.toString());
        }
    }

    public EditDeviceInterfaceModuleActivity() {
        this.f133f = new ArrayList();
        this.f134g = new C00421(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_interface_module);
        ListView listView = (ListView) findViewById(R.id.listView_devices);
        listView.setAdapter(new ArrayAdapter(this, 17367043, getResources().getStringArray(R.array.device_interface_module_options_array)));
        listView.setOnItemClickListener(this.f134g);
        this.f130c = this;
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.f128a = new Utility(this);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f132e = (EditText) findViewById(R.id.device_interface_module_name);
        this.f132e.addTextChangedListener(new C0043a());
    }

    protected void onStart() {
        super.onStart();
        this.f128a.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        this.f129b = this.f128a.getFilenameFromPrefs(R.string.pref_hardware_config_filename, "No current file!");
        Serializable serializableExtra = getIntent().getSerializableExtra(EDIT_DEVICE_INTERFACE_MODULE_CONFIG);
        if (serializableExtra != null) {
            this.f131d = (DeviceInterfaceModuleConfiguration) serializableExtra;
            this.f133f = (ArrayList) this.f131d.getDevices();
            this.f132e.setText(this.f131d.getName());
            ((TextView) findViewById(R.id.device_interface_module_serialNumber)).setText(this.f131d.getSerialNumber().toString());
        }
    }

    private void m96a(int i, List<DeviceConfiguration> list, Class cls) {
        Bundle bundle = new Bundle();
        for (int i2 = 0; i2 < list.size(); i2++) {
            bundle.putSerializable(String.valueOf(i2), (Serializable) list.get(i2));
        }
        Intent intent = new Intent(this.f130c, cls);
        intent.putExtras(bundle);
        setResult(-1, intent);
        startActivityForResult(intent, i);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            Bundle extras;
            if (requestCode == EDIT_PWM_PORT_REQUEST_CODE) {
                extras = data.getExtras();
                if (extras != null) {
                    this.f131d.setPwmDevices(m94a(extras));
                }
            } else if (requestCode == EDIT_ANALOG_INPUT_REQUEST_CODE) {
                extras = data.getExtras();
                if (extras != null) {
                    this.f131d.setAnalogInputDevices(m94a(extras));
                }
            } else if (requestCode == EDIT_DIGITAL_REQUEST_CODE) {
                extras = data.getExtras();
                if (extras != null) {
                    this.f131d.setDigitalDevices(m94a(extras));
                }
            } else if (requestCode == EDIT_I2C_PORT_REQUEST_CODE) {
                extras = data.getExtras();
                if (extras != null) {
                    this.f131d.setI2cDevices(m94a(extras));
                }
            } else if (requestCode == EDIT_ANALOG_OUTPUT_REQUEST_CODE) {
                extras = data.getExtras();
                if (extras != null) {
                    this.f131d.setAnalogOutputDevices(m94a(extras));
                }
            }
            m95a();
        }
    }

    private ArrayList<DeviceConfiguration> m94a(Bundle bundle) {
        ArrayList<DeviceConfiguration> arrayList = new ArrayList();
        for (String str : bundle.keySet()) {
            arrayList.add(Integer.parseInt(str), (DeviceConfiguration) bundle.getSerializable(str));
        }
        return arrayList;
    }

    private void m95a() {
        if (!this.f129b.toLowerCase().contains("Unsaved".toLowerCase())) {
            String str = "Unsaved " + this.f129b;
            this.f128a.saveToPreferences(str, R.string.pref_hardware_config_filename);
            this.f129b = str;
        }
        this.f128a.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
    }

    public void saveDeviceInterface(View v) {
        m98b();
    }

    private void m98b() {
        Intent intent = new Intent();
        this.f131d.setName(this.f132e.getText().toString());
        intent.putExtra(EDIT_DEVICE_INTERFACE_MODULE_CONFIG, this.f131d);
        setResult(-1, intent);
        finish();
    }

    public void cancelDeviceInterface(View v) {
        setResult(0, new Intent());
        finish();
    }
}
