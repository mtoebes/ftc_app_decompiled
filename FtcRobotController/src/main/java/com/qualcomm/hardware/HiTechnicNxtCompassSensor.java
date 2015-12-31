package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.CompassSensor;
import com.qualcomm.robotcore.hardware.CompassSensor.CompassMode;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;

public class HiTechnicNxtCompassSensor extends CompassSensor implements I2cPortReadyCallback {
    public static final byte CALIBRATION = (byte) 67;
    public static final byte CALIBRATION_FAILURE = (byte) 70;
    public static final int COMPASS_BUFFER = 65;
    public static final int COMPASS_BUFFER_SIZE = 5;
    public static final byte DIRECTION_END = (byte) 9;
    public static final byte DIRECTION_START = (byte) 7;
    public static final byte HEADING_IN_TWO_DEGREE_INCREMENTS = (byte) 66;
    public static final int HEADING_WORD_LENGTH = 2;
    public static final byte I2C_ADDRESS = (byte) 2;
    public static final double INVALID_DIRECTION = -1.0d;
    public static final byte MEASUREMENT = (byte) 0;
    public static final byte MODE_CONTROL_ADDRESS = (byte) 65;
    public static final byte ONE_DEGREE_HEADING_ADDER = (byte) 67;
    private final ModernRoboticsUsbLegacyModule f34a;
    private final byte[] f35b;
    private final Lock f36c;
    private final byte[] f37d;
    private final Lock f38e;
    private final int f39f;
    private CompassMode f40g;
    private boolean f41h;

    public HiTechnicNxtCompassSensor(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        this.f40g = CompassMode.MEASUREMENT_MODE;
        this.f41h = false;
        boolean f42i = false;
        legacyModule.enableI2cReadMode(physicalPort, HEADING_WORD_LENGTH, COMPASS_BUFFER, COMPASS_BUFFER_SIZE);
        this.f34a = legacyModule;
        this.f35b = legacyModule.getI2cReadCache(physicalPort);
        this.f36c = legacyModule.getI2cReadCacheLock(physicalPort);
        this.f37d = legacyModule.getI2cWriteCache(physicalPort);
        this.f38e = legacyModule.getI2cWriteCacheLock(physicalPort);
        this.f39f = physicalPort;
        legacyModule.registerForI2cPortReadyCallback(this, physicalPort);
    }

    public double getDirection() {
        if (this.f41h || (this.f40g == CompassMode.CALIBRATION_MODE)) {
            return INVALID_DIRECTION;
        }
        try {
            this.f36c.lock();
            byte[] copyOfRange = Arrays.copyOfRange(this.f35b, 7, 9);
            return (double) TypeConversion.byteArrayToShort(copyOfRange, ByteOrder.LITTLE_ENDIAN);
        } finally {
            this.f36c.unlock();
        }
    }

    public String status() {
        Object[] objArr = new Object[HEADING_WORD_LENGTH];
        objArr[0] = this.f34a.getSerialNumber().toString();
        objArr[1] = this.f39f;
        return String.format("NXT Compass Sensor, connected via device %s, port %d", objArr);
    }

    public void setMode(CompassMode mode) {
        if (this.f40g != mode) {
            this.f40g = mode;
            m38a();
        }
    }

    private void m38a() {
        this.f41h = true;
        byte b = (this.f40g == CompassMode.CALIBRATION_MODE) ? ONE_DEGREE_HEADING_ADDER : MEASUREMENT;
        this.f34a.enableI2cWriteMode(this.f39f, HEADING_WORD_LENGTH, COMPASS_BUFFER, 1);
        try {
            this.f38e.lock();
            this.f37d[3] = b;
        } finally {
            this.f38e.unlock();
        }
    }

    private void m39b() {
        if (this.f40g == CompassMode.MEASUREMENT_MODE) {
            this.f34a.enableI2cReadMode(this.f39f, HEADING_WORD_LENGTH, COMPASS_BUFFER, COMPASS_BUFFER_SIZE);
        }
        this.f41h = false;
    }

    public boolean calibrationFailed() {
        boolean z = false;
        if (!((this.f40g == CompassMode.CALIBRATION_MODE) || this.f41h)) {
            try {
                this.f36c.lock();
                if (this.f35b[3] == 70) {
                    z = true;
                }
                this.f36c.unlock();
            } catch (Throwable th) {
                this.f36c.unlock();
            }
        }
        return z;
    }

    public void portIsReady(int port) {
        this.f34a.setI2cPortActionFlag(this.f39f);
        this.f34a.readI2cCacheFromController(this.f39f);
        if (this.f41h) {
            m39b();
            this.f34a.writeI2cCacheToController(this.f39f);
            return;
        }
        this.f34a.writeI2cPortFlagOnlyToController(this.f39f);
    }

    public String getDeviceName() {
        return "NXT Compass Sensor";
    }

    public String getConnectionInfo() {
        return this.f34a.getConnectionInfo() + "; port " + this.f39f;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
