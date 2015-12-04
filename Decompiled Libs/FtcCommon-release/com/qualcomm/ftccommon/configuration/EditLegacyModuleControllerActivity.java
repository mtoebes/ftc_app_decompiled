package com.qualcomm.ftccommon.configuration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.qualcomm.ftccommon.BuildConfig;
import com.qualcomm.ftccommon.LaunchActivityConstantsList;
import com.qualcomm.ftccommon.R;
import com.qualcomm.robotcore.hardware.configuration.ControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import com.qualcomm.robotcore.hardware.configuration.LegacyModuleControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.MatrixControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.MotorConfiguration;
import com.qualcomm.robotcore.hardware.configuration.MotorControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.ServoConfiguration;
import com.qualcomm.robotcore.hardware.configuration.ServoControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.Utility;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EditLegacyModuleControllerActivity extends Activity {
    public static final String EDIT_LEGACY_CONFIG = "EDIT_LEGACY_CONFIG";
    public static final int EDIT_MATRIX_CONTROLLER_REQUEST_CODE = 103;
    public static final int EDIT_MOTOR_CONTROLLER_REQUEST_CODE = 101;
    public static final int EDIT_SERVO_CONTROLLER_REQUEST_CODE = 102;
    private static boolean f150a;
    private Utility f151b;
    private String f152c;
    private Context f153d;
    private LegacyModuleControllerConfiguration f154e;
    private EditText f155f;
    private ArrayList<DeviceConfiguration> f156g;
    private View f157h;
    private View f158i;
    private View f159j;
    private View f160k;
    private View f161l;
    private View f162m;
    private OnItemSelectedListener f163n;

    /* renamed from: com.qualcomm.ftccommon.configuration.EditLegacyModuleControllerActivity.1 */
    class C00411 implements OnItemSelectedListener {
        final /* synthetic */ EditLegacyModuleControllerActivity f146a;

        C00411(EditLegacyModuleControllerActivity editLegacyModuleControllerActivity) {
            this.f146a = editLegacyModuleControllerActivity;
        }

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
            String obj = parent.getItemAtPosition(pos).toString();
            LinearLayout linearLayout = (LinearLayout) view.getParent().getParent().getParent();
            if (obj.equalsIgnoreCase(ConfigurationType.NOTHING.toString())) {
                this.f146a.m117a(linearLayout);
            } else {
                this.f146a.m118a(linearLayout, obj);
            }
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.EditLegacyModuleControllerActivity.a */
    private class C0042a implements TextWatcher {
        final /* synthetic */ EditLegacyModuleControllerActivity f147a;
        private int f148b;
        private boolean f149c;

        private C0042a(EditLegacyModuleControllerActivity editLegacyModuleControllerActivity) {
            this.f147a = editLegacyModuleControllerActivity;
            this.f149c = false;
            this.f149c = true;
        }

        private C0042a(EditLegacyModuleControllerActivity editLegacyModuleControllerActivity, View view) {
            this.f147a = editLegacyModuleControllerActivity;
            this.f149c = false;
            this.f148b = Integer.parseInt(((TextView) view.findViewById(R.id.portNumber)).getText().toString());
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            String obj = editable.toString();
            if (this.f149c) {
                this.f147a.f154e.setName(obj);
            } else {
                ((DeviceConfiguration) this.f147a.f156g.get(this.f148b)).setName(obj);
            }
        }
    }

    public EditLegacyModuleControllerActivity() {
        this.f156g = new ArrayList();
        this.f163n = new C00411(this);
    }

    static {
        f150a = false;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legacy);
        this.f157h = getLayoutInflater().inflate(R.layout.simple_device, (LinearLayout) findViewById(R.id.linearLayout0), true);
        ((TextView) this.f157h.findViewById(R.id.portNumber)).setText("0");
        this.f158i = getLayoutInflater().inflate(R.layout.simple_device, (LinearLayout) findViewById(R.id.linearLayout1), true);
        ((TextView) this.f158i.findViewById(R.id.portNumber)).setText("1");
        this.f159j = getLayoutInflater().inflate(R.layout.simple_device, (LinearLayout) findViewById(R.id.linearLayout2), true);
        ((TextView) this.f159j.findViewById(R.id.portNumber)).setText("2");
        this.f160k = getLayoutInflater().inflate(R.layout.simple_device, (LinearLayout) findViewById(R.id.linearLayout3), true);
        ((TextView) this.f160k.findViewById(R.id.portNumber)).setText("3");
        this.f161l = getLayoutInflater().inflate(R.layout.simple_device, (LinearLayout) findViewById(R.id.linearLayout4), true);
        ((TextView) this.f161l.findViewById(R.id.portNumber)).setText("4");
        this.f162m = getLayoutInflater().inflate(R.layout.simple_device, (LinearLayout) findViewById(R.id.linearLayout5), true);
        ((TextView) this.f162m.findViewById(R.id.portNumber)).setText("5");
        this.f153d = this;
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.f151b = new Utility(this);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f155f = (EditText) findViewById(R.id.device_interface_module_name);
    }

    protected void onStart() {
        super.onStart();
        this.f151b.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        this.f152c = this.f151b.getFilenameFromPrefs(R.string.pref_hardware_config_filename, "No current file!");
        Serializable serializableExtra = getIntent().getSerializableExtra(EDIT_LEGACY_CONFIG);
        if (serializableExtra != null) {
            this.f154e = (LegacyModuleControllerConfiguration) serializableExtra;
            this.f156g = (ArrayList) this.f154e.getDevices();
            this.f155f.setText(this.f154e.getName());
            this.f155f.addTextChangedListener(new C0042a());
            ((TextView) findViewById(R.id.legacy_serialNumber)).setText(this.f154e.getSerialNumber().toString());
            for (int i = 0; i < this.f156g.size(); i++) {
                DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f156g.get(i);
                if (f150a) {
                    RobotLog.e("[onStart] module name: " + deviceConfiguration.getName() + ", port: " + deviceConfiguration.getPort() + ", type: " + deviceConfiguration.getType());
                }
                m115a(m110a(i), deviceConfiguration);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Serializable serializable = null;
        if (resultCode == -1) {
            if (requestCode == EDIT_MOTOR_CONTROLLER_REQUEST_CODE) {
                serializable = data.getSerializableExtra(EditMotorControllerActivity.EDIT_MOTOR_CONTROLLER_CONFIG);
            } else if (requestCode == EDIT_SERVO_CONTROLLER_REQUEST_CODE) {
                serializable = data.getSerializableExtra(EditServoControllerActivity.EDIT_SERVO_ACTIVITY);
            } else if (requestCode == EDIT_MATRIX_CONTROLLER_REQUEST_CODE) {
                serializable = data.getSerializableExtra(EditMatrixControllerActivity.EDIT_MATRIX_ACTIVITY);
            }
            if (serializable != null) {
                DeviceConfiguration deviceConfiguration = (ControllerConfiguration) serializable;
                m123b(deviceConfiguration);
                m115a(m110a(deviceConfiguration.getPort()), (DeviceConfiguration) this.f156g.get(deviceConfiguration.getPort()));
                if (!this.f152c.toLowerCase().contains("Unsaved".toLowerCase())) {
                    String str = "Unsaved " + this.f152c;
                    this.f151b.saveToPreferences(str, R.string.pref_hardware_config_filename);
                    this.f152c = str;
                }
                this.f151b.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
            }
        }
    }

    public void saveLegacyController(View v) {
        m112a();
    }

    private void m112a() {
        Intent intent = new Intent();
        this.f154e.setName(this.f155f.getText().toString());
        intent.putExtra(EDIT_LEGACY_CONFIG, this.f154e);
        setResult(-1, intent);
        finish();
    }

    public void cancelLegacyController(View v) {
        setResult(0, new Intent());
        finish();
    }

    private void m115a(View view, DeviceConfiguration deviceConfiguration) {
        Spinner spinner = (Spinner) view.findViewById(R.id.choiceSpinner_legacyModule);
        spinner.setSelection(((ArrayAdapter) spinner.getAdapter()).getPosition(deviceConfiguration.getType().toString()));
        spinner.setOnItemSelectedListener(this.f163n);
        Object name = deviceConfiguration.getName();
        EditText editText = (EditText) view.findViewById(R.id.editTextResult_name);
        int parseInt = Integer.parseInt(((TextView) view.findViewById(R.id.portNumber)).getText().toString());
        editText.addTextChangedListener(new C0042a(m110a(parseInt), null));
        editText.setText(name);
        if (f150a) {
            RobotLog.e("[populatePort] name: " + name + ", port: " + parseInt + ", type: " + deviceConfiguration.getType());
        }
    }

    private void m117a(LinearLayout linearLayout) {
        int parseInt = Integer.parseInt(((TextView) linearLayout.findViewById(R.id.portNumber)).getText().toString());
        EditText editText = (EditText) linearLayout.findViewById(R.id.editTextResult_name);
        editText.setEnabled(false);
        editText.setText("NO DEVICE ATTACHED");
        DeviceConfiguration deviceConfiguration = new DeviceConfiguration(ConfigurationType.NOTHING);
        deviceConfiguration.setPort(parseInt);
        m123b(deviceConfiguration);
        m113a(parseInt, 8);
    }

    private void m118a(LinearLayout linearLayout, String str) {
        int parseInt = Integer.parseInt(((TextView) linearLayout.findViewById(R.id.portNumber)).getText().toString());
        EditText editText = (EditText) linearLayout.findViewById(R.id.editTextResult_name);
        DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f156g.get(parseInt);
        editText.setEnabled(true);
        m116a(editText, deviceConfiguration);
        ConfigurationType typeFromString = deviceConfiguration.typeFromString(str);
        if (typeFromString == ConfigurationType.MOTOR_CONTROLLER || typeFromString == ConfigurationType.SERVO_CONTROLLER || typeFromString == ConfigurationType.MATRIX_CONTROLLER) {
            m114a(parseInt, str);
            m113a(parseInt, 0);
        } else {
            deviceConfiguration.setType(typeFromString);
            if (typeFromString == ConfigurationType.NOTHING) {
                deviceConfiguration.setEnabled(false);
            } else {
                deviceConfiguration.setEnabled(true);
            }
            m113a(parseInt, 8);
        }
        if (f150a) {
            DeviceConfiguration deviceConfiguration2 = (DeviceConfiguration) this.f156g.get(parseInt);
            RobotLog.e("[changeDevice] modules.get(port) name: " + deviceConfiguration2.getName() + ", port: " + deviceConfiguration2.getPort() + ", type: " + deviceConfiguration2.getType());
        }
    }

    private void m114a(int i, String str) {
        DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f156g.get(i);
        String name = deviceConfiguration.getName();
        List arrayList = new ArrayList();
        SerialNumber serialNumber = ControllerConfiguration.NO_SERIAL_NUMBER;
        if (!deviceConfiguration.getType().toString().equalsIgnoreCase(str)) {
            deviceConfiguration = new ControllerConfiguration("dummy module", arrayList, serialNumber, ConfigurationType.NOTHING);
            int i2;
            if (str.equalsIgnoreCase(ConfigurationType.MOTOR_CONTROLLER.toString())) {
                for (i2 = 1; i2 <= 2; i2++) {
                    arrayList.add(new MotorConfiguration(i2));
                }
                deviceConfiguration = new MotorControllerConfiguration(name, arrayList, serialNumber);
                deviceConfiguration.setPort(i);
            } else if (str.equalsIgnoreCase(ConfigurationType.SERVO_CONTROLLER.toString())) {
                for (i2 = 1; i2 <= 6; i2++) {
                    arrayList.add(new ServoConfiguration(i2));
                }
                deviceConfiguration = new ServoControllerConfiguration(name, arrayList, serialNumber);
                deviceConfiguration.setPort(i);
            } else if (str.equalsIgnoreCase(ConfigurationType.MATRIX_CONTROLLER.toString())) {
                arrayList = new ArrayList();
                for (i2 = 1; i2 <= 4; i2++) {
                    arrayList.add(new MotorConfiguration(i2));
                }
                List arrayList2 = new ArrayList();
                for (i2 = 1; i2 <= 4; i2++) {
                    arrayList2.add(new ServoConfiguration(i2));
                }
                deviceConfiguration = new MatrixControllerConfiguration(name, arrayList, arrayList2, serialNumber);
                deviceConfiguration.setPort(i);
            }
            deviceConfiguration.setEnabled(true);
            m123b(deviceConfiguration);
        }
    }

    public void editController_portALL(View v) {
        LinearLayout linearLayout = (LinearLayout) v.getParent().getParent();
        int parseInt = Integer.parseInt(((TextView) linearLayout.findViewById(R.id.portNumber)).getText().toString());
        DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f156g.get(parseInt);
        if (!m124c(deviceConfiguration)) {
            m114a(parseInt, ((Spinner) linearLayout.findViewById(R.id.choiceSpinner_legacyModule)).getSelectedItem().toString());
        }
        m121a(deviceConfiguration);
    }

    private void m121a(DeviceConfiguration deviceConfiguration) {
        deviceConfiguration.setName(((EditText) ((LinearLayout) m110a(deviceConfiguration.getPort())).findViewById(R.id.editTextResult_name)).getText().toString());
        Intent intent;
        if (deviceConfiguration.getType() == ConfigurationType.MOTOR_CONTROLLER) {
            intent = new Intent(this.f153d, EditMotorControllerActivity.class);
            intent.putExtra(EditMotorControllerActivity.EDIT_MOTOR_CONTROLLER_CONFIG, deviceConfiguration);
            intent.putExtra("requestCode", EDIT_MOTOR_CONTROLLER_REQUEST_CODE);
            setResult(-1, intent);
            startActivityForResult(intent, EDIT_MOTOR_CONTROLLER_REQUEST_CODE);
        } else if (deviceConfiguration.getType() == ConfigurationType.SERVO_CONTROLLER) {
            intent = new Intent(this.f153d, EditServoControllerActivity.class);
            intent.putExtra(EditServoControllerActivity.EDIT_SERVO_ACTIVITY, deviceConfiguration);
            setResult(-1, intent);
            startActivityForResult(intent, EDIT_SERVO_CONTROLLER_REQUEST_CODE);
        } else if (deviceConfiguration.getType() == ConfigurationType.MATRIX_CONTROLLER) {
            intent = new Intent(this.f153d, EditMatrixControllerActivity.class);
            intent.putExtra(EditMatrixControllerActivity.EDIT_MATRIX_ACTIVITY, deviceConfiguration);
            setResult(-1, intent);
            startActivityForResult(intent, EDIT_MATRIX_CONTROLLER_REQUEST_CODE);
        }
    }

    private void m123b(DeviceConfiguration deviceConfiguration) {
        this.f156g.set(deviceConfiguration.getPort(), deviceConfiguration);
    }

    private View m110a(int i) {
        switch (i) {
            case 0:
                return this.f157h;
            case BuildConfig.VERSION_CODE /*1*/:
                return this.f158i;
            case 2:
                return this.f159j;
            case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                return this.f160k;
            case 4:
                return this.f161l;
            case 5:
                return this.f162m;
            default:
                return null;
        }
    }

    private void m113a(int i, int i2) {
        ((Button) m110a(i).findViewById(R.id.edit_controller_btn)).setVisibility(i2);
    }

    private boolean m124c(DeviceConfiguration deviceConfiguration) {
        return deviceConfiguration.getType() == ConfigurationType.MOTOR_CONTROLLER || deviceConfiguration.getType() == ConfigurationType.SERVO_CONTROLLER;
    }

    private void m116a(EditText editText, DeviceConfiguration deviceConfiguration) {
        if (editText.getText().toString().equalsIgnoreCase("NO DEVICE ATTACHED")) {
            editText.setText(BuildConfig.VERSION_NAME);
            deviceConfiguration.setName(BuildConfig.VERSION_NAME);
            return;
        }
        editText.setText(deviceConfiguration.getName());
    }
}
