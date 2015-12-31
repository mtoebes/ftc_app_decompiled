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

import java.lang.reflect.Array;

public class Pose {
    public MatrixD poseMatrix;
    public double transX;
    public double transY;
    public double transZ;

    public Pose(MatrixD poseMatrix) {
        if (poseMatrix == null) {
            throw new IllegalArgumentException("Attempted to construct Pose from null matrix");
        } else if ((poseMatrix.numRows() == 3) && (poseMatrix.numCols() == 4)) {
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
        assert anglesAroundZ != null;
        stringBuilder.append(String.format("(XYZ %1$,.2f ", this.transX));
        stringBuilder.append(String.format(" %1$,.2f ", this.transY));
        stringBuilder.append(String.format(" %1$,.2f mm)", this.transZ));
        stringBuilder.append(String.format("(Angles %1$,.2f, ", anglesAroundZ[0]));
        stringBuilder.append(String.format(" %1$,.2f, ", anglesAroundZ[1]));
        stringBuilder.append(String.format(" %1$,.2f ", anglesAroundZ[2]));
        stringBuilder.append('\u00b0');
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public double getDistanceInMm() {
        return Math.sqrt(Math.pow(this.transX, 2.0d) + Math.pow(this.transY, 2.0d) + Math.pow(this.transZ, 2.0d));
    }
}
