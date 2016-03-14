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
import java.io.Serializable;
import java.util.ArrayList;

public class EditDigitalDevicesActivity extends Activity {
    private Utility f138a;
    private View f139b;
    private View f140c;
    private View f141d;
    private View f142e;
    private View f143f;
    private View f144g;
    private View f145h;
    private View f146i;
    private ArrayList<DeviceConfiguration> f147j;
    private OnItemSelectedListener f148k;

    /* renamed from: com.qualcomm.ftccommon.configuration.EditDigitalDevicesActivity.1 */
    class C00441 implements OnItemSelectedListener {
        final /* synthetic */ EditDigitalDevicesActivity f135a;

        C00441(EditDigitalDevicesActivity editDigitalDevicesActivity) {
            this.f135a = editDigitalDevicesActivity;
        }

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
            String obj = parent.getItemAtPosition(pos).toString();
            LinearLayout linearLayout = (LinearLayout) view.getParent().getParent().getParent();
            if (obj.equalsIgnoreCase(ConfigurationType.NOTHING.toString())) {
                this.f135a.m105a(linearLayout);
            } else {
                this.f135a.m106a(linearLayout, obj);
            }
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.EditDigitalDevicesActivity.a */
    private class C0045a implements TextWatcher {
        final /* synthetic */ EditDigitalDevicesActivity f136a;
        private int f137b;

        private C0045a(EditDigitalDevicesActivity editDigitalDevicesActivity, View view) {
            this.f136a = editDigitalDevicesActivity;
            this.f137b = Integer.parseInt(((TextView) view.findViewById(R.id.port_number_digital_device)).getText().toString());
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            ((DeviceConfiguration) this.f136a.f147j.get(this.f137b)).setName(editable.toString());
        }
    }

    public EditDigitalDevicesActivity() {
        this.f147j = new ArrayList();
        this.f148k = new C00441(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.digital_devices);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        this.f138a = new Utility(this);
        RobotLog.writeLogcatToDisk(this, 1024);
        this.f139b = getLayoutInflater().inflate(R.layout.digital_device, (LinearLayout) findViewById(R.id.linearLayout_digital_device0), true);
        ((TextView) this.f139b.findViewById(R.id.port_number_digital_device)).setText("0");
        this.f140c = getLayoutInflater().inflate(R.layout.digital_device, (LinearLayout) findViewById(R.id.linearLayout_digital_device1), true);
        ((TextView) this.f140c.findViewById(R.id.port_number_digital_device)).setText("1");
        this.f141d = getLayoutInflater().inflate(R.layout.digital_device, (LinearLayout) findViewById(R.id.linearLayout_digital_device2), true);
        ((TextView) this.f141d.findViewById(R.id.port_number_digital_device)).setText("2");
        this.f142e = getLayoutInflater().inflate(R.layout.digital_device, (LinearLayout) findViewById(R.id.linearLayout_digital_device3), true);
        ((TextView) this.f142e.findViewById(R.id.port_number_digital_device)).setText("3");
        this.f143f = getLayoutInflater().inflate(R.layout.digital_device, (LinearLayout) findViewById(R.id.linearLayout_digital_device4), true);
        ((TextView) this.f143f.findViewById(R.id.port_number_digital_device)).setText("4");
        this.f144g = getLayoutInflater().inflate(R.layout.digital_device, (LinearLayout) findViewById(R.id.linearLayout_digital_device5), true);
        ((TextView) this.f144g.findViewById(R.id.port_number_digital_device)).setText("5");
        this.f145h = getLayoutInflater().inflate(R.layout.digital_device, (LinearLayout) findViewById(R.id.linearLayout_digital_device6), true);
        ((TextView) this.f145h.findViewById(R.id.port_number_digital_device)).setText("6");
        this.f146i = getLayoutInflater().inflate(R.layout.digital_device, (LinearLayout) findViewById(R.id.linearLayout_digital_device7), true);
        ((TextView) this.f146i.findViewById(R.id.port_number_digital_device)).setText("7");
    }

    protected void onStart() {
        super.onStart();
        this.f138a.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            for (String str : extras.keySet()) {
                this.f147j.add(Integer.parseInt(str), (DeviceConfiguration) extras.getSerializable(str));
            }
            for (int i = 0; i < this.f147j.size(); i++) {
                View a = m99a(i);
                DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f147j.get(i);
                m102a(a);
                m109b(a, deviceConfiguration);
                m103a(a, deviceConfiguration);
            }
        }
    }

    private void m103a(View view, DeviceConfiguration deviceConfiguration) {
        Spinner spinner = (Spinner) view.findViewById(R.id.choiceSpinner_digital_device);
        ArrayAdapter arrayAdapter = (ArrayAdapter) spinner.getAdapter();
        if (deviceConfiguration.isEnabled()) {
            spinner.setSelection(arrayAdapter.getPosition(deviceConfiguration.getType().toString()));
        } else {
            spinner.setSelection(0);
        }
        spinner.setOnItemSelectedListener(this.f148k);
    }

    private void m109b(View view, DeviceConfiguration deviceConfiguration) {
        EditText editText = (EditText) view.findViewById(R.id.editTextResult_digital_device);
        if (deviceConfiguration.isEnabled()) {
            editText.setText(deviceConfiguration.getName());
            editText.setEnabled(true);
            return;
        }
        editText.setText("NO DEVICE ATTACHED");
        editText.setEnabled(false);
    }

    private void m102a(View view) {
        ((EditText) view.findViewById(R.id.editTextResult_digital_device)).addTextChangedListener(new C0045a(view, null));
    }

    private View m99a(int i) {
        switch (i) {
            case 0:
                return this.f139b;
            case BuildConfig.VERSION_CODE /*1*/:
                return this.f140c;
            case 2:
                return this.f141d;
            case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                return this.f142e;
            case 4:
                return this.f143f;
            case 5:
                return this.f144g;
            case 6:
                return this.f145h;
            case 7:
                return this.f146i;
            default:
                return null;
        }
    }

    public void saveDigitalDevices(View v) {
        m101a();
    }

    private void m101a() {
        Bundle bundle = new Bundle();
        for (int i = 0; i < this.f147j.size(); i++) {
            bundle.putSerializable(String.valueOf(i), (Serializable) this.f147j.get(i));
        }
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.putExtras(bundle);
        setResult(-1, intent);
        finish();
    }

    public void cancelDigitalDevices(View v) {
        setResult(0, new Intent());
        finish();
    }

    private void m105a(LinearLayout linearLayout) {
        int parseInt = Integer.parseInt(((TextView) linearLayout.findViewById(R.id.port_number_digital_device)).getText().toString());
        EditText editText = (EditText) linearLayout.findViewById(R.id.editTextResult_digital_device);
        editText.setEnabled(false);
        editText.setText("NO DEVICE ATTACHED");
        ((DeviceConfiguration) this.f147j.get(parseInt)).setEnabled(false);
    }

    private void m106a(LinearLayout linearLayout, String str) {
        int parseInt = Integer.parseInt(((TextView) linearLayout.findViewById(R.id.port_number_digital_device)).getText().toString());
        EditText editText = (EditText) linearLayout.findViewById(R.id.editTextResult_digital_device);
        editText.setEnabled(true);
        DeviceConfiguration deviceConfiguration = (DeviceConfiguration) this.f147j.get(parseInt);
        deviceConfiguration.setType(deviceConfiguration.typeFromString(str));
        deviceConfiguration.setEnabled(true);
        m104a(editText, deviceConfiguration);
    }

    private void m104a(EditText editText, DeviceConfiguration deviceConfiguration) {
        if (editText.getText().toString().equalsIgnoreCase("NO DEVICE ATTACHED")) {
            editText.setText(BuildConfig.VERSION_NAME);
            deviceConfiguration.setName(BuildConfig.VERSION_NAME);
            return;
        }
        editText.setText(deviceConfiguration.getName());
    }
}
