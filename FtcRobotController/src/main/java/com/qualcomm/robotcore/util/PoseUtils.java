package com.qualcomm.robotcore.util;

import android.util.Log;
import com.qualcomm.robotcore.hardware.Servo;

public class PoseUtils {
    public static double[] getAnglesAroundZ(Pose inputPose) {
        if (inputPose != null && inputPose.poseMatrix != null) {
            return getAnglesAroundZ(inputPose.poseMatrix.submatrix(3, 3, 0, 0));
        }
        Log.e("PoseUtils", "null input");
        return null;
    }

    public static double[] getAnglesAroundZ(MatrixD rotMat) {
        if (rotMat.numRows() == 3 && rotMat.numCols() == 3) {
            double[][] r0 = new double[3][];
            r0[0] = new double[]{0.0d};
            r0[1] = new double[]{0.0d};
            r0[2] = new double[]{Servo.MAX_POSITION};
            MatrixD times = rotMat.times(new MatrixD(r0));
            double toDegrees = Math.toDegrees(Math.atan2(times.data()[1][0], times.data()[0][0]));
            double toDegrees2 = Math.toDegrees(Math.atan2(times.data()[0][0], times.data()[1][0]));
            double toDegrees3 = Math.toDegrees(Math.asin(times.data()[2][0] / times.length()));
            return new double[]{toDegrees, toDegrees2, toDegrees3};
        }
        throw new IllegalArgumentException("Invalid Matrix Dimension: Expected (3,3) got (" + rotMat.numRows() + "," + rotMat.numCols() + ")");
    }

    public static double smallestAngularDifferenceDegrees(double firstAngleDeg, double secondAngleDeg) {
        double d = ((firstAngleDeg - secondAngleDeg) * 3.141592653589793d) / 180.0d;
        return (Math.atan2(Math.sin(d), Math.cos(d)) * 180.0d) / 3.141592653589793d;
    }
}
