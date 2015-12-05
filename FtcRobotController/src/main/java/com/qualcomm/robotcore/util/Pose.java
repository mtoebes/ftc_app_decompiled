package com.qualcomm.robotcore.util;

import com.qualcomm.robotcore.hardware.Servo;
import java.lang.reflect.Array;

public class Pose {
    public MatrixD poseMatrix;
    public double transX;
    public double transY;
    public double transZ;

    public Pose(MatrixD poseMatrix) {
        if (poseMatrix == null) {
            throw new IllegalArgumentException("Attempted to construct Pose from null matrix");
        } else if (poseMatrix.numRows() == 3 && poseMatrix.numCols() == 4) {
            this.poseMatrix = poseMatrix;
            this.transX = poseMatrix.data()[0][3];
            this.transY = poseMatrix.data()[1][3];
            this.transZ = poseMatrix.data()[2][3];
        } else {
            throw new IllegalArgumentException("Invalid matrix size ( " + poseMatrix.numRows() + ", " + poseMatrix.numCols() + " )");
        }
    }

    public Pose(double transX, double transY, double transZ) {
        this.transX = transX;
        this.transY = transY;
        this.transZ = transZ;
        this.poseMatrix = new MatrixD(3, 4);
        double[] dArr = this.poseMatrix.data()[0];
        double[] dArr2 = this.poseMatrix.data()[1];
        this.poseMatrix.data()[2][2] = Servo.MAX_POSITION;
        dArr2[1] = Servo.MAX_POSITION;
        dArr[0] = Servo.MAX_POSITION;
        this.poseMatrix.data()[0][3] = transX;
        this.poseMatrix.data()[1][3] = transY;
        this.poseMatrix.data()[2][3] = transZ;
    }

    public Pose() {
        this.transX = 0.0d;
        this.transY = 0.0d;
        this.transZ = 0.0d;
    }

    public MatrixD getTranslationMatrix() {
        double[][] matrixArray = new double[3][];
        matrixArray[0] = new double[]{this.transX};
        matrixArray[1] = new double[]{this.transY};
        matrixArray[2] = new double[]{this.transZ};
        return new MatrixD(matrixArray);
    }

    public static MatrixD makeRotationX(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double[][] matrixArray = new double[][]{
                {Servo.MAX_POSITION, 0, 0},
                {0, cos, -sin},
                {0, sin, cos}
        };
        return new MatrixD(matrixArray);
    }

    public static MatrixD makeRotationY(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        double[][] matrixArray = new double[][]{
                {cos, 0, sin},
                {0, Servo.MAX_POSITION, 0},
                {-sin, 0, cos}
        };
        return new MatrixD(matrixArray);
    }

    public static MatrixD makeRotationZ(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        double[][] matrixArray = new double[][]{
                {cos, -sin, 0},
                {sin, cos, 0},
                {0, 0, Servo.MAX_POSITION}
        };
        return new MatrixD(matrixArray);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        double[] anglesAroundZ = PoseUtils.getAnglesAroundZ(this);
        stringBuilder.append(String.format("(XYZ %1$,.2f ", transX));
        stringBuilder.append(String.format(" %1$,.2f ", transY));
        stringBuilder.append(String.format(" %1$,.2f mm)", transZ));
        if(anglesAroundZ != null && anglesAroundZ.length >= 3) {
            stringBuilder.append("(Angles");
            stringBuilder.append(String.format(" %1$,.2f, ", anglesAroundZ[0]));
            stringBuilder.append(String.format(" %1$,.2f, ", anglesAroundZ[1]));
            stringBuilder.append(String.format(" %1$,.2f ", anglesAroundZ[2]));
            stringBuilder.append('\u00b0');
            stringBuilder.append(")");
        }
        return stringBuilder.toString();
    }

    public double getDistanceInMm() {
        return Math.sqrt(Math.pow(transX, 2.0d) + Math.pow(transY, 2.0d) + Math.pow(transZ, 2.0d));
    }
}
