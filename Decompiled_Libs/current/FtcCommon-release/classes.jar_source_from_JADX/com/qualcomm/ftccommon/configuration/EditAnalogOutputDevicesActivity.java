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
import com.qualcomm.ftccommon.R;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import com.qualcomm.robotcore.hardware.configuration.Utility;
import com.qualcomm.robotcore.util.RobotLog;
import java.util.ArrayList;

public class EditAnalogOutputDevicesActivity extends Activity {
    private Utility f106a;
    private View f107b;
    private View f108c;
    private ArrayList<DeviceConfiguration> f109d;
    private OnItemSelectedListener f110e;

    /* renamed from: com.qualcomm.ftccommon.configuration.EditAnalogOutputDevicesActivity.1 */
    class C00331 implements OnItemSelectedListener {
        final /* synthetic */ EditAnalogOutputDevicesActivity f103a;

        C00331(EditAnalogOutputDevicesActivity editAnalogOutputDevicesActivity) {
            this.f103a = editAnalogOutputDevicesActivity;
        }

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
            String obj = parent.getItemAtPosition(pos).toString();
            LinearLayout linearLayout = (LinearLayout) view.getParent().getParent().getParent();
            if (obj.equalsIgnoreCase(ConfigurationType.NOTHING.toString())) {
                this.f103a.m77a(linearLayout);
            } else {
                this.f103a.m78a(linearLayout, obj);
            }
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.EditAnalogOutputDevicesActivity.a */
    private class C0034a implements TextWatcher {
        final /* synthetic */ EditAnalogOutputDevicesActivity f104a;
        private int f105b;

        private C0034a(EditAnalogOutputDevicesActivity editAnalogOutputDevicesActivity, View view) {
            this.f104a = editAnalogOutputDevicesActivity;
            this.f105b = Integer.parseInt(((TextView) view.findViewById(R.id.port_number_analogOutput)).getText().toString());
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            ((DeviceConfiguration) this.f104a.f109d.get(this.f105b)).setName(editable.toString());
        }
    }

    public EditAnalogOutputDevicesActivity() {
        this.f109d = new ArrayList();
        this.f110e = new C00331(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analog_outputs);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.f106a = new Utility(this);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f107b = getLayoutInflater().inflate(R.layout.analog_output_device, (LinearLayout) findViewById(R.id.linearLayout_analogOutput0), true);
        ((TextView) this.f107b.findViewById(R.id.port_number_analogOutput)).setText("0");
        this.f108c = getLayoutInflater().inflate(R.layout.analog_output_device, (LinearLayout) findViewById(R.id.linearLayout_analogOutput1), true);
        ((TextView) this.f108c.findViewById(R.id.port_number_analogOutput)).setText("1");
    }

    protected void onStart() {
        super.onStart();
        this.f106a.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            for (String str : extras.keySet()) {
                this.f109d.add(Integer.parseInt(str), (DeviceConfiguration) extras.getSerializable(str));
            }
            for (int i = 0; i < this.f109d.size(); i++) {
                View a = m71a(i);
                DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f109d.get(i);
                m74a(a);
                m81b(a, deviceConfiguration);
                m75a(a, deviceConfiguration);
            }
        }
    }

    private void m75a(View view, DeviceConfiguration deviceConfiguration) {
        Spinner spinner = (Spinner) view.findViewById(R.id.choiceSpinner_analogOutput);
        ArrayAdapter arrayAdapter = (ArrayAdapter) spinner.getAdapter();
        if (deviceConfiguration.isEnabled()) {
            spinner.setSelection(arrayAdapter.getPosition(deviceConfiguration.getType().toString()));
        } else {
            spinner.setSelection(0);
        }
        spinner.setOnItemSelectedListener(this.f110e);
    }

    private void m81b(View view, DeviceConfiguration deviceConfiguration) {
        EditText editText = (EditText) view.findViewById(R.id.editTextResult_analogOutput);
        if (deviceConfiguration.isEnabled()) {
            editText.setText(deviceConfiguration.getName());
            editText.setEnabled(true);
            return;
        }
        editText.setText("NO DEVICE ATTACHED");
        editText.setEnabled(false);
    }

    private void m74a(View view) {
        ((EditText) view.findViewById(R.id.editTextResult_analogOutput)).addTextChangedListener(new C0034a(view, null));
    }

    private View m71a(int i) {
        switch (i) {
            case 0:
                return this.f107b;
            case BuildConfig.VERSION_CODE /*1*/:
                return this.f108c;
            default:
                return null;
        }
    }

    public void saveanalogOutputDevices(View v) {
        m73a();
    }

    private void m73a() {
        Bundle bundle = new Bundle();
        for (int i = 0; i < this.f109d.size(); i++) {
            bundle.putSerializable(String.valueOf(i), (DeviceConfiguration) this.f109d.get(i));
        }
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.putExtras(bundle);
        setResult(-1, intent);
        finish();
    }

    public void cancelanalogOutputDevices(View v) {
        setResult(0, new Intent());
        finish();
    }

    private void m77a(LinearLayout linearLayout) {
        int parseInt = Integer.parseInt(((TextView) linearLayout.findViewById(R.id.port_number_analogOutput)).getText().toString());
        EditText editText = (EditText) linearLayout.findViewById(R.id.editTextResult_analogOutput);
        editText.setEnabled(false);
        editText.setText("NO DEVICE ATTACHED");
        ((DeviceConfiguration) this.f109d.get(parseInt)).setEnabled(false);
    }

    private void m78a(LinearLayout linearLayout, String str) {
        int parseInt = Integer.parseInt(((TextView) linearLayout.findViewById(R.id.port_number_analogOutput)).getText().toString());
        EditText editText = (EditText) linearLayout.findViewById(R.id.editTextResult_analogOutput);
        editText.setEnabled(true);
        DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f109d.get(parseInt);
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
