package com.qualcomm.robotcore.hardware.logitech;

import android.os.Build.VERSION;
import android.view.MotionEvent;

import com.qualcomm.robotcore.hardware.Gamepad;

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

        this.left_stick_x = cleanMotionValues(event.getAxisValue(MotionEvent.ACTION_DOWN));
        this.left_stick_y = cleanMotionValues(event.getAxisValue(MotionEvent.ACTION_UP));

        this.dpad_down = event.getAxisValue(MotionEvent.AXIS_HAT_Y) > this.dpadThreshold;
        this.dpad_up = event.getAxisValue(MotionEvent.AXIS_HAT_Y) < (-this.dpadThreshold);
        this.dpad_right = event.getAxisValue(MotionEvent.AXIS_HAT_X) > this.dpadThreshold;
        this.dpad_left = event.getAxisValue(MotionEvent.AXIS_HAT_X) < (-this.dpadThreshold);

        if (VERSION.RELEASE.startsWith("5")) {
            this.right_stick_x = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_Z));
            this.right_stick_y = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_RZ));
            this.left_trigger = event.getAxisValue(MotionEvent.AXIS_BRAKE);
            this.right_trigger = event.getAxisValue(MotionEvent.AXIS_GAS);
        } else {
            this.right_stick_x = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_RX));
            this.right_stick_y = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_RY));
            this.left_trigger = (event.getAxisValue(MotionEvent.AXIS_Z) + 1) / 2.0f;
            this.right_trigger = (event.getAxisValue(MotionEvent.AXIS_RZ) + 1) / 2.0f;
        }
        callCallback();
    }

    public String type() {
        return "F310";
    }
}
