package com.qualcomm.robotcore.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import com.qualcomm.robotcore.util.RobotLog;

public class WifiAssistant {
    private final IntentFilter f429a;
    private final Context f430b;
    private final C0055a f431c;

    public interface WifiAssistantCallback {
        void wifiEventCallback(WifiState wifiState);
    }

    public enum WifiState {
        CONNECTED,
        NOT_CONNECTED
    }

    /* renamed from: com.qualcomm.robotcore.wifi.WifiAssistant.a */
    private static class C0055a extends BroadcastReceiver {
        private WifiState f427a;
        private final WifiAssistantCallback f428b;

        public C0055a(WifiAssistantCallback wifiAssistantCallback) {
            this.f427a = null;
            this.f428b = wifiAssistantCallback;
        }

        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals("android.net.wifi.STATE_CHANGE")) {
                return;
            }
            if (((NetworkInfo) intent.getParcelableExtra("networkInfo")).isConnected()) {
                m238a(WifiState.CONNECTED);
            } else {
                m238a(WifiState.NOT_CONNECTED);
            }
        }

        private void m238a(WifiState wifiState) {
            if (this.f427a != wifiState) {
                this.f427a = wifiState;
                if (this.f428b != null) {
                    this.f428b.wifiEventCallback(this.f427a);
                }
            }
        }
    }

    public WifiAssistant(Context context, WifiAssistantCallback callback) {
        this.f430b = context;
        if (callback == null) {
            RobotLog.v("WifiAssistantCallback is null");
        }
        this.f431c = new C0055a(callback);
        this.f429a = new IntentFilter();
        this.f429a.addAction("android.net.wifi.STATE_CHANGE");
    }

    public void enable() {
        this.f430b.registerReceiver(this.f431c, this.f429a);
    }

    public void disable() {
        this.f430b.unregisterReceiver(this.f431c);
    }
}
