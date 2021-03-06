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

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

/**
 * Base class for user defined linear operation modes (op modes).
 * <p/>
 * This class derives from OpMode, but you should not override the methods from OpMode.
 */
public abstract class LinearOpMode extends OpMode {
    private LinearOpModeRunnable linearOpModeRunnable;
    private Thread linearOpModeThread;
    private ElapsedTime elapsedTime = new ElapsedTime();
    private volatile boolean isRunning;

    private static class LinearOpModeRunnable implements Runnable {
        private RuntimeException runtimeException;
        private boolean isRunning;
        private final LinearOpMode linearOpMode;

        public LinearOpModeRunnable(LinearOpMode linearOpMode) {
            this.linearOpMode = linearOpMode;
        }

        public void run() {
            this.runtimeException = null;
            this.isRunning = false;
            try {
                this.linearOpMode.runOpMode();
            } catch (InterruptedException e) {
                RobotLog.d("LinearOpMode received an Interrupted Exception; shutting down this linear op mode");
            } catch (RuntimeException e2) {
                this.runtimeException = e2;
            } finally {
                this.isRunning = true;
            }
        }

        public boolean encounteredRuntimeException() {
            return this.runtimeException != null;
        }

        public RuntimeException getRuntimeException() {
            return this.runtimeException;
        }

        public boolean isRunning() {
            return this.isRunning;
        }
    }

    /**
     * Override this method and place your code here.
     * <p/>
     * Please do not swallow the InterruptedException, as it is used in cases where the op mode needs to be terminated early.
     *
     * @throws InterruptedException
     */
    public abstract void runOpMode() throws InterruptedException;

    public LinearOpMode() {
    }

    /**
     * Pause the Linear Op Mode until start has been pressed
     *
     * @throws InterruptedException
     */
    public synchronized void waitForStart() throws InterruptedException {
        while (!this.isRunning) {
            synchronized (this) {
                this.wait();
            }
        }
    }

    /**
     * Wait for one full cycle of the hardware
     * <p/>
     * Each cycle of the hardware your commands are sent out to the hardware; and the latest data is read back in.
     * <p/>
     * This method has a strong guarantee to wait for <b>at least one</b> full hardware hardware cycle.
     *
     * @throws InterruptedException
     */
    public void waitOneFullHardwareCycle() throws InterruptedException {
        waitForNextHardwareCycle();
        Thread.sleep(1);
        waitForNextHardwareCycle();
    }

    /**
     * Wait for the start of the next hardware cycle
     * <p/>
     * Each cycle of the hardware your commands are sent out to the hardware; and the latest data is read back in.
     * <p/>
     * This method will wait for the current hardware cycle to finish, which is also the start of the next hardware cycle.
     *
     * @throws InterruptedException
     */
    public void waitForNextHardwareCycle() throws InterruptedException {
        synchronized (this) {
            wait();
        }
    }

    /**
     * Sleep for the given amount of milliseconds
     *
     * @param milliseconds amount of time to sleep
     * @throws InterruptedException
     */
    public void sleep(long milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
    }

    /**
     * Returns true as long as the op mode is active.
     * <p/>
     * An op mode is considered active after the call to start() and before the call to stop().
     *
     * @return true if op mode is running
     */
    public boolean opModeIsActive() {
        return this.isRunning;
    }

    /**
     * From the non-linear OpMode; do not override
     */
    public final void init() {
        this.linearOpModeRunnable = new LinearOpModeRunnable(this);
        this.linearOpModeThread = new Thread(this.linearOpModeRunnable, "Linear OpMode Helper");
        this.linearOpModeThread.start();
    }

    /**
     * From the non-linear OpMode; do not override
     */
    public final void init_loop() {
        performLoop();
    }

    /**
     * From the non-linear OpMode; do not override
     */
    public final void start() {
        this.isRunning = true;
        synchronized (this) {
            notifyAll();
        }
    }

    /**
     * From the non-linear OpMode; do not override
     */
    public final void loop() {
        performLoop();
    }

    /**
     * From the non-linear OpMode; do not override
     */
    public final void stop() {
        this.isRunning = false;
        if (!this.linearOpModeRunnable.isRunning()) {
            this.linearOpModeThread.interrupt();
        }
        this.elapsedTime.reset();
        while ((!this.linearOpModeRunnable.isRunning()) && (this.elapsedTime.time() < 0.5d)) {
            Thread.yield();
        }
        if (!this.linearOpModeRunnable.isRunning()) {
            RobotLog.e("*****************************************************************");
            RobotLog.e("User Linear Op Mode took too long to exit; emergency killing app.");
            RobotLog.e("Possible infinite loop in user code?");
            RobotLog.e("*****************************************************************");
            System.exit(-1);
        }
    }

    private void performLoop() {
        if (this.linearOpModeRunnable.encounteredRuntimeException()) {
            throw this.linearOpModeRunnable.getRuntimeException();
        }
        synchronized (this) {
            notifyAll();
        }
    }
}
