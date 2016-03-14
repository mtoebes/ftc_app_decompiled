package com.qualcomm.ftccommon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import com.qualcomm.robotcore.wifi.FixWifiDirectSetup;

public class ConfigWifiDirectActivity extends Activity {
    private ProgressDialog f5a;
    private Context f6b;

    /* renamed from: com.qualcomm.ftccommon.ConfigWifiDirectActivity.a */
    private class C0004a implements Runnable {
        final /* synthetic */ ConfigWifiDirectActivity f4a;

        /* renamed from: com.qualcomm.ftccommon.ConfigWifiDirectActivity.a.1 */
        class C00021 implements Runnable {
            final /* synthetic */ C0004a f2a;

            C00021(C0004a c0004a) {
                this.f2a = c0004a;
            }

            public void run() {
                this.f2a.f4a.f5a = new ProgressDialog(this.f2a.f4a.f6b, R.style.CustomAlertDialog);
                this.f2a.f4a.f5a.setMessage("Please wait");
                this.f2a.f4a.f5a.setTitle("Configuring Wifi Direct");
                this.f2a.f4a.f5a.setIndeterminate(true);
                this.f2a.f4a.f5a.show();
            }
        }

        /* renamed from: com.qualcomm.ftccommon.ConfigWifiDirectActivity.a.2 */
        class C00032 implements Runnable {
            final /* synthetic */ C0004a f3a;

            C00032(C0004a c0004a) {
                this.f3a = c0004a;
            }

            public void run() {
                this.f3a.f4a.f5a.dismiss();
                this.f3a.f4a.finish();
            }
        }

        private C0004a(ConfigWifiDirectActivity configWifiDirectActivity) {
            this.f4a = configWifiDirectActivity;
        }

        public void run() {
            DbgLog.msg("attempting to reconfigure Wifi Direct");
            this.f4a.runOnUiThread(new C00021(this));
            try {
                FixWifiDirectSetup.fixWifiDirectSetup((WifiManager) this.f4a.getSystemService("wifi"));
            } catch (InterruptedException e) {
                DbgLog.msg("Cannot fix wifi setup - interrupted");
            }
            this.f4a.runOnUiThread(new C00032(this));
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_wifi_direct);
        this.f6b = this;
    }

    protected void onResume() {
        super.onResume();
        new Thread(new C0004a()).start();
    }
}
