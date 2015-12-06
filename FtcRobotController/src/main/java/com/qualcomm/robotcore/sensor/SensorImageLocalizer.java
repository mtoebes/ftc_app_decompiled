package com.qualcomm.robotcore.sensor;

import android.util.Log;
import com.qualcomm.robotcore.util.MatrixD;
import com.qualcomm.robotcore.util.Pose;
import com.qualcomm.robotcore.util.PoseUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorImageLocalizer extends SensorBase<Pose> implements SensorListener<List<TrackedTargetInfo>> {
    private final static boolean DEBUG = false;
    private final String TAG = "SensorImageLocalizer";

    private final Map<String, TargetInfo> targetInfoMap = new HashMap<String, TargetInfo>();
    private final HashMap<String, TrackedTargetData> targetDataMap = new HashMap<String, TrackedTargetData>();
    private Pose mRobotwrtCamera;
    private TrackedTargetData latestData;

    private class TrackedTargetData {
        public final static int RESET_COUNT_TIME_LIMIT = 120;
        public final static int TARGET_SWITCH_INTERVAL = 10;
        public long lastUpdateTime;
        public long lastTimeTracked;
        public int count;
        public String id;
        public double confidence;

        TrackedTargetData(TrackedTargetInfo info) {
            id = info.mTargetInfo.mTargetName;
            lastTimeTracked = info.mTimeTracked;
            confidence = info.mConfidence;
            count = 1;
        }

        public void updateData(TrackedTargetInfo info, long currentTimeMillis) {
            lastTimeTracked = info.mTimeTracked;
            confidence = info.mConfidence;
            if (currentTimeMillis - lastTimeTracked > TrackedTargetData.RESET_COUNT_TIME_LIMIT) {
                count = 1;
            } else {
                count++;
            }
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
        MatrixD matrixD;
        MatrixD makeRotationY = Pose.makeRotationY(-angle);
        matrixD = new MatrixD(3, 4);
        matrixD.setSubmatrix(makeRotationY, 3, 3, 0, 0);
        matrixD.data()[0][3] = width;
        matrixD.data()[1][3] = -height;
        matrixD.data()[2][3] = length;
        this.mRobotwrtCamera = new Pose(matrixD);
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

    private boolean isValidTarget(TrackedTargetInfo trackedTargetInfo) {
        long currentTimeMillis = System.currentTimeMillis() / 1000;
        TrackedTargetData data;
        if (this.targetDataMap.containsKey(trackedTargetInfo.mTargetInfo.mTargetName)) {
            data = targetDataMap.get(trackedTargetInfo.mTargetInfo.mTargetName);
            data.updateData(trackedTargetInfo, currentTimeMillis);
        } else {
            data = new TrackedTargetData(trackedTargetInfo);
            this.targetDataMap.put(trackedTargetInfo.mTargetInfo.mTargetName, data);
        }
        if (this.latestData != null &&
                !latestData.id.equals(data.id) &&
                currentTimeMillis - this.latestData.lastUpdateTime < TrackedTargetData.TARGET_SWITCH_INTERVAL) {
            Log.d(TAG, "Ignoring target " + trackedTargetInfo.mTargetInfo.mTargetName + " Time diff " + (currentTimeMillis - this.latestData.lastUpdateTime));
            return false;
        }
        return true;
    }

   private TrackedTargetInfo getTarget(List<TrackedTargetInfo> targetPoses) {
       boolean targetFound = false;
       double maxConfidence = Double.MIN_VALUE;
       long currentTimeMillis = System.currentTimeMillis() / 1000;
       TrackedTargetInfo bestPose = null;
       TrackedTargetData bestTargetData = null;

       // Find best target from list of known targets
       for (TrackedTargetInfo targetPose : targetPoses) {
           if (this.targetInfoMap.containsKey(targetPose.mTargetInfo.mTargetName)) {
               boolean validTarget = isValidTarget(targetPose);
               if (validTarget && targetPose.mConfidence > maxConfidence) {
                   bestTargetData = this.targetDataMap.get(targetPose.mTargetInfo.mTargetName);
                   bestPose = targetPose;
                   targetFound = true;
                   maxConfidence = targetPose.mConfidence;
                   Log.d(TAG, "Potential target " + targetPose.mTargetInfo.mTargetName + " Confidence " + targetPose.mConfidence);
               } else{
                   Log.d(TAG, "Ignoring target " + targetPose.mTargetInfo.mTargetName + " Confidence " + targetPose.mConfidence);
               }
           }
       }

       // No target found
       if (!targetFound) {
           update(null);
           return null;
       } else {
           bestTargetData.lastUpdateTime = currentTimeMillis;
           latestData = bestTargetData;
           Log.d(TAG, "Selected target " + bestPose.mTargetInfo.mTargetName + " time " + currentTimeMillis);
           return bestPose;
       }

   }
    public void onUpdate(List<TrackedTargetInfo> targetPoses) {
        Log.d(TAG, "SensorImageLocalizer onUpdate");
        if (targetPoses == null || targetPoses.size() < 1) {
            Log.d(TAG, "SensorImageLocalizer onUpdate NULL");
            update(null);
            return;
        }

        TrackedTargetInfo target = getTarget(targetPoses);

        if(target == null) {
            return;
        }

        TargetInfo targetInfo = this.targetInfoMap.get(target.mTargetInfo.mTargetName);

        MatrixD robotWrtCamera = null;
        if (mRobotwrtCamera != null) {
            robotWrtCamera = mRobotwrtCamera.poseMatrix.submatrix(3, 3, 0, 0);
        }

        MatrixD targetWrtCamera = target.mTargetInfo.mTargetPose.poseMatrix.submatrix(3, 3, 0, 0);
        MatrixD cameraWrtTarget = targetWrtCamera.transpose();
        MatrixD targetWrtWorld = targetInfo.mTargetPose.poseMatrix.submatrix(3, 3, 0, 0);

        MatrixD transformAxes = Pose.makeRotationX(Math.toRadians(90));
        transformAxes.times(Pose.makeRotationY(Math.toRadians(90)));

        MatrixD robotHeading = transformAxes.times(targetWrtWorld).times(cameraWrtTarget);

        if(robotWrtCamera != null){
            robotHeading = robotHeading.times(robotWrtCamera);
        }

        MatrixD targetToVertex = new MatrixD(3, 1);
        targetToVertex.data()[0][0] = targetInfo.mTargetSize.mLongSide;
        targetToVertex.data()[1][0] = targetInfo.mTargetSize.mShortSide;
        targetToVertex.data()[2][0] = 0;

        MatrixD tPose = target.mTargetInfo.mTargetPose.getTranslationMatrix();

        MatrixD transVec = cameraWrtTarget.times(tPose);

        MatrixD tRobotWrtCamera;
        if(mRobotwrtCamera!=null){
            tRobotWrtCamera = mRobotwrtCamera.getTranslationMatrix();
        } else {
            tRobotWrtCamera = new MatrixD(3, 1);
        }

        MatrixD transVecRobotWrtTarget = cameraWrtTarget.times(tRobotWrtCamera);

        transVec = transVec.add(transVecRobotWrtTarget);
        transVec = transVec.add(targetToVertex);
        transVec = targetWrtWorld.times(transVec);

        MatrixD transVecTargetWrtWorld = targetInfo.mTargetPose.getTranslationMatrix();

        transVec = transVecTargetWrtWorld.subtract(transVec);
        transVec = transformAxes.times(transVec);

        MatrixD robotPosMat = new MatrixD(3,4);
        robotPosMat.setSubmatrix(robotHeading, 3, 3, 0, 0);

        robotPosMat.setSubmatrix(transVec, 3, 1, 0, 3);

        Pose robotPose = new Pose(robotPosMat);
        double[] angles = PoseUtils.getAnglesAroundZ(robotPose);
        Log.d(TAG, String.format("POSE_HEADING: x %8.4f z %8.4f up %8.4f", angles[0], angles[1], angles[2]));
        robotPosMat = robotPose.getTranslationMatrix();
        Log.d(TAG, String.format("POSE_TRANS: x %8.4f y %8.4f z %8.4f", robotPosMat.data()[0][0], robotPosMat.data()[1][0], robotPosMat.data()[2][0]));
        update(robotPose);
    }
}
