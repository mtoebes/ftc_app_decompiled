package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.hardware.IrSeekerSensor.IrSeekerIndividualSensor;
import com.qualcomm.robotcore.hardware.IrSeekerSensor.Mode;
import com.qualcomm.robotcore.util.TypeConversion;
import java.util.concurrent.locks.Lock;

public class HiTechnicNxtIrSeekerSensor extends IrSeekerSensor implements I2cPortReadyCallback {
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
    private final ModernRoboticsUsbLegacyModule f56a;
    private final byte[] f57b;
    private final Lock f58c;
    private final byte[] f59d;
    private final Lock f60e;
    private final int f61f;
    private Mode f62g;
    private double f63h;
    private volatile boolean f64i;

    static {
        DIRECTION_TO_ANGLE = new double[]{0.0d, -120.0d, -90.0d, -60.0d, -30.0d, 0.0d, 30.0d, 60.0d, 90.0d, 120.0d};
    }

    public HiTechnicNxtIrSeekerSensor(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        this.f63h = DEFAULT_SIGNAL_DETECTED_THRESHOLD;
        this.f56a = legacyModule;
        this.f57b = legacyModule.getI2cReadCache(physicalPort);
        this.f58c = legacyModule.getI2cReadCacheLock(physicalPort);
        this.f59d = legacyModule.getI2cWriteCache(physicalPort);
        this.f60e = legacyModule.getI2cWriteCacheLock(physicalPort);
        this.f61f = physicalPort;
        this.f62g = Mode.MODE_1200HZ;
        legacyModule.registerForI2cPortReadyCallback(this, physicalPort);
        this.f64i = true;
    }

    public void setSignalDetectedThreshold(double threshold) {
        this.f63h = threshold;
    }

    public double getSignalDetectedThreshold() {
        return this.f63h;
    }

    public void setMode(Mode mode) {
        if (this.f62g != mode) {
            this.f62g = mode;
            m44a();
        }
    }

    public Mode getMode() {
        return this.f62g;
    }

    public boolean signalDetected() {
        boolean z = true;
        if (this.f64i) {
            return false;
        }
        try {
            this.f58c.lock();
            boolean z2 = this.f57b[4] != 0; //TODO was comparing to null, investigate what byte value to compare to
            this.f58c.unlock();
            if (!z2 || getStrength() <= this.f63h) {
                z = false;
            }
            return z;
        } catch (Throwable th) {
            this.f58c.unlock();
        }
        return false; //TODO return statement was missing, need to investigate proper return value
    }

    public double getAngle() {
        double d = 0.0d;
        if (!this.f64i) {
            try {
                this.f58c.lock();
                if (this.f57b[4] >= 1 && this.f57b[4] <= 9) {
                    d = DIRECTION_TO_ANGLE[this.f57b[4]];
                }
                this.f58c.unlock();
            } catch (Throwable th) {
                this.f58c.unlock();
            }
        }
        return d;
    }

    public double getStrength() {
        double d = 0.0d;
        if (!this.f64i) {
            try {
                this.f58c.lock();
                int i = 0;
                while (i < 9) {
                    double max = Math.max(d, m43a(this.f57b, i));
                    i++;
                    d = max;
                }
            } finally {
                this.f58c.unlock();
            }
        }
        return d;
    }

    public IrSeekerIndividualSensor[] getIndividualSensors() {
        IrSeekerIndividualSensor[] irSeekerIndividualSensorArr = new IrSeekerIndividualSensor[9];
        if (!this.f64i) {
            try {
                this.f58c.lock();
                for (int i = 0; i < 9; i++) {
                    irSeekerIndividualSensorArr[i] = new IrSeekerIndividualSensor(DIRECTION_TO_ANGLE[(i * 2) + 1], m43a(this.f57b, i));
                }
            } finally {
                this.f58c.unlock();
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

    private void m44a() {
        this.f64i = true;
        byte b = this.f62g == Mode.MODE_600HZ ? MODE_DC : MODE_AC;
        this.f56a.enableI2cWriteMode(this.f61f, I2C_ADDRESS, MEM_MODE_ADDRESS, 1);
        try {
            this.f60e.lock();
            this.f59d[4] = b;
        } finally {
            this.f60e.unlock();
        }
    }

    private double m43a(byte[] bArr, int i) {
        return TypeConversion.unsignedByteToDouble(bArr[i + 5]) / MAX_SENSOR_STRENGTH;
    }

    public void portIsReady(int port) {
        this.f56a.setI2cPortActionFlag(this.f61f);
        this.f56a.readI2cCacheFromController(this.f61f);
        if (this.f64i) {
            if (this.f62g == Mode.MODE_600HZ) {
                this.f56a.enableI2cReadMode(this.f61f, I2C_ADDRESS, MEM_DC_START_ADDRESS, MEM_READ_LENGTH);
            } else {
                this.f56a.enableI2cReadMode(this.f61f, I2C_ADDRESS, MEM_AC_START_ADDRESS, MEM_READ_LENGTH);
            }
            this.f56a.writeI2cCacheToController(this.f61f);
            this.f64i = false;
            return;
        }
        this.f56a.writeI2cPortFlagOnlyToController(this.f61f);
    }

    public String getDeviceName() {
        return "NXT IR Seeker Sensor";
    }

    public String getConnectionInfo() {
        return this.f56a.getConnectionInfo() + "; port " + this.f61f;
    }

    public int getVersion() {
        return 2;
    }

    public void close() {
    }
}
