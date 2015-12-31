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

package com.qualcomm.robotcore.sensor;

import android.util.Log;

import com.qualcomm.robotcore.util.MatrixD;
import com.qualcomm.robotcore.util.Pose;
import com.qualcomm.robotcore.util.PoseUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorImageLocalizer extends SensorBase<Pose> implements SensorListener<List<TrackedTargetInfo>> {
    private static final boolean DEBUG = false;
    private static final String TAG = "SensorImageLocalizer";
    private static final MatrixD TRANSFORM_AXES = Pose.makeRotationX(Math.toRadians(90.0d)).times(Pose.makeRotationY(Math.toRadians(90.0d)));

    private final Map<String, TargetInfo> targetInfoMap = new HashMap<String, TargetInfo>();
    private Pose robotWrtCameraPose = new Pose(0, 0, 0); // with respect to the camera
    private final HashMap<String, TrackedTargetData> dataMap = new HashMap<String, TrackedTargetData>();
    private TrackedTargetData lastData;

    private class TrackedTargetData {
        public final static int RESET_COUNT_TIME_LIMIT = 120;
        public final static int TARGET_SWITCH_INTERVAL = 10;

        public long lastUpdateTime;
        public long timeTracked;
        public int count;
        public String targetName;
        public double confidence;

        TrackedTargetData(String name) {
            this.targetName = name;
        }
    }

    public SensorImageLocalizer(List<SensorListener<Pose>> l) {
        super(l);
    }

    public boolean initialize() {
        return true;
    }

    public boolean shutdown() {
        return true;
    }

    public boolean resume() {
        return true;
    }

    public boolean pause() {
        return true;
    }

    public void AddListener(SensorListener<Pose> l) {
        synchronized (this.mListeners) {
            if (!this.mListeners.contains(l)) {
                this.mListeners.add(l);
            }
        }
    }

    public void RemoveListener(SensorListener<Pose> l) {
        synchronized (this.mListeners) {
            if (this.mListeners.contains(l)) {
                this.mListeners.remove(l);
            }
        }
    }

    public boolean addTargetReference(String targetName, double xTrans, double yTrans, double zTrans, double angle, double longSideTransFromCenterToVertex, double shortSideTransFromCenterToVertex) {
        if (targetName == null) {
            throw new IllegalArgumentException("Null targetInfoWorldRef");
        } else if (this.targetInfoMap.containsKey(targetName)) {
            return false;
        } else {
            MatrixD makeRotationY = Pose.makeRotationY(Math.toRadians(angle));
            MatrixD matrixD = new MatrixD(3, 4);
            matrixD.setSubmatrix(makeRotationY, 3, 3, 0, 0);
            matrixD.data()[0][3] = yTrans;
            matrixD.data()[1][3] = zTrans;
            matrixD.data()[2][3] = xTrans;
            Pose pose = new Pose(matrixD);
            Log.d(TAG, "Target Pose \n" + matrixD);
            this.targetInfoMap.put(targetName, new TargetInfo(targetName, pose, new TargetSize(targetName, longSideTransFromCenterToVertex, shortSideTransFromCenterToVertex)));
            return true;
        }
    }

    public boolean addRobotToCameraRef(double length, double width, double height, double angle) {
        MatrixD makeRotationY = Pose.makeRotationY(-angle);
        MatrixD matrixD = new MatrixD(3, 4);
        matrixD.setSubmatrix(makeRotationY, 3, 3, 0, 0);
        matrixD.data()[0][3] = width;
        matrixD.data()[1][3] = -height;
        matrixD.data()[2][3] = length;
        this.robotWrtCameraPose = new Pose(matrixD);
        return true;
    }

    public boolean removeTargetReference(String targetName) {
        if (targetName == null) {
            throw new IllegalArgumentException("Null targetName");
        } else if (!this.targetInfoMap.containsKey(targetName)) {
            return false;
        } else {
            this.targetInfoMap.remove(targetName);
            return true;
        }
    }

    private boolean isValidInfo(TrackedTargetInfo trackedTargetInfo) {
        long currentTimeMillis = System.currentTimeMillis() / 1000;
        TrackedTargetData trackedTargetData;
        String targetName = trackedTargetInfo.mTargetInfo.mTargetName;
        if (this.dataMap.containsKey(targetName)) {
            trackedTargetData = this.dataMap.get(targetName);
            trackedTargetData.timeTracked = trackedTargetInfo.mTimeTracked;
            trackedTargetData.confidence = trackedTargetInfo.mConfidence;
            if ((currentTimeMillis - trackedTargetData.timeTracked) > TrackedTargetData.RESET_COUNT_TIME_LIMIT) {
                trackedTargetData.count = 1;
            } else {
                trackedTargetData.count++;
            }
        } else {
            trackedTargetData = new TrackedTargetData(targetName);
            trackedTargetData.timeTracked = trackedTargetInfo.mTimeTracked;
            trackedTargetData.confidence = trackedTargetInfo.mConfidence;
            trackedTargetData.count = 1;
            this.dataMap.put(targetName, trackedTargetData);
        }
        if ((this.lastData == null) ||
                (this.lastData.targetName.equals(trackedTargetData.targetName)) ||
                ((currentTimeMillis - this.lastData.lastUpdateTime) >= TrackedTargetData.TARGET_SWITCH_INTERVAL)) {
            return true;
        } else {
            Log.d(TAG, "Ignoring target " + targetName + " Time diff " + (currentTimeMillis - this.lastData.lastUpdateTime));
            return false;
        }
    }

    private TrackedTargetInfo getBestTarget(List<TrackedTargetInfo> targetPoses) {
        double bestConfidence = Double.MIN_VALUE;
        TrackedTargetInfo bestInfo = null;

        for (TrackedTargetInfo info : targetPoses) {
            if (this.targetInfoMap.containsKey(info.mTargetInfo.mTargetName)) {
                if (isValidInfo(info) && (info.mConfidence > bestConfidence)) {
                    bestConfidence = info.mConfidence;
                    bestInfo = info;
                    Log.d(TAG, "Potential target " + info.mTargetInfo.mTargetName +
                            " Confidence " + info.mConfidence);
                } else {
                    Log.d(TAG, "Ignoring target " + info.mTargetInfo.mTargetName +
                            " Confidence " + info.mConfidence);
                }
            }
        }
        return bestInfo;
    }

    public void onUpdate(List<TrackedTargetInfo> targetPoses) {
        Log.d(TAG, "SensorImageLocalizer onUpdate");
        if ((targetPoses == null) || (targetPoses.size() < 1)) {
            Log.d(TAG, "SensorImageLocalizer onUpdate NULL");
            update(null);
            return;
        }

        long currentTimeMillis = System.currentTimeMillis() / 1000;

        TrackedTargetInfo target = getBestTarget(targetPoses);

        if (target == null) {
            update(null);
            return;
        }

        String targetName = target.mTargetInfo.mTargetName;

        TrackedTargetData targetData = this.dataMap.get(targetName);
        targetData.lastUpdateTime = currentTimeMillis;
        this.lastData = targetData;
        Log.d(TAG, "Selected target " + targetName + " time " + currentTimeMillis);

        TargetInfo targetInfo = this.targetInfoMap.get(targetName);
        Pose targetWrtWorldPose = targetInfo.mTargetPose;
        Pose targetWrtCameraPose = target.mTargetInfo.mTargetPose;

        MatrixD robotWrtCameraRot = this.robotWrtCameraPose.poseMatrix.submatrix(3, 3, 0, 0);
        MatrixD robotWrtCameraTrans = this.robotWrtCameraPose.getTranslationMatrix();

        MatrixD targetWrtCameraRot = targetWrtCameraPose.poseMatrix.submatrix(3, 3, 0, 0);
        MatrixD targetWrtCameraTrans = targetWrtCameraPose.getTranslationMatrix();

        MatrixD cameraWrtTargetRot = targetWrtCameraRot.transpose();

        MatrixD targetWrtWorldRot = targetWrtWorldPose.poseMatrix.submatrix(3, 3, 0, 0);
        MatrixD targetWrtWorldTrans = targetWrtWorldPose.getTranslationMatrix();

        MatrixD cameraWrtWorldRot = targetWrtWorldRot.times(cameraWrtTargetRot);

        MatrixD robotWrtWorldRot = cameraWrtWorldRot.times(robotWrtCameraRot);

        MatrixD robotRot = TRANSFORM_AXES.times(robotWrtWorldRot);

        MatrixD vertexWrtTargetTrans = new MatrixD(3, 1);
        vertexWrtTargetTrans.data()[0][0] = targetInfo.mTargetSize.mLongSide;
        vertexWrtTargetTrans.data()[1][0] = targetInfo.mTargetSize.mShortSide;
        vertexWrtTargetTrans.data()[2][0] = 0;

        MatrixD cameraWrtTargetTrans = cameraWrtTargetRot.times(targetWrtCameraTrans);
        MatrixD robotWrtTargetTrans = cameraWrtTargetRot.times(robotWrtCameraTrans);
        MatrixD robotWrtVertexTrans = robotWrtTargetTrans.add(cameraWrtTargetTrans).add(vertexWrtTargetTrans);

        MatrixD robotWrtWorldTrans = targetWrtWorldRot.times(robotWrtVertexTrans);
        MatrixD robotTrans = targetWrtWorldTrans.subtract(robotWrtWorldTrans);
        robotTrans = TRANSFORM_AXES.times(robotTrans);

        MatrixD robotPoseMat = new MatrixD(3, 4);
        robotPoseMat.setSubmatrix(robotRot, 3, 3, 0, 0);
        robotPoseMat.setSubmatrix(robotTrans, 3, 1, 0, 3);
        Pose pose = new Pose(robotPoseMat);
        double[] angles = PoseUtils.getAnglesAroundZ(pose);
        assert angles != null;
        Log.d(TAG, String.format("POSE_HEADING: x %8.4f z %8.4f up %8.4f", angles[0], angles[1], angles[2]));
        robotPoseMat = pose.getTranslationMatrix();
        Log.d(TAG, String.format("POSE_TRANS: x %8.4f y %8.4f z %8.4f", robotPoseMat.data()[0][0], robotPoseMat.data()[1][0], robotPoseMat.data()[2][0]));
        update(pose);
    }
}
