package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.util.TypeConversion;
import java.util.concurrent.locks.Lock;

class HiTechnicNxtIrSeekerSensor extends IrSeekerSensor implements I2cPortReadyCallback {
    private static final int VERSION = 2;

    private static final double DEFAULT_SIGNAL_DETECTED_THRESHOLD = 0.00390625d;
    private static final byte OFFSET_DIRECTION = (byte) 4;
    private static final int I2C_ADDRESS = 16;

    private static final byte INVALID_ANGLE = (byte) 0;
    private static final byte MIN_ANGLE = (byte) 1;
    private static final byte MAX_ANGLE = (byte) 9;

    private static final double MAX_SENSOR_STRENGTH = 256.0d;
    private static final int MEM_AC_START_ADDRESS = 73;
    private static final int MEM_DC_START_ADDRESS = 66;
    private static final int MEM_MODE_ADDRESS = 65;
    private static final int BUFFER_SIZE = 6;
    private static final byte MODE_AC = (byte) 0;
    private static final byte MODE_DC = (byte) 2;
    private static final byte SENSOR_COUNT = (byte) 9;
    private static final byte SENSOR_FIRST = (byte) 5;
    private final ModernRoboticsUsbLegacyModule legacyModule;
    private final byte[] readCache;
    private final Lock readCacheLock;
    private final byte[] writeCache;
    private final Lock writeCacheLock;
    private final int physicalPort;
    private Mode mode = Mode.MODE_1200HZ;
    private double signalDetectedThreshold = DEFAULT_SIGNAL_DETECTED_THRESHOLD;
    private volatile boolean isUnknown = true; //TODO identify this

    public HiTechnicNxtIrSeekerSensor(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        this.legacyModule = legacyModule;
        this.readCache = legacyModule.getI2cReadCache(physicalPort);
        this.readCacheLock = legacyModule.getI2cReadCacheLock(physicalPort);
        this.writeCache = legacyModule.getI2cWriteCache(physicalPort);
        this.writeCacheLock = legacyModule.getI2cWriteCacheLock(physicalPort);
        this.physicalPort = physicalPort;
        legacyModule.registerForI2cPortReadyCallback(this, physicalPort);
    }

    public void setSignalDetectedThreshold(double threshold) {
        this.signalDetectedThreshold = threshold;
    }

    public double getSignalDetectedThreshold() {
        return this.signalDetectedThreshold;
    }

    public void setMode(Mode mode) {
        if (this.mode != mode) {
            this.mode = mode;
            writeDirection();
        }
    }

    public Mode getMode() {
        return this.mode;
    }

    public boolean signalDetected() {
        boolean signalDetected = true;
        if (this.isUnknown) {
            return false;
        }
        try {
            this.readCacheLock.lock();
            int direction = this.readCache[OFFSET_DIRECTION];
            if (direction == 0 || getStrength() <= this.signalDetectedThreshold) {
                signalDetected = false;
            }
        } finally {
            this.readCacheLock.unlock();
        }
        return signalDetected;
    }

    public double getAngle() {
        double angle = 0.0d;
        if (!this.isUnknown) {
            try {
                this.readCacheLock.lock();
                int direction = this.readCache[OFFSET_DIRECTION];
                angle = directionToAngle(direction);
            } finally {
                this.readCacheLock.unlock();
            }
        }
        return angle;
    }

    public double getStrength() {
        double maxStrength = 0.0d;
        if (!this.isUnknown) {
            try {
                this.readCacheLock.lock();
                for(int sensor=0; sensor<SENSOR_COUNT; sensor++) {
                    maxStrength = Math.max(maxStrength, getSensorStrength(sensor)); //TODO
                }
            } finally {
                this.readCacheLock.unlock();
            }
        }
        return maxStrength;
    }

    public IrSeekerIndividualSensor[] getIndividualSensors() {
        IrSeekerIndividualSensor[] irSeekerIndividualSensorArr = new IrSeekerIndividualSensor[SENSOR_COUNT];
        if (!this.isUnknown) {
            try {
                this.readCacheLock.lock();
                for (int sensor = 0; sensor < SENSOR_COUNT; sensor++) {
                    double angle = directionToAngle(sensor+1);
                    double strength = getSensorStrength(sensor);
                    irSeekerIndividualSensorArr[sensor] = new IrSeekerIndividualSensor(angle, strength);
                }
            } finally {
                this.readCacheLock.unlock();
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

    private void writeDirection() {
        this.isUnknown = true;
        byte modeByte = this.mode == Mode.MODE_600HZ ? MODE_DC : MODE_AC;
        this.legacyModule.enableI2cWriteMode(this.physicalPort, I2C_ADDRESS, MEM_MODE_ADDRESS, 1);
        try {
            this.writeCacheLock.lock();
            this.writeCache[OFFSET_DIRECTION] = modeByte;
        } finally {
            this.writeCacheLock.unlock();
        }
    }

    private double getSensorStrength(int sensor) {
        return TypeConversion.unsignedByteToDouble(this.readCache[sensor + SENSOR_FIRST]) / MAX_SENSOR_STRENGTH;
    }

    public void portIsReady(int port) {
        this.legacyModule.setI2cPortActionFlag(this.physicalPort);
        this.legacyModule.readI2cCacheFromController(this.physicalPort);
        if (this.isUnknown) {
            if (this.mode == Mode.MODE_600HZ) {
                this.legacyModule.enableI2cReadMode(this.physicalPort, I2C_ADDRESS, MEM_DC_START_ADDRESS, BUFFER_SIZE);
            } else {
                this.legacyModule.enableI2cReadMode(this.physicalPort, I2C_ADDRESS, MEM_AC_START_ADDRESS, BUFFER_SIZE);
            }
            this.legacyModule.writeI2cCacheToController(this.physicalPort);
            this.isUnknown = false;
            return;
        }
        this.legacyModule.writeI2cPortFlagOnlyToController(this.physicalPort);
    }

    public String getDeviceName() {
        return "NXT IR Seeker Sensor";
    }

    public String getConnectionInfo() {
        return this.legacyModule.getConnectionInfo() + "; port " + this.physicalPort;
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }

    private double directionToAngle(int direction) {
        if(direction < MIN_ANGLE || direction > MAX_ANGLE) {
            return INVALID_ANGLE;
        } else {
            return (direction - 5) * 30;
        }
    }
}
