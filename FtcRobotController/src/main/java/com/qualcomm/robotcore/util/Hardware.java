/*
 * Copyright (c) 2014, 2015 Qualcomm Technologies Inc
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * (subject to the limitations in the disclaimer below) provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Qualcomm Technologies Inc nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.qualcomm.robotcore.util;

import android.os.Build;
import android.view.InputDevice;

import java.util.HashSet;
import java.util.Set;

public class Hardware {
    private static final boolean isIFC = CheckIfIFC();

    public static Set<Integer> getGameControllerIds() {
        Set<Integer> hashSet = new HashSet<Integer>();
        for (int deviceId : InputDevice.getDeviceIds()) {
            int sources = InputDevice.getDevice(deviceId).getSources();
            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) ||
                    ((sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)) {
                hashSet.add(deviceId);
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
        if ("MSM8960".equals(board) && "qcom".equals(brand) && "msm8960".equals(device) && "qcom".equals(hardware) && "unknown".equals(manufacturer) && "msm8960".equals(model) && "msm8960".equals(product)) {
            RobotLog.d("Detected IFC6410 Device!");
            return true;
        }
        RobotLog.d("Detected regular SmartPhone Device!");
        return false;
    }
}
