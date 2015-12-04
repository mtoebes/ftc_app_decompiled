package com.ftdi.j2xx;

/* renamed from: com.ftdi.j2xx.m */
class C0019m {
    private int f108a;
    private int f109b;

    C0019m(int i, int i2) {
        this.f108a = i;
        this.f109b = i2;
    }

    C0019m() {
        this.f108a = 0;
        this.f109b = 0;
    }

    public int m123a() {
        return this.f108a;
    }

    public int m124b() {
        return this.f109b;
    }

    public String toString() {
        return "Vendor: " + String.format("%04x", new Object[]{Integer.valueOf(this.f108a)}) + ", Product: " + String.format("%04x", new Object[]{Integer.valueOf(this.f109b)});
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof C0019m)) {
            return false;
        }
        C0019m c0019m = (C0019m) o;
        if (this.f108a != c0019m.f108a) {
            return false;
        }
        if (this.f109b != c0019m.f109b) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        throw new UnsupportedOperationException();
    }
}
