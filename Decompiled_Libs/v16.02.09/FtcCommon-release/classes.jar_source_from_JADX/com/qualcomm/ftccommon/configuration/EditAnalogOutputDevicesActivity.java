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
    private Utility f121a;
    private View f122b;
    private View f123c;
    private ArrayList<DeviceConfiguration> f124d;
    private OnItemSelectedListener f125e;

    /* renamed from: com.qualcomm.ftccommon.configuration.EditAnalogOutputDevicesActivity.1 */
    class C00401 implements OnItemSelectedListener {
        final /* synthetic */ EditAnalogOutputDevicesActivity f118a;

        C00401(EditAnalogOutputDevicesActivity editAnalogOutputDevicesActivity) {
            this.f118a = editAnalogOutputDevicesActivity;
        }

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
            String obj = parent.getItemAtPosition(pos).toString();
            LinearLayout linearLayout = (LinearLayout) view.getParent().getParent().getParent();
            if (obj.equalsIgnoreCase(ConfigurationType.NOTHING.toString())) {
                this.f118a.m88a(linearLayout);
            } else {
                this.f118a.m89a(linearLayout, obj);
            }
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.EditAnalogOutputDevicesActivity.a */
    private class C0041a implements TextWatcher {
        final /* synthetic */ EditAnalogOutputDevicesActivity f119a;
        private int f120b;

        private C0041a(EditAnalogOutputDevicesActivity editAnalogOutputDevicesActivity, View view) {
            this.f119a = editAnalogOutputDevicesActivity;
            this.f120b = Integer.parseInt(((TextView) view.findViewById(R.id.port_number_analogOutput)).getText().toString());
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            ((DeviceConfiguration) this.f119a.f124d.get(this.f120b)).setName(editable.toString());
        }
    }

    public EditAnalogOutputDevicesActivity() {
        this.f124d = new ArrayList();
        this.f125e = new C00401(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analog_outputs);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.f121a = new Utility(this);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f122b = getLayoutInflater().inflate(R.layout.analog_output_device, (LinearLayout) findViewById(R.id.linearLayout_analogOutput0), true);
        ((TextView) this.f122b.findViewById(R.id.port_number_analogOutput)).setText("0");
        this.f123c = getLayoutInflater().inflate(R.layout.analog_output_device, (LinearLayout) findViewById(R.id.linearLayout_analogOutput1), true);
        ((TextView) this.f123c.findViewById(R.id.port_number_analogOutput)).setText("1");
    }

    protected void onStart() {
        super.onStart();
        this.f121a.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            for (String str : extras.keySet()) {
                this.f124d.add(Integer.parseInt(str), (DeviceConfiguration) extras.getSerializable(str));
            }
            for (int i = 0; i < this.f124d.size(); i++) {
                View a = m82a(i);
                DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f124d.get(i);
                m85a(a);
                m92b(a, deviceConfiguration);
                m86a(a, deviceConfiguration);
            }
        }
    }

    private void m86a(View view, DeviceConfiguration deviceConfiguration) {
        Spinner spinner = (Spinner) view.findViewById(R.id.choiceSpinner_analogOutput);
        ArrayAdapter arrayAdapter = (ArrayAdapter) spinner.getAdapter();
        if (deviceConfiguration.isEnabled()) {
            spinner.setSelection(arrayAdapter.getPosition(deviceConfiguration.getType().toString()));
        } else {
            spinner.setSelection(0);
        }
        spinner.setOnItemSelectedListener(this.f125e);
    }

    private void m92b(View view, DeviceConfiguration deviceConfiguration) {
        EditText editText = (EditText) view.findViewById(R.id.editTextResult_analogOutput);
        if (deviceConfiguration.isEnabled()) {
            editText.setText(deviceConfiguration.getName());
            editText.setEnabled(true);
            return;
        }
        editText.setText("NO DEVICE ATTACHED");
        editText.setEnabled(false);
    }

    private void m85a(View view) {
        ((EditText) view.findViewById(R.id.editTextResult_analogOutput)).addTextChangedListener(new C0041a(view, null));
    }

    private View m82a(int i) {
        switch (i) {
            case 0:
                return this.f122b;
            case BuildConfig.VERSION_CODE /*1*/:
                return this.f123c;
            default:
                return null;
        }
    }

    public void saveanalogOutputDevices(View v) {
        m84a();
    }

    private void m84a() {
        Bundle bundle = new Bundle();
        for (int i = 0; i < this.f124d.size(); i++) {
            bundle.putSerializable(String.valueOf(i), (DeviceConfiguration) this.f124d.get(i));
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

    private void m88a(LinearLayout linearLayout) {
        int parseInt = Integer.parseInt(((TextView) linearLayout.findViewById(R.id.port_number_analogOutput)).getText().toString());
        EditText editText = (EditText) linearLayout.findViewById(R.id.editTextResult_analogOutput);
        editText.setEnabled(false);
        editText.setText("NO DEVICE ATTACHED");
        ((DeviceConfiguration) this.f124d.get(parseInt)).setEnabled(false);
    }

    private void m89a(LinearLayout linearLayout, String str) {
        int parseInt = Integer.parseInt(((TextView) linearLayout.findViewById(R.id.port_number_analogOutput)).getText().toString());
        EditText editText = (EditText) linearLayout.findViewById(R.id.editTextResult_analogOutput);
        editText.setEnabled(true);
        DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f124d.get(parseInt);
        deviceConfiguration.setType(deviceConfiguration.typeFromString(str));
        deviceConfiguration.setEnabled(true);
        m87a(editText, deviceConfiguration);
    }

    private void m87a(EditText editText, DeviceConfiguration deviceConfiguration) {
        if (editText.getText().toString().equalsIgnoreCase("NO DEVICE ATTACHED")) {
            editText.setText(BuildConfig.VERSION_NAME);
            deviceConfiguration.setName(BuildConfig.VERSION_NAME);
            return;
        }
        editText.setText(deviceConfiguration.getName());
    }
}
