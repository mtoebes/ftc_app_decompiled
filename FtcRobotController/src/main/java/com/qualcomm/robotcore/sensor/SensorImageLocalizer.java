package com.qualcomm.robotcore.sensor;

import android.util.Log;
import com.qualcomm.robotcore.util.MatrixD;
import com.qualcomm.robotcore.util.Pose;
import com.qualcomm.robotcore.util.PoseUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorImageLocalizer extends SensorBase<Pose> implements SensorListener<List<TrackedTargetInfo>> {
    private final boolean f352a;
    private final String f353b;
    private final Map<String, TargetInfo> f354c;
    private Pose f355d;
    private final HashMap<String, C0042a> f356e;
    private C0042a f357f;

    /* renamed from: com.qualcomm.robotcore.sensor.SensorImageLocalizer.a */
    private class C0042a {
        public long f346a;
        public long f347b;
        public int f348c;
        public String f349d;
        public double f350e;
        final /* synthetic */ SensorImageLocalizer f351f;

        private C0042a(SensorImageLocalizer sensorImageLocalizer) {
            this.f351f = sensorImageLocalizer;
        }
    }

    public SensorImageLocalizer(List<SensorListener<Pose>> l) {
        super(l);
        this.f352a = false;
        this.f353b = "SensorImageLocalizer";
        this.f356e = new HashMap();
        this.f354c = new HashMap();
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
        } else if (this.f354c.containsKey(targetName)) {
            return false;
        } else {
            MatrixD makeRotationY = Pose.makeRotationY(Math.toRadians(angle));
            MatrixD matrixD = new MatrixD(3, 4);
            matrixD.setSubmatrix(makeRotationY, 3, 3, 0, 0);
            matrixD.data()[0][3] = yTrans;
            matrixD.data()[1][3] = zTrans;
            matrixD.data()[2][3] = xTrans;
            Pose pose = new Pose(matrixD);
            Log.d("SensorImageLocalizer", "Target Pose \n" + matrixD);
            this.f354c.put(targetName, new TargetInfo(targetName, pose, new TargetSize(targetName, longSideTransFromCenterToVertex, shortSideTransFromCenterToVertex)));
            return true;
        }
    }

    public boolean addRobotToCameraRef(double length, double width, double height, double angle) {
        MatrixD matrixD = new MatrixD(3, 3);
        MatrixD makeRotationY = Pose.makeRotationY(-angle);
        matrixD = new MatrixD(3, 4);
        matrixD.setSubmatrix(makeRotationY, 3, 3, 0, 0);
        matrixD.data()[0][3] = width;
        matrixD.data()[1][3] = -height;
        matrixD.data()[2][3] = length;
        this.f355d = new Pose(matrixD);
        return true;
    }

    public boolean removeTargetReference(String targetName) {
        if (targetName == null) {
            throw new IllegalArgumentException("Null targetName");
        } else if (!this.f354c.containsKey(targetName)) {
            return false;
        } else {
            this.f354c.remove(targetName);
            return true;
        }
    }

    private boolean m212a(TrackedTargetInfo trackedTargetInfo) {
        long currentTimeMillis = System.currentTimeMillis() / 1000;
        C0042a c0042a;
        if (this.f356e.containsKey(trackedTargetInfo.mTargetInfo.mTargetName)) {
            c0042a = (C0042a) this.f356e.get(trackedTargetInfo.mTargetInfo.mTargetName);
            c0042a.f347b = trackedTargetInfo.mTimeTracked;
            c0042a.f350e = trackedTargetInfo.mConfidence;
            if (currentTimeMillis - c0042a.f347b > 120) {
                c0042a.f348c = 1;
            } else {
                c0042a.f348c++;
            }
        } else {
            c0042a = new C0042a(this);
            c0042a.f350e = trackedTargetInfo.mConfidence;
            c0042a.f349d = trackedTargetInfo.mTargetInfo.mTargetName;
            c0042a.f347b = trackedTargetInfo.mTimeTracked;
            c0042a.f348c = 1;
            this.f356e.put(trackedTargetInfo.mTargetInfo.mTargetName, c0042a);
        }
        if (this.f357f == null || this.f357f.f349d == c0042a.f349d || currentTimeMillis - this.f357f.f346a >= 10) {
            return true;
        }
        Log.d("SensorImageLocalizer", "Ignoring target " + trackedTargetInfo.mTargetInfo.mTargetName + " Time diff " + (currentTimeMillis - this.f357f.f346a));
        return false;
    }

    public void onUpdate(List<TrackedTargetInfo> targetPoses) {
        Log.d("SensorImageLocalizer", "SensorImageLocalizer onUpdate");
        if (targetPoses == null || targetPoses.size() < 1) {
            Log.d("SensorImageLocalizer", "SensorImageLocalizer onUpdate NULL");
            update(null);
            return;
        }
        Object obj = null;
        double d = Double.MIN_VALUE;
        long currentTimeMillis = System.currentTimeMillis() / 1000;
        TrackedTargetInfo trackedTargetInfo = null;
        C0042a c0042a = null;
        for (TrackedTargetInfo trackedTargetInfo2 : targetPoses) {
            double d2;
            Object obj2;
            double d3;
            if (this.f354c.containsKey(trackedTargetInfo2.mTargetInfo.mTargetName)) {
                if (!m212a(trackedTargetInfo2) || trackedTargetInfo2.mConfidence <= d) {
                    Log.d("SensorImageLocalizer", "Ignoring target " + trackedTargetInfo2.mTargetInfo.mTargetName + " Confidence " + trackedTargetInfo2.mConfidence);
                } else {
                    c0042a = (C0042a) this.f356e.get(trackedTargetInfo2.mTargetInfo.mTargetName);
                    d2 = trackedTargetInfo2.mConfidence;
                    obj2 = 1;
                    Log.d("SensorImageLocalizer", "Potential target " + trackedTargetInfo2.mTargetInfo.mTargetName + " Confidence " + trackedTargetInfo2.mConfidence);
                    d3 = d2;
                    trackedTargetInfo = trackedTargetInfo2;
                    obj = obj2;
                    d = d3;
                }
            }
            TrackedTargetInfo trackedTargetInfo22 = trackedTargetInfo;
            d3 = d;
            obj2 = obj;
            d2 = d3;
            d3 = d2;
            trackedTargetInfo = trackedTargetInfo22;
            obj = obj2;
            d = d3;
        }
        if (obj == null) {
            update(null);
            return;
        }
        TargetInfo targetInfo = (TargetInfo) this.f354c.get(trackedTargetInfo.mTargetInfo.mTargetName);
        c0042a.f346a = currentTimeMillis;
        this.f357f = c0042a;
        Log.d("SensorImageLocalizer", "Selected target " + trackedTargetInfo.mTargetInfo.mTargetName + " time " + currentTimeMillis);
        MatrixD matrixD = null;
        if (this.f355d != null) {
            matrixD = this.f355d.poseMatrix.submatrix(3, 3, 0, 0);
        }
        MatrixD transpose = trackedTargetInfo.mTargetInfo.mTargetPose.poseMatrix.submatrix(3, 3, 0, 0).transpose();
        MatrixD submatrix = targetInfo.mTargetPose.poseMatrix.submatrix(3, 3, 0, 0);
        MatrixD times = Pose.makeRotationX(Math.toRadians(90.0d)).times(Pose.makeRotationY(Math.toRadians(90.0d)));
        MatrixD times2 = times.times(submatrix).times(transpose);
        if (matrixD != null) {
            matrixD = times2.times(matrixD);
        } else {
            matrixD = times2;
        }
        times2 = new MatrixD(3, 1);
        times2.data()[0][0] = targetInfo.mTargetSize.mLongSide;
        times2.data()[1][0] = targetInfo.mTargetSize.mShortSide;
        times2.data()[2][0] = 0.0d;
        MatrixD times3 = transpose.times(trackedTargetInfo.mTargetInfo.mTargetPose.getTranslationMatrix());
        MatrixD matrixD2 = new MatrixD(3, 1);
        if (this.f355d != null) {
            matrixD2 = this.f355d.getTranslationMatrix();
        }
        times = times.times(targetInfo.mTargetPose.getTranslationMatrix().subtract(submatrix.times(times3.add(transpose.times(matrixD2)).add(times2))));
        MatrixD matrixD3 = new MatrixD(3, 4);
        matrixD3.setSubmatrix(matrixD, 3, 3, 0, 0);
        matrixD3.setSubmatrix(times, 3, 1, 0, 3);
        Pose pose = new Pose(matrixD3);
        double[] anglesAroundZ = PoseUtils.getAnglesAroundZ(pose);
        Log.d("SensorImageLocalizer", String.format("POSE_HEADING: x %8.4f z %8.4f up %8.4f", new Object[]{Double.valueOf(anglesAroundZ[0]), Double.valueOf(anglesAroundZ[1]), Double.valueOf(anglesAroundZ[2])}));
        matrixD3 = pose.getTranslationMatrix();
        Log.d("SensorImageLocalizer", String.format("POSE_TRANS: x %8.4f y %8.4f z %8.4f", new Object[]{Double.valueOf(matrixD3.data()[0][0]), Double.valueOf(matrixD3.data()[1][0]), Double.valueOf(matrixD3.data()[2][0])}));
        update(pose);
    }
}
