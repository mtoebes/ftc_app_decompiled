package com.qualcomm.robotcore.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import com.qualcomm.robotcore.util.RobotLog;

public class WifiAssistant {
    private final IntentFilter f434a;
    private final Context f435b;
    private final C0060a f436c;

    public interface WifiAssistantCallback {
        void wifiEventCallback(WifiState wifiState);
    }

    public enum WifiState {
        CONNECTED,
        NOT_CONNECTED
    }

    /* renamed from: com.qualcomm.robotcore.wifi.WifiAssistant.a */
    private static class C0060a extends BroadcastReceiver {
        private WifiState f432a;
        private final WifiAssistantCallback f433b;

        public C0060a(WifiAssistantCallback wifiAssistantCallback) {
            this.f432a = null;
            this.f433b = wifiAssistantCallback;
        }

        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals("android.net.wifi.STATE_CHANGE")) {
                return;
            }
            if (((NetworkInfo) intent.getParcelableExtra("networkInfo")).isConnected()) {
                m261a(WifiState.CONNECTED);
            } else {
                m261a(WifiState.NOT_CONNECTED);
            }
        }

        private void m261a(WifiState wifiState) {
            if (this.f432a != wifiState) {
                this.f432a = wifiState;
                if (this.f433b != null) {
                    this.f433b.wifiEventCallback(this.f432a);
                }
            }
        }
    }

    public WifiAssistant(Context context, WifiAssistantCallback callback) {
        this.f435b = context;
        if (callback == null) {
            RobotLog.m254v("WifiAssistantCallback is null");
        }
        this.f436c = new C0060a(callback);
        this.f434a = new IntentFilter();
        this.f434a.addAction("android.net.wifi.STATE_CHANGE");
    }

    public void enable() {
        this.f435b.registerReceiver(this.f436c, this.f434a);
    }

    public void disable() {
        this.f435b.unregisterReceiver(this.f436c);
    }
}
