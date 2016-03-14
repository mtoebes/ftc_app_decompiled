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
    private Utility f152a;
    private View f153b;
    private View f154c;
    private View f155d;
    private View f156e;
    private View f157f;
    private View f158g;
    private ArrayList<DeviceConfiguration> f159h;
    private OnItemSelectedListener f160i;

    /* renamed from: com.qualcomm.ftccommon.configuration.EditI2cDevicesActivity.1 */
    class C00461 implements OnItemSelectedListener {
        final /* synthetic */ EditI2cDevicesActivity f149a;

        C00461(EditI2cDevicesActivity editI2cDevicesActivity) {
            this.f149a = editI2cDevicesActivity;
        }

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
            String obj = parent.getItemAtPosition(pos).toString();
            LinearLayout linearLayout = (LinearLayout) view.getParent().getParent().getParent();
            if (obj.equalsIgnoreCase(ConfigurationType.NOTHING.toString())) {
                this.f149a.m116a(linearLayout);
            } else {
                this.f149a.m117a(linearLayout, obj);
            }
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.EditI2cDevicesActivity.a */
    private class C0047a implements TextWatcher {
        final /* synthetic */ EditI2cDevicesActivity f150a;
        private int f151b;

        private C0047a(EditI2cDevicesActivity editI2cDevicesActivity, View view) {
            this.f150a = editI2cDevicesActivity;
            this.f151b = Integer.parseInt(((TextView) view.findViewById(R.id.port_number_i2c)).getText().toString());
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            ((DeviceConfiguration) this.f150a.f159h.get(this.f151b)).setName(editable.toString());
        }
    }

    public EditI2cDevicesActivity() {
        this.f159h = new ArrayList();
        this.f160i = new C00461(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.i2cs);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.f152a = new Utility(this);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f153b = getLayoutInflater().inflate(R.layout.i2c_device, (LinearLayout) findViewById(R.id.linearLayout_i2c0), true);
        ((TextView) this.f153b.findViewById(R.id.port_number_i2c)).setText("0");
        this.f154c = getLayoutInflater().inflate(R.layout.i2c_device, (LinearLayout) findViewById(R.id.linearLayout_i2c1), true);
        ((TextView) this.f154c.findViewById(R.id.port_number_i2c)).setText("1");
        this.f155d = getLayoutInflater().inflate(R.layout.i2c_device, (LinearLayout) findViewById(R.id.linearLayout_i2c2), true);
        ((TextView) this.f155d.findViewById(R.id.port_number_i2c)).setText("2");
        this.f156e = getLayoutInflater().inflate(R.layout.i2c_device, (LinearLayout) findViewById(R.id.linearLayout_i2c3), true);
        ((TextView) this.f156e.findViewById(R.id.port_number_i2c)).setText("3");
        this.f157f = getLayoutInflater().inflate(R.layout.i2c_device, (LinearLayout) findViewById(R.id.linearLayout_i2c4), true);
        ((TextView) this.f157f.findViewById(R.id.port_number_i2c)).setText("4");
        this.f158g = getLayoutInflater().inflate(R.layout.i2c_device, (LinearLayout) findViewById(R.id.linearLayout_i2c5), true);
        ((TextView) this.f158g.findViewById(R.id.port_number_i2c)).setText("5");
    }

    protected void onStart() {
        super.onStart();
        this.f152a.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            for (String str : extras.keySet()) {
                this.f159h.add(Integer.parseInt(str), (DeviceConfiguration) extras.getSerializable(str));
            }
            for (int i = 0; i < this.f159h.size(); i++) {
                View a = m110a(i);
                DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f159h.get(i);
                m113a(a);
                m120b(a, deviceConfiguration);
                m114a(a, deviceConfiguration);
            }
        }
    }

    private void m114a(View view, DeviceConfiguration deviceConfiguration) {
        Spinner spinner = (Spinner) view.findViewById(R.id.choiceSpinner_i2c);
        ArrayAdapter arrayAdapter = (ArrayAdapter) spinner.getAdapter();
        if (deviceConfiguration.isEnabled()) {
            spinner.setSelection(arrayAdapter.getPosition(deviceConfiguration.getType().toString()));
        } else {
            spinner.setSelection(0);
        }
        spinner.setOnItemSelectedListener(this.f160i);
    }

    private void m120b(View view, DeviceConfiguration deviceConfiguration) {
        EditText editText = (EditText) view.findViewById(R.id.editTextResult_i2c);
        if (deviceConfiguration.isEnabled()) {
            editText.setText(deviceConfiguration.getName());
            editText.setEnabled(true);
            return;
        }
        editText.setText("NO DEVICE ATTACHED");
        editText.setEnabled(false);
    }

    private void m113a(View view) {
        ((EditText) view.findViewById(R.id.editTextResult_i2c)).addTextChangedListener(new C0047a(view, null));
    }

    private View m110a(int i) {
        switch (i) {
            case 0:
                return this.f153b;
            case BuildConfig.VERSION_CODE /*1*/:
                return this.f154c;
            case 2:
                return this.f155d;
            case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                return this.f156e;
            case 4:
                return this.f157f;
            case 5:
                return this.f158g;
            default:
                return null;
        }
    }

    public void saveI2cDevices(View v) {
        m112a();
    }

    private void m112a() {
        Bundle bundle = new Bundle();
        for (int i = 0; i < this.f159h.size(); i++) {
            bundle.putSerializable(String.valueOf(i), (DeviceConfiguration) this.f159h.get(i));
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

    private void m116a(LinearLayout linearLayout) {
        int parseInt = Integer.parseInt(((TextView) linearLayout.findViewById(R.id.port_number_i2c)).getText().toString());
        EditText editText = (EditText) linearLayout.findViewById(R.id.editTextResult_i2c);
        editText.setEnabled(false);
        editText.setText("NO DEVICE ATTACHED");
        ((DeviceConfiguration) this.f159h.get(parseInt)).setEnabled(false);
    }

    private void m117a(LinearLayout linearLayout, String str) {
        int parseInt = Integer.parseInt(((TextView) linearLayout.findViewById(R.id.port_number_i2c)).getText().toString());
        EditText editText = (EditText) linearLayout.findViewById(R.id.editTextResult_i2c);
        editText.setEnabled(true);
        DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f159h.get(parseInt);
        deviceConfiguration.setType(deviceConfiguration.typeFromString(str));
        deviceConfiguration.setEnabled(true);
        m115a(editText, deviceConfiguration);
    }

    private void m115a(EditText editText, DeviceConfiguration deviceConfiguration) {
        if (editText.getText().toString().equalsIgnoreCase("NO DEVICE ATTACHED")) {
            editText.setText(BuildConfig.VERSION_NAME);
            deviceConfiguration.setName(BuildConfig.VERSION_NAME);
            return;
        }
        editText.setText(deviceConfiguration.getName());
    }
}
