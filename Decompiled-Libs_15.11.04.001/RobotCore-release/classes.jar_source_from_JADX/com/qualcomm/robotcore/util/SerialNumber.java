package com.qualcomm.robotcore.util;

import java.io.Serializable;

public class SerialNumber implements Serializable {
    private String f422a;

    public SerialNumber() {
        this.f422a = "N/A";
    }

    public SerialNumber(String serialNumber) {
        this.f422a = serialNumber;
    }

    public String getSerialNumber() {
        return this.f422a;
    }

    public void setSerialNumber(String serialNumber) {
        this.f422a = serialNumber;
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (object instanceof SerialNumber) {
            return this.f422a.equals(((SerialNumber) object).getSerialNumber());
        }
        if (object instanceof String) {
            return this.f422a.equals(object);
        }
        return false;
    }

    public int hashCode() {
        return this.f422a.hashCode();
    }

    public String toString() {
        return this.f422a;
    }
}
