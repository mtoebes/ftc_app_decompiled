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

package com.qualcomm.robotcore.eventloop.opmode;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Telemetry;

import java.util.concurrent.TimeUnit;

/**
 * Base class for user defined operation modes (op modes).
 */
public abstract class OpMode {
    private long startTime = System.nanoTime();
    /**
     * Gamepad 1
     */
    public Gamepad gamepad1 = new Gamepad();
    /**
     * Gamepad 2
     */
    public Gamepad gamepad2 = new Gamepad();
    /**
     * Hardware Mappings
     */
    public HardwareMap hardwareMap = new HardwareMap();
    /**
     * Telemetry Data
     */
    public Telemetry telemetry = new Telemetry();
    /**
     * number of seconds this op mode has been running, this is updated before every call to loop.
     */
    public double time;

    /**
     * User defined init method
     * <p/>
     * This method will be called once when the INIT button is pressed.
     */
    public abstract void init();

    /**
     * User defined loop method
     * <p/>
     * This method will be called repeatedly in a loop while this op mode is running
     */
    public abstract void loop();

    /**
     * OpMode constructor
     * <p/>
     * The op mode name should be unique. It will be the name displayed on the driver station. If multiple op modes have the same name, only one will be available.
     */
    public OpMode() {
    }

    /**
     * User defined init_loop method
     * <p/>
     * This method will be called repeatedly when the INIT button is pressed. This method is optional. By default this method takes no action.
     */
    public void init_loop() {
    }


    /**
     * User defined start method.
     * <p/>
     * This method will be called once when the PLAY button is first pressed. This method is optional. By default this method takes not action. Example usage: Starting another thread.
     */
    public void start() {
    }

    /**
     * User defined stop method
     * <p/>
     * This method will be called when this op mode is first disabled The stop method is optional. By default this method takes no action.
     */
    public void stop() {
    }

    /**
     * Get the number of seconds this op mode has been running
     * <p/>
     * This method has sub millisecond accuracy.
     *
     * @return number of seconds this op mode has been running
     */
    public double getRuntime() {
        return ((double) (System.nanoTime() - this.startTime)) / ((double) TimeUnit.SECONDS.toNanos(1));
    }

    /**
     * Reset the start time to zero.
     */
    public void resetStartTime() {
        this.startTime = System.nanoTime();
    }
}
