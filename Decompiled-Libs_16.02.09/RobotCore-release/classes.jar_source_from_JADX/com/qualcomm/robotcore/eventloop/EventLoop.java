package com.qualcomm.robotcore.eventloop;

import android.hardware.usb.UsbDevice;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.usb.RobotUsbModule;
import com.qualcomm.robotcore.robocol.Command;

public interface EventLoop {
    OpModeManager getOpModeManager();

    void handleUsbModuleDetach(RobotUsbModule robotUsbModule) throws RobotCoreException, InterruptedException;

    void init(EventLoopManager eventLoopManager) throws RobotCoreException, InterruptedException;

    void loop() throws RobotCoreException, InterruptedException;

    void onUsbDeviceAttached(UsbDevice usbDevice);

    void processCommand(Command command);

    void processedRecentlyAttachedUsbDevices() throws RobotCoreException, InterruptedException;

    void teardown() throws RobotCoreException, InterruptedException;
}
