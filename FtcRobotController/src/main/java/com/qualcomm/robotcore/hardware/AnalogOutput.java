package com.qualcomm.robotcore.hardware;

/**
 * Control a single analog device
 */
public class AnalogOutput implements HardwareDevice {
    private static final String DEVICE_NAME = "Analog Output";
    private static final int VERSION = 1;

    private AnalogOutputController controller;
    private int channel;

    /**
     * constructor
     *
     * @param controller AnalogOutput controller this channel is attached to
     * @param channel    channel on the analog output controller
     */
    public AnalogOutput(AnalogOutputController controller, int channel) {
        this.controller = controller;
        this.channel = channel;
    }

    /**
     * Sets the channel output voltage. If mode == 0: takes input from -1023-1023, output in the range -4 to +4 volts. If mode == 1, 2, or 3: takes input from 0-1023, output in the range 0 to 8 volts.
     *
     * @param voltage voltage value in the correct range.
     */
    public void setAnalogOutputVoltage(int voltage) {
        this.controller.setAnalogOutputVoltage(this.channel, voltage);
    }

    /**
     * Sets the channel output frequency in the range 1-5,000 Hz in mode 1, 2 or 3. If mode 0 is selected, this field will be over-written to 0.
     *
     * @param freq output frequency in the range1-5,000Hz
     */
    public void setAnalogOutputFrequency(int freq) {
        this.controller.setAnalogOutputFrequency(this.channel, freq);
    }

    /**
     * Sets the channel operating mode. Mode 0: Voltage output. Range: -4V - 4V Mode 1: Sine wave output. Range: 0 - 8V Mode 2: Square wave output. Range: 0 - 8V Mode 3: Triangle wave output. Range: 0 - 8V
     *
     * @param mode voltage, sine, square, or triangle
     */
    public void setAnalogOutputMode(byte mode) {
        this.controller.setAnalogOutputMode(this.channel, mode);
    }

    public String getDeviceName() {
        return DEVICE_NAME;
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; analog port " + this.channel;
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }
}
