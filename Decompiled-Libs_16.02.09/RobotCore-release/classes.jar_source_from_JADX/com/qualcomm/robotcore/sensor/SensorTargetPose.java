package com.qualcomm.robotcore.sensor;

import java.util.List;

public abstract class SensorTargetPose extends SensorBase<List<TrackedTargetInfo>> {
    public SensorTargetPose(List<SensorListener<List<TrackedTargetInfo>>> listeners) {
        super(listeners);
    }
}
