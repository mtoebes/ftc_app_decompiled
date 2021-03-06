package com.qualcomm.robotcore.hardware;

public abstract class ColorSensor implements HardwareDevice {
    public abstract int alpha();

    public abstract int argb();

    public abstract int blue();

    public abstract void enableLed(boolean z);

    public abstract int getI2cAddress();

    public abstract int green();

    public abstract int red();

    public abstract void setI2cAddress(int i);

    public String toString() {
        return String.format("argb: %d", new Object[]{Integer.valueOf(argb())});
    }
}
