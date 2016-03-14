package com.qualcomm.robotcore.hardware;

import com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;
import com.qualcomm.robotcore.hardware.usb.RobotUsbModule;
import com.qualcomm.robotcore.hardware.usb.RobotUsbModule.ARMINGSTATE;
import com.qualcomm.robotcore.hardware.usb.RobotUsbModule.ArmingStateCallback;
import com.qualcomm.robotcore.robocol.RobocolConfig;

public abstract class I2cControllerPortDeviceImpl implements I2cControllerPortDevice, ArmingStateCallback {
    protected final I2cController controller;
    protected final int physicalPort;

    /* renamed from: com.qualcomm.robotcore.hardware.I2cControllerPortDeviceImpl.1 */
    static /* synthetic */ class C00381 {
        static final /* synthetic */ int[] f238a;

        static {
            f238a = new int[ARMINGSTATE.values().length];
            try {
                f238a[ARMINGSTATE.ARMED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f238a[ARMINGSTATE.PRETENDING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f238a[ARMINGSTATE.DISARMED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    protected I2cControllerPortDeviceImpl(I2cController controller, int physicalPort) {
        this.controller = controller;
        this.physicalPort = physicalPort;
    }

    protected void finishConstruction() {
        controllerNowArmedOrPretending();
        if (this.controller instanceof RobotUsbModule) {
            ((RobotUsbModule) this.controller).registerCallback(this);
        }
    }

    protected void controllerNowArmedOrPretending() {
    }

    protected void controllerNowDisarmed() {
    }

    public synchronized void onModuleStateChange(RobotUsbModule module, ARMINGSTATE state) {
        switch (C00381.f238a[state.ordinal()]) {
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
            case SpiSlaveResponseEvent.IO_ERROR /*2*/:
                controllerNowArmedOrPretending();
                break;
            case RobocolConfig.TTL /*3*/:
                controllerNowDisarmed();
                break;
        }
    }

    public I2cController getI2cController() {
        return this.controller;
    }

    public int getPort() {
        return this.physicalPort;
    }
}
