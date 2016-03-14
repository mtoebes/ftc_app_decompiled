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
    private static int f56a;
    private Button f57b;
    private Button f58c;
    private Spinner f59d;
    private ProgressDialog f60e;
    private WifiDirectChannelSelection f61f;
    private int f62g;
    private int f63h;
    private Context f64i;

    /* renamed from: com.qualcomm.ftccommon.FtcWifiChannelSelectorActivity.1 */
    class C00181 implements Runnable {
        final /* synthetic */ FtcWifiChannelSelectorActivity f52a;

        /* renamed from: com.qualcomm.ftccommon.FtcWifiChannelSelectorActivity.1.1 */
        class C00171 implements Runnable {
            final /* synthetic */ C00181 f51a;

            C00171(C00181 c00181) {
                this.f51a = c00181;
            }

            public void run() {
                this.f51a.f52a.setResult(-1);
                this.f51a.f52a.f60e.dismiss();
                this.f51a.f52a.finish();
            }
        }

        C00181(FtcWifiChannelSelectorActivity ftcWifiChannelSelectorActivity) {
            this.f52a = ftcWifiChannelSelectorActivity;
        }

        public void run() {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            this.f52a.runOnUiThread(new C00171(this));
        }
    }

    /* renamed from: com.qualcomm.ftccommon.FtcWifiChannelSelectorActivity.2 */
    class C00192 implements Runnable {
        final /* synthetic */ String f53a;
        final /* synthetic */ int f54b;
        final /* synthetic */ FtcWifiChannelSelectorActivity f55c;

        C00192(FtcWifiChannelSelectorActivity ftcWifiChannelSelectorActivity, String str, int i) {
            this.f55c = ftcWifiChannelSelectorActivity;
            this.f53a = str;
            this.f54b = i;
        }

        public void run() {
            Toast.makeText(this.f55c.f64i, this.f53a, this.f54b).show();
        }
    }

    public FtcWifiChannelSelectorActivity() {
        this.f62g = -1;
        this.f63h = -1;
    }

    static {
        f56a = 0;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftc_wifi_channel_selector);
        this.f64i = this;
        this.f59d = (Spinner) findViewById(R.id.spinnerChannelSelect);
        SpinnerAdapter createFromResource = ArrayAdapter.createFromResource(this, R.array.wifi_direct_channels, 17367048);
        createFromResource.setDropDownViewResource(17367049);
        this.f59d.setAdapter(createFromResource);
        this.f59d.setOnItemSelectedListener(this);
        this.f57b = (Button) findViewById(R.id.buttonConfigure);
        this.f57b.setOnClickListener(this);
        this.f58c = (Button) findViewById(R.id.buttonWifiSettings);
        this.f58c.setOnClickListener(this);
        this.f61f = new WifiDirectChannelSelection(this, (WifiManager) getSystemService("wifi"));
    }

    protected void onStart() {
        super.onStart();
        this.f59d.setSelection(f56a);
    }

    public void onItemSelected(AdapterView<?> adapterView, View v, int item, long l) {
        switch (item) {
            case 0:
                this.f62g = -1;
                this.f63h = -1;
            case BuildConfig.VERSION_CODE /*1*/:
                this.f62g = 81;
                this.f63h = 1;
            case 2:
                this.f62g = 81;
                this.f63h = 6;
            case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                this.f62g = 81;
                this.f63h = 11;
            case 4:
                this.f62g = 124;
                this.f63h = 149;
            case 5:
                this.f62g = 124;
                this.f63h = 153;
            case 6:
                this.f62g = 124;
                this.f63h = 157;
            case 7:
                this.f62g = 124;
                this.f63h = 161;
            default:
        }
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void onClick(View v) {
        if (v.getId() == R.id.buttonConfigure) {
            f56a = this.f59d.getSelectedItemPosition();
            m33a();
        } else if (v.getId() == R.id.buttonWifiSettings) {
            DbgLog.msg("launch wifi settings");
            startActivity(new Intent("android.net.wifi.PICK_WIFI_NETWORK"));
        }
    }

    private void m33a() {
        DbgLog.msg(String.format("configure p2p channel - class %d channel %d", new Object[]{Integer.valueOf(this.f62g), Integer.valueOf(this.f63h)}));
        try {
            this.f60e = ProgressDialog.show(this, "Configuring Channel", "Please Wait", true);
            this.f61f.config(this.f62g, this.f63h);
            new Thread(new C00181(this)).start();
        } catch (IOException e) {
            m34a("Failed - root is required", 0);
            e.printStackTrace();
        }
    }

    private void m34a(String str, int i) {
        runOnUiThread(new C00192(this, str, i));
    }
}
