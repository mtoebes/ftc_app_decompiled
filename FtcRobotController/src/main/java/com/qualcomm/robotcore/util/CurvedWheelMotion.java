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

public class CurvedWheelMotion {
    public static int velocityForRotationMmPerSec(int rotateAroundXInMM, int rotateAroundYInMM, double rotationalVelocityInDegPerSec, int wheelOffsetXInMm, int wheelOffsetYInMm) {
        int sqrt = (int) ((((double) (int) Math.sqrt(Math.pow((double) (wheelOffsetXInMm - rotateAroundXInMM), 2.0d) + Math.pow((double) (wheelOffsetYInMm - rotateAroundYInMM), 2.0d)) * 6.283185307179586d) / 360.0d) * rotationalVelocityInDegPerSec);
        RobotLog.d("CurvedWheelMotion rX " + rotateAroundXInMM + ", theta " + rotationalVelocityInDegPerSec + ", velocity " + sqrt);
        return sqrt;
    }

    public static int getDiffDriveRobotWheelVelocity(int linearVelocityInMmPerSec, double rotationalVelocityInDegPerSec, int wheelRadiusInMm, int axleLengthInMm, boolean leftWheel) {
        double toRadians = Math.toRadians(rotationalVelocityInDegPerSec);
        if (leftWheel) {
            toRadians = ((double) (linearVelocityInMmPerSec * 2) - (toRadians * (double) axleLengthInMm)) / (double) (wheelRadiusInMm * 2);
        } else {
            toRadians = ((toRadians * (double) axleLengthInMm) + (double) (linearVelocityInMmPerSec * 2)) / (double) (wheelRadiusInMm * 2);
        }
        return (int) (toRadians * (double) wheelRadiusInMm);
    }

    public static int getDiffDriveRobotTransVelocity(int leftVelocityInMmPerSec, int rightVelocityInMmPerSec) {
        return (leftVelocityInMmPerSec + rightVelocityInMmPerSec) / 2;
    }

    public static double getDiffDriveRobotRotVelocity(int leftVelocityInMmPerSec, int rightVelocityInMmPerSec, int axleLengthInMm) {
        return Math.toDegrees((double) ((rightVelocityInMmPerSec - leftVelocityInMmPerSec) / axleLengthInMm));
    }
}
