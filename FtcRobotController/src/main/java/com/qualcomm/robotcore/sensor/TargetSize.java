package com.qualcomm.robotcore.sensor;

public class TargetSize {
    public double mLongSide;
    public double mShortSide;
    public String mTargetName;

    public TargetSize() {
        this("", 0.0d, 0.0d);
    }

    public TargetSize(String targetName, double longSide, double shortSide) {
        this.mTargetName = targetName;
        this.mLongSide = longSide;
        this.mShortSide = shortSide;
    }
}
