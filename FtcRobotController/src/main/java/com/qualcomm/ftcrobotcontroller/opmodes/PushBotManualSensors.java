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

//------------------------------------------------------------------------------
//
// PushBotManualSensors
//

/**
 * Provide a basic manual operational mode that uses the left and right
 * drive motors, left arm motor, servo motors and gamepad input from two
 * gamepads for the Push Bot.  This manual op-mode use the touch sensors and
 * gamepad controls to drive the arm to a predefined position (i.e. the location
 * of the touch sensor).
 *
 * @author SSI Robotics
 * @version 2015-08-25-14-40
 */
public class PushBotManualSensors extends PushBotTelemetrySensors

{
    //--------------------------------------------------------------------------
    //
    // PushBotManualSensors
    //

    /**
     * Construct the class.
     * <p/>
     * The system calls this member when the class is instantiated.
     */
    public PushBotManualSensors()

    {
        //
        // Initialize base classes.
        //
        // All via self-construction.

        //
        // Initialize class members.
        //
        // All via self-construction.

    } // PushBotManualSensors

    //--------------------------------------------------------------------------
    //
    // loop
    //

    /**
     * Implement a state machine that controls the robot during
     * manual-operation.  The state machine uses gamepad and sensor input to
     * transition between states.
     * <p/>
     * The system calls this member repeatedly while the OpMode is running.
     */
    @Override
    public void loop()

    {
        //----------------------------------------------------------------------
        //
        // DC Motors
        //
        // Obtain the current values of the joystick controllers.
        //
        // Note that x and y equal -1 when the joystick is pushed all of the way
        // forward (i.e. away from the human holder's body).
        //
        // The clip method guarantees the value never exceeds the range +-1.
        //
        // The DC motors are scaled to make it easier to control them at slower
        // speeds.
        //
        // The setPower methods write the motor power values to the DcMotor
        // class, but the power levels aren't applied until this method ends.
        //

        //
        // Manage the drive wheel motors.
        //
        float l_gp1_left_stick_y = -gamepad1.left_stick_y;
        float l_left_drive_power
                = (float) scale_motor_power(l_gp1_left_stick_y);

        float l_gp1_right_stick_y = -gamepad1.right_stick_y;
        float l_right_drive_power
                = (float) scale_motor_power(l_gp1_right_stick_y);

        set_drive_power(l_left_drive_power, l_right_drive_power);

        //
        // Does the user want the arm to rise until the touch sensor is
        // triggered (i.e. is the Y button on gamepad 2 being held down)?
        //
        // Realize that the button may be depressed for multiple loops - the
        // human hand can only react so fast...this loop will be called multiple
        // times before the button is released.
        //
        if (gamepad2.y) {
            //
            // If the button has been pressed from a previous iteration, then
            // do not set power to the arm once the touch sensor has been
            // triggered.
            //
            if (!v_raise_arm_automatically) {
                v_raise_arm_automatically = true;
            }
        }

        //
        // Has the user commanded the arm to be raised until the touch sensor
        // has been pressed?
        //
        float l_gp2_left_stick_y = -gamepad2.left_stick_y;
        float l_arm_command = 0.0f;
        if (v_raise_arm_automatically) {
            //
            // Has the touch sensor been triggered? Or has the user cancelled
            // the operation with the joystick?
            //
            l_arm_command = 1.0f;
            if ((is_touch_sensor_pressed()) ||
                    (Math.abs(l_gp2_left_stick_y) > 0.8)
                    ) {
                //
                // Stop moving the arm.
                //
                l_arm_command = 0.0f;
                v_raise_arm_automatically = false;
            }
        }
        //
        // The user has not commanded the use of the touch sensor.  Apply power
        // to the arm motor according to the joystick value.
        //
        else {
            v_raise_arm_automatically = false;

            l_arm_command = (float) scale_motor_power(l_gp2_left_stick_y);
        }
        m_left_arm_power(l_arm_command);

        //----------------------------------------------------------------------
        //
        // Servo Motors
        //
        // Obtain the current values of the gamepad 'x' and 'b' buttons.
        //
        // Note that x and b buttons have boolean values of true and false.
        //
        // The clip method guarantees the value never exceeds the allowable range of
        // [0,1].
        //
        // The setPosition methods write the motor power values to the Servo
        // class, but the positions aren't applied until this method ends.
        //
        if (gamepad2.x) {
            m_hand_position(a_hand_position() + 0.05);
        } else if (gamepad2.b) {
            m_hand_position(a_hand_position() - 0.05);
        }

        //
        // Send telemetry data to the driver station.
        //
        update_telemetry(); // Update common telemetry
        telemetry.addData("18", "Raise Arm: " + v_raise_arm_automatically);
        telemetry.addData("19", "Left arm command: " + l_arm_command);

    } // loop

    //--------------------------------------------------------------------------
    //
    // v_raise_arm_automatically
    //
    //--------
    // This class member remembers whether the 'Y' button has been pressed AND
    // released.  The arm is only driven when the button has been pressed AND
    // released to avoid the phenomena where a humans hand holds the button for
    // multiple iterations of the loop method.
    //--------
    private boolean v_raise_arm_automatically = false;

} // PushBotManualSensors
