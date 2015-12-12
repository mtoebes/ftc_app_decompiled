package com.qualcomm.robotcore.util;

public class CurvedWheelMotion {
    public static int velocityForRotationMmPerSec(int rotateAroundXInMM, int rotateAroundYInMM, double rotationalVelocityInDegPerSec, int wheelOffsetXInMm, int wheelOffsetYInMm) {
        int sqrt = (int) (((((double) ((int) Math.sqrt(Math.pow((double) (wheelOffsetXInMm - rotateAroundXInMM), 2.0d) + Math.pow((double) (wheelOffsetYInMm - rotateAroundYInMM), 2.0d)))) * 6.283185307179586d) / 360.0d) * rotationalVelocityInDegPerSec);
        RobotLog.m230d("CurvedWheelMotion rX " + rotateAroundXInMM + ", theta " + rotationalVelocityInDegPerSec + ", velocity " + sqrt);
        return sqrt;
    }

    public static int getDiffDriveRobotWheelVelocity(int linearVelocityInMmPerSec, double rotationalVelocityInDegPerSec, int wheelRadiusInMm, int axleLengthInMm, boolean leftWheel) {
        double toRadians = Math.toRadians(rotationalVelocityInDegPerSec);
        if (leftWheel) {
            toRadians = (((double) (linearVelocityInMmPerSec * 2)) - (toRadians * ((double) axleLengthInMm))) / ((double) (wheelRadiusInMm * 2));
        } else {
            toRadians = ((toRadians * ((double) axleLengthInMm)) + ((double) (linearVelocityInMmPerSec * 2))) / ((double) (wheelRadiusInMm * 2));
        }
        return (int) (toRadians * ((double) wheelRadiusInMm));
    }

    public static int getDiffDriveRobotTransVelocity(int leftVelocityInMmPerSec, int rightVelocityInMmPerSec) {
        return (leftVelocityInMmPerSec + rightVelocityInMmPerSec) / 2;
    }

    public static double getDiffDriveRobotRotVelocity(int leftVelocityInMmPerSec, int rightVelocityInMmPerSec, int axleLengthInMm) {
        return Math.toDegrees((double) ((rightVelocityInMmPerSec - leftVelocityInMmPerSec) / axleLengthInMm));
    }
}
