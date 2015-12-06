package com.qualcomm.robotcore.hardware.usb;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.util.SerialNumber;

public interface RobotUsbManager {
    String getDeviceDescriptionByIndex(int index) throws RobotCoreException;

    SerialNumber getDeviceSerialNumberByIndex(int index) throws RobotCoreException;

    RobotUsbDevice openBySerialNumber(SerialNumber serialNumber) throws RobotCoreException;

    int scanForDevices() throws RobotCoreException;
}
