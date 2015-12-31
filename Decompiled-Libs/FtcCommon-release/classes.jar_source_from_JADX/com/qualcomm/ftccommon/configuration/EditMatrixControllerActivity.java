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
    private Utility f169a;
    private MatrixControllerConfiguration f170b;
    private ArrayList<DeviceConfiguration> f171c;
    private ArrayList<DeviceConfiguration> f172d;
    private EditText f173e;
    private View f174f;
    private View f175g;
    private View f176h;
    private View f177i;
    private View f178j;
    private View f179k;
    private View f180l;
    private View f181m;

    /* renamed from: com.qualcomm.ftccommon.configuration.EditMatrixControllerActivity.1 */
    class C00431 implements OnClickListener {
        final /* synthetic */ EditText f164a;
        final /* synthetic */ DeviceConfiguration f165b;
        final /* synthetic */ EditMatrixControllerActivity f166c;

        C00431(EditMatrixControllerActivity editMatrixControllerActivity, EditText editText, DeviceConfiguration deviceConfiguration) {
            this.f166c = editMatrixControllerActivity;
            this.f164a = editText;
            this.f165b = deviceConfiguration;
        }

        public void onClick(View view) {
            if (((CheckBox) view).isChecked()) {
                this.f164a.setEnabled(true);
                this.f164a.setText(BuildConfig.VERSION_NAME);
                this.f165b.setEnabled(true);
                this.f165b.setName(BuildConfig.VERSION_NAME);
                return;
            }
            this.f164a.setEnabled(false);
            this.f165b.setEnabled(false);
            this.f165b.setName("NO DEVICE ATTACHED");
            this.f164a.setText("NO DEVICE ATTACHED");
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.EditMatrixControllerActivity.a */
    private class C0044a implements TextWatcher {
        final /* synthetic */ EditMatrixControllerActivity f167a;
        private DeviceConfiguration f168b;

        private C0044a(EditMatrixControllerActivity editMatrixControllerActivity, DeviceConfiguration deviceConfiguration) {
            this.f167a = editMatrixControllerActivity;
            this.f168b = deviceConfiguration;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            this.f168b.setName(editable.toString());
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matrices);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.f169a = new Utility(this);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f173e = (EditText) findViewById(R.id.matrixcontroller_name);
        this.f174f = getLayoutInflater().inflate(R.layout.matrix_devices, (LinearLayout) findViewById(R.id.linearLayout_matrix1), true);
        ((TextView) this.f174f.findViewById(R.id.port_number_matrix)).setText("1");
        this.f175g = getLayoutInflater().inflate(R.layout.matrix_devices, (LinearLayout) findViewById(R.id.linearLayout_matrix2), true);
        ((TextView) this.f175g.findViewById(R.id.port_number_matrix)).setText("2");
        this.f176h = getLayoutInflater().inflate(R.layout.matrix_devices, (LinearLayout) findViewById(R.id.linearLayout_matrix3), true);
        ((TextView) this.f176h.findViewById(R.id.port_number_matrix)).setText("3");
        this.f177i = getLayoutInflater().inflate(R.layout.matrix_devices, (LinearLayout) findViewById(R.id.linearLayout_matrix4), true);
        ((TextView) this.f177i.findViewById(R.id.port_number_matrix)).setText("4");
        this.f178j = getLayoutInflater().inflate(R.layout.matrix_devices, (LinearLayout) findViewById(R.id.linearLayout_matrix5), true);
        ((TextView) this.f178j.findViewById(R.id.port_number_matrix)).setText("1");
        this.f179k = getLayoutInflater().inflate(R.layout.matrix_devices, (LinearLayout) findViewById(R.id.linearLayout_matrix6), true);
        ((TextView) this.f179k.findViewById(R.id.port_number_matrix)).setText("2");
        this.f180l = getLayoutInflater().inflate(R.layout.matrix_devices, (LinearLayout) findViewById(R.id.linearLayout_matrix7), true);
        ((TextView) this.f180l.findViewById(R.id.port_number_matrix)).setText("3");
        this.f181m = getLayoutInflater().inflate(R.layout.matrix_devices, (LinearLayout) findViewById(R.id.linearLayout_matrix8), true);
        ((TextView) this.f181m.findViewById(R.id.port_number_matrix)).setText("4");
    }

    protected void onStart() {
        int i = 0;
        super.onStart();
        this.f169a.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        Serializable serializableExtra = getIntent().getSerializableExtra(EDIT_MATRIX_ACTIVITY);
        if (serializableExtra != null) {
            this.f170b = (MatrixControllerConfiguration) serializableExtra;
            this.f171c = (ArrayList) this.f170b.getMotors();
            this.f172d = (ArrayList) this.f170b.getServos();
        }
        this.f173e.setText(this.f170b.getName());
        for (int i2 = 0; i2 < this.f171c.size(); i2++) {
            View b = m129b(i2 + 1);
            m130b(i2 + 1, b, this.f171c);
            m128a(b, (DeviceConfiguration) this.f171c.get(i2));
            m127a(i2 + 1, b, this.f171c);
        }
        while (i < this.f172d.size()) {
            View a = m125a(i + 1);
            m130b(i + 1, a, this.f172d);
            m128a(a, (DeviceConfiguration) this.f172d.get(i));
            m127a(i + 1, a, this.f172d);
            i++;
        }
    }

    private void m128a(View view, DeviceConfiguration deviceConfiguration) {
        ((EditText) view.findViewById(R.id.editTextResult_matrix)).addTextChangedListener(new C0044a(deviceConfiguration, null));
    }

    private void m127a(int i, View view, ArrayList<DeviceConfiguration> arrayList) {
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

    private void m130b(int i, View view, ArrayList<DeviceConfiguration> arrayList) {
        ((CheckBox) view.findViewById(R.id.checkbox_port_matrix)).setOnClickListener(new C00431(this, (EditText) view.findViewById(R.id.editTextResult_matrix), (DeviceConfiguration) arrayList.get(i - 1)));
    }

    private View m125a(int i) {
        switch (i) {
            case BuildConfig.VERSION_CODE /*1*/:
                return this.f174f;
            case 2:
                return this.f175g;
            case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                return this.f176h;
            case 4:
                return this.f177i;
            default:
                return null;
        }
    }

    private View m129b(int i) {
        switch (i) {
            case BuildConfig.VERSION_CODE /*1*/:
                return this.f178j;
            case 2:
                return this.f179k;
            case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                return this.f180l;
            case 4:
                return this.f181m;
            default:
                return null;
        }
    }

    public void saveMatrixController(View v) {
        m126a();
    }

    private void m126a() {
        Intent intent = new Intent();
        this.f170b.addServos(this.f172d);
        this.f170b.addMotors(this.f171c);
        this.f170b.setName(this.f173e.getText().toString());
        intent.putExtra(EDIT_MATRIX_ACTIVITY, this.f170b);
        setResult(-1, intent);
        finish();
    }

    public void cancelMatrixController(View v) {
        setResult(0, new Intent());
        finish();
    }
}
