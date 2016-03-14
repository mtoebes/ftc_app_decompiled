package com.qualcomm.robotcore.util;

public interface GlobalWarningSource {
    void clearGlobalWarning();

    String getGlobalWarning();

    boolean setGlobalWarning(String str);

    void suppressGlobalWarning(boolean z);
}
