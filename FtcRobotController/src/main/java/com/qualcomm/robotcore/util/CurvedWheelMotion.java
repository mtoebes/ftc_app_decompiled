package com.qualcomm.robotcore.util;

/**
 * This class consists of static utility functions that calculate robot motions for different drive
 * mechanisms.
 */
public class CurvedWheelMotion {
    /**
     * Given rotational velocity where (+) is CCW, calculate linear wheel velocity where (+) is
     * robot forward.
     */
    public static int velocityForRotationMmPerSec(int rotateAroundXInMM, int rotateAroundYInMM,
                                                  double rotationalVelocityInDegPerSec,
                                                  int wheelOffsetXInMm, int wheelOffsetYInMm) {
        int wheelRadiusInMm = (int) Math.sqrt(
                Math.pow((wheelOffsetXInMm - rotateAroundXInMM), 2) +
                        Math.pow((wheelOffsetYInMm - rotateAroundYInMM), 2)
        );

        int wheelVelocityInMmPerSec = (int) (rotationalVelocityInDegPerSec *
                ((Math.PI * wheelRadiusInMm) / 180));

        RobotLog.d("CurvedWheelMotion rX " + rotateAroundXInMM +
                ", theta " + rotationalVelocityInDegPerSec +
                ", velocity " + wheelVelocityInMmPerSec);

        return wheelVelocityInMmPerSec;
    }

    /**
     * Calculate the left or right wheel velocity for a differential drive robot given its
     * translational velocity and rotational velocities.
     * <br>
     * Note: for a skid-steering (4-wheel) robot, this calculation is approximate.
     *
     * @param linearVelocityInMmPerSec robot linear velocity in mm per sec (+ve = forward, -ve = reverse)
     * @param rotationalVelocityInDegPerSec robot rotational velocity in degrees per sec (+ve = CCW, -ve = CW)
     * @param wheelRadiusInMm radius of each wheel in mm
     * @param axleLengthInMm axle length in mm
     * @param leftWheel boolean indicating this is for the left wheel (true) or right wheel (false)
     * @return translational left wheel velocity (along the ground)
     */
    public static int getDiffDriveRobotWheelVelocity(int linearVelocityInMmPerSec, double rotationalVelocityInDegPerSec,
                                                     int wheelRadiusInMm, int axleLengthInMm, boolean leftWheel) {
        // robot rotational velocity in radians per sec
        double rotationalVelocityInRadPerSec = Math.toRadians(rotationalVelocityInDegPerSec);

        // vleft = (2v – ωL) / 2R

        // calculate the left wheel rotational velocity
        final double wheelVelocityInRadsPerSec;

        if (leftWheel) {
            wheelVelocityInRadsPerSec =
                    ((2 * linearVelocityInMmPerSec) - (rotationalVelocityInRadPerSec * axleLengthInMm))
                            / (2* wheelRadiusInMm);
        } else {
            wheelVelocityInRadsPerSec =
                    ((2 * linearVelocityInMmPerSec) + (rotationalVelocityInRadPerSec * axleLengthInMm))
                            / (2* wheelRadiusInMm);
        }

        // calculate linear velocities (wheel rotations * wheel radius)
        return (int) (wheelVelocityInRadsPerSec * wheelRadiusInMm);
    }


    /**
     * Calculate the translational velocity for a differential drive robot given its left and right
     * wheel velocities.
     *
     * @param leftVelocityInMmPerSec left wheel velocity in mm per sec (+ve = forward, -ve = reverse)
     * @param rightVelocityInMmPerSec right wheel velocity in mm per sec (+ve = forward, -ve = reverse)
     * @return robot translational (linear) velocity in mm per sec (+ve = forward, -ve = reverse)
     */
    public static int getDiffDriveRobotTransVelocity(int leftVelocityInMmPerSec, int rightVelocityInMmPerSec) {
        // V = (vleft + vright) / 2
        return (leftVelocityInMmPerSec + rightVelocityInMmPerSec) / 2;
    }

    /**
     * Calculate the rotational velocity for a differential drive robot given its left and right
     * wheel velocities.
     *
     * @param leftVelocityInMmPerSec left wheel velocity in mm per sec (+ve = forward, -ve = reverse)
     * @param rightVelocityInMmPerSec right wheel velocity in mm per sec (+ve = forward, -ve = reverse)
     * @param axleLengthInMm length of axle (distance between the wheels) in mm
     * @return robot rotational velocity in degrees per second (+ve = CCW, -ve = CW)
     */
    public static double getDiffDriveRobotRotVelocity(int leftVelocityInMmPerSec, int rightVelocityInMmPerSec,
                                                      int axleLengthInMm) {
        // V = (vright - vleft) / L
        double angularV = ((double)(rightVelocityInMmPerSec - leftVelocityInMmPerSec))/axleLengthInMm;

        // Convert into degrees
        return Math.toDegrees(angularV);
    }
}
