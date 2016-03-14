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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Map.Entry;

public class FtcEventLoopHandler implements BatteryWatcher {
    public static final String NO_VOLTAGE_SENSOR = "no voltage sensor found";
    EventLoopManager f10a;
    BatteryChecker f11b;
    Context f12c;
    ElapsedTime f13d;
    double f14e;
    Callback f15f;
    HardwareFactory f16g;
    HardwareMap f17h;

    public FtcEventLoopHandler(HardwareFactory hardwareFactory, Callback callback, Context robotControllerContext) {
        this.f13d = new ElapsedTime();
        this.f14e = 0.25d;
        this.f17h = new HardwareMap();
        this.f16g = hardwareFactory;
        this.f15f = callback;
        this.f12c = robotControllerContext;
        this.f11b = new BatteryChecker(robotControllerContext, this, 180000);
        this.f11b.startBatteryMonitoring();
    }

    public void init(EventLoopManager eventLoopManager) {
        this.f10a = eventLoopManager;
    }

    public HardwareMap getHardwareMap() throws RobotCoreException, InterruptedException {
        this.f17h = this.f16g.createHardwareMap(this.f10a);
        return this.f17h;
    }

    public void displayGamePadInfo(String activeOpModeName) {
        this.f15f.updateUi(activeOpModeName, this.f10a.getGamepads());
    }

    public Gamepad[] getGamepads() {
        return this.f10a.getGamepads();
    }

    public void resetGamepads() {
        this.f10a.resetGamepads();
    }

    public void sendTelemetryData(Telemetry telemetry) {
        if (this.f13d.time() > this.f14e) {
            this.f13d.reset();
            if (telemetry.hasData()) {
                this.f10a.sendTelemetryData(telemetry);
            }
            telemetry.clearData();
        }
    }

    public void sendBatteryInfo() {
        m11a();
        m12b();
    }

    private void m11a() {
        sendTelemetry("RobotController Battery Level", String.valueOf(this.f11b.getBatteryLevel()));
    }

    private void m12b() {
        String str;
        Iterator it = this.f17h.voltageSensor.iterator();
        double d = Double.MAX_VALUE;
        while (it.hasNext()) {
            double voltage;
            VoltageSensor voltageSensor = (VoltageSensor) it.next();
            if (voltageSensor.getVoltage() < d) {
                voltage = voltageSensor.getVoltage();
            } else {
                voltage = d;
            }
            d = voltage;
        }
        if (this.f17h.voltageSensor.size() == 0) {
            str = NO_VOLTAGE_SENSOR;
        } else {
            str = String.valueOf(new BigDecimal(d).setScale(2, RoundingMode.HALF_UP).doubleValue());
        }
        sendTelemetry("Robot Battery Level", str);
    }

    public void sendTelemetry(String tag, String msg) {
        Telemetry telemetry = new Telemetry();
        telemetry.setTag(tag);
        telemetry.addData(tag, msg);
        if (this.f10a != null) {
            this.f10a.sendTelemetryData(telemetry);
            telemetry.clearData();
        }
    }

    public void shutdownMotorControllers() {
        for (Entry entry : this.f17h.dcMotorController.entrySet()) {
            String str = (String) entry.getKey();
            DcMotorController dcMotorController = (DcMotorController) entry.getValue();
            DbgLog.msg("Stopping DC Motor Controller " + str);
            dcMotorController.close();
        }
    }

    public void shutdownServoControllers() {
        for (Entry entry : this.f17h.servoController.entrySet()) {
            String str = (String) entry.getKey();
            ServoController servoController = (ServoController) entry.getValue();
            DbgLog.msg("Stopping Servo Controller " + str);
            servoController.close();
        }
    }

    public void shutdownLegacyModules() {
        for (Entry entry : this.f17h.legacyModule.entrySet()) {
            String str = (String) entry.getKey();
            LegacyModule legacyModule = (LegacyModule) entry.getValue();
            DbgLog.msg("Stopping Legacy Module " + str);
            legacyModule.close();
        }
    }

    public void shutdownCoreInterfaceDeviceModules() {
        for (Entry entry : this.f17h.deviceInterfaceModule.entrySet()) {
            String str = (String) entry.getKey();
            DeviceInterfaceModule deviceInterfaceModule = (DeviceInterfaceModule) entry.getValue();
            DbgLog.msg("Stopping Core Interface Device Module " + str);
            deviceInterfaceModule.close();
        }
    }

    public void restartRobot() {
        this.f11b.endBatteryMonitoring();
        this.f15f.restartRobot();
    }

    public void sendCommand(Command command) {
        this.f10a.sendCommand(command);
    }

    public String getOpMode(String extra) {
        if (this.f10a.state != RobotState.RUNNING) {
            return "Stop Robot";
        }
        return extra;
    }

    public void updateBatteryLevel(float percent) {
        sendTelemetry("RobotController Battery Level", String.valueOf(percent));
    }
}
