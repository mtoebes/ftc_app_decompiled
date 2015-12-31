package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.hardware.DigitalChannelController.Mode;

public class LED implements HardwareDevice {
    private static final String DEVICE_NAME = null;
    private static final int VERSION = 0;

    private DigitalChannelController controller;
    private int physicalPort;

    /**
     * Constructor
     *
     * @param controller   Digital Channel Controller this LED is attached to
     * @param physicalPort the physical port it's plugged into.
     */
    public LED(DigitalChannelController controller, int physicalPort) {
        this.controller = controller;
        this.physicalPort = physicalPort;
        controller.setDigitalChannelMode(physicalPort, Mode.OUTPUT);
    }

    /**
     * A method to turn on or turn off the LED
     *
     * @param set true turns it on, false turns it off.
     */
    public void enable(boolean set) {
        this.controller.setDigitalChannelState(this.physicalPort, set);
    }

    public String getDeviceName() {
        return DEVICE_NAME;
    }

    public String getConnectionInfo() {
        return null;
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }
}
