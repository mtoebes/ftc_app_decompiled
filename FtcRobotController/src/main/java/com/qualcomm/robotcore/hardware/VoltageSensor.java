package com.qualcomm.robotcore.hardware;

/**
 * Voltage Sensor
 */
public interface VoltageSensor extends HardwareDevice {
    /**
     * Get the current voltage
     *
     * @return voltage
     */
    double getVoltage();
}
