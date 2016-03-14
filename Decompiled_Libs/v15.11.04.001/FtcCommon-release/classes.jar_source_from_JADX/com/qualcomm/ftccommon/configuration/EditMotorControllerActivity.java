package com.qualcomm.ftccommon.configuration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.qualcomm.ftccommon.BuildConfig;
import com.qualcomm.ftccommon.R;
import com.qualcomm.robotcore.hardware.configuration.ControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import com.qualcomm.robotcore.hardware.configuration.MotorConfiguration;
import com.qualcomm.robotcore.hardware.configuration.MotorControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.Utility;
import com.qualcomm.robotcore.util.RobotLog;
import java.io.Serializable;
import java.util.ArrayList;

public class EditMotorControllerActivity extends Activity {
    public static final String EDIT_MOTOR_CONTROLLER_CONFIG = "EDIT_MOTOR_CONTROLLER_CONFIG";
    private Utility f184a;
    private MotorControllerConfiguration f185b;
    private ArrayList<DeviceConfiguration> f186c;
    private MotorConfiguration f187d;
    private MotorConfiguration f188e;
    private EditText f189f;
    private boolean f190g;
    private boolean f191h;
    private CheckBox f192i;
    private CheckBox f193j;
    private EditText f194k;
    private EditText f195l;

    /* renamed from: com.qualcomm.ftccommon.configuration.EditMotorControllerActivity.1 */
    class C00451 implements OnClickListener {
        final /* synthetic */ EditMotorControllerActivity f182a;

        C00451(EditMotorControllerActivity editMotorControllerActivity) {
            this.f182a = editMotorControllerActivity;
        }

        public void onClick(View view) {
            if (((CheckBox) view).isChecked()) {
                this.f182a.f190g = true;
                this.f182a.f194k.setEnabled(true);
                this.f182a.f194k.setText(BuildConfig.VERSION_NAME);
                this.f182a.f187d.setPort(1);
                this.f182a.f187d.setType(ConfigurationType.MOTOR);
                return;
            }
            this.f182a.f190g = false;
            this.f182a.f194k.setEnabled(false);
            this.f182a.f194k.setText("NO DEVICE ATTACHED");
            this.f182a.f187d.setType(ConfigurationType.NOTHING);
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.EditMotorControllerActivity.2 */
    class C00462 implements OnClickListener {
        final /* synthetic */ EditMotorControllerActivity f183a;

        C00462(EditMotorControllerActivity editMotorControllerActivity) {
            this.f183a = editMotorControllerActivity;
        }

        public void onClick(View view) {
            if (((CheckBox) view).isChecked()) {
                this.f183a.f191h = true;
                this.f183a.f195l.setEnabled(true);
                this.f183a.f195l.setText(BuildConfig.VERSION_NAME);
                this.f183a.f188e.setPort(2);
                this.f183a.f187d.setType(ConfigurationType.MOTOR);
                return;
            }
            this.f183a.f191h = false;
            this.f183a.f195l.setEnabled(false);
            this.f183a.f195l.setText("NO DEVICE ATTACHED");
            this.f183a.f187d.setType(ConfigurationType.NOTHING);
        }
    }

    public EditMotorControllerActivity() {
        this.f186c = new ArrayList();
        this.f187d = new MotorConfiguration(1);
        this.f188e = new MotorConfiguration(2);
        this.f190g = true;
        this.f191h = true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.motors);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.f184a = new Utility(this);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f189f = (EditText) findViewById(R.id.controller_name);
        this.f192i = (CheckBox) findViewById(R.id.checkbox_port7);
        this.f193j = (CheckBox) findViewById(R.id.checkbox_port6);
        this.f194k = (EditText) findViewById(R.id.editTextResult_analogInput7);
        this.f195l = (EditText) findViewById(R.id.editTextResult_analogInput6);
    }

    protected void onStart() {
        super.onStart();
        this.f184a.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        Serializable serializableExtra = getIntent().getSerializableExtra(EDIT_MOTOR_CONTROLLER_CONFIG);
        if (serializableExtra != null) {
            this.f185b = (MotorControllerConfiguration) serializableExtra;
            this.f186c = (ArrayList) this.f185b.getMotors();
            this.f187d = (MotorConfiguration) this.f186c.get(0);
            this.f188e = (MotorConfiguration) this.f186c.get(1);
            this.f189f.setText(this.f185b.getName());
            TextView textView = (TextView) findViewById(R.id.motor_controller_serialNumber);
            CharSequence serialNumber = this.f185b.getSerialNumber().toString();
            if (serialNumber.equalsIgnoreCase(ControllerConfiguration.NO_SERIAL_NUMBER.toString())) {
                serialNumber = "No serial number";
            }
            textView.setText(serialNumber);
            this.f194k.setText(this.f187d.getName());
            this.f195l.setText(this.f188e.getName());
            m132a();
            m133a(this.f187d, this.f192i);
            m136b();
            m133a(this.f188e, this.f193j);
        }
    }

    private void m133a(MotorConfiguration motorConfiguration, CheckBox checkBox) {
        if (motorConfiguration.getName().equals("NO DEVICE ATTACHED") || motorConfiguration.getType() == ConfigurationType.NOTHING) {
            checkBox.setChecked(true);
            checkBox.performClick();
            return;
        }
        checkBox.setChecked(true);
    }

    private void m132a() {
        this.f192i.setOnClickListener(new C00451(this));
    }

    private void m136b() {
        this.f193j.setOnClickListener(new C00462(this));
    }

    public void saveMotorController(View v) {
        m139c();
    }

    private void m139c() {
        Intent intent = new Intent();
        ArrayList arrayList = new ArrayList();
        if (this.f190g) {
            MotorConfiguration motorConfiguration = new MotorConfiguration(this.f194k.getText().toString());
            motorConfiguration.setEnabled(true);
            motorConfiguration.setPort(1);
            arrayList.add(motorConfiguration);
        } else {
            arrayList.add(new MotorConfiguration(1));
        }
        if (this.f191h) {
            motorConfiguration = new MotorConfiguration(this.f195l.getText().toString());
            motorConfiguration.setEnabled(true);
            motorConfiguration.setPort(2);
            arrayList.add(motorConfiguration);
        } else {
            arrayList.add(new MotorConfiguration(2));
        }
        this.f185b.addMotors(arrayList);
        this.f185b.setName(this.f189f.getText().toString());
        intent.putExtra(EDIT_MOTOR_CONTROLLER_CONFIG, this.f185b);
        setResult(-1, intent);
        finish();
    }

    public void cancelMotorController(View v) {
        setResult(0, new Intent());
        finish();
    }
}
