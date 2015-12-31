package com.qualcomm.robotcore.util;

import java.io.Serializable;

/**
 * Manage a serial number
 */
public class SerialNumber implements Serializable {
    private String serialNumber = "N/A";

    /**
     * Constructor - use default serial number
     */
    public SerialNumber() {
    }

    /**
     * Constructor - use supplied serial number
     *
     * @param serialNumber serial number to use
     */
    public SerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Get the serial number
     *
     * @return serial number
     */
    public String getSerialNumber() {
        return this.serialNumber;
    }

    /**
     * Set the serial number
     *
     * @param serialNumber serial number
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        } else if (object == this) {
            return true;
        } else if (object instanceof SerialNumber) {
            return this.serialNumber.equals(((SerialNumber) object).getSerialNumber());
        } else
            return (object instanceof String) && (this.serialNumber.equals(object));
    }

    @Override
    public int hashCode() {
        return this.serialNumber.hashCode();
    }

    @Override
    public String toString() {
        return this.serialNumber;
    }
}
