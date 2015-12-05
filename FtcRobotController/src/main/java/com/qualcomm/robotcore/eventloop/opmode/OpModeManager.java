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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class OpModeManager {
    public static final OpMode DEFAULT_OP_MODE;
    public static final String DEFAULT_OP_MODE_NAME = "Stop Robot";
    private Map<String, Class<?>> f209a;
    private Map<String, OpMode> f210b;
    private String f211c;
    private OpMode f212d;
    private String f213e;
    private HardwareMap f214f;
    private HardwareMap f215g;
    private C0033b f216h;
    private boolean f217i;
    private boolean f218j;
    private boolean f219k;

    /* renamed from: com.qualcomm.robotcore.eventloop.opmode.OpModeManager.a */
    private static class C0032a extends OpMode {
        public void init() {
            m185a();
        }

        public void init_loop() {
            m185a();
            this.telemetry.addData("Status", "Robot is stopped");
        }

        public void loop() {
            m185a();
            this.telemetry.addData("Status", "Robot is stopped");
        }

        public void stop() {
        }

        private void m185a() {
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

    /* renamed from: com.qualcomm.robotcore.eventloop.opmode.OpModeManager.b */
    private enum C0033b {
        INIT,
        LOOPING
    }

    static {
        DEFAULT_OP_MODE = new C0032a();
    }

    public OpModeManager(HardwareMap hardwareMap) {
        this.f209a = new LinkedHashMap();
        this.f210b = new LinkedHashMap();
        this.f211c = DEFAULT_OP_MODE_NAME;
        this.f212d = DEFAULT_OP_MODE;
        this.f213e = DEFAULT_OP_MODE_NAME;
        this.f214f = new HardwareMap();
        this.f215g = new HardwareMap();
        this.f216h = C0033b.INIT;
        this.f217i = false;
        this.f218j = false;
        this.f219k = false;
        this.f214f = hardwareMap;
        register(DEFAULT_OP_MODE_NAME, C0032a.class);
        initActiveOpMode(DEFAULT_OP_MODE_NAME);
    }

    public void registerOpModes(OpModeRegister register) {
        register.register(this);
    }

    public void setHardwareMap(HardwareMap hardwareMap) {
        this.f214f = hardwareMap;
    }

    public HardwareMap getHardwareMap() {
        return this.f214f;
    }

    public Set<String> getOpModes() {
        Set<String> linkedHashSet = new LinkedHashSet();
        linkedHashSet.addAll(this.f209a.keySet());
        linkedHashSet.addAll(this.f210b.keySet());
        return linkedHashSet;
    }

    public String getActiveOpModeName() {
        return this.f211c;
    }

    public OpMode getActiveOpMode() {
        return this.f212d;
    }

    public void initActiveOpMode(String name) {
        this.f213e = name;
        this.f217i = true;
        this.f218j = true;
        this.f216h = C0033b.INIT;
    }

    public void startActiveOpMode() {
        this.f216h = C0033b.LOOPING;
        this.f219k = true;
    }

    public void stopActiveOpMode() {
        this.f212d.stop();
        initActiveOpMode(DEFAULT_OP_MODE_NAME);
    }

    public void runActiveOpMode(Gamepad[] gamepads) {
        this.f212d.time = this.f212d.getRuntime();
        this.f212d.gamepad1 = gamepads[0];
        this.f212d.gamepad2 = gamepads[1];
        if (this.f217i) {
            this.f212d.stop();
            m186a();
            this.f216h = C0033b.INIT;
            this.f218j = true;
        }
        if (this.f216h == C0033b.INIT) {
            if (this.f218j) {
                this.f212d.hardwareMap = this.f214f;
                this.f212d.resetStartTime();
                this.f212d.init();
                this.f218j = false;
            }
            this.f212d.init_loop();
            return;
        }
        if (this.f219k) {
            this.f212d.start();
            this.f219k = false;
        }
        this.f212d.loop();
    }

    public void logOpModes() {
        RobotLog.i("There are " + (this.f209a.size() + this.f210b.size()) + " Op Modes");
        for (Entry key : this.f209a.entrySet()) {
            RobotLog.i("   Op Mode: " + ((String) key.getKey()));
        }
        for (Entry key2 : this.f210b.entrySet()) {
            RobotLog.i("   Op Mode: " + ((String) key2.getKey()));
        }
    }

    public void register(String name, Class opMode) {
        if (m188a(name)) {
            throw new IllegalArgumentException("Cannot register the same op mode name twice");
        }
        this.f209a.put(name, opMode);
    }

    public void register(String name, OpMode opMode) {
        if (m188a(name)) {
            throw new IllegalArgumentException("Cannot register the same op mode name twice");
        }
        this.f210b.put(name, opMode);
    }

    private void m186a() {
        RobotLog.i("Attempting to switch to op mode " + this.f213e);
        try {
            if (this.f210b.containsKey(this.f213e)) {
                this.f212d = (OpMode) this.f210b.get(this.f213e);
            } else {
                this.f212d = (OpMode) ((Class) this.f209a.get(this.f213e)).newInstance();
            }
            this.f211c = this.f213e;
        } catch (Exception e) {
            m187a(e);
        } catch (Exception e2) {
            m187a(e2);
        }
        this.f217i = false;
    }

    private boolean m188a(String str) {
        return getOpModes().contains(str);
    }

    private void m187a(Exception exception) {
        RobotLog.e("Unable to start op mode " + this.f211c);
        RobotLog.logStacktrace(exception);
        this.f211c = DEFAULT_OP_MODE_NAME;
        this.f212d = DEFAULT_OP_MODE;
    }
}
