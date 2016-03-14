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
    private Utility f225a;
    private ServoControllerConfiguration f226b;
    private ArrayList<DeviceConfiguration> f227c;
    private EditText f228d;
    private View f229e;
    private View f230f;
    private View f231g;
    private View f232h;
    private View f233i;
    private View f234j;

    /* renamed from: com.qualcomm.ftccommon.configuration.EditServoControllerActivity.1 */
    class C00561 implements OnClickListener {
        final /* synthetic */ EditText f220a;
        final /* synthetic */ DeviceConfiguration f221b;
        final /* synthetic */ EditServoControllerActivity f222c;

        C00561(EditServoControllerActivity editServoControllerActivity, EditText editText, DeviceConfiguration deviceConfiguration) {
            this.f222c = editServoControllerActivity;
            this.f220a = editText;
            this.f221b = deviceConfiguration;
        }

        public void onClick(View view) {
            if (((CheckBox) view).isChecked()) {
                this.f220a.setEnabled(true);
                this.f220a.setText(BuildConfig.VERSION_NAME);
                this.f221b.setEnabled(true);
                this.f221b.setName(BuildConfig.VERSION_NAME);
                return;
            }
            this.f220a.setEnabled(false);
            this.f221b.setEnabled(false);
            this.f221b.setName("NO DEVICE ATTACHED");
            this.f220a.setText("NO DEVICE ATTACHED");
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.EditServoControllerActivity.a */
    private class C0057a implements TextWatcher {
        final /* synthetic */ EditServoControllerActivity f223a;
        private int f224b;

        private C0057a(EditServoControllerActivity editServoControllerActivity, View view) {
            this.f223a = editServoControllerActivity;
            this.f224b = Integer.parseInt(((TextView) view.findViewById(R.id.port_number_servo)).getText().toString());
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            ((DeviceConfiguration) this.f223a.f227c.get(this.f224b - 1)).setName(editable.toString());
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servos);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.f225a = new Utility(this);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f228d = (EditText) findViewById(R.id.servocontroller_name);
        this.f229e = getLayoutInflater().inflate(R.layout.servo, (LinearLayout) findViewById(R.id.linearLayout_servo1), true);
        ((TextView) this.f229e.findViewById(R.id.port_number_servo)).setText("1");
        this.f230f = getLayoutInflater().inflate(R.layout.servo, (LinearLayout) findViewById(R.id.linearLayout_servo2), true);
        ((TextView) this.f230f.findViewById(R.id.port_number_servo)).setText("2");
        this.f231g = getLayoutInflater().inflate(R.layout.servo, (LinearLayout) findViewById(R.id.linearLayout_servo3), true);
        ((TextView) this.f231g.findViewById(R.id.port_number_servo)).setText("3");
        this.f232h = getLayoutInflater().inflate(R.layout.servo, (LinearLayout) findViewById(R.id.linearLayout_servo4), true);
        ((TextView) this.f232h.findViewById(R.id.port_number_servo)).setText("4");
        this.f233i = getLayoutInflater().inflate(R.layout.servo, (LinearLayout) findViewById(R.id.linearLayout_servo5), true);
        ((TextView) this.f233i.findViewById(R.id.port_number_servo)).setText("5");
        this.f234j = getLayoutInflater().inflate(R.layout.servo, (LinearLayout) findViewById(R.id.linearLayout_servo6), true);
        ((TextView) this.f234j.findViewById(R.id.port_number_servo)).setText("6");
    }

    protected void onStart() {
        super.onStart();
        this.f225a.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        Serializable serializableExtra = getIntent().getSerializableExtra(EDIT_SERVO_ACTIVITY);
        if (serializableExtra != null) {
            this.f226b = (ServoControllerConfiguration) serializableExtra;
            this.f227c = (ArrayList) this.f226b.getServos();
        }
        this.f228d.setText(this.f226b.getName());
        TextView textView = (TextView) findViewById(R.id.servo_controller_serialNumber);
        CharSequence serialNumber = this.f226b.getSerialNumber().toString();
        if (serialNumber.equalsIgnoreCase(ControllerConfiguration.NO_SERIAL_NUMBER.toString())) {
            serialNumber = "No serial number";
        }
        textView.setText(serialNumber);
        for (int i = 0; i < this.f227c.size(); i++) {
            m162c(i + 1);
            m160a(i + 1);
            m161b(i + 1);
        }
    }

    private void m160a(int i) {
        View d = m163d(i);
        ((EditText) d.findViewById(R.id.editTextResult_servo)).addTextChangedListener(new C0057a(d, null));
    }

    private void m161b(int i) {
        View d = m163d(i);
        CheckBox checkBox = (CheckBox) d.findViewById(R.id.checkbox_port_servo);
        DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f227c.get(i - 1);
        if (deviceConfiguration.isEnabled()) {
            checkBox.setChecked(true);
            ((EditText) d.findViewById(R.id.editTextResult_servo)).setText(deviceConfiguration.getName());
            return;
        }
        checkBox.setChecked(true);
        checkBox.performClick();
    }

    private void m162c(int i) {
        View d = m163d(i);
        ((CheckBox) d.findViewById(R.id.checkbox_port_servo)).setOnClickListener(new C00561(this, (EditText) d.findViewById(R.id.editTextResult_servo), (DeviceConfiguration) this.f227c.get(i - 1)));
    }

    private View m163d(int i) {
        switch (i) {
            case BuildConfig.VERSION_CODE /*1*/:
                return this.f229e;
            case 2:
                return this.f230f;
            case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                return this.f231g;
            case 4:
                return this.f232h;
            case 5:
                return this.f233i;
            case 6:
                return this.f234j;
            default:
                return null;
        }
    }

    public void saveServoController(View v) {
        m159a();
    }

    private void m159a() {
        Intent intent = new Intent();
        this.f226b.addServos(this.f227c);
        this.f226b.setName(this.f228d.getText().toString());
        intent.putExtra(EDIT_SERVO_ACTIVITY, this.f226b);
        setResult(-1, intent);
        finish();
    }

    public void cancelServoController(View v) {
        setResult(0, new Intent());
        finish();
    }
}
