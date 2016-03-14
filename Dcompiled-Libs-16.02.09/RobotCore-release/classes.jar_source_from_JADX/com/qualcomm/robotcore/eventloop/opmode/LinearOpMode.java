package com.qualcomm.robotcore.eventloop.opmode;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.Util;

public abstract class LinearOpMode extends OpMode {
    private C0035a f209a;
    private Thread f210b;
    private ElapsedTime f211c;
    private volatile boolean f212d;

    /* renamed from: com.qualcomm.robotcore.eventloop.opmode.LinearOpMode.a */
    private static class C0035a implements Runnable {
        private RuntimeException f205a;
        private boolean f206b;
        private final LinearOpMode f207c;

        /* renamed from: com.qualcomm.robotcore.eventloop.opmode.LinearOpMode.a.1 */
        class C00341 implements Runnable {
            final /* synthetic */ C0035a f204a;

            C00341(C0035a c0035a) {
                this.f204a = c0035a;
            }

            public void run() {
                this.f204a.f205a = null;
                this.f204a.f206b = false;
                try {
                    this.f204a.f207c.runOpMode();
                } catch (InterruptedException e) {
                    RobotLog.m248d("LinearOpMode received an Interrupted Exception; shutting down this linear op mode");
                } catch (RuntimeException e2) {
                    this.f204a.f205a = e2;
                } finally {
                    this.f204a.f206b = true;
                }
            }
        }

        public C0035a(LinearOpMode linearOpMode) {
            this.f205a = null;
            this.f206b = false;
            this.f207c = linearOpMode;
        }

        public void run() {
            Util.logThreadLifeCycle("LinearOpModeHelper.run()", new C00341(this));
        }

        public boolean m191a() {
            return this.f205a != null;
        }

        public RuntimeException m192b() {
            return this.f205a;
        }

        public boolean m193c() {
            return this.f206b;
        }
    }

    public abstract void runOpMode() throws InterruptedException;

    public LinearOpMode() {
        this.f209a = null;
        this.f210b = null;
        this.f211c = new ElapsedTime();
        this.f212d = false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void waitForStart() throws java.lang.InterruptedException {
        /*
        r1 = this;
        monitor-enter(r1);
    L_0x0001:
        r0 = r1.f212d;	 Catch:{ all -> 0x000e }
        if (r0 != 0) goto L_0x0011;
    L_0x0005:
        monitor-enter(r1);	 Catch:{ all -> 0x000e }
        r1.wait();	 Catch:{ all -> 0x000b }
        monitor-exit(r1);	 Catch:{ all -> 0x000b }
        goto L_0x0001;
    L_0x000b:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x000b }
        throw r0;	 Catch:{ all -> 0x000e }
    L_0x000e:
        r0 = move-exception;
        monitor-exit(r1);
        throw r0;
    L_0x0011:
        monitor-exit(r1);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.qualcomm.robotcore.eventloop.opmode.LinearOpMode.waitForStart():void");
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
        return this.f212d;
    }

    public final void init() {
        this.f209a = new C0035a(this);
        this.f210b = new Thread(this.f209a, "Linear OpMode Helper");
        this.f210b.start();
    }

    public final void init_loop() {
        m194a();
    }

    public final void start() {
        this.f212d = true;
        synchronized (this) {
            notifyAll();
        }
    }

    public final void loop() {
        m194a();
    }

    public final void stop() {
        this.f212d = false;
        if (!this.f209a.m193c()) {
            this.f210b.interrupt();
        }
        this.f211c.reset();
        while (!this.f209a.m193c() && this.f211c.time() < 0.5d) {
            Thread.yield();
        }
        if (!this.f209a.m193c()) {
            RobotLog.m250e("*****************************************************************");
            RobotLog.m250e("User Linear Op Mode took too long to exit; emergency killing app.");
            RobotLog.m250e("Possible infinite loop in user code?");
            RobotLog.m250e("*****************************************************************");
            System.exit(-1);
        }
    }

    private void m194a() {
        if (this.f209a.m191a()) {
            throw this.f209a.m192b();
        }
        synchronized (this) {
            notifyAll();
        }
    }
}
