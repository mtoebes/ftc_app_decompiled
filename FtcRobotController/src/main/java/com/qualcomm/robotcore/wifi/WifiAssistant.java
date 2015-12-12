package com.qualcomm.robotcore.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;

import com.qualcomm.robotcore.util.RobotLog;

public class WifiAssistant {
    private final IntentFilter filter = new IntentFilter();
    private final Context context;
    private final WifiBroadcastReceiver wifiBroadcastReceiver;

    public interface WifiAssistantCallback {
        void wifiEventCallback(WifiState wifiState);
    }

    public enum WifiState {
        CONNECTED,
        NOT_CONNECTED
    }

    private static class WifiBroadcastReceiver extends BroadcastReceiver {
        private WifiState wifiState = null;
        private final WifiAssistantCallback wifiAssistantCallback;

        public WifiBroadcastReceiver(WifiAssistantCallback wifiAssistantCallback) {
            this.wifiAssistantCallback = wifiAssistantCallback;
        }

        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                return;
            }
            if (((NetworkInfo) intent.getParcelableExtra("networkInfo")).isConnected()) {
                setWifiState(WifiState.CONNECTED);
            } else {
                setWifiState(WifiState.NOT_CONNECTED);
            }
        }

        private void setWifiState(WifiState wifiState) {
            if (this.wifiState != wifiState) {
                this.wifiState = wifiState;
                if (this.wifiAssistantCallback != null) {
                    this.wifiAssistantCallback.wifiEventCallback(this.wifiState);
                }
            }
        }
    }

    public WifiAssistant(Context context, WifiAssistantCallback callback) {
        this.context = context;
        if (callback == null) {
            RobotLog.v("WifiAssistantCallback is null");
        }
        this.wifiBroadcastReceiver = new WifiBroadcastReceiver(callback);
        this.filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    }

    public void enable() {
        this.context.registerReceiver(this.wifiBroadcastReceiver, this.filter);
    }

    public void disable() {
        this.context.unregisterReceiver(this.wifiBroadcastReceiver);
    }
}
