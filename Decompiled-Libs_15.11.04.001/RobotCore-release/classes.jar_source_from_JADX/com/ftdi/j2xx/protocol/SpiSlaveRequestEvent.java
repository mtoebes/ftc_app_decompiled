package com.ftdi.j2xx.protocol;

public class SpiSlaveRequestEvent extends SpiSlaveEvent {
    protected static final int REQ_DESTORY_THREAD = -1;
    protected static final int REQ_INIT_SLAVE = 1;
    protected static final int REQ_SLAVE_READ = 3;
    protected static final int REQ_SLAVE_WRITE = 2;

    public SpiSlaveRequestEvent(int iEventType, boolean bSync, Object pArg0, Object pArg1, Object pArg2) {
        super(iEventType, bSync, pArg0, pArg1, pArg2);
    }
}
