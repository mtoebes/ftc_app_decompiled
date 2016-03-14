package com.qualcomm.robotcore.util;

import java.io.Serializable;

public class SerialNumber implements Serializable {
    private String f424a;

    public SerialNumber() {
        this.f424a = "N/A";
    }

    public SerialNumber(String serialNumber) {
        this.f424a = serialNumber;
    }

    public String getSerialNumber() {
        return this.f424a;
    }

    public void setSerialNumber(String serialNumber) {
        this.f424a = serialNumber;
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (object instanceof SerialNumber) {
            return this.f424a.equals(((SerialNumber) object).getSerialNumber());
        }
        if (object instanceof String) {
            return this.f424a.equals(object);
        }
        return false;
    }

    public int hashCode() {
        return this.f424a.hashCode();
    }

    public String toString() {
        return this.f424a;
    }
}
