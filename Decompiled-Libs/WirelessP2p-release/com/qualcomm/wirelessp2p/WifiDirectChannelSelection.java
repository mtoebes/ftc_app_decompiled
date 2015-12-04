package com.qualcomm.wirelessp2p;

import android.content.Context;
import android.net.wifi.WifiManager;
import com.qualcomm.WirelessP2p.BuildConfig;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.RunShellCommand;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class WifiDirectChannelSelection {
    public static final int INVALID = -1;
    private final String f0a;
    private final String f1b;
    private final String f2c;
    private final WifiManager f3d;
    private final RunShellCommand f4e;

    public WifiDirectChannelSelection(Context context, WifiManager wifiManager) {
        this.f4e = new RunShellCommand();
        this.f0a = context.getFilesDir().getAbsolutePath() + "/";
        this.f3d = wifiManager;
        this.f1b = this.f0a + "get_current_wifi_direct_staus";
        this.f2c = this.f0a + "config_wifi_direct";
    }

    public void config(int wifiClass, int wifiChannel) throws IOException {
        try {
            this.f3d.setWifiEnabled(false);
            m3c();
            this.f4e.runAsRoot(this.f1b);
            m1a(wifiClass, wifiChannel);
            m2b();
            this.f4e.runAsRoot(this.f2c);
            this.f3d.setWifiEnabled(true);
        } finally {
            m4d();
        }
    }

    private int m0a() throws RuntimeException {
        for (String str : this.f4e.run("/system/bin/ps").split("\n")) {
            if (str.contains("wpa_supplicant")) {
                return Integer.parseInt(str.split("\\s+")[1]);
            }
        }
        throw new RuntimeException("could not find wpa_supplicant PID");
    }

    private void m2b() {
        try {
            char[] cArr = new char[4096];
            FileReader fileReader = new FileReader(this.f0a + "wpa_supplicant.conf");
            int read = fileReader.read(cArr);
            fileReader.close();
            String str = new String(cArr, 0, read);
            RobotLog.v("WPA FILE: \n" + str);
            String replaceAll = str.replaceAll("(?s)network\\s*=\\{.*\\}", BuildConfig.VERSION_NAME).replaceAll("(?m)^\\s+$", BuildConfig.VERSION_NAME);
            RobotLog.v("WPA REPLACE: \n" + replaceAll);
            FileWriter fileWriter = new FileWriter(this.f0a + "wpa_supplicant.conf");
            fileWriter.write(replaceAll);
            fileWriter.close();
        } catch (FileNotFoundException e) {
            RobotLog.e("File not found: " + e.toString());
            e.printStackTrace();
        } catch (IOException e2) {
            RobotLog.e("FIO exception: " + e2.toString());
            e2.printStackTrace();
        }
    }

    private void m1a(int i, int i2) {
        try {
            char[] cArr = new char[8192];
            FileReader fileReader = new FileReader(this.f0a + "p2p_supplicant.conf");
            int read = fileReader.read(cArr);
            fileReader.close();
            String str = new String(cArr, 0, read);
            RobotLog.v("P2P ORIG FILE: \n" + str);
            String replaceAll = str.replaceAll("p2p_listen_reg_class\\w*=.*", BuildConfig.VERSION_NAME).replaceAll("p2p_listen_channel\\w*=.*", BuildConfig.VERSION_NAME).replaceAll("p2p_oper_reg_class\\w*=.*", BuildConfig.VERSION_NAME).replaceAll("p2p_oper_channel\\w*=.*", BuildConfig.VERSION_NAME).replaceAll("p2p_pref_chan\\w*=.*", BuildConfig.VERSION_NAME).replaceAll("(?s)network\\s*=\\{.*\\}", BuildConfig.VERSION_NAME).replaceAll("(?m)^\\s+$", BuildConfig.VERSION_NAME);
            if (!(i == INVALID || i2 == INVALID)) {
                replaceAll = ((replaceAll + "p2p_oper_reg_class=" + i + "\n") + "p2p_oper_channel=" + i2 + "\n") + "p2p_pref_chan=" + i + ":" + i2 + "\n";
            }
            RobotLog.v("P2P NEW FILE: \n" + replaceAll);
            FileWriter fileWriter = new FileWriter(this.f0a + "p2p_supplicant.conf");
            fileWriter.write(replaceAll);
            fileWriter.close();
        } catch (FileNotFoundException e) {
            RobotLog.e("File not found: " + e.toString());
            e.printStackTrace();
        } catch (IOException e2) {
            RobotLog.e("FIO exception: " + e2.toString());
            e2.printStackTrace();
        }
    }

    private void m3c() throws IOException {
        String format = String.format("cp /data/misc/wifi/wpa_supplicant.conf %s/wpa_supplicant.conf \ncp /data/misc/wifi/p2p_supplicant.conf %s/p2p_supplicant.conf \nchmod 666 %s/*supplicant* \n", new Object[]{this.f0a, this.f0a, this.f0a});
        String format2 = String.format("cp %s/p2p_supplicant.conf /data/misc/wifi/p2p_supplicant.conf \ncp %s/wpa_supplicant.conf /data/misc/wifi/wpa_supplicant.conf \nrm %s/*supplicant* \nchown system.wifi /data/misc/wifi/wpa_supplicant.conf \nchown system.wifi /data/misc/wifi/p2p_supplicant.conf \nkill -HUP %d \n", new Object[]{this.f0a, this.f0a, this.f0a, Integer.valueOf(m0a())});
        FileWriter fileWriter = new FileWriter(this.f1b);
        fileWriter.write(format);
        fileWriter.close();
        FileWriter fileWriter2 = new FileWriter(this.f2c);
        fileWriter2.write(format2);
        fileWriter2.close();
        this.f4e.run("chmod 700 " + this.f1b);
        this.f4e.run("chmod 700 " + this.f2c);
    }

    private void m4d() {
        new File(this.f1b).delete();
        new File(this.f2c).delete();
    }
}
