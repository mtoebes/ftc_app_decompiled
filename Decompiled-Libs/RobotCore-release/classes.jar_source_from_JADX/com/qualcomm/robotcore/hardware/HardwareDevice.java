package com.qualcomm.robotcore.hardware;

public interface HardwareDevice {
    void close();

    String getConnectionInfo();

    String getDeviceName();

    int getVersion();
}
