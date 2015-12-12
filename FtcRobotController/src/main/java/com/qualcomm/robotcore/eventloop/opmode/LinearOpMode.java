package com.qualcomm.robotcore.eventloop.opmode;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

public abstract class LinearOpMode extends OpMode {
    private C0031a f202a;
    private Thread f203b;
    private ElapsedTime f204c;
    private volatile boolean f205d;

    /* renamed from: com.qualcomm.robotcore.eventloop.opmode.LinearOpMode.a */
    private static class C0031a implements Runnable {
        private RuntimeException f198a;
        private boolean f199b;
        private final LinearOpMode f200c;

        public C0031a(LinearOpMode linearOpMode) {
            this.f198a = null;
            this.f199b = false;
            this.f200c = linearOpMode;
        }

        public void run() {
            this.f198a = null;
            this.f199b = false;
            try {
                this.f200c.runOpMode();
            } catch (InterruptedException e) {
                RobotLog.d("LinearOpMode received an Interrupted Exception; shutting down this linear op mode");
            } catch (RuntimeException e2) {
                this.f198a = e2;
            } finally {
                this.f199b = true;
            }
        }

        public boolean m181a() {
            return this.f198a != null;
        }

        public RuntimeException m182b() {
            return this.f198a;
        }

        public boolean m183c() {
            return this.f199b;
        }
    }

    public abstract void runOpMode() throws InterruptedException;

    public LinearOpMode() {
        this.f202a = null;
        this.f203b = null;
        this.f204c = new ElapsedTime();
        this.f205d = false;
    }

    public synchronized void waitForStart() throws InterruptedException {
        while(!this.f205d) {
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
        return this.f205d;
    }

    public final void init() {
        this.f202a = new C0031a(this);
        this.f203b = new Thread(this.f202a, "Linear OpMode Helper");
        this.f203b.start();
    }

    public final void init_loop() {
        m184a();
    }

    public final void start() {
        this.f205d = true;
        synchronized (this) {
            notifyAll();
        }
    }

    public final void loop() {
        m184a();
    }

    public final void stop() {
        this.f205d = false;
        if (!this.f202a.m183c()) {
            this.f203b.interrupt();
        }
        this.f204c.reset();
        while (!this.f202a.m183c() && this.f204c.time() < 0.5d) {
            Thread.yield();
        }
        if (!this.f202a.m183c()) {
            RobotLog.e("*****************************************************************");
            RobotLog.e("User Linear Op Mode took too long to exit; emergency killing app.");
            RobotLog.e("Possible infinite loop in user code?");
            RobotLog.e("*****************************************************************");
            System.exit(-1);
        }
    }

    private void m184a() {
        if (this.f202a.m181a()) {
            throw this.f202a.m182b();
        }
        synchronized (this) {
            notifyAll();
        }
    }
}
