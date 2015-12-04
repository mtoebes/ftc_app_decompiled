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
    private Utility f113a;
    private String f114b;
    private Context f115c;
    private DeviceInterfaceModuleConfiguration f116d;
    private EditText f117e;
    private ArrayList<DeviceConfiguration> f118f;
    private OnItemClickListener f119g;

    /* renamed from: com.qualcomm.ftccommon.configuration.EditDeviceInterfaceModuleActivity.1 */
    class C00351 implements OnItemClickListener {
        final /* synthetic */ EditDeviceInterfaceModuleActivity f111a;

        C00351(EditDeviceInterfaceModuleActivity editDeviceInterfaceModuleActivity) {
            this.f111a = editDeviceInterfaceModuleActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            switch (position) {
                case 0:
                    this.f111a.m85a(EditDeviceInterfaceModuleActivity.EDIT_PWM_PORT_REQUEST_CODE, this.f111a.f116d.getPwmDevices(), EditPWMDevicesActivity.class);
                case BuildConfig.VERSION_CODE /*1*/:
                    this.f111a.m85a(EditDeviceInterfaceModuleActivity.EDIT_I2C_PORT_REQUEST_CODE, this.f111a.f116d.getI2cDevices(), EditI2cDevicesActivity.class);
                case 2:
                    this.f111a.m85a(EditDeviceInterfaceModuleActivity.EDIT_ANALOG_INPUT_REQUEST_CODE, this.f111a.f116d.getAnalogInputDevices(), EditAnalogInputDevicesActivity.class);
                case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                    this.f111a.m85a(EditDeviceInterfaceModuleActivity.EDIT_DIGITAL_REQUEST_CODE, this.f111a.f116d.getDigitalDevices(), EditDigitalDevicesActivity.class);
                case 4:
                    this.f111a.m85a(EditDeviceInterfaceModuleActivity.EDIT_ANALOG_OUTPUT_REQUEST_CODE, this.f111a.f116d.getAnalogOutputDevices(), EditAnalogOutputDevicesActivity.class);
                default:
            }
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.EditDeviceInterfaceModuleActivity.a */
    private class C0036a implements TextWatcher {
        final /* synthetic */ EditDeviceInterfaceModuleActivity f112a;

        private C0036a(EditDeviceInterfaceModuleActivity editDeviceInterfaceModuleActivity) {
            this.f112a = editDeviceInterfaceModuleActivity;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            this.f112a.f116d.setName(editable.toString());
        }
    }

    public EditDeviceInterfaceModuleActivity() {
        this.f118f = new ArrayList();
        this.f119g = new C00351(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_interface_module);
        ListView listView = (ListView) findViewById(R.id.listView_devices);
        listView.setAdapter(new ArrayAdapter(this, 17367043, getResources().getStringArray(R.array.device_interface_module_options_array)));
        listView.setOnItemClickListener(this.f119g);
        this.f115c = this;
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.f113a = new Utility(this);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f117e = (EditText) findViewById(R.id.device_interface_module_name);
        this.f117e.addTextChangedListener(new C0036a());
    }

    protected void onStart() {
        super.onStart();
        this.f113a.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        this.f114b = this.f113a.getFilenameFromPrefs(R.string.pref_hardware_config_filename, "No current file!");
        Serializable serializableExtra = getIntent().getSerializableExtra(EDIT_DEVICE_INTERFACE_MODULE_CONFIG);
        if (serializableExtra != null) {
            this.f116d = (DeviceInterfaceModuleConfiguration) serializableExtra;
            this.f118f = (ArrayList) this.f116d.getDevices();
            this.f117e.setText(this.f116d.getName());
            ((TextView) findViewById(R.id.device_interface_module_serialNumber)).setText(this.f116d.getSerialNumber().toString());
        }
    }

    private void m85a(int i, List<DeviceConfiguration> list, Class cls) {
        Bundle bundle = new Bundle();
        for (int i2 = 0; i2 < list.size(); i2++) {
            bundle.putSerializable(String.valueOf(i2), (Serializable) list.get(i2));
        }
        Intent intent = new Intent(this.f115c, cls);
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
                    this.f116d.setPwmDevices(m83a(extras));
                }
            } else if (requestCode == EDIT_ANALOG_INPUT_REQUEST_CODE) {
                extras = data.getExtras();
                if (extras != null) {
                    this.f116d.setAnalogInputDevices(m83a(extras));
                }
            } else if (requestCode == EDIT_DIGITAL_REQUEST_CODE) {
                extras = data.getExtras();
                if (extras != null) {
                    this.f116d.setDigitalDevices(m83a(extras));
                }
            } else if (requestCode == EDIT_I2C_PORT_REQUEST_CODE) {
                extras = data.getExtras();
                if (extras != null) {
                    this.f116d.setI2cDevices(m83a(extras));
                }
            } else if (requestCode == EDIT_ANALOG_OUTPUT_REQUEST_CODE) {
                extras = data.getExtras();
                if (extras != null) {
                    this.f116d.setAnalogOutputDevices(m83a(extras));
                }
            }
            m84a();
        }
    }

    private ArrayList<DeviceConfiguration> m83a(Bundle bundle) {
        ArrayList<DeviceConfiguration> arrayList = new ArrayList();
        for (String str : bundle.keySet()) {
            arrayList.add(Integer.parseInt(str), (DeviceConfiguration) bundle.getSerializable(str));
        }
        return arrayList;
    }

    private void m84a() {
        if (!this.f114b.toLowerCase().contains("Unsaved".toLowerCase())) {
            String str = "Unsaved " + this.f114b;
            this.f113a.saveToPreferences(str, R.string.pref_hardware_config_filename);
            this.f114b = str;
        }
        this.f113a.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
    }

    public void saveDeviceInterface(View v) {
        m87b();
    }

    private void m87b() {
        Intent intent = new Intent();
        this.f116d.setName(this.f117e.getText().toString());
        intent.putExtra(EDIT_DEVICE_INTERFACE_MODULE_CONFIG, this.f116d);
        setResult(-1, intent);
        finish();
    }

    public void cancelDeviceInterface(View v) {
        setResult(0, new Intent());
        finish();
    }
}
