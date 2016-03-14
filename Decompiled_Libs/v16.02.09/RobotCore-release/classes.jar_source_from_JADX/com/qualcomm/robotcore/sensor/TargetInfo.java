package com.qualcomm.robotcore.sensor;

import com.qualcomm.robotcore.util.Pose;

public class TargetInfo {
    public String mTargetName;
    public Pose mTargetPose;
    public TargetSize mTargetSize;

    public TargetInfo(String targetName, Pose targetPose, TargetSize targetSize) {
        this.mTargetName = targetName;
        this.mTargetPose = targetPose;
        this.mTargetSize = targetSize;
    }
}
