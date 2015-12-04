package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.hardware.IrSeekerSensor.IrSeekerIndividualSensor;
import com.qualcomm.robotcore.hardware.IrSeekerSensor.Mode;
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
    private final DeviceInterfaceModule f166a;
    private final int f167b;
    private Mode f168c;
    private final byte[] f169d;
    private final Lock f170e;
    private double f171f;

    public ModernRoboticsI2cIrSeekerSensorV3(DeviceInterfaceModule module, int physicalPort) {
        this.I2C_ADDRESS = 56;
        this.f171f = DEFAULT_SIGNAL_DETECTED_THRESHOLD;
        this.f166a = module;
        this.f167b = physicalPort;
        this.f168c = Mode.MODE_1200HZ;
        this.f169d = this.f166a.getI2cReadCache(physicalPort);
        this.f170e = this.f166a.getI2cReadCacheLock(physicalPort);
        this.f166a.enableI2cReadMode(physicalPort, this.I2C_ADDRESS, OFFSET_1200HZ_HEADING_DATA, OFFSET_600HZ_LEFT_SIDE_RAW_DATA);
        this.f166a.setI2cPortActionFlag(physicalPort);
        this.f166a.writeI2cCacheToController(physicalPort);
        this.f166a.registerForI2cPortReadyCallback(this, physicalPort);
    }

    public void setSignalDetectedThreshold(double threshold) {
        this.f171f = threshold;
    }

    public double getSignalDetectedThreshold() {
        return this.f171f;
    }

    public void setMode(Mode mode) {
        this.f168c = mode;
    }

    public Mode getMode() {
        return this.f168c;
    }

    public boolean signalDetected() {
        return getStrength() > this.f171f;
    }

    public double getAngle() {
        int i = this.f168c == Mode.MODE_1200HZ ? OFFSET_1200HZ_HEADING_DATA : OFFSET_600HZ_HEADING_DATA;
        try {
            this.f170e.lock();
            double d = (double) this.f169d[i];
            return d;
        } finally {
            this.f170e.unlock();
        }
    }

    public double getStrength() {
        int i = this.f168c == Mode.MODE_1200HZ ? OFFSET_1200HZ_SIGNAL_STRENGTH : OFFSET_600HZ_SIGNAL_STRENGTH;
        try {
            this.f170e.lock();
            double unsignedByteToDouble = TypeConversion.unsignedByteToDouble(this.f169d[i]) / MAX_SENSOR_STRENGTH;
            return unsignedByteToDouble;
        } finally {
            this.f170e.unlock();
        }
    }

    public IrSeekerIndividualSensor[] getIndividualSensors() {
        IrSeekerIndividualSensor[] irSeekerIndividualSensorArr = new IrSeekerIndividualSensor[2];
        try {
            this.f170e.lock();
            r2 = new byte[2];
            System.arraycopy(this.f169d, this.f168c == Mode.MODE_1200HZ ? OFFSET_1200HZ_LEFT_SIDE_RAW_DATA : OFFSET_600HZ_LEFT_SIDE_RAW_DATA, r2, 0, r2.length);
            irSeekerIndividualSensorArr[0] = new IrSeekerIndividualSensor(HiTechnicNxtCompassSensor.INVALID_DIRECTION, ((double) TypeConversion.byteArrayToShort(r2, ByteOrder.LITTLE_ENDIAN)) / MAX_SENSOR_STRENGTH);
            r2 = new byte[2];
            System.arraycopy(this.f169d, this.f168c == Mode.MODE_1200HZ ? OFFSET_1200HZ_RIGHT_SIDE_RAW_DATA : OFFSET_600HZ_RIGHT_SIDE_RAW_DATA, r2, 0, r2.length);
            irSeekerIndividualSensorArr[1] = new IrSeekerIndividualSensor(1.0d, ((double) TypeConversion.byteArrayToShort(r2, ByteOrder.LITTLE_ENDIAN)) / MAX_SENSOR_STRENGTH);
            return irSeekerIndividualSensorArr;
        } finally {
            irSeekerIndividualSensorArr = this.f170e;
            irSeekerIndividualSensorArr.unlock();
        }
    }

    public void portIsReady(int port) {
        this.f166a.setI2cPortActionFlag(port);
        this.f166a.readI2cCacheFromController(port);
        this.f166a.writeI2cPortFlagOnlyToController(port);
    }

    public String getDeviceName() {
        return "Modern Robotics I2C IR Seeker Sensor";
    }

    public String getConnectionInfo() {
        return this.f166a.getConnectionInfo() + "; I2C port " + this.f167b;
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
        this.f166a.enableI2cReadMode(this.f167b, this.I2C_ADDRESS, OFFSET_1200HZ_HEADING_DATA, OFFSET_600HZ_LEFT_SIDE_RAW_DATA);
        this.f166a.setI2cPortActionFlag(this.f167b);
        this.f166a.writeI2cCacheToController(this.f167b);
        this.f166a.registerForI2cPortReadyCallback(this, this.f167b);
    }

    public int getI2cAddress() {
        return this.I2C_ADDRESS;
    }
}
