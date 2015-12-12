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
        } else if (object == this) {
            return true;
        } else if (object instanceof SerialNumber) {
            return this.serialNumber.equals(((SerialNumber) object).getSerialNumber());
        } else
            return object instanceof String && this.serialNumber.equals(object);
    }

    public int hashCode() {
        return this.serialNumber.hashCode();
    }

    public String toString() {
        return this.serialNumber;
    }
}
