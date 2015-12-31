package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.hardware.DigitalChannelController.Mode;

/**
 * Control a single digital channel
 */
public class DigitalChannel implements HardwareDevice {
    private static final String DEVICE_NAME = "Digital Channel";
    private static final int VERSION = 1;

    private DigitalChannelController controller;
    private int channel;

    /**
     * Constructor
     *
     * @param controller Digital channel controller this channel is attached to
     * @param channel    channel on the digital channel controller
     */
    public DigitalChannel(DigitalChannelController controller, int channel) {
        this.controller = controller;
        this.channel = channel;
    }

    /**
     * Get the channel mode
     *
     * @return channel mode
     */
    public Mode getMode() {
        return this.controller.getDigitalChannelMode(this.channel);
    }

    /**
     * Set the channel mode
     *
     * @param mode channel mode
     */
    public void setMode(Mode mode) {
        this.controller.setDigitalChannelMode(this.channel, mode);
    }

    /**
     * Get the channel state
     *
     * @return state
     */
    public boolean getState() {
        return this.controller.getDigitalChannelState(this.channel);
    }

    /**
     * Set the channel state
     * <p/>
     * The behavior of this method is undefined for INPUT digital channels.
     *
     * @param state channel state
     */
    public void setState(boolean state) {
        this.controller.setDigitalChannelState(this.channel, state);
    }

    public String getDeviceName() {
        return DEVICE_NAME;
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; digital port " + this.channel;
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }
}
