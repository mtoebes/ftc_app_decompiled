package com.qualcomm.ftccommon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;
import com.qualcomm.robotcore.wifi.FixWifiDirectSetup;

public class ConfigWifiDirectActivity extends Activity {
    private static Flag f10a;
    private WifiManager f11b;
    private ProgressDialog f12c;
    private Context f13d;
    private TextView f14e;
    private TextView f15f;

    /* renamed from: com.qualcomm.ftccommon.ConfigWifiDirectActivity.1 */
    class C00011 implements Runnable {
        final /* synthetic */ ConfigWifiDirectActivity f2a;

        C00011(ConfigWifiDirectActivity configWifiDirectActivity) {
            this.f2a = configWifiDirectActivity;
        }

        public void run() {
            this.f2a.f12c = new ProgressDialog(this.f2a.f13d, R.style.CustomAlertDialog);
            this.f2a.f12c.setMessage("Please wait");
            this.f2a.f12c.setTitle("Configuring Wifi Direct");
            this.f2a.f12c.setIndeterminate(true);
            this.f2a.f12c.show();
        }
    }

    /* renamed from: com.qualcomm.ftccommon.ConfigWifiDirectActivity.2 */
    class C00022 implements Runnable {
        final /* synthetic */ ConfigWifiDirectActivity f3a;

        C00022(ConfigWifiDirectActivity configWifiDirectActivity) {
            this.f3a = configWifiDirectActivity;
        }

        public void run() {
            this.f3a.f12c.dismiss();
        }
    }

    /* renamed from: com.qualcomm.ftccommon.ConfigWifiDirectActivity.3 */
    static /* synthetic */ class C00033 {
        static final /* synthetic */ int[] f4a;

        static {
            f4a = new int[Flag.values().length];
            try {
                f4a[Flag.WIFI_DIRECT_DEVICE_NAME_INVALID.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f4a[Flag.WIFI_DIRECT_FIX_CONFIG.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public enum Flag {
        NONE,
        WIFI_DIRECT_FIX_CONFIG,
        WIFI_DIRECT_DEVICE_NAME_INVALID
    }

    /* renamed from: com.qualcomm.ftccommon.ConfigWifiDirectActivity.a */
    private class C0005a implements Runnable {
        final /* synthetic */ ConfigWifiDirectActivity f7a;

        /* renamed from: com.qualcomm.ftccommon.ConfigWifiDirectActivity.a.1 */
        class C00041 implements Runnable {
            final /* synthetic */ C0005a f6a;

            C00041(C0005a c0005a) {
                this.f6a = c0005a;
            }

            public void run() {
                this.f6a.f7a.f14e.setVisibility(4);
                this.f6a.f7a.f15f.setVisibility(0);
            }
        }

        private C0005a(ConfigWifiDirectActivity configWifiDirectActivity) {
            this.f7a = configWifiDirectActivity;
        }

        public void run() {
            DbgLog.msg("attempting to disable Wifi due to bad wifi direct device name");
            this.f7a.m6a();
            try {
                FixWifiDirectSetup.disableWifiDirect(this.f7a.f11b);
            } catch (InterruptedException e) {
                DbgLog.error("Cannot fix wifi setup - interrupted");
            }
            this.f7a.runOnUiThread(new C00041(this));
            this.f7a.m9b();
        }
    }

    /* renamed from: com.qualcomm.ftccommon.ConfigWifiDirectActivity.b */
    private class C0007b implements Runnable {
        final /* synthetic */ ConfigWifiDirectActivity f9a;

        /* renamed from: com.qualcomm.ftccommon.ConfigWifiDirectActivity.b.1 */
        class C00061 implements Runnable {
            final /* synthetic */ C0007b f8a;

            C00061(C0007b c0007b) {
                this.f8a = c0007b;
            }

            public void run() {
                this.f8a.f9a.finish();
            }
        }

        private C0007b(ConfigWifiDirectActivity configWifiDirectActivity) {
            this.f9a = configWifiDirectActivity;
        }

        public void run() {
            DbgLog.msg("attempting to reconfigure Wifi Direct");
            this.f9a.m6a();
            try {
                FixWifiDirectSetup.fixWifiDirectSetup(this.f9a.f11b);
            } catch (InterruptedException e) {
                DbgLog.error("Cannot fix wifi setup - interrupted");
            }
            this.f9a.m9b();
            this.f9a.runOnUiThread(new C00061(this));
        }
    }

    static {
        f10a = Flag.NONE;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_wifi_direct);
        this.f14e = (TextView) findViewById(R.id.textPleaseWait);
        this.f15f = (TextView) findViewById(R.id.textBadDeviceName);
        this.f13d = this;
    }

    protected void onResume() {
        super.onResume();
        this.f14e.setVisibility(0);
        this.f11b = (WifiManager) getSystemService("wifi");
        DbgLog.msg("Processing flag " + f10a.toString());
        switch (C00033.f4a[f10a.ordinal()]) {
            case BuildConfig.VERSION_CODE /*1*/:
                new Thread(new C0005a()).start();
            case 2:
                new Thread(new C0007b()).start();
            default:
        }
    }

    protected void onPause() {
        super.onPause();
        f10a = Flag.NONE;
        this.f15f.setVisibility(4);
    }

    private void m6a() {
        runOnUiThread(new C00011(this));
    }

    private void m9b() {
        runOnUiThread(new C00022(this));
    }

    public static void launch(Context context) {
        launch(context, Flag.WIFI_DIRECT_FIX_CONFIG);
    }

    public static void launch(Context context, Flag flag) {
        Intent intent = new Intent(context, ConfigWifiDirectActivity.class);
        intent.addFlags(1342177280);
        context.startActivity(intent);
        f10a = flag;
    }
}
