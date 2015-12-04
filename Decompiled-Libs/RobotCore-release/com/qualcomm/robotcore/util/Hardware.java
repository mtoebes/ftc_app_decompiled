package com.qualcomm.robotcore.util;

import android.os.Build;
import android.view.InputDevice;
import java.util.HashSet;
import java.util.Set;

public class Hardware {
    private static boolean f381a;

    static {
        f381a = CheckIfIFC();
    }

    public static Set<Integer> getGameControllerIds() {
        Set<Integer> hashSet = new HashSet();
        for (int i : InputDevice.getDeviceIds()) {
            int sources = InputDevice.getDevice(i).getSources();
            if ((sources & 1025) == 1025 || (sources & 16777232) == 16777232) {
                hashSet.add(Integer.valueOf(i));
            }
        }
        return hashSet;
    }

    public static boolean IsIFC() {
        return f381a;
    }

    public static boolean CheckIfIFC() {
        String str = Build.BOARD;
        String str2 = Build.BRAND;
        String str3 = Build.DEVICE;
        String str4 = Build.HARDWARE;
        String str5 = Build.MANUFACTURER;
        String str6 = Build.MODEL;
        String str7 = Build.PRODUCT;
        RobotLog.m230d("Platform information: board = " + str + " brand = " + str2 + " device = " + str3 + " hardware = " + str4 + " manufacturer = " + str5 + " model = " + str6 + " product = " + str7);
        if (str.equals("MSM8960") && str2.equals("qcom") && str3.equals("msm8960") && str4.equals("qcom") && str5.equals("unknown") && str6.equals("msm8960") && str7.equals("msm8960")) {
            RobotLog.m230d("Detected IFC6410 Device!");
            return true;
        }
        RobotLog.m230d("Detected regular SmartPhone Device!");
        return false;
    }
}
