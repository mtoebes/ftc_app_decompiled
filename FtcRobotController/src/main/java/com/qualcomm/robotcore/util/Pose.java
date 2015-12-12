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
        double[][] r0 = new double[3][];
        r0[0] = new double[]{this.transX};
        r0[1] = new double[]{this.transY};
        r0[2] = new double[]{this.transZ};
        return new MatrixD(r0);
    }

    public static MatrixD makeRotationX(double angle) {
        double[][] dArr = (double[][]) Array.newInstance(Double.TYPE, new int[]{3, 3});
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        dArr[0][0] = Servo.MAX_POSITION;
        double[] dArr2 = dArr[0];
        double[] dArr3 = dArr[0];
        double[] dArr4 = dArr[1];
        dArr[2][0] = 0.0d;
        dArr4[0] = 0.0d;
        dArr3[2] = 0.0d;
        dArr2[1] = 0.0d;
        dArr2 = dArr[1];
        dArr[2][2] = cos;
        dArr2[1] = cos;
        dArr[1][2] = -sin;
        dArr[2][1] = sin;
        return new MatrixD(dArr);
    }

    public static MatrixD makeRotationY(double angle) {
        double[][] dArr = (double[][]) Array.newInstance(Double.TYPE, new int[]{3, 3});
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double[] dArr2 = dArr[0];
        double[] dArr3 = dArr[1];
        double[] dArr4 = dArr[1];
        dArr[2][1] = 0.0d;
        dArr4[2] = 0.0d;
        dArr3[0] = 0.0d;
        dArr2[1] = 0.0d;
        dArr[1][1] = Servo.MAX_POSITION;
        dArr2 = dArr[0];
        dArr[2][2] = cos;
        dArr2[0] = cos;
        dArr[0][2] = sin;
        dArr[2][0] = -sin;
        return new MatrixD(dArr);
    }

    public static MatrixD makeRotationZ(double angle) {
        double[][] dArr = (double[][]) Array.newInstance(Double.TYPE, new int[]{3, 3});
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        dArr[2][2] = Servo.MAX_POSITION;
        double[] dArr2 = dArr[2];
        double[] dArr3 = dArr[2];
        double[] dArr4 = dArr[0];
        dArr[1][2] = 0.0d;
        dArr4[2] = 0.0d;
        dArr3[1] = 0.0d;
        dArr2[0] = 0.0d;
        dArr2 = dArr[0];
        dArr[1][1] = cos;
        dArr2[0] = cos;
        dArr[0][1] = -sin;
        dArr[1][0] = sin;
        return new MatrixD(dArr);
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
