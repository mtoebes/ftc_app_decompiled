package com.qualcomm.robotcore.sensor;

public interface SensorListener<T> {
    void onUpdate(T t);
}
