package com.qualcomm.robotcore.util;

import android.os.Build;
import android.view.InputDevice;
import java.util.HashSet;
import java.util.Set;

public class Hardware {
    private static boolean isIFC = CheckIfIFC();

    public static Set<Integer> getGameControllerIds() {
        Set<Integer> hashSet = new HashSet<Integer>();
        for (int deviceId : InputDevice.getDeviceIds()) {
            int sources = InputDevice.getDevice(deviceId).getSources();
            if ((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD ||
                    (sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK) {
                hashSet.add(deviceId);
            }
        }
        return hashSet;
    }

    public static boolean IsIFC() {
        return isIFC;
    }

    public static boolean CheckIfIFC() {
        RobotLog.d("Platform information: board = " + Build.BOARD +
                " brand = " + Build.BRAND +
                " device = " + Build.DEVICE +
                " hardware = " + Build.HARDWARE +
                " manufacturer = " + Build.MANUFACTURER +
                " model = " + Build.MODEL +
                " product = " + Build.PRODUCT);

        if (Build.BOARD.equals("MSM8960") &&
                Build.BRAND.equals("qcom") &&
                Build.DEVICE.equals("msm8960") &&
                Build.HARDWARE.equals("qcom") &&
                Build.MANUFACTURER.equals("unknown") &&
                Build.MODEL.equals("msm8960") &&
                Build.PRODUCT.equals("msm8960")) {
            RobotLog.d("Detected IFC6410 Device!");
            return true;
        } else {
            RobotLog.d("Detected regular SmartPhone Device!");
            return false;
        }
    }
}
