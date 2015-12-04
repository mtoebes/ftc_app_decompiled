package com.ftdi.j2xx.ft4222;

import android.util.Log;
import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.FT4222_STATUS;
import com.ftdi.j2xx.interfaces.Gpio;
import com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;
import com.qualcomm.robotcore.robocol.RobocolConfig;

public class FT_4222_Gpio implements Gpio {
    boolean f66a;
    private FT_4222_Device f67b;
    private FT_Device f68c;

    public FT_4222_Gpio(FT_4222_Device ft4222Device) {
        this.f66a = true;
        this.f67b = ft4222Device;
        this.f68c = this.f67b.mFtDev;
    }

    public int cmdSet(int wValue1, int wValue2) {
        return this.f68c.VendorCmdSet(33, (wValue2 << 8) | wValue1);
    }

    public int cmdSet(int wValue1, int wValue2, byte[] buf, int datalen) {
        return this.f68c.VendorCmdSet(33, (wValue2 << 8) | wValue1, buf, datalen);
    }

    public int cmdGet(int wValue1, int wValue2, byte[] buf, int datalen) {
        return this.f68c.VendorCmdGet(32, (wValue2 << 8) | wValue1, buf, datalen);
    }

    public int init(int[] gpio) {
        C0010b c0010b = this.f67b.mChipStatus;
        C0012d c0012d = new C0012d();
        byte[] bArr = new byte[1];
        C0013e c0013e = new C0013e();
        cmdSet(7, 0);
        cmdSet(6, 0);
        int init = this.f67b.init();
        if (init != 0) {
            Log.e("GPIO_M", "FT4222_GPIO init - 1 NG ftStatus:" + init);
            return init;
        } else if (c0010b.f84a == 2 || c0010b.f84a == 3) {
            return FT4222_STATUS.FT4222_GPIO_NOT_SUPPORTED_IN_THIS_MODE;
        } else {
            m77a(c0012d);
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
            cmdSet(33, init);
            return 0;
        }
    }

    public int read(int portNum, boolean[] bValue) {
        C0012d c0012d = new C0012d();
        int a = m76a(portNum);
        if (a != 0) {
            return a;
        }
        a = m77a(c0012d);
        if (a != 0) {
            return a;
        }
        m78a(portNum, c0012d.f101d[0], bValue);
        return 0;
    }

    public int newRead(int portNum, boolean[] bValue) {
        int a = m76a(portNum);
        if (a != 0) {
            return a;
        }
        a = this.f68c.getQueueStatus();
        if (a <= 0) {
            return -1;
        }
        byte[] bArr = new byte[a];
        this.f68c.read(bArr, a);
        m78a(portNum, bArr[a - 1], bValue);
        return a;
    }

    public int write(int portNum, boolean bValue) {
        C0012d c0012d = new C0012d();
        int a = m76a(portNum);
        if (a != 0) {
            return a;
        }
        if (!m80c(portNum)) {
            return FT4222_STATUS.FT4222_GPIO_WRITE_NOT_SUPPORTED;
        }
        m77a(c0012d);
        byte[] bArr;
        if (bValue) {
            bArr = c0012d.f101d;
            bArr[0] = (byte) (bArr[0] | (1 << portNum));
        } else {
            bArr = c0012d.f101d;
            bArr[0] = (byte) (bArr[0] & (((1 << portNum) ^ -1) & 15));
        }
        return this.f68c.write(c0012d.f101d, 1);
    }

    public int newWrite(int portNum, boolean bValue) {
        boolean z = false;
        C0012d c0012d = new C0012d();
        int a = m76a(portNum);
        if (a != 0) {
            return a;
        }
        if (!m80c(portNum)) {
            return FT4222_STATUS.FT4222_GPIO_WRITE_NOT_SUPPORTED;
        }
        byte[] bArr;
        m77a(c0012d);
        if (bValue) {
            bArr = c0012d.f101d;
            bArr[0] = (byte) (bArr[0] | (1 << portNum));
        } else {
            bArr = c0012d.f101d;
            bArr[0] = (byte) (bArr[0] & (((1 << portNum) ^ -1) & 15));
        }
        if (this.f66a) {
            bArr = c0012d.f101d;
            bArr[0] = (byte) (bArr[0] | 8);
        } else {
            bArr = c0012d.f101d;
            bArr[0] = (byte) (bArr[0] & 7);
        }
        a = this.f68c.write(c0012d.f101d, 1);
        if (!this.f66a) {
            z = true;
        }
        this.f66a = z;
        return a;
    }

    int m76a(int i) {
        C0010b c0010b = this.f67b.mChipStatus;
        if (c0010b.f84a == 2 || c0010b.f84a == 3) {
            return FT4222_STATUS.FT4222_GPIO_NOT_SUPPORTED_IN_THIS_MODE;
        }
        if (i >= 4) {
            return FT4222_STATUS.FT4222_GPIO_EXCEEDED_MAX_PORTNUM;
        }
        return 0;
    }

    int m77a(C0012d c0012d) {
        r2 = new byte[8];
        int cmdGet = cmdGet(32, 0, r2, 8);
        c0012d.f98a.f95a = r2[0];
        c0012d.f98a.f96b = r2[1];
        c0012d.f99b = r2[5];
        c0012d.f100c = r2[6];
        c0012d.f101d[0] = r2[7];
        if (cmdGet == 8) {
            return 0;
        }
        return cmdGet;
    }

    void m78a(int i, byte b, boolean[] zArr) {
        zArr[0] = m81d((((1 << i) & b) >> i) & 1);
    }

    boolean m79b(int i) {
        boolean z = true;
        C0010b c0010b = this.f67b.mChipStatus;
        switch (c0010b.f84a) {
            case SpiSlaveResponseEvent.OK /*0*/:
                if ((i == 0 || i == 1) && (c0010b.f90g == (byte) 1 || c0010b.f90g == (byte) 2)) {
                    z = false;
                }
                if (m81d(c0010b.f92i) && i == 2) {
                    z = false;
                }
                if (m81d(c0010b.f93j) && i == 3) {
                    return false;
                }
                return z;
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
                if (i == 0 || i == 1) {
                    z = false;
                }
                if (m81d(c0010b.f92i) && i == 2) {
                    z = false;
                }
                if (m81d(c0010b.f93j) && i == 3) {
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

    boolean m80c(int i) {
        C0012d c0012d = new C0012d();
        boolean b = m79b(i);
        m77a(c0012d);
        if (!b || ((c0012d.f100c >> i) & 1) == 1) {
            return b;
        }
        return false;
    }

    boolean m81d(int i) {
        return i != 0;
    }
}
