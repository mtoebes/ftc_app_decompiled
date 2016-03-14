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
    private Utility f107a;
    private View f108b;
    private View f109c;
    private View f110d;
    private View f111e;
    private View f112f;
    private View f113g;
    private View f114h;
    private View f115i;
    private ArrayList<DeviceConfiguration> f116j;
    private OnItemSelectedListener f117k;

    /* renamed from: com.qualcomm.ftccommon.configuration.EditAnalogInputDevicesActivity.1 */
    class C00381 implements OnItemSelectedListener {
        final /* synthetic */ EditAnalogInputDevicesActivity f104a;

        C00381(EditAnalogInputDevicesActivity editAnalogInputDevicesActivity) {
            this.f104a = editAnalogInputDevicesActivity;
        }

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
            String obj = parent.getItemAtPosition(pos).toString();
            LinearLayout linearLayout = (LinearLayout) view.getParent().getParent().getParent();
            if (obj.equalsIgnoreCase(ConfigurationType.NOTHING.toString())) {
                this.f104a.m77a(linearLayout);
            } else {
                this.f104a.m78a(linearLayout, obj);
            }
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.EditAnalogInputDevicesActivity.a */
    private class C0039a implements TextWatcher {
        final /* synthetic */ EditAnalogInputDevicesActivity f105a;
        private int f106b;

        private C0039a(EditAnalogInputDevicesActivity editAnalogInputDevicesActivity, View view) {
            this.f105a = editAnalogInputDevicesActivity;
            this.f106b = Integer.parseInt(((TextView) view.findViewById(R.id.port_number_analogInput)).getText().toString());
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            ((DeviceConfiguration) this.f105a.f116j.get(this.f106b)).setName(editable.toString());
        }
    }

    public EditAnalogInputDevicesActivity() {
        this.f116j = new ArrayList();
        this.f117k = new C00381(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analog_inputs);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.f107a = new Utility(this);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f108b = getLayoutInflater().inflate(R.layout.analog_input_device, (LinearLayout) findViewById(R.id.linearLayout_analogInput0), true);
        ((TextView) this.f108b.findViewById(R.id.port_number_analogInput)).setText("0");
        this.f109c = getLayoutInflater().inflate(R.layout.analog_input_device, (LinearLayout) findViewById(R.id.linearLayout_analogInput1), true);
        ((TextView) this.f109c.findViewById(R.id.port_number_analogInput)).setText("1");
        this.f110d = getLayoutInflater().inflate(R.layout.analog_input_device, (LinearLayout) findViewById(R.id.linearLayout_analogInput2), true);
        ((TextView) this.f110d.findViewById(R.id.port_number_analogInput)).setText("2");
        this.f111e = getLayoutInflater().inflate(R.layout.analog_input_device, (LinearLayout) findViewById(R.id.linearLayout_analogInput3), true);
        ((TextView) this.f111e.findViewById(R.id.port_number_analogInput)).setText("3");
        this.f112f = getLayoutInflater().inflate(R.layout.analog_input_device, (LinearLayout) findViewById(R.id.linearLayout_analogInput4), true);
        ((TextView) this.f112f.findViewById(R.id.port_number_analogInput)).setText("4");
        this.f113g = getLayoutInflater().inflate(R.layout.analog_input_device, (LinearLayout) findViewById(R.id.linearLayout_analogInput5), true);
        ((TextView) this.f113g.findViewById(R.id.port_number_analogInput)).setText("5");
        this.f114h = getLayoutInflater().inflate(R.layout.analog_input_device, (LinearLayout) findViewById(R.id.linearLayout_analogInput6), true);
        ((TextView) this.f114h.findViewById(R.id.port_number_analogInput)).setText("6");
        this.f115i = getLayoutInflater().inflate(R.layout.analog_input_device, (LinearLayout) findViewById(R.id.linearLayout_analogInput7), true);
        ((TextView) this.f115i.findViewById(R.id.port_number_analogInput)).setText("7");
    }

    protected void onStart() {
        super.onStart();
        this.f107a.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            for (String str : extras.keySet()) {
                this.f116j.add(Integer.parseInt(str), (DeviceConfiguration) extras.getSerializable(str));
            }
            for (int i = 0; i < this.f116j.size(); i++) {
                View a = m71a(i);
                DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f116j.get(i);
                m74a(a);
                m81b(a, deviceConfiguration);
                m75a(a, deviceConfiguration);
            }
        }
    }

    private void m75a(View view, DeviceConfiguration deviceConfiguration) {
        Spinner spinner = (Spinner) view.findViewById(R.id.choiceSpinner_analogInput);
        ArrayAdapter arrayAdapter = (ArrayAdapter) spinner.getAdapter();
        if (deviceConfiguration.isEnabled()) {
            spinner.setSelection(arrayAdapter.getPosition(deviceConfiguration.getType().toString()));
        } else {
            spinner.setSelection(0);
        }
        spinner.setOnItemSelectedListener(this.f117k);
    }

    private void m81b(View view, DeviceConfiguration deviceConfiguration) {
        EditText editText = (EditText) view.findViewById(R.id.editTextResult_analogInput);
        if (deviceConfiguration.isEnabled()) {
            editText.setText(deviceConfiguration.getName());
            editText.setEnabled(true);
            return;
        }
        editText.setText("NO DEVICE ATTACHED");
        editText.setEnabled(false);
    }

    private void m74a(View view) {
        ((EditText) view.findViewById(R.id.editTextResult_analogInput)).addTextChangedListener(new C0039a(view, null));
    }

    private View m71a(int i) {
        switch (i) {
            case 0:
                return this.f108b;
            case BuildConfig.VERSION_CODE /*1*/:
                return this.f109c;
            case 2:
                return this.f110d;
            case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                return this.f111e;
            case 4:
                return this.f112f;
            case 5:
                return this.f113g;
            case 6:
                return this.f114h;
            case 7:
                return this.f115i;
            default:
                return null;
        }
    }

    public void saveAnalogInputDevices(View v) {
        m73a();
    }

    private void m73a() {
        Bundle bundle = new Bundle();
        for (int i = 0; i < this.f116j.size(); i++) {
            bundle.putSerializable(String.valueOf(i), (Serializable) this.f116j.get(i));
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

    private void m77a(LinearLayout linearLayout) {
        int parseInt = Integer.parseInt(((TextView) linearLayout.findViewById(R.id.port_number_analogInput)).getText().toString());
        EditText editText = (EditText) linearLayout.findViewById(R.id.editTextResult_analogInput);
        editText.setEnabled(false);
        editText.setText("NO DEVICE ATTACHED");
        ((DeviceConfiguration) this.f116j.get(parseInt)).setEnabled(false);
    }

    private void m78a(LinearLayout linearLayout, String str) {
        int parseInt = Integer.parseInt(((TextView) linearLayout.findViewById(R.id.port_number_analogInput)).getText().toString());
        EditText editText = (EditText) linearLayout.findViewById(R.id.editTextResult_analogInput);
        editText.setEnabled(true);
        DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f116j.get(parseInt);
        deviceConfiguration.setType(deviceConfiguration.typeFromString(str));
        deviceConfiguration.setEnabled(true);
        m76a(editText, deviceConfiguration);
    }

    private void m76a(EditText editText, DeviceConfiguration deviceConfiguration) {
        if (editText.getText().toString().equalsIgnoreCase("NO DEVICE ATTACHED")) {
            editText.setText(BuildConfig.VERSION_NAME);
            deviceConfiguration.setName(BuildConfig.VERSION_NAME);
            return;
        }
        editText.setText(deviceConfiguration.getName());
    }
}
