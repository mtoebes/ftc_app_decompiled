package com.qualcomm.robotcore.hardware.usb;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.util.SerialNumber;

public interface RobotUsbModule {

    public interface ArmingStateCallback {
        void onModuleStateChange(RobotUsbModule robotUsbModule, ARMINGSTATE armingstate);
    }

    public enum ARMINGSTATE {
        ARMED,
        PRETENDING,
        DISARMED,
        CLOSED,
        TO_ARMED,
        TO_PRETENDING,
        TO_DISARMED
    }

    void arm() throws RobotCoreException, InterruptedException;

    void armOrPretend() throws RobotCoreException, InterruptedException;

    void close();

    void disarm() throws RobotCoreException, InterruptedException;

    ARMINGSTATE getArmingState();

    SerialNumber getSerialNumber();

    void pretend() throws RobotCoreException, InterruptedException;

    void registerCallback(ArmingStateCallback armingStateCallback);

    void unregisterCallback(ArmingStateCallback armingStateCallback);
}
