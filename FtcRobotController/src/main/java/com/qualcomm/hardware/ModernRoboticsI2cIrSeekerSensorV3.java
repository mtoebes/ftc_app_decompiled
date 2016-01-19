package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteOrder;
import java.util.concurrent.locks.Lock;

public class ModernRoboticsI2cIrSeekerSensorV3 extends IrSeekerSensor implements I2cPortReadyCallback {
    public static final int ADDRESS_MEM_START = 4;
    public static final int MEM_LENGTH = 12;

    public static final int DEFAULT_I2C_ADDRESS = 56;

    public static final double DEFAULT_SIGNAL_DETECTED_THRESHOLD = 0.00390625d;

    public static final byte INVALID_ANGLE = (byte) 0;
    public static final byte INVALID_STRENGTH = (byte) 0;

    public static final double MAX_SENSOR_STRENGTH = 256.0d;

    public static final int OFFSET_1200HZ_HEADING_DATA = 4;
    public static final int OFFSET_1200HZ_LEFT_SIDE_RAW_DATA = 8;
    public static final int OFFSET_1200HZ_RIGHT_SIDE_RAW_DATA = 10;
    public static final int OFFSET_1200HZ_SIGNAL_STRENGTH = 5;

    public static final int OFFSET_600HZ_HEADING_DATA = 6;
    public static final int OFFSET_600HZ_LEFT_SIDE_RAW_DATA = 12;
    public static final int OFFSET_600HZ_RIGHT_SIDE_RAW_DATA = 14;
    public static final int OFFSET_600HZ_SIGNAL_STRENGTH = 7;

    private final static int VERSION = 3;
    public static final byte SENSOR_COUNT = (byte) 2;

    public volatile int i2cAddress = DEFAULT_I2C_ADDRESS;
    private Mode mode = Mode.MODE_1200HZ;
    private double signalDetectedThreshold = DEFAULT_SIGNAL_DETECTED_THRESHOLD;

    private final DeviceInterfaceModule deviceInterfaceModule;
    private final int physicalPort;
    private final byte[] readCache;
    private final Lock readCacheLock;

    public ModernRoboticsI2cIrSeekerSensorV3(DeviceInterfaceModule module, int physicalPort) {
        this.deviceInterfaceModule = module;
        this.physicalPort = physicalPort;
        this.readCache = this.deviceInterfaceModule.getI2cReadCache(physicalPort);
        this.readCacheLock = this.deviceInterfaceModule.getI2cReadCacheLock(physicalPort);

        this.deviceInterfaceModule.enableI2cReadMode(physicalPort, this.i2cAddress, ADDRESS_MEM_START, MEM_LENGTH);
        this.deviceInterfaceModule.setI2cPortActionFlag(physicalPort);
        this.deviceInterfaceModule.writeI2cCacheToController(physicalPort);
        this.deviceInterfaceModule.registerForI2cPortReadyCallback(this, physicalPort);
    }

    public void setSignalDetectedThreshold(double threshold) {
        this.signalDetectedThreshold = threshold;
    }

    public double getSignalDetectedThreshold() {
        return this.signalDetectedThreshold;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return this.mode;
    }

    public boolean signalDetected() {
        return getStrength() > this.signalDetectedThreshold;
    }

    public double getAngle() {
        int offsetHeadingData = getOffsetHeadingData();
        double angle = INVALID_ANGLE;
        try {
            this.readCacheLock.lock();
            angle =  this.readCache[offsetHeadingData];
        } finally {
            this.readCacheLock.unlock();
        }
        return angle;
    }

    public double getStrength() {
        int offsetSignalStrength = getOffsetSignalStrength();
        double strength = INVALID_STRENGTH;
        try {
            this.readCacheLock.lock();
            strength = TypeConversion.unsignedByteToDouble(this.readCache[offsetSignalStrength]) / MAX_SENSOR_STRENGTH;
        } finally {
            this.readCacheLock.unlock();
        }
        return strength;
    }

    public IrSeekerIndividualSensor[] getIndividualSensors() {
        IrSeekerIndividualSensor[] irSeekerIndividualSensorArr = new IrSeekerIndividualSensor[SENSOR_COUNT];
        try {
            this.readCacheLock.lock();
            for(int sensor=0;sensor<SENSOR_COUNT;sensor++) {
                double angle = getSensorAngle(sensor);
                int offsetRawData = getOffsetRawData(sensor);

                byte[] rawData = new byte[2];
                System.arraycopy(this.readCache, offsetRawData, rawData, 0, rawData.length);
                double strength = TypeConversion.byteArrayToShort(rawData, ByteOrder.LITTLE_ENDIAN) / MAX_SENSOR_STRENGTH;
                irSeekerIndividualSensorArr[sensor] = new IrSeekerIndividualSensor(angle, strength);
            }
        } finally {
            this.readCacheLock.unlock();
        }
        return irSeekerIndividualSensorArr;
    }

    public void portIsReady(int port) {
        this.deviceInterfaceModule.setI2cPortActionFlag(port);
        this.deviceInterfaceModule.readI2cCacheFromController(port);
        this.deviceInterfaceModule.writeI2cPortFlagOnlyToController(port);
    }

    public String getDeviceName() {
        return "Modern Robotics I2C IR Seeker Sensor";
    }

    public String getConnectionInfo() {
        return String.format("%s; I2C port %d", this.deviceInterfaceModule.getConnectionInfo(), this.physicalPort);
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }

    public void setI2cAddress(int newAddress) {
        IrSeekerSensor.throwIfModernRoboticsI2cAddressIsInvalid(newAddress);
        RobotLog.i(String.format("%s, just changed the I2C address. Original address: %d, new address: %d", getDeviceName(), this.i2cAddress, newAddress));
        this.i2cAddress = newAddress;
        this.deviceInterfaceModule.enableI2cReadMode(this.physicalPort, this.i2cAddress, OFFSET_1200HZ_HEADING_DATA, OFFSET_600HZ_LEFT_SIDE_RAW_DATA);
        this.deviceInterfaceModule.setI2cPortActionFlag(this.physicalPort);
        this.deviceInterfaceModule.writeI2cCacheToController(this.physicalPort);
        this.deviceInterfaceModule.registerForI2cPortReadyCallback(this, this.physicalPort);
    }

    private int getOffsetHeadingData() {
        if(mode == Mode.MODE_600HZ) {
            return OFFSET_600HZ_HEADING_DATA;
        } else {
            return OFFSET_1200HZ_HEADING_DATA;
        }
    }

    private int getOffsetSignalStrength() {
        if(mode == Mode.MODE_600HZ) {
            return OFFSET_600HZ_SIGNAL_STRENGTH;
        } else {
            return OFFSET_1200HZ_SIGNAL_STRENGTH;
        }
    }

    private int getOffsetRawData(int sensor) {
        if(mode == Mode.MODE_600HZ) {
            if(sensor == 0) {
                return OFFSET_600HZ_LEFT_SIDE_RAW_DATA;
            } else {
                return OFFSET_600HZ_RIGHT_SIDE_RAW_DATA;
            }
        } else {
            if(sensor == 0) {
                return OFFSET_1200HZ_LEFT_SIDE_RAW_DATA;
            } else {
                return OFFSET_1200HZ_RIGHT_SIDE_RAW_DATA;
            }
        }
    }

    private double getSensorAngle(int sensor) {
        if(sensor == 0) {
            return -1.0d;
        } else {
            return 1.0d;
        }
    }
    public int getI2cAddress() {
        return this.i2cAddress;
    }
}
