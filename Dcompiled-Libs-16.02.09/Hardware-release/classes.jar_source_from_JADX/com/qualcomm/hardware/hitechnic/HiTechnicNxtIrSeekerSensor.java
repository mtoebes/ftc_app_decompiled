package com.qualcomm.hardware.hitechnic;

import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.I2cControllerPortDeviceImpl;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.hardware.IrSeekerSensor.IrSeekerIndividualSensor;
import com.qualcomm.robotcore.hardware.IrSeekerSensor.Mode;
import com.qualcomm.robotcore.util.TypeConversion;
import java.util.concurrent.locks.Lock;

public class HiTechnicNxtIrSeekerSensor extends I2cControllerPortDeviceImpl implements I2cPortReadyCallback, IrSeekerSensor {
    public static final double DEFAULT_SIGNAL_DETECTED_THRESHOLD = 0.00390625d;
    public static final byte DIRECTION = (byte) 4;
    public static final double[] DIRECTION_TO_ANGLE;
    public static final int I2C_ADDRESS = 16;
    public static final byte INVALID_ANGLE = (byte) 0;
    public static final byte MAX_ANGLE = (byte) 9;
    public static final double MAX_SENSOR_STRENGTH = 256.0d;
    public static final int MEM_AC_START_ADDRESS = 73;
    public static final int MEM_DC_START_ADDRESS = 66;
    public static final int MEM_MODE_ADDRESS = 65;
    public static final int MEM_READ_LENGTH = 6;
    public static final byte MIN_ANGLE = (byte) 1;
    public static final byte MODE_AC = (byte) 0;
    public static final byte MODE_DC = (byte) 2;
    public static final byte SENSOR_COUNT = (byte) 9;
    public static final byte SENSOR_FIRST = (byte) 5;
    private byte[] f53a;
    private Lock f54b;
    private byte[] f55c;
    private Lock f56d;
    private Mode f57e;
    private double f58f;
    private volatile boolean f59g;

    static {
        DIRECTION_TO_ANGLE = new double[]{0.0d, -120.0d, -90.0d, -60.0d, -30.0d, 0.0d, 30.0d, 60.0d, 90.0d, HiTechnicNxtLightSensor.MIN_LIGHT_VALUE};
    }

    public HiTechnicNxtIrSeekerSensor(I2cController module, int physicalPort) {
        super(module, physicalPort);
        this.f58f = DEFAULT_SIGNAL_DETECTED_THRESHOLD;
        this.f57e = Mode.MODE_1200HZ;
        finishConstruction();
    }

    protected void controllerNowArmedOrPretending() {
        this.f53a = this.controller.getI2cReadCache(this.physicalPort);
        this.f54b = this.controller.getI2cReadCacheLock(this.physicalPort);
        this.f55c = this.controller.getI2cWriteCache(this.physicalPort);
        this.f56d = this.controller.getI2cWriteCacheLock(this.physicalPort);
        this.controller.registerForI2cPortReadyCallback(this, this.physicalPort);
        this.f59g = true;
    }

    public String toString() {
        if (!signalDetected()) {
            return "IR Seeker:  --% signal at  ---.- degrees";
        }
        return String.format("IR Seeker: %3.0f%% signal at %6.1f degrees", new Object[]{Double.valueOf(getStrength() * 100.0d), Double.valueOf(getAngle())});
    }

    public void setSignalDetectedThreshold(double threshold) {
        this.f58f = threshold;
    }

    public double getSignalDetectedThreshold() {
        return this.f58f;
    }

    public void setMode(Mode mode) {
        if (this.f57e != mode) {
            this.f57e = mode;
            m46a();
        }
    }

    public Mode getMode() {
        return this.f57e;
    }

    public boolean signalDetected() {
        boolean z = true;
        if (this.f59g) {
            return false;
        }
        try {
            this.f54b.lock();
            boolean z2 = this.f53a[4] != null;
            this.f54b.unlock();
            if (!z2 || getStrength() <= this.f58f) {
                z = false;
            }
            return z;
        } catch (Throwable th) {
            this.f54b.unlock();
        }
    }

    public double getAngle() {
        double d = 0.0d;
        if (!this.f59g) {
            try {
                this.f54b.lock();
                if (this.f53a[4] >= 1 && this.f53a[4] <= 9) {
                    d = DIRECTION_TO_ANGLE[this.f53a[4]];
                }
                this.f54b.unlock();
            } catch (Throwable th) {
                this.f54b.unlock();
            }
        }
        return d;
    }

    public double getStrength() {
        double d = 0.0d;
        if (!this.f59g) {
            try {
                this.f54b.lock();
                int i = 0;
                while (i < 9) {
                    double max = Math.max(d, m45a(this.f53a, i));
                    i++;
                    d = max;
                }
            } finally {
                this.f54b.unlock();
            }
        }
        return d;
    }

    public IrSeekerIndividualSensor[] getIndividualSensors() {
        IrSeekerIndividualSensor[] irSeekerIndividualSensorArr = new IrSeekerIndividualSensor[9];
        if (!this.f59g) {
            try {
                this.f54b.lock();
                for (int i = 0; i < 9; i++) {
                    irSeekerIndividualSensorArr[i] = new IrSeekerIndividualSensor(DIRECTION_TO_ANGLE[(i * 2) + 1], m45a(this.f53a, i));
                }
            } finally {
                this.f54b.unlock();
            }
        }
        return irSeekerIndividualSensorArr;
    }

    public void setI2cAddress(int newAddress) {
        throw new UnsupportedOperationException("This method is not supported.");
    }

    public int getI2cAddress() {
        return I2C_ADDRESS;
    }

    private void m46a() {
        this.f59g = true;
        byte b = this.f57e == Mode.MODE_600HZ ? MODE_DC : MODE_AC;
        this.controller.enableI2cWriteMode(this.physicalPort, I2C_ADDRESS, MEM_MODE_ADDRESS, 1);
        try {
            this.f56d.lock();
            this.f55c[4] = b;
        } finally {
            this.f56d.unlock();
        }
    }

    private double m45a(byte[] bArr, int i) {
        return TypeConversion.unsignedByteToDouble(bArr[i + 5]) / MAX_SENSOR_STRENGTH;
    }

    public void portIsReady(int port) {
        this.controller.setI2cPortActionFlag(this.physicalPort);
        this.controller.readI2cCacheFromController(this.physicalPort);
        if (this.f59g) {
            if (this.f57e == Mode.MODE_600HZ) {
                this.controller.enableI2cReadMode(this.physicalPort, I2C_ADDRESS, MEM_DC_START_ADDRESS, MEM_READ_LENGTH);
            } else {
                this.controller.enableI2cReadMode(this.physicalPort, I2C_ADDRESS, MEM_AC_START_ADDRESS, MEM_READ_LENGTH);
            }
            this.controller.writeI2cCacheToController(this.physicalPort);
            this.f59g = false;
            return;
        }
        this.controller.writeI2cPortFlagOnlyToController(this.physicalPort);
    }

    public String getDeviceName() {
        return "NXT IR Seeker Sensor";
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; port " + this.physicalPort;
    }

    public int getVersion() {
        return 2;
    }

    public void close() {
    }
}
