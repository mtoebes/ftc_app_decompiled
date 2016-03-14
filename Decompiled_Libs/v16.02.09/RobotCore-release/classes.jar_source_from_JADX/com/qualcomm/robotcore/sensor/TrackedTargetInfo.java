package com.qualcomm.robotcore.sensor;

public class TrackedTargetInfo {
    public double mConfidence;
    public TargetInfo mTargetInfo;
    public long mTimeTracked;

    public TrackedTargetInfo(TargetInfo targetInfo, double reProjectionError, long timeTracked) {
        this.mTargetInfo = targetInfo;
        this.mConfidence = reProjectionError;
        this.mTimeTracked = timeTracked;
    }
}
