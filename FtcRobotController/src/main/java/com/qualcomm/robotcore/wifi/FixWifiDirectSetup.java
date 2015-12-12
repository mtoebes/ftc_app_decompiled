package com.qualcomm.robotcore.wifi;

import android.net.wifi.WifiManager;

public class FixWifiDirectSetup {
    public static final int WIFI_TOGGLE_DELAY = 2000;

    public static void fixWifiDirectSetup(WifiManager wifiManager) throws InterruptedException {
        setWifiDirectEnabled(false, wifiManager);
        setWifiDirectEnabled(true, wifiManager);
    }

    private static void setWifiDirectEnabled(boolean enable, WifiManager wifiManager) throws InterruptedException {
        wifiManager.setWifiEnabled(enable);
        Thread.sleep(WIFI_TOGGLE_DELAY);
    }
}
