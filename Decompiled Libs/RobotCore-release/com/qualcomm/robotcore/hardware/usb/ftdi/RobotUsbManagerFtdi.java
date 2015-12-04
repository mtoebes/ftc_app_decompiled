package com.qualcomm.robotcore.hardware.usb.ftdi;

import android.content.Context;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.D2xxManager.D2xxException;
import com.ftdi.j2xx.FT_Device;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.hardware.usb.RobotUsbManager;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;

public class RobotUsbManagerFtdi implements RobotUsbManager {
    private Context f302a;
    private D2xxManager f303b;

    public RobotUsbManagerFtdi(Context context) {
        this.f302a = context;
        try {
            this.f303b = D2xxManager.getInstance(context);
        } catch (D2xxException e) {
            RobotLog.m231e("Unable to create D2xxManager; cannot open USB devices");
        }
    }

    public int scanForDevices() throws RobotCoreException {
        return this.f303b.createDeviceInfoList(this.f302a);
    }

    public SerialNumber getDeviceSerialNumberByIndex(int index) throws RobotCoreException {
        return new SerialNumber(this.f303b.getDeviceInfoListDetail(index).serialNumber);
    }

    public String getDeviceDescriptionByIndex(int index) throws RobotCoreException {
        return this.f303b.getDeviceInfoListDetail(index).description;
    }

    public RobotUsbDevice openBySerialNumber(SerialNumber serialNumber) throws RobotCoreException {
        FT_Device openBySerialNumber = this.f303b.openBySerialNumber(this.f302a, serialNumber.toString());
        if (openBySerialNumber != null) {
            return new RobotUsbDeviceFtdi(openBySerialNumber);
        }
        throw new RobotCoreException("FTDI driver failed to open USB device with serial number " + serialNumber + " (returned null device)");
    }
}
