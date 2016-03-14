package com.qualcomm.modernrobotics;

import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.TypeConversion;

/* renamed from: com.qualcomm.modernrobotics.a */
class C0000a {
    static boolean m7a(byte[] bArr, int i) {
        if (bArr.length < 5) {
            RobotLog.w("Modern Robotics USB header length is too short");
            return false;
        } else if (bArr[0] != 51 || bArr[1] != -52) {
            RobotLog.w("Modern Robotics USB header sync bytes are incorrect");
            return false;
        } else if (TypeConversion.unsignedByteToInt(bArr[4]) == i) {
            return true;
        } else {
            RobotLog.w("Modern Robotics USB header reported unexpected packet size");
            return false;
        }
    }
}
