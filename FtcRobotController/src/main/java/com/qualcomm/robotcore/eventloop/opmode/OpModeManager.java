package com.qualcomm.robotcore.eventloop.opmode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorController.DeviceMode;
import com.qualcomm.robotcore.hardware.DcMotorController.RunMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.RobotLog;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class OpModeManager {
    public static final OpMode DEFAULT_OP_MODE = new DefaultOpMode();
    public static final String DEFAULT_OP_MODE_NAME = "Stop Robot";
    private Map<String, Class<?>> opModeClasses = new LinkedHashMap<String, Class<?>>();
    private Map<String, OpMode> opModes = new LinkedHashMap<String, OpMode>();
    private String activeOpModeName = DEFAULT_OP_MODE_NAME;
    private OpMode activeOpMode = DEFAULT_OP_MODE;
    private String swapOpModeName;
    private HardwareMap hardwareMap;
    private opModeState activeOpModeState = opModeState.INIT;
    private boolean isSwapOpMode = false;
    private boolean f219k = false;

    private static class DefaultOpMode extends OpMode {
        public void init() {
            disableHardware();
        }

        public void init_loop() {
            disableHardware();
            this.telemetry.addData("Status", "Robot is stopped");
        }

        public void loop() {
            disableHardware();
            this.telemetry.addData("Status", "Robot is stopped");
        }

        public void stop() {
        }

        private void disableHardware() {
            for(ServoController servoController : hardwareMap.servoController) {
                servoController.pwmDisable();
            }

            for(DcMotorController DcMotorController : hardwareMap.dcMotorController) {
                DcMotorController.setMotorControllerDeviceMode(DeviceMode.WRITE_ONLY);
            }

            for(DcMotor dcMotor : hardwareMap.dcMotor) {
                dcMotor.setPower(0.0d);
                dcMotor.setMode(RunMode.RUN_WITHOUT_ENCODERS);
            }

            for(LightSensor lightSensor : hardwareMap.lightSensor) {
                lightSensor.enableLed(false);
            }
        }
    }

    private enum opModeState {
        INIT,
        LOOPING
    }

    public OpModeManager(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        register(DEFAULT_OP_MODE_NAME, DefaultOpMode.class);
        initActiveOpMode(DEFAULT_OP_MODE_NAME);
    }

    public void registerOpModes(OpModeRegister register) {
        register.register(this);
    }

    public void setHardwareMap(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
    }

    public HardwareMap getHardwareMap() {
        return this.hardwareMap;
    }

    public Set<String> getOpModes() {
        Set<String> linkedHashSet = new LinkedHashSet<String>();
        linkedHashSet.addAll(this.opModeClasses.keySet());
        linkedHashSet.addAll(this.opModes.keySet());
        return linkedHashSet;
    }

    public String getActiveOpModeName() {
        return this.activeOpModeName;
    }

    public OpMode getActiveOpMode() {
        return this.activeOpMode;
    }

    public void initActiveOpMode(String name) {
        this.swapOpModeName = name;
        this.isSwapOpMode = true;
        this.activeOpModeState = opModeState.INIT;
    }

    public void startActiveOpMode() {
        this.activeOpModeState = opModeState.LOOPING;
        this.f219k = true;
    }

    public void stopActiveOpMode() {
        this.activeOpMode.stop();
        initActiveOpMode(DEFAULT_OP_MODE_NAME);
    }

    public void runActiveOpMode(Gamepad[] gamepads) {
        this.activeOpMode.time = this.activeOpMode.getRuntime();
        this.activeOpMode.gamepad1 = gamepads[0];
        this.activeOpMode.gamepad2 = gamepads[1];
        if (this.isSwapOpMode) {
            this.activeOpMode.stop();
            switchActiveOpMode();
            this.activeOpModeState = opModeState.INIT;
            this.activeOpMode.hardwareMap = this.hardwareMap;
            this.activeOpMode.resetStartTime();
            this.activeOpMode.init();
        }
        if (this.activeOpModeState == opModeState.INIT) {
            this.activeOpMode.init_loop();
            return;
        }
        if (this.f219k) {
            this.activeOpMode.start();
            this.f219k = false;
        }
        this.activeOpMode.loop();
    }

    public void logOpModes() {
        RobotLog.i("There are " + (this.opModeClasses.size() + this.opModes.size()) + " Op Modes");
        for (Entry key : this.opModeClasses.entrySet()) {
            RobotLog.i("   Op Mode: " + key.getKey());
        }
        for (Entry key2 : this.opModes.entrySet()) {
            RobotLog.i("   Op Mode: " + key2.getKey());
        }
    }

    public void register(String name, Class opMode) {
        if (isRegistered(name)) {
            throw new IllegalArgumentException("Cannot register the same op mode name twice");
        }
        this.opModeClasses.put(name, opMode);
    }

    public void register(String name, OpMode opMode) {
        if (isRegistered(name)) {
            throw new IllegalArgumentException("Cannot register the same op mode name twice");
        }
        this.opModes.put(name, opMode);
    }

    private void switchActiveOpMode() {
        RobotLog.i("Attempting to switch to op mode " + this.swapOpModeName);
        try {
            if (this.opModes.containsKey(this.swapOpModeName)) {
                this.activeOpMode = this.opModes.get(this.swapOpModeName);
            } else {
                this.activeOpMode = (OpMode) ((Class) this.opModeClasses.get(this.swapOpModeName)).newInstance();
            }
            this.activeOpModeName = this.swapOpModeName;
        } catch (Exception e) {
            switchActiveOpModeFailed(e);
        }
        this.isSwapOpMode = false;
    }

    private boolean isRegistered(String str) {
        return getOpModes().contains(str);
    }

    private void switchActiveOpModeFailed(Exception exception) {
        RobotLog.e("Unable to start op mode " + this.activeOpModeName);
        RobotLog.logStacktrace(exception);
        this.activeOpModeName = DEFAULT_OP_MODE_NAME;
        this.activeOpMode = DEFAULT_OP_MODE;
    }
}
