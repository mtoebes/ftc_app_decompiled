package com.ftdi.j2xx.protocol;

public interface SpiSlaveListener {
    boolean OnDataReceived(SpiSlaveResponseEvent spiSlaveResponseEvent);
}
