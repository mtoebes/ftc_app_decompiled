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
    private Context f297a;
    private D2xxManager f298b;

    public RobotUsbManagerFtdi(Context context) {
        this.f297a = context;
        try {
            this.f298b = D2xxManager.getInstance(context);
        } catch (D2xxException e) {
            RobotLog.m250e("Unable to create D2xxManager; cannot open USB devices");
        }
    }

    public int scanForDevices() throws RobotCoreException {
        return this.f298b.createDeviceInfoList(this.f297a);
    }

    public SerialNumber getDeviceSerialNumberByIndex(int index) throws RobotCoreException {
        return new SerialNumber(this.f298b.getDeviceInfoListDetail(index).serialNumber);
    }

    public String getDeviceDescriptionByIndex(int index) throws RobotCoreException {
        return this.f298b.getDeviceInfoListDetail(index).description;
    }

    public RobotUsbDevice openBySerialNumber(SerialNumber serialNumber) throws RobotCoreException {
        FT_Device openBySerialNumber = this.f298b.openBySerialNumber(this.f297a, serialNumber.toString());
        if (openBySerialNumber != null) {
            return new RobotUsbDeviceFtdi(openBySerialNumber);
        }
        throw new RobotCoreException("FTDI driver failed to open USB device with serial number " + serialNumber + " (returned null device)");
    }
}
