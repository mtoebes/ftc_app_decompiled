package com.ftdi.j2xx;

import com.ftdi.j2xx.D2xxManager.D2xxException;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;
import com.qualcomm.robotcore.robocol.Command;

/* renamed from: com.ftdi.j2xx.d */
class C0006d extends C0004k {
    C0006d(FT_Device fT_Device) throws D2xxException {
        super(fT_Device);
        m40a((byte) 10);
    }

    short m63a(FT_EEPROM ft_eeprom) {
        int[] iArr = new int[this.b];
        if (ft_eeprom.getClass() != FT_EEPROM_2232D.class) {
            return (short) 1;
        }
        FT_EEPROM_2232D ft_eeprom_2232d = (FT_EEPROM_2232D) ft_eeprom;
        try {
            boolean z;
            iArr[0] = 0;
            if (ft_eeprom_2232d.A_FIFO) {
                iArr[0] = iArr[0] | 1;
            } else if (ft_eeprom_2232d.A_FIFOTarget) {
                iArr[0] = iArr[0] | 2;
            } else {
                iArr[0] = iArr[0] | 4;
            }
            if (ft_eeprom_2232d.A_HighIO) {
                iArr[0] = iArr[0] | 16;
            }
            if (ft_eeprom_2232d.A_LoadVCP) {
                iArr[0] = iArr[0] | 8;
            } else if (ft_eeprom_2232d.B_FIFO) {
                iArr[0] = iArr[0] | Command.MAX_COMMAND_LENGTH;
            } else if (ft_eeprom_2232d.B_FIFOTarget) {
                iArr[0] = iArr[0] | 512;
            } else {
                iArr[0] = iArr[0] | 1024;
            }
            if (ft_eeprom_2232d.B_HighIO) {
                iArr[0] = iArr[0] | 4096;
            }
            if (ft_eeprom_2232d.B_LoadVCP) {
                iArr[0] = iArr[0] | 2048;
            }
            iArr[1] = ft_eeprom_2232d.VendorId;
            iArr[2] = ft_eeprom_2232d.ProductId;
            iArr[3] = 1280;
            iArr[4] = m41a((Object) ft_eeprom);
            iArr[4] = m54b(ft_eeprom);
            int i = 75;
            if (this.a == (short) 70) {
                i = 11;
                z = true;
            } else {
                z = false;
            }
            i = m42a(ft_eeprom_2232d.Product, iArr, m42a(ft_eeprom_2232d.Manufacturer, iArr, i, 7, z), 8, z);
            if (ft_eeprom_2232d.SerNumEnable) {
                m42a(ft_eeprom_2232d.SerialNumber, iArr, i, 9, z);
            }
            iArr[10] = this.a;
            if (iArr[1] == 0 || iArr[2] == 0) {
                return (short) 2;
            }
            if (m51a(iArr, this.b - 1)) {
                return (short) 0;
            }
            return (short) 1;
        } catch (Exception e) {
            e.printStackTrace();
            return (short) 0;
        }
    }

    FT_EEPROM m62a() {
        int i = 0;
        FT_EEPROM ft_eeprom_2232d = new FT_EEPROM_2232D();
        int[] iArr = new int[this.b];
        while (i < this.b) {
            try {
                iArr[i] = m43a((short) i);
                i++;
            } catch (Exception e) {
                return null;
            }
        }
        switch ((short) (iArr[0] & 7)) {
            case SpiSlaveResponseEvent.OK /*0*/:
                ft_eeprom_2232d.A_UART = true;
                break;
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
                ft_eeprom_2232d.A_FIFO = true;
                break;
            case SpiSlaveResponseEvent.IO_ERROR /*2*/:
                ft_eeprom_2232d.A_FIFOTarget = true;
                break;
            case FT_4222_Defines.DEBUG_REQ_READ_SFR /*4*/:
                ft_eeprom_2232d.A_FastSerial = true;
                break;
        }
        if (((short) ((iArr[0] & 8) >> 3)) == (short) 1) {
            ft_eeprom_2232d.A_LoadVCP = true;
        } else {
            ft_eeprom_2232d.A_HighIO = true;
        }
        if (((short) ((iArr[0] & 16) >> 4)) == (short) 1) {
            ft_eeprom_2232d.A_HighIO = true;
        }
        switch ((short) ((iArr[0] & 1792) >> 8)) {
            case SpiSlaveResponseEvent.OK /*0*/:
                ft_eeprom_2232d.B_UART = true;
                break;
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
                ft_eeprom_2232d.B_FIFO = true;
                break;
            case SpiSlaveResponseEvent.IO_ERROR /*2*/:
                ft_eeprom_2232d.B_FIFOTarget = true;
                break;
            case FT_4222_Defines.DEBUG_REQ_READ_SFR /*4*/:
                ft_eeprom_2232d.B_FastSerial = true;
                break;
        }
        if (((short) ((iArr[0] & 2048) >> 11)) == (short) 1) {
            ft_eeprom_2232d.B_LoadVCP = true;
        } else {
            ft_eeprom_2232d.B_LoadD2XX = true;
        }
        if (((short) ((iArr[0] & 4096) >> 12)) == (short) 1) {
            ft_eeprom_2232d.B_HighIO = true;
        }
        ft_eeprom_2232d.VendorId = (short) iArr[1];
        ft_eeprom_2232d.ProductId = (short) iArr[2];
        m48a(ft_eeprom_2232d, iArr[4]);
        i = iArr[7] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        if (this.a == (short) 70) {
            ft_eeprom_2232d.Manufacturer = m46a((i - 128) / 2, iArr);
            ft_eeprom_2232d.Product = m46a(((iArr[8] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) - 128) / 2, iArr);
            ft_eeprom_2232d.SerialNumber = m46a(((iArr[9] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) - 128) / 2, iArr);
            return ft_eeprom_2232d;
        }
        ft_eeprom_2232d.Manufacturer = m46a(i / 2, iArr);
        ft_eeprom_2232d.Product = m46a((iArr[8] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) / 2, iArr);
        ft_eeprom_2232d.SerialNumber = m46a((iArr[9] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) / 2, iArr);
        return ft_eeprom_2232d;
    }

    int m65b() {
        int a = m43a((short) 9);
        return (((this.b - 1) - 1) - ((((a & 65280) >> 8) / 2) + (a & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST))) * 2;
    }

    int m61a(byte[] bArr) {
        if (bArr.length > m65b()) {
            return 0;
        }
        int[] iArr = new int[this.b];
        for (short s = (short) 0; s < this.b; s = (short) (s + 1)) {
            iArr[s] = m43a(s);
        }
        int b = (short) (((this.b - (m65b() / 2)) - 1) - 1);
        int i = 0;
        while (i < bArr.length) {
            int i2;
            if (i + 1 < bArr.length) {
                i2 = bArr[i + 1] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
            } else {
                i2 = 0;
            }
            int i3 = (bArr[i] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) | (i2 << 8);
            short s2 = (short) (b + 1);
            iArr[b] = i3;
            i += 2;
            short s3 = s2;
        }
        if (iArr[1] == 0 || iArr[2] == 0 || !m51a(iArr, this.b - 1)) {
            return 0;
        }
        return bArr.length;
    }

    byte[] m64a(int i) {
        byte[] bArr = new byte[i];
        if (i == 0 || i > m65b()) {
            return null;
        }
        short b = (short) (((this.b - (m65b() / 2)) - 1) - 1);
        int i2 = 0;
        while (i2 < i) {
            short s = (short) (b + 1);
            int a = m43a(b);
            if (i2 + 1 < bArr.length) {
                bArr[i2 + 1] = (byte) (a & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
            }
            bArr[i2] = (byte) ((a & 65280) >> 8);
            i2 += 2;
            b = s;
        }
        return bArr;
    }
}
