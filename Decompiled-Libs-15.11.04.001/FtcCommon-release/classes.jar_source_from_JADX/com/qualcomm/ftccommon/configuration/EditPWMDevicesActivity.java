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
    private Utility f201a;
    private View f202b;
    private View f203c;
    private ArrayList<DeviceConfiguration> f204d;

    /* renamed from: com.qualcomm.ftccommon.configuration.EditPWMDevicesActivity.1 */
    class C00471 implements OnClickListener {
        final /* synthetic */ EditText f196a;
        final /* synthetic */ DeviceConfiguration f197b;
        final /* synthetic */ EditPWMDevicesActivity f198c;

        C00471(EditPWMDevicesActivity editPWMDevicesActivity, EditText editText, DeviceConfiguration deviceConfiguration) {
            this.f198c = editPWMDevicesActivity;
            this.f196a = editText;
            this.f197b = deviceConfiguration;
        }

        public void onClick(View view) {
            if (((CheckBox) view).isChecked()) {
                this.f196a.setEnabled(true);
                this.f196a.setText(BuildConfig.VERSION_NAME);
                this.f197b.setEnabled(true);
                this.f197b.setName(BuildConfig.VERSION_NAME);
                return;
            }
            this.f196a.setEnabled(false);
            this.f197b.setEnabled(false);
            this.f197b.setName("NO DEVICE ATTACHED");
            this.f196a.setText("NO DEVICE ATTACHED");
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.EditPWMDevicesActivity.a */
    private class C0048a implements TextWatcher {
        final /* synthetic */ EditPWMDevicesActivity f199a;
        private int f200b;

        private C0048a(EditPWMDevicesActivity editPWMDevicesActivity, View view) {
            this.f199a = editPWMDevicesActivity;
            this.f200b = Integer.parseInt(((TextView) view.findViewById(R.id.port_number_pwm)).getText().toString());
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            ((DeviceConfiguration) this.f199a.f204d.get(this.f200b)).setName(editable.toString());
        }
    }

    public EditPWMDevicesActivity() {
        this.f204d = new ArrayList();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pwms);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.f201a = new Utility(this);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f202b = getLayoutInflater().inflate(R.layout.pwm_device, (LinearLayout) findViewById(R.id.linearLayout_pwm0), true);
        ((TextView) this.f202b.findViewById(R.id.port_number_pwm)).setText("0");
        this.f203c = getLayoutInflater().inflate(R.layout.pwm_device, (LinearLayout) findViewById(R.id.linearLayout_pwm1), true);
        ((TextView) this.f203c.findViewById(R.id.port_number_pwm)).setText("1");
    }

    protected void onStart() {
        super.onStart();
        this.f201a.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            for (String str : extras.keySet()) {
                this.f204d.add(Integer.parseInt(str), (DeviceConfiguration) extras.getSerializable(str));
            }
            for (int i = 0; i < this.f204d.size(); i++) {
                m145c(i);
                m144b(i);
                m143a(i);
            }
        }
    }

    private void m143a(int i) {
        View d = m146d(i);
        CheckBox checkBox = (CheckBox) d.findViewById(R.id.checkbox_port_pwm);
        DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f204d.get(i);
        if (deviceConfiguration.isEnabled()) {
            checkBox.setChecked(true);
            ((EditText) d.findViewById(R.id.editTextResult_pwm)).setText(deviceConfiguration.getName());
            return;
        }
        checkBox.setChecked(true);
        checkBox.performClick();
    }

    private void m144b(int i) {
        View d = m146d(i);
        ((EditText) d.findViewById(R.id.editTextResult_pwm)).addTextChangedListener(new C0048a(d, null));
    }

    private void m145c(int i) {
        View d = m146d(i);
        ((CheckBox) d.findViewById(R.id.checkbox_port_pwm)).setOnClickListener(new C00471(this, (EditText) d.findViewById(R.id.editTextResult_pwm), (DeviceConfiguration) this.f204d.get(i)));
    }

    private View m146d(int i) {
        switch (i) {
            case 0:
                return this.f202b;
            case BuildConfig.VERSION_CODE /*1*/:
                return this.f203c;
            default:
                return null;
        }
    }

    public void savePWMDevices(View v) {
        m142a();
    }

    private void m142a() {
        Bundle bundle = new Bundle();
        for (int i = 0; i < this.f204d.size(); i++) {
            bundle.putSerializable(String.valueOf(i), (Serializable) this.f204d.get(i));
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
