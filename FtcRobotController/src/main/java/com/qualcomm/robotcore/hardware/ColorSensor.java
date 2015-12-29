package com.qualcomm.robotcore.hardware;

public abstract class ColorSensor implements HardwareDevice {
    public abstract int alpha();

    public abstract int argb();

    public abstract int blue();

    public abstract void enableLed(boolean enable);

    public abstract int getI2cAddress();

    public abstract int green();

    public abstract int red();

    public abstract void setI2cAddress(int newAddress);

    public String toString() {
        return String.format("argb: %d", argb());
    }
}
