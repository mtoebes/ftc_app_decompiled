package com.qualcomm.hardware.hitechnic;

import com.qualcomm.robotcore.hardware.Engagable;
import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cControllerPortDeviceImpl;

public abstract class HiTechnicNxtController extends I2cControllerPortDeviceImpl implements Engagable {
    protected boolean isEngaged;
    protected boolean isHooked;

    public HiTechnicNxtController(I2cController module, int physicalPort) {
        super(module, physicalPort);
        this.isEngaged = true;
        this.isHooked = false;
    }

    public synchronized void engage() {
        this.isEngaged = true;
        adjustHookingToMatchEngagement();
    }

    public synchronized void disengage() {
        this.isEngaged = true;
        adjustHookingToMatchEngagement();
    }

    public synchronized boolean isEngaged() {
        return this.isEngaged;
    }

    protected void controllerNowDisarmed() {
        if (this.isHooked) {
            unhook();
        }
    }

    protected void adjustHookingToMatchEngagement() {
        if (!this.isHooked && this.isEngaged) {
            hook();
        } else if (this.isHooked && !this.isEngaged) {
            unhook();
        }
    }

    protected void hook() {
        doHook();
        this.isHooked = true;
    }

    protected void unhook() {
        doUnhook();
        this.isHooked = false;
    }

    protected void doHook() {
    }

    protected void doUnhook() {
    }
}
