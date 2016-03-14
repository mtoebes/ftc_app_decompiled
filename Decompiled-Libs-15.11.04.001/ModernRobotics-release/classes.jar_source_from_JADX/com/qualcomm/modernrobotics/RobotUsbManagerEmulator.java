package com.qualcomm.modernrobotics;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.hardware.usb.RobotUsbManager;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;
import java.util.ArrayList;
import java.util.Iterator;

public class RobotUsbManagerEmulator implements RobotUsbManager {
    private ArrayList<C0002b> f1a;

    public RobotUsbManagerEmulator() {
        this.f1a = new ArrayList();
    }

    public int scanForDevices() throws RobotCoreException {
        return this.f1a.size();
    }

    public SerialNumber getDeviceSerialNumberByIndex(int index) throws RobotCoreException {
        return ((C0002b) this.f1a.get(index)).f5b;
    }

    public String getDeviceDescriptionByIndex(int index) throws RobotCoreException {
        return ((C0002b) this.f1a.get(index)).f6c;
    }

    public RobotUsbDevice openBySerialNumber(SerialNumber serialNumber) throws RobotCoreException {
        RobotLog.d("attempting to open emulated device " + serialNumber);
        Iterator it = this.f1a.iterator();
        while (it.hasNext()) {
            C0002b c0002b = (C0002b) it.next();
            if (c0002b.f5b.equals(serialNumber)) {
                return c0002b;
            }
        }
        throw new RobotCoreException("cannot open device - could not find device with serial number " + serialNumber);
    }
}
