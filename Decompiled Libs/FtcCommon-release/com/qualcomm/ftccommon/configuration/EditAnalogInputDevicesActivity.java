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

public class EditAnalogInputDevicesActivity extends Activity {
    private Utility f92a;
    private View f93b;
    private View f94c;
    private View f95d;
    private View f96e;
    private View f97f;
    private View f98g;
    private View f99h;
    private View f100i;
    private ArrayList<DeviceConfiguration> f101j;
    private OnItemSelectedListener f102k;

    /* renamed from: com.qualcomm.ftccommon.configuration.EditAnalogInputDevicesActivity.1 */
    class C00311 implements OnItemSelectedListener {
        final /* synthetic */ EditAnalogInputDevicesActivity f89a;

        C00311(EditAnalogInputDevicesActivity editAnalogInputDevicesActivity) {
            this.f89a = editAnalogInputDevicesActivity;
        }

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
            String obj = parent.getItemAtPosition(pos).toString();
            LinearLayout linearLayout = (LinearLayout) view.getParent().getParent().getParent();
            if (obj.equalsIgnoreCase(ConfigurationType.NOTHING.toString())) {
                this.f89a.m66a(linearLayout);
            } else {
                this.f89a.m67a(linearLayout, obj);
            }
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.EditAnalogInputDevicesActivity.a */
    private class C0032a implements TextWatcher {
        final /* synthetic */ EditAnalogInputDevicesActivity f90a;
        private int f91b;

        private C0032a(EditAnalogInputDevicesActivity editAnalogInputDevicesActivity, View view) {
            this.f90a = editAnalogInputDevicesActivity;
            this.f91b = Integer.parseInt(((TextView) view.findViewById(R.id.port_number_analogInput)).getText().toString());
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            ((DeviceConfiguration) this.f90a.f101j.get(this.f91b)).setName(editable.toString());
        }
    }

    public EditAnalogInputDevicesActivity() {
        this.f101j = new ArrayList();
        this.f102k = new C00311(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analog_inputs);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.f92a = new Utility(this);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f93b = getLayoutInflater().inflate(R.layout.analog_input_device, (LinearLayout) findViewById(R.id.linearLayout_analogInput0), true);
        ((TextView) this.f93b.findViewById(R.id.port_number_analogInput)).setText("0");
        this.f94c = getLayoutInflater().inflate(R.layout.analog_input_device, (LinearLayout) findViewById(R.id.linearLayout_analogInput1), true);
        ((TextView) this.f94c.findViewById(R.id.port_number_analogInput)).setText("1");
        this.f95d = getLayoutInflater().inflate(R.layout.analog_input_device, (LinearLayout) findViewById(R.id.linearLayout_analogInput2), true);
        ((TextView) this.f95d.findViewById(R.id.port_number_analogInput)).setText("2");
        this.f96e = getLayoutInflater().inflate(R.layout.analog_input_device, (LinearLayout) findViewById(R.id.linearLayout_analogInput3), true);
        ((TextView) this.f96e.findViewById(R.id.port_number_analogInput)).setText("3");
        this.f97f = getLayoutInflater().inflate(R.layout.analog_input_device, (LinearLayout) findViewById(R.id.linearLayout_analogInput4), true);
        ((TextView) this.f97f.findViewById(R.id.port_number_analogInput)).setText("4");
        this.f98g = getLayoutInflater().inflate(R.layout.analog_input_device, (LinearLayout) findViewById(R.id.linearLayout_analogInput5), true);
        ((TextView) this.f98g.findViewById(R.id.port_number_analogInput)).setText("5");
        this.f99h = getLayoutInflater().inflate(R.layout.analog_input_device, (LinearLayout) findViewById(R.id.linearLayout_analogInput6), true);
        ((TextView) this.f99h.findViewById(R.id.port_number_analogInput)).setText("6");
        this.f100i = getLayoutInflater().inflate(R.layout.analog_input_device, (LinearLayout) findViewById(R.id.linearLayout_analogInput7), true);
        ((TextView) this.f100i.findViewById(R.id.port_number_analogInput)).setText("7");
    }

    protected void onStart() {
        super.onStart();
        this.f92a.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            for (String str : extras.keySet()) {
                this.f101j.add(Integer.parseInt(str), (DeviceConfiguration) extras.getSerializable(str));
            }
            for (int i = 0; i < this.f101j.size(); i++) {
                View a = m60a(i);
                DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f101j.get(i);
                m63a(a);
                m70b(a, deviceConfiguration);
                m64a(a, deviceConfiguration);
            }
        }
    }

    private void m64a(View view, DeviceConfiguration deviceConfiguration) {
        Spinner spinner = (Spinner) view.findViewById(R.id.choiceSpinner_analogInput);
        ArrayAdapter arrayAdapter = (ArrayAdapter) spinner.getAdapter();
        if (deviceConfiguration.isEnabled()) {
            spinner.setSelection(arrayAdapter.getPosition(deviceConfiguration.getType().toString()));
        } else {
            spinner.setSelection(0);
        }
        spinner.setOnItemSelectedListener(this.f102k);
    }

    private void m70b(View view, DeviceConfiguration deviceConfiguration) {
        EditText editText = (EditText) view.findViewById(R.id.editTextResult_analogInput);
        if (deviceConfiguration.isEnabled()) {
            editText.setText(deviceConfiguration.getName());
            editText.setEnabled(true);
            return;
        }
        editText.setText("NO DEVICE ATTACHED");
        editText.setEnabled(false);
    }

    private void m63a(View view) {
        ((EditText) view.findViewById(R.id.editTextResult_analogInput)).addTextChangedListener(new C0032a(view, null));
    }

    private View m60a(int i) {
        switch (i) {
            case 0:
                return this.f93b;
            case BuildConfig.VERSION_CODE /*1*/:
                return this.f94c;
            case 2:
                return this.f95d;
            case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                return this.f96e;
            case 4:
                return this.f97f;
            case 5:
                return this.f98g;
            case 6:
                return this.f99h;
            case 7:
                return this.f100i;
            default:
                return null;
        }
    }

    public void saveAnalogInputDevices(View v) {
        m62a();
    }

    private void m62a() {
        Bundle bundle = new Bundle();
        for (int i = 0; i < this.f101j.size(); i++) {
            bundle.putSerializable(String.valueOf(i), (Serializable) this.f101j.get(i));
        }
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.putExtras(bundle);
        setResult(-1, intent);
        finish();
    }

    public void cancelAnalogInputDevices(View v) {
        setResult(0, new Intent());
        finish();
    }

    private void m66a(LinearLayout linearLayout) {
        int parseInt = Integer.parseInt(((TextView) linearLayout.findViewById(R.id.port_number_analogInput)).getText().toString());
        EditText editText = (EditText) linearLayout.findViewById(R.id.editTextResult_analogInput);
        editText.setEnabled(false);
        editText.setText("NO DEVICE ATTACHED");
        ((DeviceConfiguration) this.f101j.get(parseInt)).setEnabled(false);
    }

    private void m67a(LinearLayout linearLayout, String str) {
        int parseInt = Integer.parseInt(((TextView) linearLayout.findViewById(R.id.port_number_analogInput)).getText().toString());
        EditText editText = (EditText) linearLayout.findViewById(R.id.editTextResult_analogInput);
        editText.setEnabled(true);
        DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f101j.get(parseInt);
        deviceConfiguration.setType(deviceConfiguration.typeFromString(str));
        deviceConfiguration.setEnabled(true);
        m65a(editText, deviceConfiguration);
    }

    private void m65a(EditText editText, DeviceConfiguration deviceConfiguration) {
        if (editText.getText().toString().equalsIgnoreCase("NO DEVICE ATTACHED")) {
            editText.setText(BuildConfig.VERSION_NAME);
            deviceConfiguration.setName(BuildConfig.VERSION_NAME);
            return;
        }
        editText.setText(deviceConfiguration.getName());
    }
}
