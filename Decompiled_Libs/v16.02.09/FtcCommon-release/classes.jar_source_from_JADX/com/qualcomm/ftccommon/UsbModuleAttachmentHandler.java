package com.qualcomm.ftccommon;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.usb.RobotUsbModule;

public interface UsbModuleAttachmentHandler {
    void handleUsbModuleAttach(RobotUsbModule robotUsbModule) throws RobotCoreException, InterruptedException;

    void handleUsbModuleDetach(RobotUsbModule robotUsbModule) throws RobotCoreException, InterruptedException;
}
