package com.qualcomm.robotcore.eventloop.opmode;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

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

    public abstract void runOpMode() throws InterruptedException;

    public LinearOpMode() {
    }

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
        this.linearOpModeRunnable = new LinearOpModeRunnable(this);
        this.linearOpModeThread = new Thread(this.linearOpModeRunnable, "Linear OpMode Helper");
        this.linearOpModeThread.start();
    }

    public final void init_loop() {
        performLoop();
    }

    public final void start() {
        this.isRunning = true;
        synchronized (this) {
            notifyAll();
        }
    }

    public final void loop() {
        performLoop();
    }

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
