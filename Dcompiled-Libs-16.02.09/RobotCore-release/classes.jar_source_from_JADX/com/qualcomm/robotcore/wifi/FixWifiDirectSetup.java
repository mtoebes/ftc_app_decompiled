package com.qualcomm.robotcore.wifi;

import android.net.wifi.WifiManager;

public class FixWifiDirectSetup {
    public static final int WIFI_TOGGLE_DELAY = 2000;

    public static void fixWifiDirectSetup(WifiManager wifiManager) throws InterruptedException {
        m260a(false, wifiManager);
        m260a(true, wifiManager);
    }

    public static void disableWifiDirect(WifiManager wifiManager) throws InterruptedException {
        m260a(false, wifiManager);
    }

    private static void m260a(boolean z, WifiManager wifiManager) throws InterruptedException {
        wifiManager.setWifiEnabled(z);
        Thread.sleep(2000);
    }
}
