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
    public static final double DEFAULT_SIGNAL_DETECTED_THRESHOLD = 0.00390625d;
    public static final byte INVALID_ANGLE = (byte) 0;
    public static final double MAX_SENSOR_STRENGTH = 256.0d;
    public static final int MEM_LENGTH = 12;
    public static final int OFFSET_1200HZ_HEADING_DATA = 4;
    public static final int OFFSET_1200HZ_LEFT_SIDE_RAW_DATA = 8;
    public static final int OFFSET_1200HZ_RIGHT_SIDE_RAW_DATA = 10;
    public static final int OFFSET_1200HZ_SIGNAL_STRENGTH = 5;
    public static final int OFFSET_600HZ_HEADING_DATA = 6;
    public static final int OFFSET_600HZ_LEFT_SIDE_RAW_DATA = 12;
    public static final int OFFSET_600HZ_RIGHT_SIDE_RAW_DATA = 14;
    public static final int OFFSET_600HZ_SIGNAL_STRENGTH = 7;
    public static final byte SENSOR_COUNT = (byte) 2;
    public volatile int I2C_ADDRESS;
    private final DeviceInterfaceModule deviceInterfaceModule;
    private final int physicalPort;
    private Mode mode;
    private final byte[] readCache;
    private final Lock readCacheLock;
    private double signalDetectedThreshold;

    public ModernRoboticsI2cIrSeekerSensorV3(DeviceInterfaceModule module, int physicalPort) {
        this.I2C_ADDRESS = 56;
        this.signalDetectedThreshold = DEFAULT_SIGNAL_DETECTED_THRESHOLD;
        this.deviceInterfaceModule = module;
        this.physicalPort = physicalPort;
        this.mode = Mode.MODE_1200HZ;
        this.readCache = this.deviceInterfaceModule.getI2cReadCache(physicalPort);
        this.readCacheLock = this.deviceInterfaceModule.getI2cReadCacheLock(physicalPort);
        this.deviceInterfaceModule.enableI2cReadMode(physicalPort, this.I2C_ADDRESS, OFFSET_1200HZ_HEADING_DATA, OFFSET_600HZ_LEFT_SIDE_RAW_DATA);
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
        int offsetHeadingData = this.mode == Mode.MODE_1200HZ ? OFFSET_1200HZ_HEADING_DATA : OFFSET_600HZ_HEADING_DATA;
        try {
            this.readCacheLock.lock();
            return (double) this.readCache[offsetHeadingData];
        } finally {
            this.readCacheLock.unlock();
        }
    }

    public double getStrength() {
        int offsetSignalStrength = this.mode == Mode.MODE_1200HZ ? OFFSET_1200HZ_SIGNAL_STRENGTH : OFFSET_600HZ_SIGNAL_STRENGTH;
        try {
            this.readCacheLock.lock();
            return TypeConversion.unsignedByteToDouble(this.readCache[offsetSignalStrength]) / MAX_SENSOR_STRENGTH;
        } finally {
            this.readCacheLock.unlock();
        }
    }

    public IrSeekerIndividualSensor[] getIndividualSensors() {
        IrSeekerIndividualSensor[] irSeekerIndividualSensorArr = new IrSeekerIndividualSensor[2];
        try {
            this.readCacheLock.lock();
            byte[] rawData = new byte[2];
            System.arraycopy(this.readCache, this.mode == Mode.MODE_1200HZ ? OFFSET_1200HZ_LEFT_SIDE_RAW_DATA : OFFSET_600HZ_LEFT_SIDE_RAW_DATA, rawData, 0, rawData.length);
            irSeekerIndividualSensorArr[0] = new IrSeekerIndividualSensor(HiTechnicNxtCompassSensor.INVALID_DIRECTION, ((double) TypeConversion.byteArrayToShort(rawData, ByteOrder.LITTLE_ENDIAN)) / MAX_SENSOR_STRENGTH);
            rawData = new byte[2];
            System.arraycopy(this.readCache, this.mode == Mode.MODE_1200HZ ? OFFSET_1200HZ_RIGHT_SIDE_RAW_DATA : OFFSET_600HZ_RIGHT_SIDE_RAW_DATA, rawData, 0, rawData.length);
            irSeekerIndividualSensorArr[1] = new IrSeekerIndividualSensor(1.0d, ((double) TypeConversion.byteArrayToShort(rawData, ByteOrder.LITTLE_ENDIAN)) / MAX_SENSOR_STRENGTH);
            return irSeekerIndividualSensorArr;
        } finally {
            Lock lock = this.readCacheLock;
            lock.unlock();
        }
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
        return this.deviceInterfaceModule.getConnectionInfo() + "; I2C port " + this.physicalPort;
    }

    public int getVersion() {
        return 3;
    }

    public void close() {
    }

    public void setI2cAddress(int newAddress) {
        IrSeekerSensor.throwIfModernRoboticsI2cAddressIsInvalid(newAddress);
        RobotLog.i(getDeviceName() + ", just changed the I2C address. Original address: " + this.I2C_ADDRESS + ", new address: " + newAddress);
        this.I2C_ADDRESS = newAddress;
        this.deviceInterfaceModule.enableI2cReadMode(this.physicalPort, this.I2C_ADDRESS, OFFSET_1200HZ_HEADING_DATA, OFFSET_600HZ_LEFT_SIDE_RAW_DATA);
        this.deviceInterfaceModule.setI2cPortActionFlag(this.physicalPort);
        this.deviceInterfaceModule.writeI2cCacheToController(this.physicalPort);
        this.deviceInterfaceModule.registerForI2cPortReadyCallback(this, this.physicalPort);
    }

    public int getI2cAddress() {
        return this.I2C_ADDRESS;
    }
}
