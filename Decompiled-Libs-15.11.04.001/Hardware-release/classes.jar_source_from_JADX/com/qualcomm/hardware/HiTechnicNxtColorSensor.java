package com.qualcomm.hardware;

import android.graphics.Color;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.util.TypeConversion;
import java.util.concurrent.locks.Lock;

public class HiTechnicNxtColorSensor extends ColorSensor implements I2cPortReadyCallback {
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
    private final LegacyModule f26a;
    private final byte[] f27b;
    private final Lock f28c;
    private final byte[] f29d;
    private final Lock f30e;
    private C0003a f31f;
    private volatile int f32g;
    private final int f33h;

    /* renamed from: com.qualcomm.hardware.HiTechnicNxtColorSensor.a */
    private enum C0003a {
        READING_ONLY,
        PERFORMING_WRITE,
        SWITCHING_TO_READ
    }

    HiTechnicNxtColorSensor(LegacyModule legacyModule, int physicalPort) {
        this.f31f = C0003a.READING_ONLY;
        this.f32g = COMMAND_ACTIVE_LED;
        this.f26a = legacyModule;
        this.f33h = physicalPort;
        this.f27b = legacyModule.getI2cReadCache(physicalPort);
        this.f28c = legacyModule.getI2cReadCacheLock(physicalPort);
        this.f29d = legacyModule.getI2cWriteCache(physicalPort);
        this.f30e = legacyModule.getI2cWriteCacheLock(physicalPort);
        legacyModule.enableI2cReadMode(physicalPort, ADDRESS_I2C, ADDRESS_COMMAND, OFFSET_COLOR_NUMBER);
        legacyModule.setI2cPortActionFlag(physicalPort);
        legacyModule.writeI2cCacheToController(physicalPort);
        legacyModule.registerForI2cPortReadyCallback(this, physicalPort);
    }

    public int red() {
        return m37a(OFFSET_RED_READING);
    }

    public int green() {
        return m37a(OFFSET_GREEN_READING);
    }

    public int blue() {
        return m37a(OFFSET_BLUE_READING);
    }

    public int alpha() {
        return COMMAND_ACTIVE_LED;
    }

    public int argb() {
        return Color.argb(alpha(), red(), green(), blue());
    }

    public void enableLed(boolean enable) {
        byte b = (byte) 1;
        if (enable) {
            b = (byte) 0;
        }
        if (this.f32g != b) {
            this.f32g = b;
            this.f31f = C0003a.PERFORMING_WRITE;
            try {
                this.f30e.lock();
                this.f29d[OFFSET_COMMAND] = b;
            } finally {
                this.f30e.unlock();
            }
        }
    }

    public void setI2cAddress(int newAddress) {
        throw new UnsupportedOperationException("setI2cAddress is not supported.");
    }

    public int getI2cAddress() {
        throw new UnsupportedOperationException("getI2cAddress is not supported.");
    }

    private int m37a(int i) {
        try {
            this.f28c.lock();
            byte b = this.f27b[i];
            return TypeConversion.unsignedByteToInt(b);
        } finally {
            this.f28c.unlock();
        }
    }

    public String getDeviceName() {
        return "NXT Color Sensor";
    }

    public String getConnectionInfo() {
        return this.f26a.getConnectionInfo() + "; I2C port: " + this.f33h;
    }

    public int getVersion() {
        return ADDRESS_I2C;
    }

    public void close() {
    }

    public void portIsReady(int port) {
        this.f26a.setI2cPortActionFlag(this.f33h);
        this.f26a.readI2cCacheFromController(this.f33h);
        if (this.f31f == C0003a.PERFORMING_WRITE) {
            this.f26a.enableI2cWriteMode(this.f33h, ADDRESS_I2C, ADDRESS_COMMAND, OFFSET_COLOR_NUMBER);
            this.f26a.writeI2cCacheToController(this.f33h);
            this.f31f = C0003a.SWITCHING_TO_READ;
        } else if (this.f31f == C0003a.SWITCHING_TO_READ) {
            this.f26a.enableI2cReadMode(this.f33h, ADDRESS_I2C, ADDRESS_COMMAND, OFFSET_COLOR_NUMBER);
            this.f26a.writeI2cCacheToController(this.f33h);
            this.f31f = C0003a.READING_ONLY;
        } else {
            this.f26a.writeI2cPortFlagOnlyToController(this.f33h);
        }
    }
}
