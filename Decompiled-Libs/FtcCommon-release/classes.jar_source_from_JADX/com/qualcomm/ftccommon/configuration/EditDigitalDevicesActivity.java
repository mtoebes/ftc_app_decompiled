package com.qualcomm.ftccommon.configuration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.qualcomm.ftccommon.BuildConfig;
import com.qualcomm.ftccommon.LaunchActivityConstantsList;
import com.qualcomm.ftccommon.R;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import com.qualcomm.robotcore.hardware.configuration.Utility;
import com.qualcomm.robotcore.util.RobotLog;
import java.io.Serializable;
import java.util.ArrayList;

public class EditDigitalDevicesActivity extends Activity {
    private Utility f123a;
    private View f124b;
    private View f125c;
    private View f126d;
    private View f127e;
    private View f128f;
    private View f129g;
    private View f130h;
    private View f131i;
    private ArrayList<DeviceConfiguration> f132j;
    private OnItemSelectedListener f133k;

    /* renamed from: com.qualcomm.ftccommon.configuration.EditDigitalDevicesActivity.1 */
    class C00371 implements OnItemSelectedListener {
        final /* synthetic */ EditDigitalDevicesActivity f120a;

        C00371(EditDigitalDevicesActivity editDigitalDevicesActivity) {
            this.f120a = editDigitalDevicesActivity;
        }

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
            String obj = parent.getItemAtPosition(pos).toString();
            LinearLayout linearLayout = (LinearLayout) view.getParent().getParent().getParent();
            if (obj.equalsIgnoreCase(ConfigurationType.NOTHING.toString())) {
                this.f120a.m94a(linearLayout);
            } else {
                this.f120a.m95a(linearLayout, obj);
            }
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.EditDigitalDevicesActivity.a */
    private class C0038a implements TextWatcher {
        final /* synthetic */ EditDigitalDevicesActivity f121a;
        private int f122b;

        private C0038a(EditDigitalDevicesActivity editDigitalDevicesActivity, View view) {
            this.f121a = editDigitalDevicesActivity;
            this.f122b = Integer.parseInt(((TextView) view.findViewById(R.id.port_number_digital_device)).getText().toString());
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            ((DeviceConfiguration) this.f121a.f132j.get(this.f122b)).setName(editable.toString());
        }
    }

    public EditDigitalDevicesActivity() {
        this.f132j = new ArrayList();
        this.f133k = new C00371(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.digital_devices);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.f123a = new Utility(this);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f124b = getLayoutInflater().inflate(R.layout.digital_device, (LinearLayout) findViewById(R.id.linearLayout_digital_device0), true);
        ((TextView) this.f124b.findViewById(R.id.port_number_digital_device)).setText("0");
        this.f125c = getLayoutInflater().inflate(R.layout.digital_device, (LinearLayout) findViewById(R.id.linearLayout_digital_device1), true);
        ((TextView) this.f125c.findViewById(R.id.port_number_digital_device)).setText("1");
        this.f126d = getLayoutInflater().inflate(R.layout.digital_device, (LinearLayout) findViewById(R.id.linearLayout_digital_device2), true);
        ((TextView) this.f126d.findViewById(R.id.port_number_digital_device)).setText("2");
        this.f127e = getLayoutInflater().inflate(R.layout.digital_device, (LinearLayout) findViewById(R.id.linearLayout_digital_device3), true);
        ((TextView) this.f127e.findViewById(R.id.port_number_digital_device)).setText("3");
        this.f128f = getLayoutInflater().inflate(R.layout.digital_device, (LinearLayout) findViewById(R.id.linearLayout_digital_device4), true);
        ((TextView) this.f128f.findViewById(R.id.port_number_digital_device)).setText("4");
        this.f129g = getLayoutInflater().inflate(R.layout.digital_device, (LinearLayout) findViewById(R.id.linearLayout_digital_device5), true);
        ((TextView) this.f129g.findViewById(R.id.port_number_digital_device)).setText("5");
        this.f130h = getLayoutInflater().inflate(R.layout.digital_device, (LinearLayout) findViewById(R.id.linearLayout_digital_device6), true);
        ((TextView) this.f130h.findViewById(R.id.port_number_digital_device)).setText("6");
        this.f131i = getLayoutInflater().inflate(R.layout.digital_device, (LinearLayout) findViewById(R.id.linearLayout_digital_device7), true);
        ((TextView) this.f131i.findViewById(R.id.port_number_digital_device)).setText("7");
    }

    protected void onStart() {
        super.onStart();
        this.f123a.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            for (String str : extras.keySet()) {
                this.f132j.add(Integer.parseInt(str), (DeviceConfiguration) extras.getSerializable(str));
            }
            for (int i = 0; i < this.f132j.size(); i++) {
                View a = m88a(i);
                DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f132j.get(i);
                m91a(a);
                m98b(a, deviceConfiguration);
                m92a(a, deviceConfiguration);
            }
        }
    }

    private void m92a(View view, DeviceConfiguration deviceConfiguration) {
        Spinner spinner = (Spinner) view.findViewById(R.id.choiceSpinner_digital_device);
        ArrayAdapter arrayAdapter = (ArrayAdapter) spinner.getAdapter();
        if (deviceConfiguration.isEnabled()) {
            spinner.setSelection(arrayAdapter.getPosition(deviceConfiguration.getType().toString()));
        } else {
            spinner.setSelection(0);
        }
        spinner.setOnItemSelectedListener(this.f133k);
    }

    private void m98b(View view, DeviceConfiguration deviceConfiguration) {
        EditText editText = (EditText) view.findViewById(R.id.editTextResult_digital_device);
        if (deviceConfiguration.isEnabled()) {
            editText.setText(deviceConfiguration.getName());
            editText.setEnabled(true);
            return;
        }
        editText.setText("NO DEVICE ATTACHED");
        editText.setEnabled(false);
    }

    private void m91a(View view) {
        ((EditText) view.findViewById(R.id.editTextResult_digital_device)).addTextChangedListener(new C0038a(view, null));
    }

    private View m88a(int i) {
        switch (i) {
            case 0:
                return this.f124b;
            case BuildConfig.VERSION_CODE /*1*/:
                return this.f125c;
            case 2:
                return this.f126d;
            case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                return this.f127e;
            case 4:
                return this.f128f;
            case 5:
                return this.f129g;
            case 6:
                return this.f130h;
            case 7:
                return this.f131i;
            default:
                return null;
        }
    }

    public void saveDigitalDevices(View v) {
        m90a();
    }

    private void m90a() {
        Bundle bundle = new Bundle();
        for (int i = 0; i < this.f132j.size(); i++) {
            bundle.putSerializable(String.valueOf(i), (Serializable) this.f132j.get(i));
        }
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.putExtras(bundle);
        setResult(-1, intent);
        finish();
    }

    public void cancelDigitalDevices(View v) {
        setResult(0, new Intent());
        finish();
    }

    private void m94a(LinearLayout linearLayout) {
        int parseInt = Integer.parseInt(((TextView) linearLayout.findViewById(R.id.port_number_digital_device)).getText().toString());
        EditText editText = (EditText) linearLayout.findViewById(R.id.editTextResult_digital_device);
        editText.setEnabled(false);
        editText.setText("NO DEVICE ATTACHED");
        ((DeviceConfiguration) this.f132j.get(parseInt)).setEnabled(false);
    }

    private void m95a(LinearLayout linearLayout, String str) {
        int parseInt = Integer.parseInt(((TextView) linearLayout.findViewById(R.id.port_number_digital_device)).getText().toString());
        EditText editText = (EditText) linearLayout.findViewById(R.id.editTextResult_digital_device);
        editText.setEnabled(true);
        DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f132j.get(parseInt);
        deviceConfiguration.setType(deviceConfiguration.typeFromString(str));
        deviceConfiguration.setEnabled(true);
        m93a(editText, deviceConfiguration);
    }

    private void m93a(EditText editText, DeviceConfiguration deviceConfiguration) {
        if (editText.getText().toString().equalsIgnoreCase("NO DEVICE ATTACHED")) {
            editText.setText(BuildConfig.VERSION_NAME);
            deviceConfiguration.setName(BuildConfig.VERSION_NAME);
            return;
        }
        editText.setText(deviceConfiguration.getName());
    }
}
