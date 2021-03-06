/*
 * Copyright (c) 2014, 2015 Qualcomm Technologies Inc
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * (subject to the limitations in the disclaimer below) provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Qualcomm Technologies Inc nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

/**
 * Manages Op Modes Able to switch between op modes
 */
public class OpModeManager {
    public static final OpMode DEFAULT_OP_MODE = new DefaultOpMode();
    public static final String DEFAULT_OP_MODE_NAME = "Stop Robot";
    private Map<String, Class<?>> opModeClasses = new LinkedHashMap<String, Class<?>>();
    private Map<String, OpMode> opModes = new LinkedHashMap<String, OpMode>();
    private String activeOpModeName = DEFAULT_OP_MODE_NAME;
    private OpMode activeOpMode = DEFAULT_OP_MODE;
    private String newOpModeName;
    private HardwareMap hardwareMap = new HardwareMap();
    private OpModeState activeOpModeState = OpModeState.INIT;
    private boolean hasNewOpMode;
    private boolean isActiveOpModeStarted = true;

    private static class DefaultOpMode extends OpMode {
        public void init() {
            disable();
        }

        public void init_loop() {
            disable();
            this.telemetry.addData("Status", "Robot is stopped");
        }

        public void loop() {
            disable();
            this.telemetry.addData("Status", "Robot is stopped");
        }

        public void stop() {
        }

        private void disable() {
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

    private enum OpModeState {
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
        this.newOpModeName = name;
        this.hasNewOpMode = true;
        this.activeOpModeState = OpModeState.INIT;
    }

    public void startActiveOpMode() {
        this.activeOpModeState = OpModeState.LOOPING;
        this.isActiveOpModeStarted = false;
    }

    public void stopActiveOpMode() {
        this.activeOpMode.stop();
        initActiveOpMode(DEFAULT_OP_MODE_NAME);
    }

    public void runActiveOpMode(Gamepad[] gamepads) {
        this.activeOpMode.time = this.activeOpMode.getRuntime();
        this.activeOpMode.gamepad1 = gamepads[0];
        this.activeOpMode.gamepad2 = gamepads[1];
        if (this.hasNewOpMode) {
            this.activeOpMode.stop();
            switchToNewOpMode();
            this.activeOpModeState = OpModeState.INIT;
            this.activeOpMode.hardwareMap = this.hardwareMap;
            this.activeOpMode.resetStartTime();
            this.activeOpMode.init();
        }
        if (this.activeOpModeState == OpModeState.INIT) {
            this.activeOpMode.init_loop();
            return;
        }
        if (!this.isActiveOpModeStarted) {
            this.activeOpMode.start();
            this.isActiveOpModeStarted = true;
        }
        this.activeOpMode.loop();
    }

    public void logOpModes() {
        RobotLog.i("There are " + (this.opModeClasses.size() + this.opModes.size()) + " Op Modes");
        for (Entry key : this.opModeClasses.entrySet()) {
            RobotLog.i("   Op Mode: " + (key.getKey()));
        }
        for (Entry key : this.opModes.entrySet()) {
            RobotLog.i("   Op Mode: " + (key.getKey()));
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

    private void switchToNewOpMode() {
        RobotLog.i("Attempting to switch to op mode " + this.newOpModeName);
        try {
            if (this.opModes.containsKey(this.newOpModeName)) {
                this.activeOpMode = this.opModes.get(this.newOpModeName);
            } else {
                this.activeOpMode = (OpMode) ((Class) this.opModeClasses.get(this.newOpModeName)).newInstance();
            }
            this.activeOpModeName = this.newOpModeName;
        } catch (Exception e) {
            RobotLog.e("Unable to start op mode " + this.activeOpModeName);
            RobotLog.logStacktrace(e);
            this.activeOpModeName = DEFAULT_OP_MODE_NAME;
            this.activeOpMode = DEFAULT_OP_MODE;
        }
        this.hasNewOpMode = false;
    }

    private boolean isRegistered(String str) {
        return getOpModes().contains(str);
    }
}
