package com.qualcomm.robotcore.eventloop.opmode;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Telemetry;
import java.util.concurrent.TimeUnit;

public abstract class OpMode {
    private long startTime = System.nanoTime();
    public Gamepad gamepad1 = new Gamepad();
    public Gamepad gamepad2 = new Gamepad();
    public HardwareMap hardwareMap = new HardwareMap();
    public Telemetry telemetry = new Telemetry();
    public double time;

    public abstract void init();

    public abstract void loop();

    public OpMode() {
    }

    public void init_loop() {
    }

    public void start() {
    }

    public void stop() {
    }

    public double getRuntime() {
        return ((double) (System.nanoTime() - this.startTime)) / ((double) TimeUnit.SECONDS.toNanos(1));
    }

    public void resetStartTime() {
        this.startTime = System.nanoTime();
    }
}
