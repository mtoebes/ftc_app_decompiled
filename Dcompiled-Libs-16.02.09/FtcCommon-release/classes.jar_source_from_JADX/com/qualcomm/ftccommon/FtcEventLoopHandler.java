package com.qualcomm.ftccommon;

import android.content.Context;
import com.qualcomm.ftccommon.UpdateUI.Callback;
import com.qualcomm.hardware.HardwareFactory;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.robocol.Command;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.robot.RobotState;
import com.qualcomm.robotcore.util.BatteryChecker;
import com.qualcomm.robotcore.util.BatteryChecker.BatteryWatcher;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.util.Iterator;
import java.util.Map.Entry;

public class FtcEventLoopHandler implements BatteryWatcher {
    public static final String NO_VOLTAGE_SENSOR = "$no$voltage$sensor$";
    EventLoopManager f18a;
    BatteryChecker f19b;
    Context f20c;
    ElapsedTime f21d;
    double f22e;
    ElapsedTime f23f;
    double f24g;
    ElapsedTime f25h;
    double f26i;
    Callback f27j;
    HardwareFactory f28k;
    HardwareMap f29l;

    public FtcEventLoopHandler(HardwareFactory hardwareFactory, Callback callback, Context robotControllerContext) {
        this.f21d = new ElapsedTime();
        this.f22e = 2.0d;
        this.f23f = new ElapsedTime();
        this.f24g = 0.25d;
        this.f25h = new ElapsedTime();
        this.f26i = 0.25d;
        this.f28k = null;
        this.f29l = null;
        this.f28k = hardwareFactory;
        this.f27j = callback;
        this.f20c = robotControllerContext;
        this.f19b = new BatteryChecker(robotControllerContext, this, 180000);
        this.f19b.startBatteryMonitoring();
    }

    public void init(EventLoopManager eventLoopManager) {
        this.f18a = eventLoopManager;
    }

    public EventLoopManager getEventLoopManager() {
        return this.f18a;
    }

    public HardwareMap getHardwareMap() throws RobotCoreException, InterruptedException {
        if (this.f29l == null) {
            this.f29l = this.f28k.createHardwareMap(this.f18a);
        }
        return this.f29l;
    }

    public void displayGamePadInfo(String activeOpModeName) {
        if (this.f25h.time() > this.f26i) {
            this.f25h.reset();
            this.f27j.updateUi(activeOpModeName, this.f18a.getGamepads());
        }
    }

    public Gamepad[] getGamepads() {
        return this.f18a.getGamepads();
    }

    public void sendTelemetryData(Telemetry telemetry) {
        if (this.f23f.time() > this.f24g) {
            this.f23f.reset();
            if (telemetry.hasData() || this.f21d.time() > this.f22e) {
                telemetry.addData("$Robot$Battery$Level$", m20a());
                this.f18a.sendTelemetryData(telemetry);
                telemetry.clearData();
                this.f21d.reset();
            }
        }
    }

    public void sendBatteryInfo() {
        sendTelemetry("$RobotController$Battery$Level$", String.valueOf(this.f19b.getBatteryLevel()));
        sendTelemetry("$Robot$Battery$Level$", m20a());
    }

    private String m20a() {
        Iterator it = this.f29l.voltageSensor.iterator();
        double d = Double.POSITIVE_INFINITY;
        while (it.hasNext()) {
            double voltage = ((VoltageSensor) it.next()).getVoltage();
            if (voltage < 1.0d || voltage >= d) {
                voltage = d;
            }
            d = voltage;
        }
        if (d == Double.POSITIVE_INFINITY) {
            return NO_VOLTAGE_SENSOR;
        }
        String num = Integer.toString((int) (100.0d * d));
        return (num.length() - 2);
    }

    public void sendTelemetry(String tag, String msg) {
        Telemetry telemetry = new Telemetry();
        telemetry.setTag(tag);
        telemetry.addData(tag, msg);
        if (this.f18a != null) {
            this.f18a.sendTelemetryData(telemetry);
            telemetry.clearData();
        }
    }

    public void shutdownMotorControllers() {
        for (Entry entry : this.f29l.dcMotorController.entrySet()) {
            String str = (String) entry.getKey();
            DcMotorController dcMotorController = (DcMotorController) entry.getValue();
            DbgLog.msg("Stopping DC Motor Controller " + str);
            dcMotorController.close();
        }
    }

    public void shutdownServoControllers() {
        for (Entry entry : this.f29l.servoController.entrySet()) {
            String str = (String) entry.getKey();
            ServoController servoController = (ServoController) entry.getValue();
            DbgLog.msg("Stopping Servo Controller " + str);
            servoController.close();
        }
    }

    public void shutdownLegacyModules() {
        for (Entry entry : this.f29l.legacyModule.entrySet()) {
            String str = (String) entry.getKey();
            LegacyModule legacyModule = (LegacyModule) entry.getValue();
            DbgLog.msg("Stopping Legacy Module " + str);
            legacyModule.close();
        }
    }

    public void shutdownCoreInterfaceDeviceModules() {
        for (Entry entry : this.f29l.deviceInterfaceModule.entrySet()) {
            String str = (String) entry.getKey();
            DeviceInterfaceModule deviceInterfaceModule = (DeviceInterfaceModule) entry.getValue();
            DbgLog.msg("Stopping Core Interface Device Module " + str);
            deviceInterfaceModule.close();
        }
    }

    public void restartRobot() {
        this.f19b.endBatteryMonitoring();
        this.f27j.restartRobot();
    }

    public void sendCommand(Command command) {
        this.f18a.sendCommand(command);
    }

    public String getOpMode(String extra) {
        if (this.f18a.state != RobotState.RUNNING) {
            return "$Stop$Robot$";
        }
        return extra;
    }

    public void updateBatteryLevel(float percent) {
        sendTelemetry("$RobotController$Battery$Level$", String.valueOf(percent));
    }
}
