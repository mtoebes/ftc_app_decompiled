package com.qualcomm.robotcore.util;

import android.util.Log;

public class PoseUtils {
    public static double[] getAnglesAroundZ(Pose inputPose) {
        if ((inputPose != null) && (inputPose.poseMatrix != null)) {
            return getAnglesAroundZ(inputPose.poseMatrix.submatrix(3, 3, 0, 0));
        }
        Log.e("PoseUtils", "null input");
        return null;
    }

    public static double[] getAnglesAroundZ(MatrixD rotMat) {
        if ((rotMat.numRows() == 3) && (rotMat.numCols() == 3)) {
            double[][] vector = new double[3][];
            vector[0] = new double[]{0.0d};
            vector[1] = new double[]{0.0d};
            vector[2] = new double[]{1.0d};
            MatrixD zVector = new MatrixD(vector);
            zVector = rotMat.times(zVector);
            double xAngle = Math.toDegrees(Math.atan2(zVector.data()[1][0], zVector.data()[0][0]));
            double yAngle = Math.toDegrees(Math.atan2(zVector.data()[0][0], zVector.data()[1][0]));
            double zAngle = Math.toDegrees(Math.asin(zVector.data()[2][0] / zVector.length()));
            return new double[]{xAngle, yAngle, zAngle};
        }
        throw new IllegalArgumentException("Invalid Matrix Dimension: Expected (3,3) got (" + rotMat.numRows() + "," + rotMat.numCols() + ")");
    }

    public static double smallestAngularDifferenceDegrees(double firstAngleDeg, double secondAngleDeg) {
        double deltaDegrees = (firstAngleDeg - secondAngleDeg) * Math.PI / 180.0d;
        return Math.atan2(Math.sin(deltaDegrees), Math.cos(deltaDegrees)) * 180.0d / Math.PI;
    }
}
