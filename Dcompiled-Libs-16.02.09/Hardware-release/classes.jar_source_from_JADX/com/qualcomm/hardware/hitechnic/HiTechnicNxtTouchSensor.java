package com.qualcomm.hardware.hitechnic;

import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.LegacyModulePortDeviceImpl;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteOrder;

public class HiTechnicNxtTouchSensor extends LegacyModulePortDeviceImpl implements TouchSensor {
    public HiTechnicNxtTouchSensor(LegacyModule legacyModule, int physicalPort) {
        super(legacyModule, physicalPort);
        finishConstruction();
    }

    protected void moduleNowArmedOrPretending() {
        this.module.enableAnalogReadMode(this.physicalPort);
    }

    public String toString() {
        return String.format("Touch Sensor: %1.2f", new Object[]{Double.valueOf(getValue())});
    }

    public String status() {
        return String.format("NXT Touch Sensor, connected via device %s, port %d", new Object[]{this.module.getSerialNumber().toString(), Integer.valueOf(this.physicalPort)});
    }

    public double getValue() {
        return ((double) TypeConversion.byteArrayToShort(this.module.readAnalog(this.physicalPort), ByteOrder.LITTLE_ENDIAN)) > 675.0d ? 0.0d : 1.0d;
    }

    public boolean isPressed() {
        return getValue() > 0.0d;
    }

    public String getDeviceName() {
        return "NXT Touch Sensor";
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
