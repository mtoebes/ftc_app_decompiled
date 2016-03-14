package com.qualcomm.hardware.logitech;

import android.os.Build.VERSION;
import android.view.MotionEvent;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Gamepad.GamepadCallback;

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
            m51a(event);
            return;
        }
        boolean z2;
        this.left_stick_x = cleanMotionValues(event.getAxisValue(0));
        this.left_stick_y = cleanMotionValues(event.getAxisValue(1));
        this.right_stick_x = cleanMotionValues(event.getAxisValue(12));
        this.right_stick_y = cleanMotionValues(event.getAxisValue(13));
        this.left_trigger = (event.getAxisValue(11) + 1.0f) / 2.0f;
        this.right_trigger = (event.getAxisValue(14) + 1.0f) / 2.0f;
        this.dpad_down = event.getAxisValue(16) > this.dpadThreshold;
        if (event.getAxisValue(16) < (-this.dpadThreshold)) {
            z2 = true;
        } else {
            z2 = false;
        }
        this.dpad_up = z2;
        if (event.getAxisValue(15) > this.dpadThreshold) {
            z2 = true;
        } else {
            z2 = false;
        }
        this.dpad_right = z2;
        if (event.getAxisValue(15) >= (-this.dpadThreshold)) {
            z = false;
        }
        this.dpad_left = z;
        callCallback();
    }

    private void m51a(MotionEvent motionEvent) {
        boolean z;
        boolean z2 = true;
        this.left_stick_x = cleanMotionValues(motionEvent.getAxisValue(0));
        this.left_stick_y = cleanMotionValues(motionEvent.getAxisValue(1));
        this.right_stick_x = cleanMotionValues(motionEvent.getAxisValue(11));
        this.right_stick_y = cleanMotionValues(motionEvent.getAxisValue(14));
        this.left_trigger = motionEvent.getAxisValue(23);
        this.right_trigger = motionEvent.getAxisValue(22);
        this.dpad_down = motionEvent.getAxisValue(16) > this.dpadThreshold;
        if (motionEvent.getAxisValue(16) < (-this.dpadThreshold)) {
            z = true;
        } else {
            z = false;
        }
        this.dpad_up = z;
        if (motionEvent.getAxisValue(15) > this.dpadThreshold) {
            z = true;
        } else {
            z = false;
        }
        this.dpad_right = z;
        if (motionEvent.getAxisValue(15) >= (-this.dpadThreshold)) {
            z2 = false;
        }
        this.dpad_left = z2;
        callCallback();
    }

    public String type() {
        return "F310";
    }
}
