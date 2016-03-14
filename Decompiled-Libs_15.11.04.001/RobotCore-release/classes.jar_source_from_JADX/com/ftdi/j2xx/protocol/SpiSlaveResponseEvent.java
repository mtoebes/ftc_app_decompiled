package com.ftdi.j2xx.protocol;

public class SpiSlaveResponseEvent extends SpiSlaveEvent {
    public static final int DATA_CORRUPTED = 1;
    public static final int IO_ERROR = 2;
    public static final int OK = 0;
    public static final int RESET = 3;
    public static final int RES_SLAVE_READ = 3;
    private int f170a;

    public SpiSlaveResponseEvent(int iEventType, int responseCode, Object pArg0, Object pArg1, Object pArg2) {
        super(iEventType, false, pArg0, pArg1, pArg2);
        this.f170a = responseCode;
    }

    public int getResponseCode() {
        return this.f170a;
    }
}
