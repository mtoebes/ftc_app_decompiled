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
        double deltaDegrees = (firstAngleDeg - secondAngleDeg) * (Math.PI / 180.0d);
        return Math.atan2(Math.sin(deltaDegrees), Math.cos(deltaDegrees)) * (180.0d / Math.PI);
    }
}
