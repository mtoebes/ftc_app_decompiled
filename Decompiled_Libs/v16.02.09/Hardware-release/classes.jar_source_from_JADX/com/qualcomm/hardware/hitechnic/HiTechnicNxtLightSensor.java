package com.qualcomm.hardware.hitechnic;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDcMotorController;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.LegacyModulePortDeviceImpl;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteOrder;

public class HiTechnicNxtLightSensor extends LegacyModulePortDeviceImpl implements LightSensor {
    public static final byte LED_DIGITAL_LINE_NUMBER = (byte) 0;
    public static final double MAX_LIGHT_VALUE = 870.0d;
    public static final double MIN_LIGHT_VALUE = 120.0d;

    public HiTechnicNxtLightSensor(LegacyModule legacyModule, int physicalPort) {
        super(legacyModule, physicalPort);
        finishConstruction();
    }

    protected void moduleNowArmedOrPretending() {
        this.module.enableAnalogReadMode(this.physicalPort);
    }

    public String toString() {
        return String.format("Light Level: %1.2f", new Object[]{Double.valueOf(getLightDetected())});
    }

    public double getLightDetected() {
        return Range.clip(Range.scale((double) getLightDetectedRaw(), MIN_LIGHT_VALUE, MAX_LIGHT_VALUE, 0.0d, 1.0d), 0.0d, 1.0d);
    }

    public int getLightDetectedRaw() {
        return 1023 - (TypeConversion.byteArrayToShort(this.module.readAnalog(this.physicalPort), ByteOrder.LITTLE_ENDIAN) & ModernRoboticsUsbDcMotorController.BATTERY_MAX_MEASURABLE_VOLTAGE_INT);
    }

    public void enableLed(boolean enable) {
        this.module.setDigitalLine(this.physicalPort, 0, enable);
    }

    public String status() {
        return String.format("NXT Light Sensor, connected via device %s, port %d", new Object[]{this.module.getSerialNumber().toString(), Integer.valueOf(this.physicalPort)});
    }

    public String getDeviceName() {
        return "NXT Light Sensor";
    }

    public String getConnectionInfo() {
        return this.module.getConnectionInfo() + "; port " + this.physicalPort;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
