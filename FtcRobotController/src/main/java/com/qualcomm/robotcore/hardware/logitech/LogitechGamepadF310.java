package com.qualcomm.robotcore.hardware.logitech;

import android.os.Build.VERSION;
import android.view.MotionEvent;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Gamepad.GamepadCallback;
import com.qualcomm.robotcore.util.Dimmer;

public class LogitechGamepadF310 extends Gamepad {
    public LogitechGamepadF310() {
        this(null);
    }

    public LogitechGamepadF310(GamepadCallback callback) {
        super(callback);
        this.joystickDeadzone = 0.06f;
    }

    public void update(MotionEvent event) {
        this.id = event.getDeviceId();
        this.timestamp = event.getEventTime();
        if (VERSION.RELEASE.startsWith("5")) {
            right_stick_x = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_Z));
            right_stick_y = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_RZ));
            left_trigger = event.getAxisValue(MotionEvent.AXIS_BRAKE);
            right_trigger = event.getAxisValue(MotionEvent.AXIS_GAS);
        } else {
            right_stick_x = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_RX));
            right_stick_y = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_RY));
            left_trigger = (event.getAxisValue(MotionEvent.AXIS_Z) + 1/2.0f);
            right_trigger = (event.getAxisValue(MotionEvent.AXIS_RZ) + 1/2.0f);
        }
        left_stick_x = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_X));
        left_stick_y = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_Y));
        dpad_down = event.getAxisValue(MotionEvent.AXIS_HAT_Y) > dpadThreshold;
        dpad_up = (event.getAxisValue(MotionEvent.AXIS_HAT_Y) < (-dpadThreshold));
        dpad_right = (event.getAxisValue(MotionEvent.AXIS_HAT_X) > dpadThreshold);
        dpad_left = (event.getAxisValue(MotionEvent.AXIS_HAT_X) < (-dpadThreshold));
        callCallback();
    }

    public String type() {
        return "F310";
    }
}
