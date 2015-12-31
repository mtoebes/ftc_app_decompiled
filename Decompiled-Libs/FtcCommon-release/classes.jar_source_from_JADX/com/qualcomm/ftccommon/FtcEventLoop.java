package com.qualcomm.ftccommon;

import android.content.Context;
import com.qualcomm.ftccommon.UpdateUI.Callback;
import com.qualcomm.hardware.HardwareFactory;
import com.qualcomm.modernrobotics.ModernRoboticsUsbUtil;
import com.qualcomm.robotcore.eventloop.EventLoop;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegister;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Command;
import com.qualcomm.robotcore.util.Util;

public class FtcEventLoop implements EventLoop {
    FtcEventLoopHandler f7a;
    OpModeManager f8b;
    OpModeRegister f9c;

    public FtcEventLoop(HardwareFactory hardwareFactory, OpModeRegister register, Callback callback, Context robotControllerContext) {
        this.f8b = new OpModeManager(new HardwareMap());
        this.f7a = new FtcEventLoopHandler(hardwareFactory, callback, robotControllerContext);
        this.f9c = register;
    }

    public OpModeManager getOpModeManager() {
        return this.f8b;
    }

    public void init(EventLoopManager eventLoopManager) throws RobotCoreException, InterruptedException {
        DbgLog.msg("======= INIT START =======");
        this.f8b.registerOpModes(this.f9c);
        this.f7a.init(eventLoopManager);
        HardwareMap hardwareMap = this.f7a.getHardwareMap();
        ModernRoboticsUsbUtil.init(hardwareMap.appContext, hardwareMap);
        this.f8b.setHardwareMap(hardwareMap);
        hardwareMap.logDevices();
        DbgLog.msg("======= INIT FINISH =======");
    }

    public void loop() throws RobotCoreException {
        this.f7a.displayGamePadInfo(this.f8b.getActiveOpModeName());
        this.f8b.runActiveOpMode(this.f7a.getGamepads());
        this.f7a.sendTelemetryData(this.f8b.getActiveOpMode().telemetry);
    }

    public void teardown() throws RobotCoreException {
        DbgLog.msg("======= TEARDOWN =======");
        this.f8b.stopActiveOpMode();
        this.f7a.shutdownMotorControllers();
        this.f7a.shutdownServoControllers();
        this.f7a.shutdownLegacyModules();
        this.f7a.shutdownCoreInterfaceDeviceModules();
        DbgLog.msg("======= TEARDOWN COMPLETE =======");
    }

    public void processCommand(Command command) {
        DbgLog.msg("Processing Command: " + command.getName() + " " + command.getExtra());
        this.f7a.sendBatteryInfo();
        String name = command.getName();
        String extra = command.getExtra();
        if (name.equals(CommandList.CMD_RESTART_ROBOT)) {
            m7a();
        } else if (name.equals(CommandList.CMD_REQUEST_OP_MODE_LIST)) {
            m9b();
        } else if (name.equals(CommandList.CMD_INIT_OP_MODE)) {
            m8a(extra);
        } else if (name.equals(CommandList.CMD_RUN_OP_MODE)) {
            m10c();
        } else {
            DbgLog.msg("Unknown command: " + name);
        }
    }

    private void m7a() {
        this.f7a.restartRobot();
    }

    private void m9b() {
        String str = BuildConfig.VERSION_NAME;
        for (String str2 : this.f8b.getOpModes()) {
            if (!str2.equals("Stop Robot")) {
                if (!str.isEmpty()) {
                    str = str + Util.ASCII_RECORD_SEPARATOR;
                }
                str = str + str2;
            }
        }
        this.f7a.sendCommand(new Command(CommandList.CMD_REQUEST_OP_MODE_LIST_RESP, str));
    }

    private void m8a(String str) {
        String opMode = this.f7a.getOpMode(str);
        this.f7a.resetGamepads();
        this.f8b.initActiveOpMode(opMode);
        this.f7a.sendCommand(new Command(CommandList.CMD_INIT_OP_MODE_RESP, opMode));
    }

    private void m10c() {
        this.f8b.startActiveOpMode();
        this.f7a.sendCommand(new Command(CommandList.CMD_RUN_OP_MODE_RESP, this.f8b.getActiveOpModeName()));
    }
}
