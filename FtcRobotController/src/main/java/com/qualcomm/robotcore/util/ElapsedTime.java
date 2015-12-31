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

package com.qualcomm.robotcore.util;

/**
 * Measure elapsed time
 * <p/>
 * Does not measure deep sleep. Nanosecond accuracy.
 */
//TODO check if setting resolution correctly (may be backwards?)
public class ElapsedTime {
    private static final double TIME_IN_MILLI = 1.0E6d;
    private static final double TIME_IN_NANO = 1.0E9d;

    private long startTime;
    private double timeResolution = TIME_IN_NANO;

    public enum Resolution {
        SECONDS,
        MILLISECONDS
    }

    /**
     * Constructor
     * <p/>
     * Starts the timer
     */
    public ElapsedTime() {
        reset();
    }

    /**
     * Constructor
     * <p/>
     * Starts timer with a pre-set time
     *
     * @param startTime pre-set time
     */
    public ElapsedTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * /**
     * Constructor
     * <p/>
     * Starts timer with a given time resolution
     *
     * @param resolution time resolution
     */
    public ElapsedTime(Resolution resolution) {
        reset();
        switch (resolution) {
            case SECONDS:
                this.timeResolution = TIME_IN_NANO;
            case MILLISECONDS:
                this.timeResolution = TIME_IN_MILLI;
            default:
        }
    }

    /**
     * Reset the start time to now
     */
    public void reset() {
        this.startTime = System.nanoTime();
    }

    /**
     * Get the relative start time
     * <p/>
     * Nanosecond accuracy.
     *
     * @return relative start time
     */
    public double startTime() {
        return (double) this.startTime / this.timeResolution;
    }

    /**
     * How many seconds since the start time.
     * <p/>
     * Nanosecond accuracy.
     *
     * @return time
     */
    public double time() {
        return (double) (System.nanoTime() - this.startTime) / this.timeResolution;
    }

    private String timeResolutionToString() {
        if (this.timeResolution == TIME_IN_NANO) {
            return "seconds";
        }
        if (this.timeResolution == TIME_IN_MILLI) {
            return "milliseconds";
        }
        return "Unknown units";
    }

    /**
     * Log a message stating how long the timer has been running
     *
     * @param label message to log
     */
    public void log(String label) {
        RobotLog.v(String.format("TIMER: %20s - %1.3f %s", label, time(), timeResolutionToString()));
    }

    @Override
    /**
     Return a string stating the number of seconds that have passed
     * @return string
     */
    public String toString() {
        return String.format("%1.4f %s", time(), timeResolutionToString());
    }
}
