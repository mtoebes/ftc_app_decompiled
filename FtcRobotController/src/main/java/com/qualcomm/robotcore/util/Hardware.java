package com.qualcomm.robotcore.util;

import android.os.Build;
import android.view.InputDevice;
import java.util.HashSet;
import java.util.Set;

public class Hardware {
    private static boolean isIFC;

    static {
        isIFC = CheckIfIFC();
    }

    public static Set<Integer> getGameControllerIds() {
        Set<Integer> hashSet = new HashSet();
        for (int deviceId : InputDevice.getDeviceIds()) {
            int sources = InputDevice.getDevice(deviceId).getSources();
            if ((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD || (sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK) {
                hashSet.add(Integer.valueOf(deviceId));
            }
        }
        return hashSet;
    }

    public static boolean IsIFC() {
        return isIFC;
    }

    public static boolean CheckIfIFC() {
        String board = Build.BOARD;
        String brand = Build.BRAND;
        String device = Build.DEVICE;
        String hardware = Build.HARDWARE;
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String product = Build.PRODUCT;
        RobotLog.d("Platform information: board = " + board + " brand = " + brand + " device = " + device + " hardware = " + hardware + " manufacturer = " + manufacturer + " model = " + model + " product = " + product);
        if (board.equals("MSM8960") && brand.equals("qcom") && device.equals("msm8960") && hardware.equals("qcom") && manufacturer.equals("unknown") && model.equals("msm8960") && product.equals("msm8960")) {
            RobotLog.d("Detected IFC6410 Device!");
            return true;
        }
        RobotLog.d("Detected regular SmartPhone Device!");
        return false;
    }
}
