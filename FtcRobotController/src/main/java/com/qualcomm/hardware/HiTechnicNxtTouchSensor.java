package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteOrder;

class HiTechnicNxtTouchSensor extends TouchSensor {
    private static final int VERSION = 1;
    private static final double TOUCH_SENSOR_PRESSED_THRESHOLD = 675.0d;
    private final ModernRoboticsUsbLegacyModule legacyModule;
    private final int physicalPort;

    public HiTechnicNxtTouchSensor(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        legacyModule.enableAnalogReadMode(physicalPort);
        this.legacyModule = legacyModule;
        this.physicalPort = physicalPort;
    }

    public String status() {
        return String.format("NXT Touch Sensor, connected via device %s, port %d", this.legacyModule.getSerialNumber().toString(), this.physicalPort);
    }

    public double getValue() {
        double analogValue = TypeConversion.byteArrayToShort(this.legacyModule.readAnalog(this.physicalPort), ByteOrder.LITTLE_ENDIAN);
        return (analogValue > TOUCH_SENSOR_PRESSED_THRESHOLD) ? 0 : 1;
    }

    public boolean isPressed() {
        return getValue() == 1;
    }

    public String getDeviceName() {
        return "NXT Touch Sensor";
    }

    public String getConnectionInfo() {
        return String.format("%s; port %d", this.legacyModule.getConnectionInfo(), this.physicalPort);
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }
}
