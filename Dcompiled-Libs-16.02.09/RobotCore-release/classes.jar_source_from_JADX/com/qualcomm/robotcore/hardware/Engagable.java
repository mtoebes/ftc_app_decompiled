package com.qualcomm.robotcore.hardware;

public interface Engagable {
    void disengage();

    void engage();

    boolean isEngaged();
}
