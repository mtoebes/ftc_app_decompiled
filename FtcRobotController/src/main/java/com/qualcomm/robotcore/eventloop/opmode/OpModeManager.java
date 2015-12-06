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
    private String newOpModeName = null;
    private HardwareMap hardwareMap;
    private opModeState activeOpModeState = opModeState.INIT;
    private boolean isOpModeStarted = false;
    private boolean waitForStart = true;

    private static class DefaultOpMode extends OpMode {
        public void init() {
            disableHardware();
        }

        public void init_loop() {
            disableHardware();
            telemetry.addData("Status", "Robot is stopped");
        }

        public void loop() {
            disableHardware();
            telemetry.addData("Status", "Robot is stopped");
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
        return hardwareMap;
    }

    public Set<String> getOpModes() {
        Set<String> linkedHashSet = new LinkedHashSet<String>();
        linkedHashSet.addAll(opModeClasses.keySet());
        linkedHashSet.addAll(opModes.keySet());
        return linkedHashSet;
    }

    public String getActiveOpModeName() {
        return activeOpModeName;
    }

    public OpMode getActiveOpMode() {
        return activeOpMode;
    }

    public void initActiveOpMode(String name) {
        newOpModeName = name;
        activeOpModeState = opModeState.INIT; // Stop old opMode from Looping
    }

    public void startActiveOpMode() {
        activeOpModeState = opModeState.LOOPING;
        waitForStart = false;
    }

    public void stopActiveOpMode() {
        activeOpMode.stop();
        initActiveOpMode(DEFAULT_OP_MODE_NAME);
    }

    public void runActiveOpMode(Gamepad[] gamepads) {
        activeOpMode.time = activeOpMode.getRuntime();
        activeOpMode.gamepad1 = gamepads[0];
        activeOpMode.gamepad2 = gamepads[1];
        if (newOpModeName != null) {
            // Do swap here rather than initActiveOpMode to get correct resetStartTime and hardwareMap
            activeOpMode.stop();
            switchActiveOpMode();
            activeOpModeState = opModeState.INIT;
            activeOpMode.hardwareMap = hardwareMap;
            activeOpMode.resetStartTime();
            activeOpMode.init();
        }

        if (activeOpModeState == opModeState.INIT) {
            activeOpMode.init_loop();
        } else {
            if (!isOpModeStarted && !waitForStart) {
                activeOpMode.start();
                isOpModeStarted = true;
            }
            activeOpMode.loop();
        }
    }

    public void logOpModes() {
        RobotLog.i("There are " + (opModeClasses.size() + opModes.size()) + " Op Modes");
        for (Entry opModeClass : opModeClasses.entrySet()) {
            RobotLog.i("   Op Mode: " + opModeClass.getKey());
        }
        for (Entry opMode : opModes.entrySet()) {
            RobotLog.i("   Op Mode: " + opMode.getKey());
        }
    }

    public void register(String name, Class opMode) {
        if (isRegistered(name)) {
            throw new IllegalArgumentException("Cannot register the same op mode name twice");
        }
        opModeClasses.put(name, opMode);
    }

    public void register(String name, OpMode opMode) {
        if (isRegistered(name)) {
            throw new IllegalArgumentException("Cannot register the same op mode name twice");
        }
        opModes.put(name, opMode);
    }

    private void switchActiveOpMode() {
        RobotLog.i("Attempting to switch to op mode " + newOpModeName);
        try {
            if (opModes.containsKey(newOpModeName)) {
                activeOpMode = opModes.get(newOpModeName);
            } else {
                activeOpMode = (OpMode) ((Class) opModeClasses.get(newOpModeName)).newInstance();
            }
            activeOpModeName = newOpModeName;
        } catch (Exception e) {
            switchActiveOpModeFailed(e);
        }
        newOpModeName = null;
    }

    private boolean isRegistered(String str) {
        return getOpModes().contains(str);
    }

    private void switchActiveOpModeFailed(Exception exception) {
        RobotLog.e("Unable to start op mode " + activeOpModeName);
        RobotLog.logStacktrace(exception);
        activeOpModeName = DEFAULT_OP_MODE_NAME;
        activeOpMode = DEFAULT_OP_MODE;
    }
}
