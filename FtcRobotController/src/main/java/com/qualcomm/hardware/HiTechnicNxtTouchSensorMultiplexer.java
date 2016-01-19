package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.TouchSensorMultiplexer;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteOrder;

public class HiTechnicNxtTouchSensorMultiplexer extends TouchSensorMultiplexer {
    public static final int VERSION = 1;

    public static final int MAX_CHANNEL = 4;
    private final ModernRoboticsUsbLegacyModule legacyModule;
    private final int physicalPort;

    public HiTechnicNxtTouchSensorMultiplexer(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        legacyModule.enableAnalogReadMode(physicalPort);
        this.legacyModule = legacyModule;
        this.physicalPort = physicalPort;
    }

    public String status() {
        return String.format("NXT Touch Sensor Multiplexer, connected via device %s, port %d", this.legacyModule.getSerialNumber().toString(), this.physicalPort);
    }

    public String getDeviceName() {
        return "NXT Touch Sensor Multiplexer";
    }

    public String getConnectionInfo() {
        return String.format("%s; port %d", this.legacyModule.getConnectionInfo(), this.physicalPort);
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }

    public boolean isTouchSensorPressed(int channel) {
        validateChannel(channel);
        return (getSwitches() & (1 << channel)) > 0;
    }

    public int getSwitches() {
        int analogValue = TypeConversion.byteArrayToShort(this.legacyModule.readAnalog(3), ByteOrder.LITTLE_ENDIAN);
        return ((((1023 - analogValue) * 339) / (analogValue)) + 5) / 10; // TODO understand this math
    }

    private void validateChannel(int i) {
        if (i <= 0 || i > MAX_CHANNEL) {
            throw new IllegalArgumentException(String.format("Channel %d is invalid; valid channels are 1..%d", i, MAX_CHANNEL));
        }
    }
}
