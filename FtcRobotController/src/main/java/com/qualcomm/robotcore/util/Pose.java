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

        this.poseMatrix.data()[0][0] = 1.0d;
        this.poseMatrix.data()[1][1] = 1.0d;
        this.poseMatrix.data()[2][2] = 1.0d;

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
        double[][] matrixBuffer = new double[3][];
        matrixBuffer[0] = new double[]{this.transX};
        matrixBuffer[1] = new double[]{this.transY};
        matrixBuffer[2] = new double[]{this.transZ};
        return new MatrixD(matrixBuffer);
    }

    public static MatrixD makeRotationX(double angle) {
        double[][] matrixBuffer = (double[][]) Array.newInstance(Double.TYPE, 3, 3);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        matrixBuffer[0][0] = 1.0d;
        matrixBuffer[2][0] = 0.0d;
        matrixBuffer[1][0] = 0.0d;
        matrixBuffer[0][2] = 0.0d;
        matrixBuffer[0][1] = 0.0d;
        matrixBuffer[2][2] = cos;
        matrixBuffer[1][1] = cos;
        matrixBuffer[1][2] = -sin;
        matrixBuffer[2][1] = sin;
        return new MatrixD(matrixBuffer);
    }

    public static MatrixD makeRotationY(double angle) {
        double[][] matrixBuffer = (double[][]) Array.newInstance(Double.TYPE, 3, 3);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        matrixBuffer[2][1] = 0.0d;
        matrixBuffer[1][2] = 0.0d;
        matrixBuffer[1][0] = 0.0d;
        matrixBuffer[0][1] = 0.0d;
        matrixBuffer[1][1] = 1.0d;
        matrixBuffer[2][2] = cos;
        matrixBuffer[0][0] = cos;
        matrixBuffer[0][2] = sin;
        matrixBuffer[2][0] = -sin;
        return new MatrixD(matrixBuffer);
    }

    public static MatrixD makeRotationZ(double angle) {
        double[][] matrixBuffer = (double[][]) Array.newInstance(Double.TYPE, 3, 3);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        matrixBuffer[2][2] = 1.0d;
        matrixBuffer[1][2] = 0.0d;
        matrixBuffer[0][2] = 0.0d;
        matrixBuffer[2][1] = 0.0d;
        matrixBuffer[2][0] = 0.0d;
        matrixBuffer[1][1] = cos;
        matrixBuffer[0][0] = cos;
        matrixBuffer[0][1] = -sin;
        matrixBuffer[1][0] = sin;
        return new MatrixD(matrixBuffer);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        double[] anglesAroundZ = PoseUtils.getAnglesAroundZ(this);
        stringBuilder.append(String.format("(XYZ %1$,.2f ", new Object[]{Double.valueOf(this.transX)}));
        stringBuilder.append(String.format(" %1$,.2f ", new Object[]{Double.valueOf(this.transY)}));
        stringBuilder.append(String.format(" %1$,.2f mm)", new Object[]{Double.valueOf(this.transZ)}));
        stringBuilder.append(String.format("(Angles %1$,.2f, ", new Object[]{Double.valueOf(anglesAroundZ[0])}));
        stringBuilder.append(String.format(" %1$,.2f, ", new Object[]{Double.valueOf(anglesAroundZ[1])}));
        stringBuilder.append(String.format(" %1$,.2f ", new Object[]{Double.valueOf(anglesAroundZ[2])}));
        stringBuilder.append('\u00b0');
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public double getDistanceInMm() {
        return Math.sqrt((Math.pow(this.transX, 2.0d) + Math.pow(this.transY, 2.0d)) + Math.pow(this.transZ, 2.0d));
    }
}
