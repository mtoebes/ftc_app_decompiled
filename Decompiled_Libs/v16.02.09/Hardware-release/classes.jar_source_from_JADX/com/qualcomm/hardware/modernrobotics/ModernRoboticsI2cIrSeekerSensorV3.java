package com.qualcomm.hardware.modernrobotics;

import com.qualcomm.hardware.hitechnic.HiTechnicNxtCompassSensor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.I2cControllerPortDeviceImpl;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.hardware.IrSeekerSensor.IrSeekerIndividualSensor;
import com.qualcomm.robotcore.hardware.IrSeekerSensor.Mode;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteOrder;
import java.util.concurrent.locks.Lock;

public class ModernRoboticsI2cIrSeekerSensorV3 extends I2cControllerPortDeviceImpl implements I2cPortReadyCallback, IrSeekerSensor {
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
    private Mode f147a;
    private byte[] f148b;
    private Lock f149c;
    private double f150d;

    public ModernRoboticsI2cIrSeekerSensorV3(DeviceInterfaceModule module, int physicalPort) {
        super(module, physicalPort);
        this.I2C_ADDRESS = 56;
        this.f150d = DEFAULT_SIGNAL_DETECTED_THRESHOLD;
        this.f147a = Mode.MODE_1200HZ;
        finishConstruction();
    }

    protected void controllerNowArmedOrPretending() {
        this.f148b = this.controller.getI2cReadCache(this.physicalPort);
        this.f149c = this.controller.getI2cReadCacheLock(this.physicalPort);
        this.controller.enableI2cReadMode(this.physicalPort, this.I2C_ADDRESS, OFFSET_1200HZ_HEADING_DATA, OFFSET_600HZ_LEFT_SIDE_RAW_DATA);
        this.controller.setI2cPortActionFlag(this.physicalPort);
        this.controller.writeI2cCacheToController(this.physicalPort);
        this.controller.registerForI2cPortReadyCallback(this, this.physicalPort);
    }

    public String toString() {
        if (!signalDetected()) {
            return "IR Seeker:  --% signal at  ---.- degrees";
        }
        return String.format("IR Seeker: %3.0f%% signal at %6.1f degrees", new Object[]{Double.valueOf(getStrength() * 100.0d), Double.valueOf(getAngle())});
    }

    public void setSignalDetectedThreshold(double threshold) {
        this.f150d = threshold;
    }

    public double getSignalDetectedThreshold() {
        return this.f150d;
    }

    public void setMode(Mode mode) {
        this.f147a = mode;
    }

    public Mode getMode() {
        return this.f147a;
    }

    public boolean signalDetected() {
        return getStrength() > this.f150d;
    }

    public double getAngle() {
        int i = this.f147a == Mode.MODE_1200HZ ? OFFSET_1200HZ_HEADING_DATA : OFFSET_600HZ_HEADING_DATA;
        try {
            this.f149c.lock();
            double d = (double) this.f148b[i];
            return d;
        } finally {
            this.f149c.unlock();
        }
    }

    public double getStrength() {
        int i = this.f147a == Mode.MODE_1200HZ ? OFFSET_1200HZ_SIGNAL_STRENGTH : OFFSET_600HZ_SIGNAL_STRENGTH;
        try {
            this.f149c.lock();
            double unsignedByteToDouble = TypeConversion.unsignedByteToDouble(this.f148b[i]) / MAX_SENSOR_STRENGTH;
            return unsignedByteToDouble;
        } finally {
            this.f149c.unlock();
        }
    }

    public IrSeekerIndividualSensor[] getIndividualSensors() {
        IrSeekerIndividualSensor[] irSeekerIndividualSensorArr = new IrSeekerIndividualSensor[2];
        try {
            this.f149c.lock();
            r2 = new byte[2];
            System.arraycopy(this.f148b, this.f147a == Mode.MODE_1200HZ ? OFFSET_1200HZ_LEFT_SIDE_RAW_DATA : OFFSET_600HZ_LEFT_SIDE_RAW_DATA, r2, 0, r2.length);
            irSeekerIndividualSensorArr[0] = new IrSeekerIndividualSensor(HiTechnicNxtCompassSensor.INVALID_DIRECTION, ((double) TypeConversion.byteArrayToShort(r2, ByteOrder.LITTLE_ENDIAN)) / MAX_SENSOR_STRENGTH);
            r2 = new byte[2];
            System.arraycopy(this.f148b, this.f147a == Mode.MODE_1200HZ ? OFFSET_1200HZ_RIGHT_SIDE_RAW_DATA : OFFSET_600HZ_RIGHT_SIDE_RAW_DATA, r2, 0, r2.length);
            irSeekerIndividualSensorArr[1] = new IrSeekerIndividualSensor(1.0d, ((double) TypeConversion.byteArrayToShort(r2, ByteOrder.LITTLE_ENDIAN)) / MAX_SENSOR_STRENGTH);
            return irSeekerIndividualSensorArr;
        } finally {
            irSeekerIndividualSensorArr = this.f149c;
            irSeekerIndividualSensorArr.unlock();
        }
    }

    public void portIsReady(int port) {
        this.controller.setI2cPortActionFlag(port);
        this.controller.readI2cCacheFromController(port);
        this.controller.writeI2cPortFlagOnlyToController(port);
    }

    public String getDeviceName() {
        return "Modern Robotics I2C IR Seeker Sensor";
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; I2C port " + this.physicalPort;
    }

    public int getVersion() {
        return 3;
    }

    public void close() {
    }

    public void setI2cAddress(int newAddress) {
        ModernRoboticsUsbDeviceInterfaceModule.throwIfModernRoboticsI2cAddressIsInvalid(newAddress);
        RobotLog.i(getDeviceName() + ", just changed the I2C address. Original address: " + this.I2C_ADDRESS + ", new address: " + newAddress);
        this.I2C_ADDRESS = newAddress;
        this.controller.enableI2cReadMode(this.physicalPort, this.I2C_ADDRESS, OFFSET_1200HZ_HEADING_DATA, OFFSET_600HZ_LEFT_SIDE_RAW_DATA);
        this.controller.setI2cPortActionFlag(this.physicalPort);
        this.controller.writeI2cCacheToController(this.physicalPort);
        this.controller.registerForI2cPortReadyCallback(this, this.physicalPort);
    }

    public int getI2cAddress() {
        return this.I2C_ADDRESS;
    }
}
