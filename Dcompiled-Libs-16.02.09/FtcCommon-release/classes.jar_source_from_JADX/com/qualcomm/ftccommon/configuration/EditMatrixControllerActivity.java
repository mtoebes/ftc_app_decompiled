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
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration;
import com.qualcomm.robotcore.hardware.configuration.MatrixControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.Utility;
import com.qualcomm.robotcore.util.RobotLog;
import java.io.Serializable;
import java.util.ArrayList;

public class EditMatrixControllerActivity extends Activity {
    public static final String EDIT_MATRIX_ACTIVITY = "Edit Matrix ControllerConfiguration Activity";
    private Utility f184a;
    private MatrixControllerConfiguration f185b;
    private ArrayList<DeviceConfiguration> f186c;
    private ArrayList<DeviceConfiguration> f187d;
    private EditText f188e;
    private View f189f;
    private View f190g;
    private View f191h;
    private View f192i;
    private View f193j;
    private View f194k;
    private View f195l;
    private View f196m;

    /* renamed from: com.qualcomm.ftccommon.configuration.EditMatrixControllerActivity.1 */
    class C00501 implements OnClickListener {
        final /* synthetic */ EditText f179a;
        final /* synthetic */ DeviceConfiguration f180b;
        final /* synthetic */ EditMatrixControllerActivity f181c;

        C00501(EditMatrixControllerActivity editMatrixControllerActivity, EditText editText, DeviceConfiguration deviceConfiguration) {
            this.f181c = editMatrixControllerActivity;
            this.f179a = editText;
            this.f180b = deviceConfiguration;
        }

        public void onClick(View view) {
            if (((CheckBox) view).isChecked()) {
                this.f179a.setEnabled(true);
                this.f179a.setText(BuildConfig.VERSION_NAME);
                this.f180b.setEnabled(true);
                this.f180b.setName(BuildConfig.VERSION_NAME);
                return;
            }
            this.f179a.setEnabled(false);
            this.f180b.setEnabled(false);
            this.f180b.setName("NO DEVICE ATTACHED");
            this.f179a.setText("NO DEVICE ATTACHED");
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.EditMatrixControllerActivity.a */
    private class C0051a implements TextWatcher {
        final /* synthetic */ EditMatrixControllerActivity f182a;
        private DeviceConfiguration f183b;

        private C0051a(EditMatrixControllerActivity editMatrixControllerActivity, DeviceConfiguration deviceConfiguration) {
            this.f182a = editMatrixControllerActivity;
            this.f183b = deviceConfiguration;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            this.f183b.setName(editable.toString());
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matrices);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.f184a = new Utility(this);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f188e = (EditText) findViewById(R.id.matrixcontroller_name);
        this.f189f = getLayoutInflater().inflate(R.layout.matrix_devices, (LinearLayout) findViewById(R.id.linearLayout_matrix1), true);
        ((TextView) this.f189f.findViewById(R.id.port_number_matrix)).setText("1");
        this.f190g = getLayoutInflater().inflate(R.layout.matrix_devices, (LinearLayout) findViewById(R.id.linearLayout_matrix2), true);
        ((TextView) this.f190g.findViewById(R.id.port_number_matrix)).setText("2");
        this.f191h = getLayoutInflater().inflate(R.layout.matrix_devices, (LinearLayout) findViewById(R.id.linearLayout_matrix3), true);
        ((TextView) this.f191h.findViewById(R.id.port_number_matrix)).setText("3");
        this.f192i = getLayoutInflater().inflate(R.layout.matrix_devices, (LinearLayout) findViewById(R.id.linearLayout_matrix4), true);
        ((TextView) this.f192i.findViewById(R.id.port_number_matrix)).setText("4");
        this.f193j = getLayoutInflater().inflate(R.layout.matrix_devices, (LinearLayout) findViewById(R.id.linearLayout_matrix5), true);
        ((TextView) this.f193j.findViewById(R.id.port_number_matrix)).setText("1");
        this.f194k = getLayoutInflater().inflate(R.layout.matrix_devices, (LinearLayout) findViewById(R.id.linearLayout_matrix6), true);
        ((TextView) this.f194k.findViewById(R.id.port_number_matrix)).setText("2");
        this.f195l = getLayoutInflater().inflate(R.layout.matrix_devices, (LinearLayout) findViewById(R.id.linearLayout_matrix7), true);
        ((TextView) this.f195l.findViewById(R.id.port_number_matrix)).setText("3");
        this.f196m = getLayoutInflater().inflate(R.layout.matrix_devices, (LinearLayout) findViewById(R.id.linearLayout_matrix8), true);
        ((TextView) this.f196m.findViewById(R.id.port_number_matrix)).setText("4");
    }

    protected void onStart() {
        int i = 0;
        super.onStart();
        this.f184a.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        Serializable serializableExtra = getIntent().getSerializableExtra(EDIT_MATRIX_ACTIVITY);
        if (serializableExtra != null) {
            this.f185b = (MatrixControllerConfiguration) serializableExtra;
            this.f186c = (ArrayList) this.f185b.getMotors();
            this.f187d = (ArrayList) this.f185b.getServos();
        }
        this.f188e.setText(this.f185b.getName());
        for (int i2 = 0; i2 < this.f186c.size(); i2++) {
            View b = m140b(i2 + 1);
            m141b(i2 + 1, b, this.f186c);
            m139a(b, (DeviceConfiguration) this.f186c.get(i2));
            m138a(i2 + 1, b, this.f186c);
        }
        while (i < this.f187d.size()) {
            View a = m136a(i + 1);
            m141b(i + 1, a, this.f187d);
            m139a(a, (DeviceConfiguration) this.f187d.get(i));
            m138a(i + 1, a, this.f187d);
            i++;
        }
    }

    private void m139a(View view, DeviceConfiguration deviceConfiguration) {
        ((EditText) view.findViewById(R.id.editTextResult_matrix)).addTextChangedListener(new C0051a(deviceConfiguration, null));
    }

    private void m138a(int i, View view, ArrayList<DeviceConfiguration> arrayList) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox_port_matrix);
        DeviceConfiguration deviceConfiguration = (DeviceConfiguration) arrayList.get(i - 1);
        if (deviceConfiguration.isEnabled()) {
            checkBox.setChecked(true);
            ((EditText) view.findViewById(R.id.editTextResult_matrix)).setText(deviceConfiguration.getName());
            return;
        }
        checkBox.setChecked(true);
        checkBox.performClick();
    }

    private void m141b(int i, View view, ArrayList<DeviceConfiguration> arrayList) {
        ((CheckBox) view.findViewById(R.id.checkbox_port_matrix)).setOnClickListener(new C00501(this, (EditText) view.findViewById(R.id.editTextResult_matrix), (DeviceConfiguration) arrayList.get(i - 1)));
    }

    private View m136a(int i) {
        switch (i) {
            case BuildConfig.VERSION_CODE /*1*/:
                return this.f189f;
            case 2:
                return this.f190g;
            case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                return this.f191h;
            case 4:
                return this.f192i;
            default:
                return null;
        }
    }

    private View m140b(int i) {
        switch (i) {
            case BuildConfig.VERSION_CODE /*1*/:
                return this.f193j;
            case 2:
                return this.f194k;
            case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                return this.f195l;
            case 4:
                return this.f196m;
            default:
                return null;
        }
    }

    public void saveMatrixController(View v) {
        m137a();
    }

    private void m137a() {
        Intent intent = new Intent();
        this.f185b.addServos(this.f187d);
        this.f185b.addMotors(this.f186c);
        this.f185b.setName(this.f188e.getText().toString());
        intent.putExtra(EDIT_MATRIX_ACTIVITY, this.f185b);
        setResult(-1, intent);
        finish();
    }

    public void cancelMatrixController(View v) {
        setResult(0, new Intent());
        finish();
    }
}
