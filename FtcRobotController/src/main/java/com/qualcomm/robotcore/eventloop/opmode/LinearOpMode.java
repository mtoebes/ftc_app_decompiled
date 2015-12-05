package com.qualcomm.robotcore.eventloop.opmode;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

public abstract class LinearOpMode extends OpMode {
    private LinearOpModeRunnable linearOpModeRunnable = null;
    private Thread linearOpModeThread = null;
    private ElapsedTime elapsedTime = new ElapsedTime();
    private volatile boolean isRunning = false;

    private class LinearOpModeRunnable implements Runnable {
        private RuntimeException runtimeException = null;
        private boolean isRunning = false;

        public void run() {
            isRunning = true;
            try {
                runOpMode();
            } catch (InterruptedException e) {
                RobotLog.d("LinearOpMode received an Interrupted Exception; shutting down this linear op mode");
            } catch (RuntimeException runtimeException) {
                this.runtimeException = runtimeException;
            } finally {
                isRunning = false;
            }
        }

        public boolean encounteredRuntimeException() {
            return runtimeException != null;
        }

        public RuntimeException getRuntimeException() {
            return runtimeException;
        }

        public boolean isRunning() {
            return isRunning;
        }
    }

    public abstract void runOpMode() throws InterruptedException;

    public synchronized void waitForStart() throws InterruptedException {
        while(!this.isRunning) {
            synchronized (this) {
                this.wait();
            }
        }
    }

    public void waitOneFullHardwareCycle() throws InterruptedException {
        waitForNextHardwareCycle();
        Thread.sleep(1);
        waitForNextHardwareCycle();
    }

    public void waitForNextHardwareCycle() throws InterruptedException {
        synchronized (this) {
            wait();
        }
    }

    public void sleep(long milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
    }

    public boolean opModeIsActive() {
        return this.isRunning;
    }

    public final void init() {
        linearOpModeRunnable = new LinearOpModeRunnable();
        linearOpModeThread = new Thread(linearOpModeRunnable, "Linear OpMode Helper");
        linearOpModeThread.start();
    }

    public final void init_loop() {
        performLoop();
    }

    public final void start() {
        isRunning = true;
        synchronized (this) {
            notifyAll();
        }
    }

    public final void loop() {
        performLoop();
    }

    public final void stop() {
        isRunning = false;
        if (linearOpModeRunnable.isRunning()) {
            linearOpModeThread.interrupt();
        }
        this.elapsedTime.reset();
        while (linearOpModeRunnable.isRunning() && elapsedTime.time() < 0.5d) {
            Thread.yield();
        }
        if (linearOpModeRunnable.isRunning()) {
            RobotLog.e("*****************************************************************");
            RobotLog.e("User Linear Op Mode took too long to exit; emergency killing app.");
            RobotLog.e("Possible infinite loop in user code?");
            RobotLog.e("*****************************************************************");
            System.exit(-1);
        }
    }

    private void performLoop() {
        if (linearOpModeRunnable.encounteredRuntimeException()) {
            throw linearOpModeRunnable.getRuntimeException();
        }
        synchronized (this) {
            notifyAll();
        }
    }
}
