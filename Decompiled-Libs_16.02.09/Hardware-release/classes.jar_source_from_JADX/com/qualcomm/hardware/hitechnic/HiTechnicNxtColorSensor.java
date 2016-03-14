package com.qualcomm.hardware.hitechnic;

import android.graphics.Color;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.I2cControllerPortDeviceImpl;
import com.qualcomm.robotcore.util.TypeConversion;
import java.util.concurrent.locks.Lock;

public class HiTechnicNxtColorSensor extends I2cControllerPortDeviceImpl implements ColorSensor, I2cPortReadyCallback {
    public static final int ADDRESS_COLOR_NUMBER = 66;
    public static final int ADDRESS_COMMAND = 65;
    public static final int ADDRESS_I2C = 2;
    public static final int BUFFER_LENGTH = 5;
    public static final int COMMAND_ACTIVE_LED = 0;
    public static final int COMMAND_PASSIVE_LED = 1;
    public static final int OFFSET_BLUE_READING = 8;
    public static final int OFFSET_COLOR_NUMBER = 5;
    public static final int OFFSET_COMMAND = 4;
    public static final int OFFSET_GREEN_READING = 7;
    public static final int OFFSET_RED_READING = 6;
    private byte[] f31a;
    private Lock f32b;
    private byte[] f33c;
    private Lock f34d;
    private C0007a f35e;
    private int f36f;

    /* renamed from: com.qualcomm.hardware.hitechnic.HiTechnicNxtColorSensor.a */
    private enum C0007a {
        READING_ONLY,
        PERFORMING_WRITE,
        SWITCHING_TO_READ
    }

    public HiTechnicNxtColorSensor(I2cController module, int physicalPort) {
        super(module, physicalPort);
        this.f35e = C0007a.READING_ONLY;
        this.f36f = COMMAND_ACTIVE_LED;
        finishConstruction();
    }

    protected void controllerNowArmedOrPretending() {
        this.f31a = this.controller.getI2cReadCache(this.physicalPort);
        this.f32b = this.controller.getI2cReadCacheLock(this.physicalPort);
        this.f33c = this.controller.getI2cWriteCache(this.physicalPort);
        this.f34d = this.controller.getI2cWriteCacheLock(this.physicalPort);
        this.controller.enableI2cReadMode(this.physicalPort, ADDRESS_I2C, ADDRESS_COMMAND, OFFSET_COLOR_NUMBER);
        this.controller.setI2cPortActionFlag(this.physicalPort);
        this.controller.writeI2cCacheToController(this.physicalPort);
        this.controller.registerForI2cPortReadyCallback(this, this.physicalPort);
    }

    public String toString() {
        Object[] objArr = new Object[COMMAND_PASSIVE_LED];
        objArr[COMMAND_ACTIVE_LED] = Integer.valueOf(argb());
        return String.format("argb: %d", objArr);
    }

    public int red() {
        return m39a(OFFSET_RED_READING);
    }

    public int green() {
        return m39a(OFFSET_GREEN_READING);
    }

    public int blue() {
        return m39a(OFFSET_BLUE_READING);
    }

    public int alpha() {
        return COMMAND_ACTIVE_LED;
    }

    public int argb() {
        return Color.argb(alpha(), red(), green(), blue());
    }

    public synchronized void enableLed(boolean enable) {
        byte b = (byte) 1;
        if (enable) {
            b = (byte) 0;
        }
        if (this.f36f != b) {
            this.f36f = b;
            this.f35e = C0007a.PERFORMING_WRITE;
            try {
                this.f34d.lock();
                this.f33c[OFFSET_COMMAND] = b;
            } finally {
                this.f34d.unlock();
            }
        }
    }

    public void setI2cAddress(int newAddress) {
        throw new UnsupportedOperationException("setI2cAddress is not supported.");
    }

    public int getI2cAddress() {
        throw new UnsupportedOperationException("getI2cAddress is not supported.");
    }

    private int m39a(int i) {
        try {
            this.f32b.lock();
            byte b = this.f31a[i];
            return TypeConversion.unsignedByteToInt(b);
        } finally {
            this.f32b.unlock();
        }
    }

    public String getDeviceName() {
        return "NXT Color Sensor";
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; I2C port: " + this.physicalPort;
    }

    public int getVersion() {
        return ADDRESS_I2C;
    }

    public void close() {
    }

    public synchronized void portIsReady(int port) {
        this.controller.setI2cPortActionFlag(this.physicalPort);
        this.controller.readI2cCacheFromController(this.physicalPort);
        if (this.f35e == C0007a.PERFORMING_WRITE) {
            this.controller.enableI2cWriteMode(this.physicalPort, ADDRESS_I2C, ADDRESS_COMMAND, OFFSET_COLOR_NUMBER);
            this.controller.writeI2cCacheToController(this.physicalPort);
            this.f35e = C0007a.SWITCHING_TO_READ;
        } else if (this.f35e == C0007a.SWITCHING_TO_READ) {
            this.controller.enableI2cReadMode(this.physicalPort, ADDRESS_I2C, ADDRESS_COMMAND, OFFSET_COLOR_NUMBER);
            this.controller.writeI2cCacheToController(this.physicalPort);
            this.f35e = C0007a.READING_ONLY;
        } else {
            this.controller.writeI2cPortFlagOnlyToController(this.physicalPort);
        }
    }
}
