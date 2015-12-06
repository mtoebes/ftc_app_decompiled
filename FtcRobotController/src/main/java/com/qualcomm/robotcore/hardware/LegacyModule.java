package com.qualcomm.robotcore.hardware;

public interface LegacyModule extends HardwareDevice {

    public interface PortReadyCallback {
        void portIsReady(int port);
    }

    void enable9v(int physicalPort, boolean enable);

    void enableAnalogReadMode(int physicalPort);

    byte[] readAnalog(int physicalPort);

    void setDigitalLine(int physicalPort, int line, boolean set);
}
