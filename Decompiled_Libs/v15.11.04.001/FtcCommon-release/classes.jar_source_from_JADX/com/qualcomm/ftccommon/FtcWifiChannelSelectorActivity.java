package com.qualcomm.ftccommon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import com.qualcomm.wirelessp2p.WifiDirectChannelSelection;
import java.io.IOException;

public class FtcWifiChannelSelectorActivity extends Activity implements OnClickListener, OnItemSelectedListener {
    private static int f43a;
    private Button f44b;
    private Button f45c;
    private Spinner f46d;
    private ProgressDialog f47e;
    private WifiDirectChannelSelection f48f;
    private int f49g;
    private int f50h;
    private Context f51i;

    /* renamed from: com.qualcomm.ftccommon.FtcWifiChannelSelectorActivity.1 */
    class C00131 implements Runnable {
        final /* synthetic */ FtcWifiChannelSelectorActivity f39a;

        /* renamed from: com.qualcomm.ftccommon.FtcWifiChannelSelectorActivity.1.1 */
        class C00121 implements Runnable {
            final /* synthetic */ C00131 f38a;

            C00121(C00131 c00131) {
                this.f38a = c00131;
            }

            public void run() {
                this.f38a.f39a.setResult(-1);
                this.f38a.f39a.f47e.dismiss();
                this.f38a.f39a.finish();
            }
        }

        C00131(FtcWifiChannelSelectorActivity ftcWifiChannelSelectorActivity) {
            this.f39a = ftcWifiChannelSelectorActivity;
        }

        public void run() {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            this.f39a.runOnUiThread(new C00121(this));
        }
    }

    /* renamed from: com.qualcomm.ftccommon.FtcWifiChannelSelectorActivity.2 */
    class C00142 implements Runnable {
        final /* synthetic */ String f40a;
        final /* synthetic */ int f41b;
        final /* synthetic */ FtcWifiChannelSelectorActivity f42c;

        C00142(FtcWifiChannelSelectorActivity ftcWifiChannelSelectorActivity, String str, int i) {
            this.f42c = ftcWifiChannelSelectorActivity;
            this.f40a = str;
            this.f41b = i;
        }

        public void run() {
            Toast.makeText(this.f42c.f51i, this.f40a, this.f41b).show();
        }
    }

    public FtcWifiChannelSelectorActivity() {
        this.f49g = -1;
        this.f50h = -1;
    }

    static {
        f43a = 0;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftc_wifi_channel_selector);
        this.f51i = this;
        this.f46d = (Spinner) findViewById(R.id.spinnerChannelSelect);
        SpinnerAdapter createFromResource = ArrayAdapter.createFromResource(this, R.array.wifi_direct_channels, 17367048);
        createFromResource.setDropDownViewResource(17367049);
        this.f46d.setAdapter(createFromResource);
        this.f46d.setOnItemSelectedListener(this);
        this.f44b = (Button) findViewById(R.id.buttonConfigure);
        this.f44b.setOnClickListener(this);
        this.f45c = (Button) findViewById(R.id.buttonWifiSettings);
        this.f45c.setOnClickListener(this);
        this.f48f = new WifiDirectChannelSelection(this, (WifiManager) getSystemService("wifi"));
    }

    protected void onStart() {
        super.onStart();
        this.f46d.setSelection(f43a);
    }

    public void onItemSelected(AdapterView<?> adapterView, View v, int item, long l) {
        switch (item) {
            case 0:
                this.f49g = -1;
                this.f50h = -1;
            case BuildConfig.VERSION_CODE /*1*/:
                this.f49g = 81;
                this.f50h = 1;
            case 2:
                this.f49g = 81;
                this.f50h = 6;
            case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                this.f49g = 81;
                this.f50h = 11;
            case 4:
                this.f49g = 124;
                this.f50h = 149;
            case 5:
                this.f49g = 124;
                this.f50h = 153;
            case 6:
                this.f49g = 124;
                this.f50h = 157;
            case 7:
                this.f49g = 124;
                this.f50h = 161;
            default:
        }
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void onClick(View v) {
        if (v.getId() == R.id.buttonConfigure) {
            f43a = this.f46d.getSelectedItemPosition();
            m24a();
        } else if (v.getId() == R.id.buttonWifiSettings) {
            DbgLog.msg("launch wifi settings");
            startActivity(new Intent("android.net.wifi.PICK_WIFI_NETWORK"));
        }
    }

    private void m24a() {
        DbgLog.msg(String.format("configure p2p channel - class %d channel %d", new Object[]{Integer.valueOf(this.f49g), Integer.valueOf(this.f50h)}));
        try {
            this.f47e = ProgressDialog.show(this, "Configuring Channel", "Please Wait", true);
            this.f48f.config(this.f49g, this.f50h);
            new Thread(new C00131(this)).start();
        } catch (IOException e) {
            m25a("Failed - root is required", 0);
            e.printStackTrace();
        }
    }

    private void m25a(String str, int i) {
        runOnUiThread(new C00142(this, str, i));
    }
}
