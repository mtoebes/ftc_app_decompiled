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

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by Jordan Burklund on 7/30/2015.
 * An example linear op mode where the pushbot
 * will track an IR beacon.
 */
public class PushBotIrSeek extends LinearOpMode {
    final static double kBaseSpeed = 0.15;  // Higher values will cause the robot to move faster

    final static double kMinimumStrength = 0.08; // Higher values will cause the robot to follow closer
    final static double kMaximumStrength = 0.60; // Lower values will cause the robot to stop sooner

    IrSeekerSensor irSeeker;
    DcMotor leftMotor;
    DcMotor rightMotor;

    @Override
    public void runOpMode() throws InterruptedException {
        irSeeker = hardwareMap.irSeekerSensor.get("sensor_ir");
        leftMotor = hardwareMap.dcMotor.get("left_drive");
        rightMotor = hardwareMap.dcMotor.get("right_drive");
        rightMotor.setDirection(DcMotor.Direction.REVERSE);

        // Wait for the start button to be pressed
        waitForStart();

        // Continuously track the IR beacon
        while (opModeIsActive()) {
            double angle = irSeeker.getAngle() / 30;  // value between -4...4
            double strength = irSeeker.getStrength();
            if (strength > kMinimumStrength && strength < kMaximumStrength) {
                double leftSpeed = Range.clip(kBaseSpeed + (angle / 8), -1, 1);
                double rightSpeed = Range.clip(kBaseSpeed - (angle / 8), -1, 1);
                leftMotor.setPower(leftSpeed);
                rightMotor.setPower(rightSpeed);
            } else {
                leftMotor.setPower(0);
                rightMotor.setPower(0);
            }
            telemetry.addData("Seeker", irSeeker.toString());
            telemetry.addData("Speed", " Left=" + leftMotor.getPower() + " Right=" + rightMotor.getPower());

            //Wait one hardware cycle to avoid taxing the processor
            waitOneFullHardwareCycle();
        }

    }
}
