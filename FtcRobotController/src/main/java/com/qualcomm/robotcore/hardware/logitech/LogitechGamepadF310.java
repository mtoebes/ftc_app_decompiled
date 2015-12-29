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
        boolean z = true;
        this.id = event.getDeviceId();
        this.timestamp = event.getEventTime();
        if (VERSION.RELEASE.startsWith("5")) {
            m206a(event);
            return;
        }
        boolean z2;
        this.left_stick_x = cleanMotionValues(event.getAxisValue(MotionEvent.ACTION_DOWN));
        this.left_stick_y = cleanMotionValues(event.getAxisValue(MotionEvent.ACTION_UP));
        this.right_stick_x = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_RX));
        this.right_stick_y = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_RY));
        this.left_trigger = (event.getAxisValue(MotionEvent.AXIS_Z) + 1) / 2.0f;
        this.right_trigger = (event.getAxisValue(MotionEvent.AXIS_RZ) + 1) / 2.0f;
        this.dpad_down = event.getAxisValue(MotionEvent.AXIS_HAT_Y) > this.dpadThreshold;
        if (event.getAxisValue(MotionEvent.AXIS_HAT_Y) < (-this.dpadThreshold)) {
            z2 = true;
        } else {
            z2 = false;
        }
        this.dpad_up = z2;
        if (event.getAxisValue(MotionEvent.AXIS_HAT_X) > this.dpadThreshold) {
            z2 = true;
        } else {
            z2 = false;
        }
        this.dpad_right = z2;
        if (event.getAxisValue(MotionEvent.AXIS_HAT_X) >= (-this.dpadThreshold)) {
            z = false;
        }
        this.dpad_left = z;
        callCallback();
    }

    private void m206a(MotionEvent motionEvent) {
        boolean z;
        boolean z2 = true;
        this.left_stick_x = cleanMotionValues(motionEvent.getAxisValue(MotionEvent.ACTION_DOWN));
        this.left_stick_y = cleanMotionValues(motionEvent.getAxisValue(MotionEvent.ACTION_UP));
        this.right_stick_x = cleanMotionValues(motionEvent.getAxisValue(MotionEvent.AXIS_Z));
        this.right_stick_y = cleanMotionValues(motionEvent.getAxisValue(MotionEvent.AXIS_RZ));
        this.left_trigger = motionEvent.getAxisValue(MotionEvent.AXIS_BRAKE);
        this.right_trigger = motionEvent.getAxisValue(MotionEvent.AXIS_GAS);
        this.dpad_down = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_Y) > this.dpadThreshold;
        if (motionEvent.getAxisValue(MotionEvent.AXIS_HAT_Y) < (-this.dpadThreshold)) {
            z = true;
        } else {
            z = false;
        }
        this.dpad_up = z;
        if (motionEvent.getAxisValue(MotionEvent.AXIS_HAT_X) > this.dpadThreshold) {
            z = true;
        } else {
            z = false;
        }
        this.dpad_right = z;
        if (motionEvent.getAxisValue(MotionEvent.AXIS_HAT_X) >= (-this.dpadThreshold)) {
            z2 = false;
        }
        this.dpad_left = z2;
        callCallback();
    }

    public String type() {
        return "F310";
    }
}
