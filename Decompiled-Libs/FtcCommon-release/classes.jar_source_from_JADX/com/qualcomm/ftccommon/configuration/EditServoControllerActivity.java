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
import com.qualcomm.ftccommon.LaunchActivityConstantsList;
import com.qualcomm.ftccommon.R;
import com.qualcomm.robotcore.hardware.configuration.ControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration;
import com.qualcomm.robotcore.hardware.configuration.ServoControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.Utility;
import com.qualcomm.robotcore.util.RobotLog;
import java.io.Serializable;
import java.util.ArrayList;

public class EditServoControllerActivity extends Activity {
    public static final String EDIT_SERVO_ACTIVITY = "Edit Servo ControllerConfiguration Activity";
    private Utility f210a;
    private ServoControllerConfiguration f211b;
    private ArrayList<DeviceConfiguration> f212c;
    private EditText f213d;
    private View f214e;
    private View f215f;
    private View f216g;
    private View f217h;
    private View f218i;
    private View f219j;

    /* renamed from: com.qualcomm.ftccommon.configuration.EditServoControllerActivity.1 */
    class C00491 implements OnClickListener {
        final /* synthetic */ EditText f205a;
        final /* synthetic */ DeviceConfiguration f206b;
        final /* synthetic */ EditServoControllerActivity f207c;

        C00491(EditServoControllerActivity editServoControllerActivity, EditText editText, DeviceConfiguration deviceConfiguration) {
            this.f207c = editServoControllerActivity;
            this.f205a = editText;
            this.f206b = deviceConfiguration;
        }

        public void onClick(View view) {
            if (((CheckBox) view).isChecked()) {
                this.f205a.setEnabled(true);
                this.f205a.setText(BuildConfig.VERSION_NAME);
                this.f206b.setEnabled(true);
                this.f206b.setName(BuildConfig.VERSION_NAME);
                return;
            }
            this.f205a.setEnabled(false);
            this.f206b.setEnabled(false);
            this.f206b.setName("NO DEVICE ATTACHED");
            this.f205a.setText("NO DEVICE ATTACHED");
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.EditServoControllerActivity.a */
    private class C0050a implements TextWatcher {
        final /* synthetic */ EditServoControllerActivity f208a;
        private int f209b;

        private C0050a(EditServoControllerActivity editServoControllerActivity, View view) {
            this.f208a = editServoControllerActivity;
            this.f209b = Integer.parseInt(((TextView) view.findViewById(R.id.port_number_servo)).getText().toString());
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            ((DeviceConfiguration) this.f208a.f212c.get(this.f209b - 1)).setName(editable.toString());
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servos);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.f210a = new Utility(this);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f213d = (EditText) findViewById(R.id.servocontroller_name);
        this.f214e = getLayoutInflater().inflate(R.layout.servo, (LinearLayout) findViewById(R.id.linearLayout_servo1), true);
        ((TextView) this.f214e.findViewById(R.id.port_number_servo)).setText("1");
        this.f215f = getLayoutInflater().inflate(R.layout.servo, (LinearLayout) findViewById(R.id.linearLayout_servo2), true);
        ((TextView) this.f215f.findViewById(R.id.port_number_servo)).setText("2");
        this.f216g = getLayoutInflater().inflate(R.layout.servo, (LinearLayout) findViewById(R.id.linearLayout_servo3), true);
        ((TextView) this.f216g.findViewById(R.id.port_number_servo)).setText("3");
        this.f217h = getLayoutInflater().inflate(R.layout.servo, (LinearLayout) findViewById(R.id.linearLayout_servo4), true);
        ((TextView) this.f217h.findViewById(R.id.port_number_servo)).setText("4");
        this.f218i = getLayoutInflater().inflate(R.layout.servo, (LinearLayout) findViewById(R.id.linearLayout_servo5), true);
        ((TextView) this.f218i.findViewById(R.id.port_number_servo)).setText("5");
        this.f219j = getLayoutInflater().inflate(R.layout.servo, (LinearLayout) findViewById(R.id.linearLayout_servo6), true);
        ((TextView) this.f219j.findViewById(R.id.port_number_servo)).setText("6");
    }

    protected void onStart() {
        super.onStart();
        this.f210a.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        Serializable serializableExtra = getIntent().getSerializableExtra(EDIT_SERVO_ACTIVITY);
        if (serializableExtra != null) {
            this.f211b = (ServoControllerConfiguration) serializableExtra;
            this.f212c = (ArrayList) this.f211b.getServos();
        }
        this.f213d.setText(this.f211b.getName());
        TextView textView = (TextView) findViewById(R.id.servo_controller_serialNumber);
        CharSequence serialNumber = this.f211b.getSerialNumber().toString();
        if (serialNumber.equalsIgnoreCase(ControllerConfiguration.NO_SERIAL_NUMBER.toString())) {
            serialNumber = "No serial number";
        }
        textView.setText(serialNumber);
        for (int i = 0; i < this.f212c.size(); i++) {
            m151c(i + 1);
            m149a(i + 1);
            m150b(i + 1);
        }
    }

    private void m149a(int i) {
        View d = m152d(i);
        ((EditText) d.findViewById(R.id.editTextResult_servo)).addTextChangedListener(new C0050a(d, null));
    }

    private void m150b(int i) {
        View d = m152d(i);
        CheckBox checkBox = (CheckBox) d.findViewById(R.id.checkbox_port_servo);
        DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f212c.get(i - 1);
        if (deviceConfiguration.isEnabled()) {
            checkBox.setChecked(true);
            ((EditText) d.findViewById(R.id.editTextResult_servo)).setText(deviceConfiguration.getName());
            return;
        }
        checkBox.setChecked(true);
        checkBox.performClick();
    }

    private void m151c(int i) {
        View d = m152d(i);
        ((CheckBox) d.findViewById(R.id.checkbox_port_servo)).setOnClickListener(new C00491(this, (EditText) d.findViewById(R.id.editTextResult_servo), (DeviceConfiguration) this.f212c.get(i - 1)));
    }

    private View m152d(int i) {
        switch (i) {
            case BuildConfig.VERSION_CODE /*1*/:
                return this.f214e;
            case 2:
                return this.f215f;
            case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                return this.f216g;
            case 4:
                return this.f217h;
            case 5:
                return this.f218i;
            case 6:
                return this.f219j;
            default:
                return null;
        }
    }

    public void saveServoController(View v) {
        m148a();
    }

    private void m148a() {
        Intent intent = new Intent();
        this.f211b.addServos(this.f212c);
        this.f211b.setName(this.f213d.getText().toString());
        intent.putExtra(EDIT_SERVO_ACTIVITY, this.f211b);
        setResult(-1, intent);
        finish();
    }

    public void cancelServoController(View v) {
        setResult(0, new Intent());
        finish();
    }
}
