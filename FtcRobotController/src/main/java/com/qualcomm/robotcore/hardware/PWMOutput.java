package com.qualcomm.robotcore.hardware;

/**
 * Control a single digital port
 */
public class PWMOutput implements HardwareDevice {
    private static final String DEVICE_NAME = "PWM Output";
    private static final int VERSION = 1;

    private PWMOutputController controller;
    private int port;

    /**
     * Constructor
     *
     * @param controller Digital port controller this port is attached to
     * @param port       port on the digital port controller
     */
    public PWMOutput(PWMOutputController controller, int port) {
        this.controller = controller;
        this.port = port;
    }

    /**
     * Set the pulse width output time for this port. Typically set to a value between 750 and 2,250 to control a servo.
     *
     * @param time pulse width for the port in microseconds.
     */
    public void setPulseWidthOutputTime(int time) {
        this.controller.setPulseWidthOutputTime(this.port, time);
    }

    /**
     * Get the pulse width output time for this port
     *
     * @return pulse width for the port in microseconds.
     */
    public int getPulseWidthOutputTime() {
        return this.controller.getPulseWidthOutputTime(this.port);
    }

    /**
     * Set the pulse width output period. Typically set to 20,000 to control servo.
     *
     * @param period pulse repetition period in microseconds.
     */
    public void setPulseWidthPeriod(int period) {
        this.controller.setPulseWidthPeriod(this.port, period);
    }

    /**
     * Get the pulse width output
     *
     * @return pulse repetition period in microseconds.
     */
    public int getPulseWidthPeriod() {
        return this.controller.getPulseWidthPeriod(this.port);
    }

    public String getDeviceName() {
        return DEVICE_NAME;
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; port " + this.port;
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }
}
