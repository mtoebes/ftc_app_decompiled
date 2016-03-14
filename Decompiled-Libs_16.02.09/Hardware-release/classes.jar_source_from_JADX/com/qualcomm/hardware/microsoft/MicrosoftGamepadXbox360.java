package com.qualcomm.hardware.microsoft;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Gamepad.GamepadCallback;

public class MicrosoftGamepadXbox360 extends Gamepad {
    public MicrosoftGamepadXbox360() {
        this(null);
    }

    public MicrosoftGamepadXbox360(GamepadCallback callback) {
        super(callback);
        this.joystickDeadzone = 0.15f;
    }

    public String type() {
        return "Xbox 360";
    }
}
