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
    private Utility f199a;
    private MotorControllerConfiguration f200b;
    private ArrayList<DeviceConfiguration> f201c;
    private MotorConfiguration f202d;
    private MotorConfiguration f203e;
    private EditText f204f;
    private boolean f205g;
    private boolean f206h;
    private CheckBox f207i;
    private CheckBox f208j;
    private EditText f209k;
    private EditText f210l;

    /* renamed from: com.qualcomm.ftccommon.configuration.EditMotorControllerActivity.1 */
    class C00521 implements OnClickListener {
        final /* synthetic */ EditMotorControllerActivity f197a;

        C00521(EditMotorControllerActivity editMotorControllerActivity) {
            this.f197a = editMotorControllerActivity;
        }

        public void onClick(View view) {
            if (((CheckBox) view).isChecked()) {
                this.f197a.f205g = true;
                this.f197a.f209k.setEnabled(true);
                this.f197a.f209k.setText(BuildConfig.VERSION_NAME);
                this.f197a.f202d.setPort(1);
                this.f197a.f202d.setType(ConfigurationType.MOTOR);
                return;
            }
            this.f197a.f205g = false;
            this.f197a.f209k.setEnabled(false);
            this.f197a.f209k.setText("NO DEVICE ATTACHED");
            this.f197a.f202d.setType(ConfigurationType.NOTHING);
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.EditMotorControllerActivity.2 */
    class C00532 implements OnClickListener {
        final /* synthetic */ EditMotorControllerActivity f198a;

        C00532(EditMotorControllerActivity editMotorControllerActivity) {
            this.f198a = editMotorControllerActivity;
        }

        public void onClick(View view) {
            if (((CheckBox) view).isChecked()) {
                this.f198a.f206h = true;
                this.f198a.f210l.setEnabled(true);
                this.f198a.f210l.setText(BuildConfig.VERSION_NAME);
                this.f198a.f203e.setPort(2);
                this.f198a.f202d.setType(ConfigurationType.MOTOR);
                return;
            }
            this.f198a.f206h = false;
            this.f198a.f210l.setEnabled(false);
            this.f198a.f210l.setText("NO DEVICE ATTACHED");
            this.f198a.f202d.setType(ConfigurationType.NOTHING);
        }
    }

    public EditMotorControllerActivity() {
        this.f201c = new ArrayList();
        this.f202d = new MotorConfiguration(1);
        this.f203e = new MotorConfiguration(2);
        this.f205g = true;
        this.f206h = true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.motors);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.f199a = new Utility(this);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f204f = (EditText) findViewById(R.id.controller_name);
        this.f207i = (CheckBox) findViewById(R.id.checkbox_port7);
        this.f208j = (CheckBox) findViewById(R.id.checkbox_port6);
        this.f209k = (EditText) findViewById(R.id.editTextResult_analogInput7);
        this.f210l = (EditText) findViewById(R.id.editTextResult_analogInput6);
    }

    protected void onStart() {
        super.onStart();
        this.f199a.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        Serializable serializableExtra = getIntent().getSerializableExtra(EDIT_MOTOR_CONTROLLER_CONFIG);
        if (serializableExtra != null) {
            this.f200b = (MotorControllerConfiguration) serializableExtra;
            this.f201c = (ArrayList) this.f200b.getMotors();
            this.f202d = (MotorConfiguration) this.f201c.get(0);
            this.f203e = (MotorConfiguration) this.f201c.get(1);
            this.f204f.setText(this.f200b.getName());
            TextView textView = (TextView) findViewById(R.id.motor_controller_serialNumber);
            CharSequence serialNumber = this.f200b.getSerialNumber().toString();
            if (serialNumber.equalsIgnoreCase(ControllerConfiguration.NO_SERIAL_NUMBER.toString())) {
                serialNumber = "No serial number";
            }
            textView.setText(serialNumber);
            this.f209k.setText(this.f202d.getName());
            this.f210l.setText(this.f203e.getName());
            m143a();
            m144a(this.f202d, this.f207i);
            m147b();
            m144a(this.f203e, this.f208j);
        }
    }

    private void m144a(MotorConfiguration motorConfiguration, CheckBox checkBox) {
        if (motorConfiguration.getName().equals("NO DEVICE ATTACHED") || motorConfiguration.getType() == ConfigurationType.NOTHING) {
            checkBox.setChecked(true);
            checkBox.performClick();
            return;
        }
        checkBox.setChecked(true);
    }

    private void m143a() {
        this.f207i.setOnClickListener(new C00521(this));
    }

    private void m147b() {
        this.f208j.setOnClickListener(new C00532(this));
    }

    public void saveMotorController(View v) {
        m150c();
    }

    private void m150c() {
        Intent intent = new Intent();
        ArrayList arrayList = new ArrayList();
        if (this.f205g) {
            MotorConfiguration motorConfiguration = new MotorConfiguration(this.f209k.getText().toString());
            motorConfiguration.setEnabled(true);
            motorConfiguration.setPort(1);
            arrayList.add(motorConfiguration);
        } else {
            arrayList.add(new MotorConfiguration(1));
        }
        if (this.f206h) {
            motorConfiguration = new MotorConfiguration(this.f210l.getText().toString());
            motorConfiguration.setEnabled(true);
            motorConfiguration.setPort(2);
            arrayList.add(motorConfiguration);
        } else {
            arrayList.add(new MotorConfiguration(2));
        }
        this.f200b.addMotors(arrayList);
        this.f200b.setName(this.f204f.getText().toString());
        intent.putExtra(EDIT_MOTOR_CONTROLLER_CONFIG, this.f200b);
        setResult(-1, intent);
        finish();
    }

    public void cancelMotorController(View v) {
        setResult(0, new Intent());
        finish();
    }
}
