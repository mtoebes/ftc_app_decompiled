package com.qualcomm.modernrobotics;

import android.content.Context;
import com.qualcomm.analytics.Analytics;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DeviceManager.DeviceType;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice.Channel;
import com.qualcomm.robotcore.hardware.usb.RobotUsbManager;
import com.qualcomm.robotcore.util.SerialNumber;

public class ModernRoboticsUsbUtil {
    public static final int DEVICE_ID_DC_MOTOR_CONTROLLER = 77;
    public static final int DEVICE_ID_DEVICE_INTERFACE_MODULE = 65;
    public static final int DEVICE_ID_LEGACY_MODULE = 73;
    public static final int DEVICE_ID_SERVO_CONTROLLER = 83;
    public static final int MFG_CODE_MODERN_ROBOTICS = 77;
    private static Analytics f0a;

    public static void init(Context context, HardwareMap map) {
        if (f0a == null) {
            f0a = new Analytics(context, "update_rc", map);
        }
    }

    public static RobotUsbDevice openUsbDevice(RobotUsbManager usbManager, SerialNumber serialNumber) throws RobotCoreException {
        return m1a(usbManager, serialNumber);
    }

    private static RobotUsbDevice m1a(RobotUsbManager robotUsbManager, SerialNumber serialNumber) throws RobotCoreException {
        String str;
        RobotUsbDevice openBySerialNumber;
        Object obj = null;
        String str2 = BuildConfig.VERSION_NAME;
        int scanForDevices = robotUsbManager.scanForDevices();
        for (int i = 0; i < scanForDevices; i++) {
            if (serialNumber.equals(robotUsbManager.getDeviceSerialNumberByIndex(i))) {
                obj = 1;
                str = robotUsbManager.getDeviceDescriptionByIndex(i) + " [" + serialNumber.getSerialNumber() + "]";
                break;
            }
        }
        str = str2;
        if (obj == null) {
            m2a("unable to find USB device with serial number " + serialNumber.toString());
        }
        RobotUsbDevice robotUsbDevice = null;
        try {
            openBySerialNumber = robotUsbManager.openBySerialNumber(serialNumber);
        } catch (RobotCoreException e) {
            m2a("Unable to open USB device " + serialNumber + " - " + str + ": " + e.getMessage());
            openBySerialNumber = robotUsbDevice;
        }
        try {
            openBySerialNumber.setBaudRate(250000);
            openBySerialNumber.setDataCharacteristics((byte) 8, (byte) 0, (byte) 0);
            openBySerialNumber.setLatencyTimer(2);
        } catch (RobotCoreException e2) {
            openBySerialNumber.close();
            m2a("Unable to open USB device " + serialNumber + " - " + str + ": " + e2.getMessage());
        }
        try {
            Thread.sleep(400);
        } catch (InterruptedException e3) {
        }
        return openBySerialNumber;
    }

    public static byte[] getUsbDeviceHeader(RobotUsbDevice dev) throws RobotCoreException {
        return m3a(dev);
    }

    private static byte[] m3a(RobotUsbDevice robotUsbDevice) throws RobotCoreException {
        r1 = new byte[5];
        r0 = new byte[3];
        byte[] bArr = new byte[]{(byte) 85, (byte) -86, Byte.MIN_VALUE, (byte) 0, (byte) 3};
        try {
            robotUsbDevice.purge(Channel.RX);
            robotUsbDevice.write(bArr);
            robotUsbDevice.read(r1);
        } catch (RobotCoreException e) {
            m2a("error reading Modern Robotics USB device headers");
        }
        if (C0000a.m7a(r1, 3)) {
            robotUsbDevice.read(r0);
        }
        return r0;
    }

    public static DeviceType getDeviceType(byte[] deviceHeader) {
        return m0a(deviceHeader);
    }

    private static DeviceType m0a(byte[] bArr) {
        if (bArr[1] != MFG_CODE_MODERN_ROBOTICS) {
            return DeviceType.FTDI_USB_UNKNOWN_DEVICE;
        }
        switch (bArr[2]) {
            case DEVICE_ID_DEVICE_INTERFACE_MODULE /*65*/:
                return DeviceType.MODERN_ROBOTICS_USB_DEVICE_INTERFACE_MODULE;
            case DEVICE_ID_LEGACY_MODULE /*73*/:
                return DeviceType.MODERN_ROBOTICS_USB_LEGACY_MODULE;
            case MFG_CODE_MODERN_ROBOTICS /*77*/:
                return DeviceType.MODERN_ROBOTICS_USB_DC_MOTOR_CONTROLLER;
            case DEVICE_ID_SERVO_CONTROLLER /*83*/:
                return DeviceType.MODERN_ROBOTICS_USB_SERVO_CONTROLLER;
            default:
                return DeviceType.MODERN_ROBOTICS_USB_UNKNOWN_DEVICE;
        }
    }

    private static void m2a(String str) throws RobotCoreException {
        System.err.println(str);
        throw new RobotCoreException(str);
    }
}
