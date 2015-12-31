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

import com.qualcomm.robotcore.hardware.IrSeekerSensor;

//------------------------------------------------------------------------------
//
// PushBotTelemetrySensors
//

/**
 * Provide telemetry provided by the PushBotHardwareSensors class.
 * <p/>
 * Insert this class between a custom op-mode and the PushBotHardwareSensors
 * class to display telemetry available from the hardware sensor class.
 *
 * @author SSI Robotics
 * @version 2015-08-02-13-57
 *          <p/>
 *          Telemetry Keys
 *          12 - The position of the touch sensor (true=pressed/false=not pressed).
 *          13 - The angle returned by the IR seeker class, which indicates the
 *          direction of the IR beacon.
 *          14 - The strength of the IR beacon.
 *          14 - The angle and strength returned by the IR seeker's first internal
 *          sensor.
 *          15 - The angle and strength returned by the IR seeker's second internal
 *          sensor.
 *          17 - The value returned by the optical distance sensor class, which
 *          indicates the amount of reflected light detected by the sensor.
 */
public class PushBotTelemetrySensors extends PushBotHardwareSensors

{
    //--------------------------------------------------------------------------
    //
    // PushBotTelemetrySensors
    //

    /**
     * Construct the class.
     * <p/>
     * The system calls this member when the class is instantiated.
     */
    public PushBotTelemetrySensors()

    {
        //
        // Initialize base classes.
        //
        // All via self-construction.

        //
        // Initialize class members.
        //
        // All via self-construction.

    } // PushBotTelemetrySensors

    //--------------------------------------------------------------------------
    //
    // update_telemetry
    //

    /**
     * Update the telemetry with current values from the base class.
     */
    public void update_telemetry()

    {
        //
        // Use a base class method to update telemetry for non-sensor hardware
        // (i.e. left/right drive wheels, left arm, etc.).
        //
        super.update_telemetry();

        //
        // Send telemetry data to the driver station.
        //
        telemetry.addData
                ("12"
                        , "Touch: " + is_touch_sensor_pressed()
                );
        telemetry.addData
                ("13"
                        , "IR Angle: " + a_ir_angle()
                );
        telemetry.addData
                ("14"
                        , "IR Strength: " + a_ir_strength()
                );
        IrSeekerSensor.IrSeekerIndividualSensor[] l_ir_angles_and_strengths
                = a_ir_angles_and_strengths();
        telemetry.addData
                ("15"
                        , "IR Sensor 1: " + l_ir_angles_and_strengths[0].toString()
                );
        telemetry.addData
                ("16"
                        , "IR Sensor 2: " + l_ir_angles_and_strengths[1].toString()
                );
        telemetry.addData
                ("17"
                        , "ODS: " + a_ods_light_detected()
                );

    } // update_telemetry

} // PushBotTelemetrySensors
