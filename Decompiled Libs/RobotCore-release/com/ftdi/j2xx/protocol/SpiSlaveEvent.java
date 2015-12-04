package com.ftdi.j2xx.protocol;

public class SpiSlaveEvent {
    private int f165a;
    private boolean f166b;
    private Object f167c;
    private Object f168d;
    private Object f169e;

    public SpiSlaveEvent(int iEventType, boolean bSync, Object pArg0, Object pArg1, Object pArg2) {
        this.f165a = iEventType;
        this.f166b = bSync;
        this.f167c = pArg0;
        this.f168d = pArg1;
        this.f169e = pArg2;
    }

    public Object getArg0() {
        return this.f167c;
    }

    public void setArg0(Object arg) {
        this.f167c = arg;
    }

    public Object getArg1() {
        return this.f168d;
    }

    public void setArg1(Object arg) {
        this.f168d = arg;
    }

    public Object getArg2() {
        return this.f169e;
    }

    public void setArg2(Object arg) {
        this.f169e = arg;
    }

    public int getEventType() {
        return this.f165a;
    }

    public void setEventType(int type) {
        this.f165a = type;
    }

    public boolean getSync() {
        return this.f166b;
    }

    public void setSync(boolean bSync) {
        this.f166b = bSync;
    }
}
