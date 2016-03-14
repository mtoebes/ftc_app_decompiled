package com.ftdi.j2xx;

import android.util.Log;
import com.ftdi.j2xx.D2xxManager.D2xxException;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.CHIPTOP_CMD;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.I2C_CMD;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.SPI_CMD;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.SPI_SLAVE_CMD;
import com.qualcomm.robotcore.BuildConfig;
import com.qualcomm.robotcore.robocol.Telemetry;

/* renamed from: com.ftdi.j2xx.k */
class C0004k {
    short f44a;
    int f45b;
    boolean f46c;
    private FT_Device f47d;

    C0004k(FT_Device fT_Device) {
        this.f47d = fT_Device;
    }

    int m43a(short s) {
        byte[] bArr = new byte[2];
        if (s >= D2xxManager.FT_FLOW_XON_XOFF) {
            return -1;
        }
        this.f47d.m28c().controlTransfer(-64, 144, 0, s, bArr, 2, 0);
        return ((bArr[1] & Telemetry.cbTagMax) << 8) | (bArr[0] & Telemetry.cbTagMax);
    }

    boolean m50a(short s, short s2) {
        int i = s2 & Telemetry.cbValueMax;
        int i2 = s & Telemetry.cbValueMax;
        if (s < D2xxManager.FT_FLOW_XON_XOFF && this.f47d.m28c().controlTransfer(64, 145, i, i2, null, 0, 0) == 0) {
            return true;
        }
        return false;
    }

    int m55c() {
        return this.f47d.m28c().controlTransfer(64, 146, 0, 0, null, 0, 0);
    }

    short m47a(FT_EEPROM ft_eeprom) {
        return (short) 1;
    }

    boolean m51a(int[] iArr, int i) {
        int i2 = 43690;
        int i3 = 0;
        while (i3 < i) {
            m50a((short) i3, (short) iArr[i3]);
            i2 = (i2 ^ iArr[i3]) & Telemetry.cbValueMax;
            i2 = (((short) ((i2 >> 15) & Telemetry.cbValueMax)) | ((short) ((i2 << 1) & Telemetry.cbValueMax))) & Telemetry.cbValueMax;
            i3++;
            Log.d("FT_EE_Ctrl", "Entered WriteWord Checksum : " + i2);
        }
        m50a((short) i, (short) i2);
        return true;
    }

    FT_EEPROM m45a() {
        return null;
    }

    int m41a(Object obj) {
        FT_EEPROM ft_eeprom = (FT_EEPROM) obj;
        int i = SPI_SLAVE_CMD.SPI_MASTER_TRANSFER;
        if (ft_eeprom.RemoteWakeup) {
            i = CHIPTOP_CMD.CHIPTOP_SET_DS_CTL0_REG;
        }
        if (ft_eeprom.SelfPowered) {
            i |= 64;
        }
        return i | ((ft_eeprom.MaxPower / 2) << 8);
    }

    void m48a(FT_EEPROM ft_eeprom, int i) {
        ft_eeprom.MaxPower = (short) (((byte) (i >> 8)) * 2);
        byte b = (byte) i;
        if ((b & 64) == 64 && (b & SPI_SLAVE_CMD.SPI_MASTER_TRANSFER) == SPI_SLAVE_CMD.SPI_MASTER_TRANSFER) {
            ft_eeprom.SelfPowered = true;
        } else {
            ft_eeprom.SelfPowered = false;
        }
        if ((b & 32) == 32) {
            ft_eeprom.RemoteWakeup = true;
        } else {
            ft_eeprom.RemoteWakeup = false;
        }
    }

    int m54b(Object obj) {
        int i;
        FT_EEPROM ft_eeprom = (FT_EEPROM) obj;
        if (ft_eeprom.PullDownEnable) {
            i = 4;
        } else {
            i = 0;
        }
        if (ft_eeprom.SerNumEnable) {
            return i | 8;
        }
        return i & 247;
    }

    void m49a(Object obj, int i) {
        FT_EEPROM ft_eeprom = (FT_EEPROM) obj;
        if ((i & 4) > 0) {
            ft_eeprom.PullDownEnable = true;
        } else {
            ft_eeprom.PullDownEnable = false;
        }
        if ((i & 8) > 0) {
            ft_eeprom.SerNumEnable = true;
        } else {
            ft_eeprom.SerNumEnable = false;
        }
    }

    int m42a(String str, int[] iArr, int i, int i2, boolean z) {
        int i3 = 0;
        int length = (str.length() * 2) + 2;
        iArr[i2] = (length << 8) | (i * 2);
        if (z) {
            iArr[i2] = iArr[i2] + SPI_SLAVE_CMD.SPI_MASTER_TRANSFER;
        }
        char[] toCharArray = str.toCharArray();
        int i4 = i + 1;
        iArr[i] = length | 768;
        int i5 = (length - 2) / 2;
        while (true) {
            length = i4 + 1;
            iArr[i4] = toCharArray[i3];
            i3++;
            if (i3 >= i5) {
                return length;
            }
            i4 = length;
        }
    }

    String m46a(int i, int[] iArr) {
        String str = BuildConfig.VERSION_NAME;
        int i2 = i + 1;
        int i3 = (((iArr[i] & Telemetry.cbTagMax) / 2) - 1) + i2;
        while (i2 < i3) {
            str = new StringBuilder(String.valueOf(str)).append((char) iArr[i2]).toString();
            i2++;
        }
        return str;
    }

    int m40a(byte b) throws D2xxException {
        int[] iArr = new int[3];
        short a = (short) m43a((short) (b & -1));
        if (a != (short) -1) {
            switch (a) {
                case SPI_CMD.SPI_SET_CPHA /*70*/:
                    this.f44a = (short) 70;
                    this.f45b = 64;
                    this.f46c = false;
                    return 64;
                case I2C_CMD.I2C_MASTER_SET_I2CMTP /*82*/:
                    this.f44a = (short) 82;
                    this.f45b = 1024;
                    this.f46c = false;
                    return 1024;
                case (short) 86:
                    this.f44a = (short) 86;
                    this.f45b = SPI_SLAVE_CMD.SPI_MASTER_TRANSFER;
                    this.f46c = false;
                    return SPI_SLAVE_CMD.SPI_MASTER_TRANSFER;
                case (short) 102:
                    this.f44a = (short) 102;
                    this.f45b = SPI_SLAVE_CMD.SPI_MASTER_TRANSFER;
                    this.f46c = false;
                    return 256;
                default:
                    return 0;
            }
        }
        boolean a2 = m50a((short) 192, (short) 192);
        iArr[0] = m43a((short) 192);
        iArr[1] = m43a((short) 64);
        iArr[2] = m43a((short) 0);
        if (a2) {
            this.f46c = true;
            if ((m43a((short) 0) & Telemetry.cbTagMax) == CHIPTOP_CMD.CHIPTOP_WRITE_OTP_TEST_BYTE) {
                m55c();
                this.f44a = (short) 70;
                this.f45b = 64;
                return 64;
            } else if ((m43a((short) 64) & Telemetry.cbTagMax) == CHIPTOP_CMD.CHIPTOP_WRITE_OTP_TEST_BYTE) {
                m55c();
                this.f44a = (short) 86;
                this.f45b = SPI_SLAVE_CMD.SPI_MASTER_TRANSFER;
                return SPI_SLAVE_CMD.SPI_MASTER_TRANSFER;
            } else if ((m43a((short) 192) & Telemetry.cbTagMax) == CHIPTOP_CMD.CHIPTOP_WRITE_OTP_TEST_BYTE) {
                m55c();
                this.f44a = (short) 102;
                this.f45b = SPI_SLAVE_CMD.SPI_MASTER_TRANSFER;
                return 256;
            } else {
                m55c();
                return 0;
            }
        }
        this.f44a = (short) 255;
        this.f45b = 0;
        return 0;
    }

    int m44a(byte[] bArr) {
        return 0;
    }

    byte[] m52a(int i) {
        return null;
    }

    int m53b() {
        return 0;
    }
}
