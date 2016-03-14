package com.ftdi.j2xx.ft4222;

import android.util.Log;
import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.FT4222_STATUS;
import com.ftdi.j2xx.interfaces.Gpio;
import com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;
import com.qualcomm.robotcore.robocol.RobocolConfig;

public class FT_4222_Gpio implements Gpio {
    private FT_4222_Device f67a;
    private FT_Device f68b;

    public FT_4222_Gpio(FT_4222_Device ft4222Device) {
        this.f67a = ft4222Device;
        this.f68b = this.f67a.mFtDev;
    }

    int m77a(int i, int i2) {
        return this.f68b.VendorCmdSet(33, (i2 << 8) | i);
    }

    int m78a(int i, int i2, byte[] bArr, int i3) {
        return this.f68b.VendorCmdGet(32, (i2 << 8) | i, bArr, i3);
    }

    public int init(int[] gpio) {
        C0010b c0010b = this.f67a.mChipStatus;
        char[] cArr = new char[1];
        m80a(cArr);
        C0012d c0012d = new C0012d(cArr);
        byte[] bArr = new byte[1];
        C0013e c0013e = new C0013e();
        m77a(7, 0);
        m77a(6, 0);
        int init = this.f67a.init();
        if (init != 0) {
            Log.e("GPIO_M", "FT4222_GPIO init - 1 NG ftStatus:" + init);
            return init;
        } else if (c0010b.f84a == 2 || c0010b.f84a == 3) {
            return FT4222_STATUS.FT4222_GPIO_NOT_SUPPORTED_IN_THIS_MODE;
        } else {
            m79a(c0012d);
            init = c0012d.f100c;
            bArr[0] = c0012d.f101d[0];
            for (int i = 0; i < 4; i++) {
                if (gpio[i] == 1) {
                    init = (byte) ((init | (1 << i)) & 15);
                } else {
                    init = (byte) ((init & ((1 << i) ^ -1)) & 15);
                }
            }
            c0013e.f104c = bArr[0];
            m77a(33, init);
            return 0;
        }
    }

    public int read(int portNum, boolean[] bValue) {
        char[] cArr = new char[1];
        m80a(cArr);
        C0012d c0012d = new C0012d(cArr);
        int a = m76a(portNum);
        if (a != 0) {
            return a;
        }
        a = m79a(c0012d);
        if (a != 0) {
            return a;
        }
        m81a(portNum, c0012d.f101d[0], bValue);
        return 0;
    }

    public int write(int portNum, boolean bValue) {
        char[] cArr = new char[1];
        m80a(cArr);
        C0012d c0012d = new C0012d(cArr);
        int a = m76a(portNum);
        if (a != 0) {
            return a;
        }
        if (!m83c(portNum)) {
            return FT4222_STATUS.FT4222_GPIO_WRITE_NOT_SUPPORTED;
        }
        m79a(c0012d);
        byte[] bArr;
        if (bValue) {
            bArr = c0012d.f101d;
            bArr[0] = (byte) (bArr[0] | (1 << portNum));
        } else {
            bArr = c0012d.f101d;
            bArr[0] = (byte) (bArr[0] & (((1 << portNum) ^ -1) & 15));
        }
        return this.f68b.write(c0012d.f101d, 1);
    }

    int m76a(int i) {
        C0010b c0010b = this.f67a.mChipStatus;
        if (c0010b.f84a == 2 || c0010b.f84a == 3) {
            return FT4222_STATUS.FT4222_GPIO_NOT_SUPPORTED_IN_THIS_MODE;
        }
        if (i >= 4) {
            return FT4222_STATUS.FT4222_GPIO_EXCEEDED_MAX_PORTNUM;
        }
        return 0;
    }

    int m79a(C0012d c0012d) {
        byte[] bArr;
        char[] cArr = new char[1];
        m80a(cArr);
        if (cArr[0] < 'B') {
            bArr = new byte[8];
        } else {
            bArr = new byte[6];
        }
        int a = m78a(32, 0, bArr, bArr.length);
        c0012d.f98a.f95a = bArr[0];
        c0012d.f98a.f96b = bArr[1];
        c0012d.f99b = bArr[bArr.length - 3];
        c0012d.f100c = bArr[bArr.length - 2];
        c0012d.f101d[0] = bArr[bArr.length - 1];
        if (a == bArr.length) {
            return 0;
        }
        return a;
    }

    void m81a(int i, byte b, boolean[] zArr) {
        zArr[0] = m84d((((1 << i) & b) >> i) & 1);
    }

    boolean m82b(int i) {
        boolean z = true;
        C0010b c0010b = this.f67a.mChipStatus;
        switch (c0010b.f84a) {
            case SpiSlaveResponseEvent.OK /*0*/:
                if ((i == 0 || i == 1) && (c0010b.f90g == (byte) 1 || c0010b.f90g == (byte) 2)) {
                    z = false;
                }
                if (m84d(c0010b.f92i) && i == 2) {
                    z = false;
                }
                if (m84d(c0010b.f93j) && i == 3) {
                    return false;
                }
                return z;
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
                if (i == 0 || i == 1) {
                    z = false;
                }
                if (m84d(c0010b.f92i) && i == 2) {
                    z = false;
                }
                if (m84d(c0010b.f93j) && i == 3) {
                    return false;
                }
                return z;
            case SpiSlaveResponseEvent.IO_ERROR /*2*/:
            case RobocolConfig.TTL /*3*/:
                return false;
            default:
                return true;
        }
    }

    boolean m83c(int i) {
        char[] cArr = new char[1];
        m80a(cArr);
        C0012d c0012d = new C0012d(cArr);
        boolean b = m82b(i);
        m79a(c0012d);
        if (!b || ((c0012d.f100c >> i) & 1) == 1) {
            return b;
        }
        return false;
    }

    boolean m84d(int i) {
        return i != 0;
    }

    int m80a(char[] cArr) {
        byte[] bArr = new byte[12];
        if (this.f68b.VendorCmdGet(32, 0, bArr, 12) < 0) {
            return 18;
        }
        if (bArr[2] == 1) {
            cArr[0] = 'A';
            return 0;
        } else if (bArr[2] != (byte) 2) {
            return 0;
        } else {
            cArr[0] = 'B';
            return 0;
        }
    }
}
