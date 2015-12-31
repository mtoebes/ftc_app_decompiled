/*
 * Copyright (c) 2014, 2015 Qualcomm Technologies Inc
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * (subject to the limitations in the disclaimer below) provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Qualcomm Technologies Inc nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
