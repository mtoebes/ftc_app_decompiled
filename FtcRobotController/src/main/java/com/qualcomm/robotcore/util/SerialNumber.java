package com.qualcomm.robotcore.util;

import java.io.Serializable;

public class SerialNumber implements Serializable {
    private String serialNumber = "N/A";

    public SerialNumber() {
    }

    public SerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
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
            return serialNumber.equals(((SerialNumber) object).getSerialNumber());
        } else if (object instanceof String) {
            return serialNumber.equals(object);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return serialNumber.hashCode();
    }

    public String toString() {
        return serialNumber;
    }
}
