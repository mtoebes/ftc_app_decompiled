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
            double[][] unitVec = new double[3][];
            unitVec[0] = new double[]{0};
            unitVec[1] = new double[]{0};
            unitVec[2] = new double[]{Servo.MAX_POSITION};
            MatrixD times = rotMat.times(new MatrixD(unitVec));
            double toDegrees = Math.toDegrees(Math.atan2(times.data()[1][0], times.data()[0][0]));
            double toDegrees2 = Math.toDegrees(Math.atan2(times.data()[0][0], times.data()[1][0]));
            double toDegrees3 = Math.toDegrees(Math.asin(times.data()[2][0] / times.length()));
            return new double[]{toDegrees, toDegrees2, toDegrees3};
        }
        throw new IllegalArgumentException("Invalid Matrix Dimension: Expected (3,3) got (" + rotMat.numRows() + "," + rotMat.numCols() + ")");
    }

    public static double smallestAngularDifferenceDegrees(double firstAngleDeg, double secondAngleDeg) {
        double d = ((firstAngleDeg - secondAngleDeg) * Math.PI) / 180;
        return (Math.atan2(Math.sin(d), Math.cos(d)) * 180) / Math.PI;
    }
}
