package com.qualcomm.robotcore.sensor;

import java.util.List;

public abstract class SensorBase<T> {
    protected final List<SensorListener<T>> mListeners;

    public abstract boolean initialize();

    public abstract boolean pause();

    public abstract boolean resume();

    public abstract boolean shutdown();

    public SensorBase(List<SensorListener<T>> listeners) {
        this.mListeners = listeners;
    }

    public final void update(T data) {
        synchronized (this.mListeners) {
            for (SensorListener<T> onUpdate : this.mListeners) {
                onUpdate.onUpdate(data);
            }
        }
    }
}
