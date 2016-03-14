package com.qualcomm.robotcore.eventloop;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.usb.RobotUsbModule;

public interface SyncdDevice {
    void blockUntilReady() throws RobotCoreException, InterruptedException;

    RobotUsbModule getOwner();

    boolean hasShutdownAbnormally();

    void setOwner(RobotUsbModule robotUsbModule);

    void startBlockingWork();
}
