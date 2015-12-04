package com.ftdi.j2xx;

import com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;

/* renamed from: com.ftdi.j2xx.b */
final class C0003b {
    static byte m35a(int i, int[] iArr, boolean z) {
        byte b = C0003b.m39b(i, iArr, z);
        if (b == (byte) -1) {
            return (byte) -1;
        }
        int i2;
        if (b == null) {
            iArr[0] = (iArr[0] & -49153) + 1;
        }
        int a = C0003b.m37a(iArr[0], iArr[1], z);
        if (i > a) {
            i2 = ((i * 100) / a) - 100;
            a = ((i % a) * 100) % a;
        } else {
            i2 = ((a * 100) / i) - 100;
            a = ((a % i) * 100) % i;
        }
        if (i2 < 3) {
            return (byte) 1;
        }
        if (i2 == 3 && r2 == 0) {
            return (byte) 1;
        }
        return (byte) 0;
    }

    private static byte m39b(int i, int[] iArr, boolean z) {
        int i2 = 32768;
        byte b = (byte) 1;
        if (i == 0) {
            return (byte) -1;
        }
        if (((3000000 / i) & -16384) > 0) {
            return (byte) -1;
        }
        iArr[0] = 3000000 / i;
        iArr[1] = 0;
        if (iArr[0] == 1 && ((3000000 % i) * 100) / i <= 3) {
            iArr[0] = 0;
        }
        if (iArr[0] == 0) {
            return (byte) 1;
        }
        int i3 = ((3000000 % i) * 100) / i;
        if (z) {
            if (i3 <= 6) {
                i2 = 0;
            } else if (i3 <= 18) {
                i2 = 49152;
            } else if (i3 > 31) {
                if (i3 <= 43) {
                    iArr[1] = 1;
                    i2 = 0;
                } else if (i3 <= 56) {
                    i2 = D2xxManager.FTDI_BREAK_ON;
                } else if (i3 <= 68) {
                    iArr[1] = 1;
                    i2 = D2xxManager.FTDI_BREAK_ON;
                } else if (i3 <= 81) {
                    iArr[1] = 1;
                } else if (i3 <= 93) {
                    i2 = 49152;
                    iArr[1] = 1;
                } else {
                    b = (byte) 0;
                    i2 = 0;
                }
            }
        } else if (i3 <= 6) {
            i2 = 0;
        } else if (i3 <= 18) {
            i2 = 49152;
        } else if (i3 > 37) {
            if (i3 <= 75) {
                i2 = D2xxManager.FTDI_BREAK_ON;
            } else {
                b = (byte) 0;
                i2 = 0;
            }
        }
        iArr[0] = i2 | iArr[0];
        return b;
    }

    private static final int m37a(int i, int i2, boolean z) {
        if (i == 0) {
            return 3000000;
        }
        int i3 = (-49153 & i) * 100;
        if (z) {
            if (i2 != 0) {
                switch (49152 & i) {
                    case SpiSlaveResponseEvent.OK /*0*/:
                        i3 += 37;
                        break;
                    case D2xxManager.FTDI_BREAK_ON /*16384*/:
                        i3 += 62;
                        break;
                    case 32768:
                        i3 += 75;
                        break;
                    case 49152:
                        i3 += 87;
                        break;
                    default:
                        break;
                }
            }
            switch (49152 & i) {
                case D2xxManager.FTDI_BREAK_ON /*16384*/:
                    i3 += 50;
                    break;
                case 32768:
                    i3 += 25;
                    break;
                case 49152:
                    i3 += 12;
                    break;
                default:
                    break;
            }
        }
        switch (49152 & i) {
            case D2xxManager.FTDI_BREAK_ON /*16384*/:
                i3 += 50;
                break;
            case 32768:
                i3 += 25;
                break;
            case 49152:
                i3 += 12;
                break;
        }
        return 300000000 / i3;
    }

    static final byte m34a(int i, int[] iArr) {
        byte b = C0003b.m38b(i, iArr);
        if (b == (byte) -1) {
            return (byte) -1;
        }
        int i2;
        if (b == null) {
            iArr[0] = (iArr[0] & -49153) + 1;
        }
        int a = C0003b.m36a(iArr[0], iArr[1]);
        if (i > a) {
            i2 = ((i * 100) / a) - 100;
            a = ((i % a) * 100) % a;
        } else {
            i2 = ((a * 100) / i) - 100;
            a = ((a % i) * 100) % i;
        }
        if (i2 < 3) {
            return (byte) 1;
        }
        if (i2 == 3 && r2 == 0) {
            return (byte) 1;
        }
        return (byte) 0;
    }

    private static byte m38b(int i, int[] iArr) {
        byte b = (byte) 1;
        if (i == 0) {
            return (byte) -1;
        }
        if (((12000000 / i) & -16384) > 0) {
            return (byte) -1;
        }
        iArr[1] = 2;
        if (i >= 11640000 && i <= 12360000) {
            iArr[0] = 0;
            return (byte) 1;
        } else if (i < 7760000 || i > 8240000) {
            iArr[0] = 12000000 / i;
            iArr[1] = 2;
            if (iArr[0] == 1 && ((12000000 % i) * 100) / i <= 3) {
                iArr[0] = 0;
            }
            if (iArr[0] == 0) {
                return (byte) 1;
            }
            int i2 = ((12000000 % i) * 100) / i;
            if (i2 <= 6) {
                i2 = 0;
            } else if (i2 <= 18) {
                i2 = 49152;
            } else if (i2 <= 31) {
                i2 = 32768;
            } else if (i2 <= 43) {
                iArr[1] = iArr[1] | 1;
                i2 = 0;
            } else if (i2 <= 56) {
                i2 = D2xxManager.FTDI_BREAK_ON;
            } else if (i2 <= 68) {
                i2 = D2xxManager.FTDI_BREAK_ON;
                iArr[1] = iArr[1] | 1;
            } else if (i2 <= 81) {
                i2 = 32768;
                iArr[1] = iArr[1] | 1;
            } else if (i2 <= 93) {
                i2 = 49152;
                iArr[1] = iArr[1] | 1;
            } else {
                b = (byte) 0;
                i2 = 0;
            }
            iArr[0] = i2 | iArr[0];
            return b;
        } else {
            iArr[0] = 1;
            return (byte) 1;
        }
    }

    private static int m36a(int i, int i2) {
        if (i == 0) {
            return 12000000;
        }
        if (i == 1) {
            return 8000000;
        }
        int i3 = (-49153 & i) * 100;
        if ((65533 & i2) != 0) {
            switch (i & 49152) {
                case SpiSlaveResponseEvent.OK /*0*/:
                    i3 += 37;
                    break;
                case D2xxManager.FTDI_BREAK_ON /*16384*/:
                    i3 += 62;
                    break;
                case 32768:
                    i3 += 75;
                    break;
                case 49152:
                    i3 += 87;
                    break;
                default:
                    break;
            }
        }
        switch (i & 49152) {
            case D2xxManager.FTDI_BREAK_ON /*16384*/:
                i3 += 50;
                break;
            case 32768:
                i3 += 25;
                break;
            case 49152:
                i3 += 12;
                break;
        }
        return 1200000000 / i3;
    }
}
