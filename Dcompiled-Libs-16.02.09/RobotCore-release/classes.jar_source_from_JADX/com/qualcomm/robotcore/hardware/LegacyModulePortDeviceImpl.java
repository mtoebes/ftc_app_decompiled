package com.qualcomm.robotcore.hardware;

import com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;
import com.qualcomm.robotcore.hardware.usb.RobotUsbModule;
import com.qualcomm.robotcore.hardware.usb.RobotUsbModule.ARMINGSTATE;
import com.qualcomm.robotcore.hardware.usb.RobotUsbModule.ArmingStateCallback;
import com.qualcomm.robotcore.robocol.RobocolConfig;

public abstract class LegacyModulePortDeviceImpl implements LegacyModulePortDevice, ArmingStateCallback {
    protected final LegacyModule module;
    protected final int physicalPort;

    /* renamed from: com.qualcomm.robotcore.hardware.LegacyModulePortDeviceImpl.1 */
    static /* synthetic */ class C00401 {
        static final /* synthetic */ int[] f246a;

        static {
            f246a = new int[ARMINGSTATE.values().length];
            try {
                f246a[ARMINGSTATE.ARMED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f246a[ARMINGSTATE.PRETENDING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f246a[ARMINGSTATE.DISARMED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    protected LegacyModulePortDeviceImpl(LegacyModule module, int physicalPort) {
        this.module = module;
        this.physicalPort = physicalPort;
    }

    protected void finishConstruction() {
        moduleNowArmedOrPretending();
        if (this.module instanceof RobotUsbModule) {
            ((RobotUsbModule) this.module).registerCallback(this);
        }
    }

    protected void moduleNowArmedOrPretending() {
    }

    protected void moduleNowDisarmed() {
    }

    public synchronized void onModuleStateChange(RobotUsbModule module, ARMINGSTATE state) {
        switch (C00401.f246a[state.ordinal()]) {
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
            case SpiSlaveResponseEvent.IO_ERROR /*2*/:
                moduleNowArmedOrPretending();
                break;
            case RobocolConfig.TTL /*3*/:
                moduleNowDisarmed();
                break;
        }
    }

    public LegacyModule getLegacyModule() {
        return this.module;
    }

    public int getPort() {
        return this.physicalPort;
    }
}
