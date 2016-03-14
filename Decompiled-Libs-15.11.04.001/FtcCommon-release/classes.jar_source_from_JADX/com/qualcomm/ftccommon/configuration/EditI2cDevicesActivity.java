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
import java.util.ArrayList;

public class EditI2cDevicesActivity extends Activity {
    private Utility f137a;
    private View f138b;
    private View f139c;
    private View f140d;
    private View f141e;
    private View f142f;
    private View f143g;
    private ArrayList<DeviceConfiguration> f144h;
    private OnItemSelectedListener f145i;

    /* renamed from: com.qualcomm.ftccommon.configuration.EditI2cDevicesActivity.1 */
    class C00391 implements OnItemSelectedListener {
        final /* synthetic */ EditI2cDevicesActivity f134a;

        C00391(EditI2cDevicesActivity editI2cDevicesActivity) {
            this.f134a = editI2cDevicesActivity;
        }

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
            String obj = parent.getItemAtPosition(pos).toString();
            LinearLayout linearLayout = (LinearLayout) view.getParent().getParent().getParent();
            if (obj.equalsIgnoreCase(ConfigurationType.NOTHING.toString())) {
                this.f134a.m105a(linearLayout);
            } else {
                this.f134a.m106a(linearLayout, obj);
            }
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.EditI2cDevicesActivity.a */
    private class C0040a implements TextWatcher {
        final /* synthetic */ EditI2cDevicesActivity f135a;
        private int f136b;

        private C0040a(EditI2cDevicesActivity editI2cDevicesActivity, View view) {
            this.f135a = editI2cDevicesActivity;
            this.f136b = Integer.parseInt(((TextView) view.findViewById(R.id.port_number_i2c)).getText().toString());
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            ((DeviceConfiguration) this.f135a.f144h.get(this.f136b)).setName(editable.toString());
        }
    }

    public EditI2cDevicesActivity() {
        this.f144h = new ArrayList();
        this.f145i = new C00391(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.i2cs);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.f137a = new Utility(this);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f138b = getLayoutInflater().inflate(R.layout.i2c_device, (LinearLayout) findViewById(R.id.linearLayout_i2c0), true);
        ((TextView) this.f138b.findViewById(R.id.port_number_i2c)).setText("0");
        this.f139c = getLayoutInflater().inflate(R.layout.i2c_device, (LinearLayout) findViewById(R.id.linearLayout_i2c1), true);
        ((TextView) this.f139c.findViewById(R.id.port_number_i2c)).setText("1");
        this.f140d = getLayoutInflater().inflate(R.layout.i2c_device, (LinearLayout) findViewById(R.id.linearLayout_i2c2), true);
        ((TextView) this.f140d.findViewById(R.id.port_number_i2c)).setText("2");
        this.f141e = getLayoutInflater().inflate(R.layout.i2c_device, (LinearLayout) findViewById(R.id.linearLayout_i2c3), true);
        ((TextView) this.f141e.findViewById(R.id.port_number_i2c)).setText("3");
        this.f142f = getLayoutInflater().inflate(R.layout.i2c_device, (LinearLayout) findViewById(R.id.linearLayout_i2c4), true);
        ((TextView) this.f142f.findViewById(R.id.port_number_i2c)).setText("4");
        this.f143g = getLayoutInflater().inflate(R.layout.i2c_device, (LinearLayout) findViewById(R.id.linearLayout_i2c5), true);
        ((TextView) this.f143g.findViewById(R.id.port_number_i2c)).setText("5");
    }

    protected void onStart() {
        super.onStart();
        this.f137a.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            for (String str : extras.keySet()) {
                this.f144h.add(Integer.parseInt(str), (DeviceConfiguration) extras.getSerializable(str));
            }
            for (int i = 0; i < this.f144h.size(); i++) {
                View a = m99a(i);
                DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f144h.get(i);
                m102a(a);
                m109b(a, deviceConfiguration);
                m103a(a, deviceConfiguration);
            }
        }
    }

    private void m103a(View view, DeviceConfiguration deviceConfiguration) {
        Spinner spinner = (Spinner) view.findViewById(R.id.choiceSpinner_i2c);
        ArrayAdapter arrayAdapter = (ArrayAdapter) spinner.getAdapter();
        if (deviceConfiguration.isEnabled()) {
            spinner.setSelection(arrayAdapter.getPosition(deviceConfiguration.getType().toString()));
        } else {
            spinner.setSelection(0);
        }
        spinner.setOnItemSelectedListener(this.f145i);
    }

    private void m109b(View view, DeviceConfiguration deviceConfiguration) {
        EditText editText = (EditText) view.findViewById(R.id.editTextResult_i2c);
        if (deviceConfiguration.isEnabled()) {
            editText.setText(deviceConfiguration.getName());
            editText.setEnabled(true);
            return;
        }
        editText.setText("NO DEVICE ATTACHED");
        editText.setEnabled(false);
    }

    private void m102a(View view) {
        ((EditText) view.findViewById(R.id.editTextResult_i2c)).addTextChangedListener(new C0040a(view, null));
    }

    private View m99a(int i) {
        switch (i) {
            case 0:
                return this.f138b;
            case BuildConfig.VERSION_CODE /*1*/:
                return this.f139c;
            case 2:
                return this.f140d;
            case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                return this.f141e;
            case 4:
                return this.f142f;
            case 5:
                return this.f143g;
            default:
                return null;
        }
    }

    public void saveI2cDevices(View v) {
        m101a();
    }

    private void m101a() {
        Bundle bundle = new Bundle();
        for (int i = 0; i < this.f144h.size(); i++) {
            bundle.putSerializable(String.valueOf(i), (DeviceConfiguration) this.f144h.get(i));
        }
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.putExtras(bundle);
        setResult(-1, intent);
        finish();
    }

    public void cancelI2cDevices(View v) {
        setResult(0, new Intent());
        finish();
    }

    private void m105a(LinearLayout linearLayout) {
        int parseInt = Integer.parseInt(((TextView) linearLayout.findViewById(R.id.port_number_i2c)).getText().toString());
        EditText editText = (EditText) linearLayout.findViewById(R.id.editTextResult_i2c);
        editText.setEnabled(false);
        editText.setText("NO DEVICE ATTACHED");
        ((DeviceConfiguration) this.f144h.get(parseInt)).setEnabled(false);
    }

    private void m106a(LinearLayout linearLayout, String str) {
        int parseInt = Integer.parseInt(((TextView) linearLayout.findViewById(R.id.port_number_i2c)).getText().toString());
        EditText editText = (EditText) linearLayout.findViewById(R.id.editTextResult_i2c);
        editText.setEnabled(true);
        DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f144h.get(parseInt);
        deviceConfiguration.setType(deviceConfiguration.typeFromString(str));
        deviceConfiguration.setEnabled(true);
        m104a(editText, deviceConfiguration);
    }

    private void m104a(EditText editText, DeviceConfiguration deviceConfiguration) {
        if (editText.getText().toString().equalsIgnoreCase("NO DEVICE ATTACHED")) {
            editText.setText(BuildConfig.VERSION_NAME);
            deviceConfiguration.setName(BuildConfig.VERSION_NAME);
            return;
        }
        editText.setText(deviceConfiguration.getName());
    }
}
