package com.qualcomm.robotcore.sensor;

import java.util.List;

public abstract class SensorBase<T> {
    protected List<SensorListener<T>> mListeners;

    public abstract boolean initialize();

    public abstract boolean pause();

    public abstract boolean resume();

    public abstract boolean shutdown();

    public SensorBase(List<SensorListener<T>> listeners) {
        this.mListeners = listeners;
    }

    public final void update(T data) {
        synchronized (this.mListeners) {
            if (this.mListeners == null) {
                return;
            }
            for (SensorListener onUpdate : this.mListeners) {
                onUpdate.onUpdate(data);
            }
        }
    }
}
