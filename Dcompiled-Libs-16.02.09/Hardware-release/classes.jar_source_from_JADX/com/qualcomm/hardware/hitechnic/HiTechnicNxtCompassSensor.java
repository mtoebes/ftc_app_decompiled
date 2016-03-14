package com.qualcomm.hardware.hitechnic;

import com.qualcomm.robotcore.hardware.CompassSensor;
import com.qualcomm.robotcore.hardware.CompassSensor.CompassMode;
import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.I2cControllerPortDeviceImpl;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;

public class HiTechnicNxtCompassSensor extends I2cControllerPortDeviceImpl implements CompassSensor, I2cPortReadyCallback {
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
    private byte[] f37a;
    private Lock f38b;
    private byte[] f39c;
    private Lock f40d;
    private CompassMode f41e;
    private boolean f42f;
    private boolean f43g;

    public HiTechnicNxtCompassSensor(I2cController module, int physicalPort) {
        super(module, physicalPort);
        this.f41e = CompassMode.MEASUREMENT_MODE;
        this.f42f = false;
        this.f43g = false;
        finishConstruction();
    }

    protected void controllerNowArmedOrPretending() {
        this.controller.enableI2cReadMode(this.physicalPort, HEADING_WORD_LENGTH, COMPASS_BUFFER, COMPASS_BUFFER_SIZE);
        this.f37a = this.controller.getI2cReadCache(this.physicalPort);
        this.f38b = this.controller.getI2cReadCacheLock(this.physicalPort);
        this.f39c = this.controller.getI2cWriteCache(this.physicalPort);
        this.f40d = this.controller.getI2cWriteCacheLock(this.physicalPort);
        this.controller.registerForI2cPortReadyCallback(this, this.physicalPort);
    }

    public String toString() {
        return String.format("Compass: %3.1f", new Object[]{Double.valueOf(getDirection())});
    }

    public double getDirection() {
        if (this.f42f || this.f41e == CompassMode.CALIBRATION_MODE) {
            return INVALID_DIRECTION;
        }
        try {
            this.f38b.lock();
            byte[] copyOfRange = Arrays.copyOfRange(this.f37a, 7, 9);
            return (double) TypeConversion.byteArrayToShort(copyOfRange, ByteOrder.LITTLE_ENDIAN);
        } finally {
            this.f38b.unlock();
        }
    }

    public String status() {
        Object[] objArr = new Object[HEADING_WORD_LENGTH];
        objArr[0] = this.controller.getSerialNumber().toString();
        objArr[1] = Integer.valueOf(this.physicalPort);
        return String.format("NXT Compass Sensor, connected via device %s, port %d", objArr);
    }

    public void setMode(CompassMode mode) {
        if (this.f41e != mode) {
            this.f41e = mode;
            m40a();
        }
    }

    private void m40a() {
        this.f42f = true;
        byte b = this.f41e == CompassMode.CALIBRATION_MODE ? ONE_DEGREE_HEADING_ADDER : MEASUREMENT;
        this.controller.enableI2cWriteMode(this.physicalPort, HEADING_WORD_LENGTH, COMPASS_BUFFER, 1);
        try {
            this.f40d.lock();
            this.f39c[3] = b;
        } finally {
            this.f40d.unlock();
        }
    }

    private void m41b() {
        if (this.f41e == CompassMode.MEASUREMENT_MODE) {
            this.controller.enableI2cReadMode(this.physicalPort, HEADING_WORD_LENGTH, COMPASS_BUFFER, COMPASS_BUFFER_SIZE);
        }
        this.f42f = false;
    }

    public boolean calibrationFailed() {
        boolean z = false;
        if (!(this.f41e == CompassMode.CALIBRATION_MODE || this.f42f)) {
            try {
                this.f38b.lock();
                if (this.f37a[3] == 70) {
                    z = true;
                }
                this.f38b.unlock();
            } catch (Throwable th) {
                this.f38b.unlock();
            }
        }
        return z;
    }

    public void portIsReady(int port) {
        this.controller.setI2cPortActionFlag(this.physicalPort);
        this.controller.readI2cCacheFromController(this.physicalPort);
        if (this.f42f) {
            m41b();
            this.controller.writeI2cCacheToController(this.physicalPort);
            return;
        }
        this.controller.writeI2cPortFlagOnlyToController(this.physicalPort);
    }

    public String getDeviceName() {
        return "NXT Compass Sensor";
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; port " + this.physicalPort;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
