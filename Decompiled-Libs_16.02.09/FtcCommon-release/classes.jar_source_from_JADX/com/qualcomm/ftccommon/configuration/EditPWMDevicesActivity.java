package com.qualcomm.ftccommon.configuration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.qualcomm.ftccommon.BuildConfig;
import com.qualcomm.ftccommon.R;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration;
import com.qualcomm.robotcore.hardware.configuration.Utility;
import com.qualcomm.robotcore.util.RobotLog;
import java.io.Serializable;
import java.util.ArrayList;

public class EditPWMDevicesActivity extends Activity {
    public static final String EDIT_PWM_DEVICES = "EDIT_PWM_DEVICES";
    private Utility f216a;
    private View f217b;
    private View f218c;
    private ArrayList<DeviceConfiguration> f219d;

    /* renamed from: com.qualcomm.ftccommon.configuration.EditPWMDevicesActivity.1 */
    class C00541 implements OnClickListener {
        final /* synthetic */ EditText f211a;
        final /* synthetic */ DeviceConfiguration f212b;
        final /* synthetic */ EditPWMDevicesActivity f213c;

        C00541(EditPWMDevicesActivity editPWMDevicesActivity, EditText editText, DeviceConfiguration deviceConfiguration) {
            this.f213c = editPWMDevicesActivity;
            this.f211a = editText;
            this.f212b = deviceConfiguration;
        }

        public void onClick(View view) {
            if (((CheckBox) view).isChecked()) {
                this.f211a.setEnabled(true);
                this.f211a.setText(BuildConfig.VERSION_NAME);
                this.f212b.setEnabled(true);
                this.f212b.setName(BuildConfig.VERSION_NAME);
                return;
            }
            this.f211a.setEnabled(false);
            this.f212b.setEnabled(false);
            this.f212b.setName("NO DEVICE ATTACHED");
            this.f211a.setText("NO DEVICE ATTACHED");
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.EditPWMDevicesActivity.a */
    private class C0055a implements TextWatcher {
        final /* synthetic */ EditPWMDevicesActivity f214a;
        private int f215b;

        private C0055a(EditPWMDevicesActivity editPWMDevicesActivity, View view) {
            this.f214a = editPWMDevicesActivity;
            this.f215b = Integer.parseInt(((TextView) view.findViewById(R.id.port_number_pwm)).getText().toString());
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            ((DeviceConfiguration) this.f214a.f219d.get(this.f215b)).setName(editable.toString());
        }
    }

    public EditPWMDevicesActivity() {
        this.f219d = new ArrayList();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pwms);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.f216a = new Utility(this);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f217b = getLayoutInflater().inflate(R.layout.pwm_device, (LinearLayout) findViewById(R.id.linearLayout_pwm0), true);
        ((TextView) this.f217b.findViewById(R.id.port_number_pwm)).setText("0");
        this.f218c = getLayoutInflater().inflate(R.layout.pwm_device, (LinearLayout) findViewById(R.id.linearLayout_pwm1), true);
        ((TextView) this.f218c.findViewById(R.id.port_number_pwm)).setText("1");
    }

    protected void onStart() {
        super.onStart();
        this.f216a.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            for (String str : extras.keySet()) {
                this.f219d.add(Integer.parseInt(str), (DeviceConfiguration) extras.getSerializable(str));
            }
            for (int i = 0; i < this.f219d.size(); i++) {
                m156c(i);
                m155b(i);
                m154a(i);
            }
        }
    }

    private void m154a(int i) {
        View d = m157d(i);
        CheckBox checkBox = (CheckBox) d.findViewById(R.id.checkbox_port_pwm);
        DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f219d.get(i);
        if (deviceConfiguration.isEnabled()) {
            checkBox.setChecked(true);
            ((EditText) d.findViewById(R.id.editTextResult_pwm)).setText(deviceConfiguration.getName());
            return;
        }
        checkBox.setChecked(true);
        checkBox.performClick();
    }

    private void m155b(int i) {
        View d = m157d(i);
        ((EditText) d.findViewById(R.id.editTextResult_pwm)).addTextChangedListener(new C0055a(d, null));
    }

    private void m156c(int i) {
        View d = m157d(i);
        ((CheckBox) d.findViewById(R.id.checkbox_port_pwm)).setOnClickListener(new C00541(this, (EditText) d.findViewById(R.id.editTextResult_pwm), (DeviceConfiguration) this.f219d.get(i)));
    }

    private View m157d(int i) {
        switch (i) {
            case 0:
                return this.f217b;
            case BuildConfig.VERSION_CODE /*1*/:
                return this.f218c;
            default:
                return null;
        }
    }

    public void savePWMDevices(View v) {
        m153a();
    }

    private void m153a() {
        Bundle bundle = new Bundle();
        for (int i = 0; i < this.f219d.size(); i++) {
            bundle.putSerializable(String.valueOf(i), (Serializable) this.f219d.get(i));
        }
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.putExtras(bundle);
        setResult(-1, intent);
        finish();
    }

    public void cancelPWMDevices(View v) {
        setResult(0, new Intent());
        finish();
    }
}
