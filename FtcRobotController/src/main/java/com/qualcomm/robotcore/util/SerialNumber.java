package com.qualcomm.robotcore.util;

import java.io.Serializable;

public class SerialNumber implements Serializable {
    private String serialNumber;

    public SerialNumber() {
        this.serialNumber = "N/A";
    }

    public SerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (object instanceof SerialNumber) {
            return this.serialNumber.equals(((SerialNumber) object).getSerialNumber());
        }
        if (object instanceof String) {
            return this.serialNumber.equals(object);
        }
        return false;
    }

    public int hashCode() {
        return this.serialNumber.hashCode();
    }

    public String toString() {
        return this.serialNumber;
    }
}
