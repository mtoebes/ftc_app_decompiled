package com.qualcomm.robotcore.eventloop.opmode;

import com.qualcomm.robotcore.BuildConfig;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorController.DeviceMode;
import com.qualcomm.robotcore.hardware.DcMotorController.RunMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.util.RobotLog;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class OpModeManager {
    public static final OpMode DEFAULT_OP_MODE;
    public static final String DEFAULT_OP_MODE_NAME = "$Stop$Robot$";
    protected OpMode activeOpMode;
    protected String activeOpModeName;
    protected boolean callToInitNeeded;
    protected boolean callToStartNeeded;
    protected EventLoopManager eventLoopManager;
    protected boolean gamepadResetNeeded;
    protected HardwareMap hardwareMap;
    protected Map<String, Class<?>> opModeClasses;
    protected Map<String, OpMode> opModeObjects;
    protected OpModeState opModeState;
    protected boolean opModeSwapNeeded;
    protected String queuedOpModeName;
    protected boolean telemetryClearNeeded;

    protected enum OpModeState {
        INIT,
        LOOPING
    }

    /* renamed from: com.qualcomm.robotcore.eventloop.opmode.OpModeManager.a */
    private static class C0036a extends OpMode {
        public void init() {
            m195a();
        }

        public void init_loop() {
            m195a();
            this.telemetry.addData("Status", "Robot is stopped");
        }

        public void loop() {
            m195a();
            this.telemetry.addData("Status", "Robot is stopped");
        }

        public void stop() {
        }

        private void m195a() {
            Iterator it = this.hardwareMap.servoController.iterator();
            while (it.hasNext()) {
                ((ServoController) it.next()).pwmDisable();
            }
            it = this.hardwareMap.dcMotorController.iterator();
            while (it.hasNext()) {
                ((DcMotorController) it.next()).setMotorControllerDeviceMode(DeviceMode.WRITE_ONLY);
            }
            it = this.hardwareMap.dcMotor.iterator();
            while (it.hasNext()) {
                DcMotor dcMotor = (DcMotor) it.next();
                dcMotor.setPower(0.0d);
                dcMotor.setMode(RunMode.RUN_WITHOUT_ENCODERS);
            }
            it = this.hardwareMap.lightSensor.iterator();
            while (it.hasNext()) {
                ((LightSensor) it.next()).enableLed(false);
            }
        }
    }

    static {
        DEFAULT_OP_MODE = new C0036a();
    }

    public OpModeManager(HardwareMap hardwareMap) {
        this.opModeClasses = new LinkedHashMap();
        this.opModeObjects = new LinkedHashMap();
        this.activeOpModeName = DEFAULT_OP_MODE_NAME;
        this.activeOpMode = DEFAULT_OP_MODE;
        this.queuedOpModeName = DEFAULT_OP_MODE_NAME;
        this.hardwareMap = null;
        this.opModeState = OpModeState.INIT;
        this.opModeSwapNeeded = false;
        this.callToInitNeeded = false;
        this.callToStartNeeded = false;
        this.gamepadResetNeeded = false;
        this.telemetryClearNeeded = false;
        this.eventLoopManager = null;
        this.hardwareMap = hardwareMap;
        register(DEFAULT_OP_MODE_NAME, C0036a.class);
        initActiveOpMode(DEFAULT_OP_MODE_NAME);
    }

    public void init(EventLoopManager eventLoopManager) {
        this.eventLoopManager = eventLoopManager;
    }

    public synchronized void registerOpModes(OpModeRegister register) {
        register.register(this);
    }

    public synchronized void setHardwareMap(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
    }

    public synchronized HardwareMap getHardwareMap() {
        return this.hardwareMap;
    }

    public synchronized Set<String> getOpModes() {
        Set<String> linkedHashSet;
        linkedHashSet = new LinkedHashSet();
        linkedHashSet.addAll(this.opModeClasses.keySet());
        linkedHashSet.addAll(this.opModeObjects.keySet());
        return linkedHashSet;
    }

    public synchronized String getActiveOpModeName() {
        return this.activeOpModeName;
    }

    public synchronized OpMode getActiveOpMode() {
        return this.activeOpMode;
    }

    public synchronized void initActiveOpMode(String name) {
        this.queuedOpModeName = name;
        this.opModeSwapNeeded = true;
        this.callToInitNeeded = true;
        this.gamepadResetNeeded = true;
        this.telemetryClearNeeded = true;
        this.opModeState = OpModeState.INIT;
    }

    public synchronized void startActiveOpMode() {
        this.opModeState = OpModeState.LOOPING;
        this.callToStartNeeded = true;
    }

    public synchronized void stopActiveOpMode() {
        callActiveOpModeStop();
        initActiveOpMode(DEFAULT_OP_MODE_NAME);
    }

    public synchronized void runActiveOpMode(Gamepad[] gamepads) {
        this.activeOpMode.time = this.activeOpMode.getRuntime();
        this.activeOpMode.gamepad1 = gamepads[0];
        this.activeOpMode.gamepad2 = gamepads[1];
        if (this.gamepadResetNeeded) {
            this.activeOpMode.gamepad1.reset();
            this.activeOpMode.gamepad2.reset();
            this.gamepadResetNeeded = false;
        }
        if (this.telemetryClearNeeded && this.eventLoopManager != null) {
            Telemetry telemetry = new Telemetry();
            telemetry.addData("\u0000", BuildConfig.VERSION_NAME);
            this.eventLoopManager.sendTelemetryData(telemetry);
            this.telemetryClearNeeded = false;
        }
        if (this.opModeSwapNeeded) {
            callActiveOpModeStop();
            m196a();
            this.opModeState = OpModeState.INIT;
            this.callToInitNeeded = true;
        }
        if (this.opModeState == OpModeState.INIT) {
            if (this.callToInitNeeded) {
                this.activeOpMode.gamepad1 = gamepads[0];
                this.activeOpMode.gamepad2 = gamepads[1];
                this.activeOpMode.hardwareMap = this.hardwareMap;
                this.activeOpMode.resetStartTime();
                callActiveOpModeInit();
                this.callToInitNeeded = false;
            }
            callActiveOpModeInitLoop();
        } else {
            if (this.callToStartNeeded) {
                callActiveOpModeStart();
                this.callToStartNeeded = false;
            }
            callActiveOpModeLoop();
        }
    }

    public synchronized void logOpModes() {
        RobotLog.m252i("There are " + (this.opModeClasses.size() + this.opModeObjects.size()) + " Op Modes");
        for (Entry key : this.opModeClasses.entrySet()) {
            RobotLog.m252i("   Op Mode: " + ((String) key.getKey()));
        }
        for (Entry key2 : this.opModeObjects.entrySet()) {
            RobotLog.m252i("   Op Mode: " + ((String) key2.getKey()));
        }
    }

    public synchronized void register(String name, Class opMode) {
        if (m198a(name)) {
            throw new IllegalArgumentException(String.format("Can't register OpMode name twice: '%s'", new Object[]{name}));
        }
        this.opModeClasses.put(name, opMode);
    }

    public synchronized void register(String name, OpMode opMode) {
        if (m198a(name)) {
            throw new IllegalArgumentException(String.format("Can't register OpMode name twice: '%s'", new Object[]{name}));
        }
        this.opModeObjects.put(name, opMode);
    }

    private void m196a() {
        RobotLog.m252i("Attempting to switch to op mode " + this.queuedOpModeName);
        try {
            if (this.opModeObjects.containsKey(this.queuedOpModeName)) {
                this.activeOpMode = (OpMode) this.opModeObjects.get(this.queuedOpModeName);
            } else {
                this.activeOpMode = (OpMode) ((Class) this.opModeClasses.get(this.queuedOpModeName)).newInstance();
            }
            this.activeOpModeName = this.queuedOpModeName;
        } catch (Exception e) {
            m197a(e);
        } catch (Exception e2) {
            m197a(e2);
        }
        this.opModeSwapNeeded = false;
    }

    private boolean m198a(String str) {
        return getOpModes().contains(str);
    }

    private void m197a(Exception exception) {
        RobotLog.m250e("Unable to start op mode " + this.activeOpModeName);
        RobotLog.logStacktrace(exception);
        this.activeOpModeName = DEFAULT_OP_MODE_NAME;
        this.activeOpMode = DEFAULT_OP_MODE;
    }

    protected void callActiveOpModeStop() {
        this.activeOpMode.stop();
    }

    protected void callActiveOpModeInit() {
        this.activeOpMode.init();
    }

    protected void callActiveOpModeStart() {
        this.activeOpMode.start();
    }

    protected void callActiveOpModeInitLoop() {
        this.activeOpMode.init_loop();
    }

    protected void callActiveOpModeLoop() {
        this.activeOpMode.loop();
    }
}
