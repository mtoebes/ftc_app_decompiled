package com.qualcomm.robotcore.hardware.usb;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.util.SerialNumber;

public interface RobotUsbManager {
    String getDeviceDescriptionByIndex(int i) throws RobotCoreException;

    SerialNumber getDeviceSerialNumberByIndex(int i) throws RobotCoreException;

    RobotUsbDevice openBySerialNumber(SerialNumber serialNumber) throws RobotCoreException;

    int scanForDevices() throws RobotCoreException;
}
