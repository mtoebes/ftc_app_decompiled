package com.qualcomm.hardware.modernrobotics;

import android.content.Context;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDevice.CreateReadWriteRunnable;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDevice.OpenRobotUsbDevice;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyBeginEndNotifications;
import com.qualcomm.robotcore.util.SerialNumber;

public abstract class ModernRoboticsUsbI2cController extends ModernRoboticsUsbDevice implements I2cController {
    protected final I2cPortReadyBeginEndNotifications[] i2cPortReadyBeginEndCallbacks;
    protected boolean notificationsActive;
    protected final int numberOfI2cPorts;

    public ModernRoboticsUsbI2cController(int numberOfI2cPorts, Context context, SerialNumber serialNumber, EventLoopManager manager, OpenRobotUsbDevice openRobotUsbDevice, CreateReadWriteRunnable createReadWriteRunnable) throws RobotCoreException, InterruptedException {
        super(context, serialNumber, manager, openRobotUsbDevice, createReadWriteRunnable);
        this.numberOfI2cPorts = numberOfI2cPorts;
        this.i2cPortReadyBeginEndCallbacks = new I2cPortReadyBeginEndNotifications[numberOfI2cPorts];
        this.notificationsActive = false;
    }

    protected void throwIfI2cPortIsInvalid(int port) {
        if (port < 0 || port >= this.numberOfI2cPorts) {
            throw new IllegalArgumentException(String.format("port %d is invalid; valid ports are %d..%d", new Object[]{Integer.valueOf(port), Integer.valueOf(0), Integer.valueOf(this.numberOfI2cPorts - 1)}));
        }
    }

    public synchronized void registerForPortReadyBeginEndCallback(I2cPortReadyBeginEndNotifications callback, int port) {
        throwIfI2cPortIsInvalid(port);
        if (callback == null) {
            throw new IllegalArgumentException(String.format("illegal null: registerForI2cNotificationsCallback(null,%d)", new Object[]{Integer.valueOf(port)}));
        }
        deregisterForPortReadyBeginEndCallback(port);
        this.i2cPortReadyBeginEndCallbacks[port] = callback;
        if (this.notificationsActive) {
            try {
                callback.onPortIsReadyCallbacksBegin(port);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public synchronized I2cPortReadyBeginEndNotifications getPortReadyBeginEndCallback(int port) {
        throwIfI2cPortIsInvalid(port);
        return this.i2cPortReadyBeginEndCallbacks[port];
    }

    public synchronized void deregisterForPortReadyBeginEndCallback(int port) {
        throwIfI2cPortIsInvalid(port);
        if (this.i2cPortReadyBeginEndCallbacks[port] != null) {
            try {
                this.i2cPortReadyBeginEndCallbacks[port].onPortIsReadyCallbacksEnd(port);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        this.i2cPortReadyBeginEndCallbacks[port] = null;
    }

    public void startupComplete() throws InterruptedException {
        this.notificationsActive = true;
        if (this.i2cPortReadyBeginEndCallbacks != null) {
            for (int i = 0; i < this.numberOfI2cPorts; i++) {
                I2cPortReadyBeginEndNotifications i2cPortReadyBeginEndNotifications = this.i2cPortReadyBeginEndCallbacks[i];
                if (i2cPortReadyBeginEndNotifications != null) {
                    i2cPortReadyBeginEndNotifications.onPortIsReadyCallbacksBegin(i);
                }
            }
        }
    }

    public void shutdownComplete() throws InterruptedException {
        if (this.i2cPortReadyBeginEndCallbacks != null) {
            for (int i = 0; i < this.numberOfI2cPorts; i++) {
                I2cPortReadyBeginEndNotifications i2cPortReadyBeginEndNotifications = this.i2cPortReadyBeginEndCallbacks[i];
                if (i2cPortReadyBeginEndNotifications != null) {
                    i2cPortReadyBeginEndNotifications.onPortIsReadyCallbacksEnd(i);
                }
            }
        }
        this.notificationsActive = false;
    }
}
