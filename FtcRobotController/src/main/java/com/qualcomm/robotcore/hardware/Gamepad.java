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

package com.qualcomm.robotcore.hardware;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.RobocolParsable;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.RobotLog;

import java.nio.ByteBuffer;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.Set;

/**
 * Monitor a hardware gamepad.
 * <p/>
 * The buttons, analog sticks, and triggers are represented a public member variables that can be read from or written to directly.
 * <p/>
 * Analog sticks are represented as floats that range from -1.0 to +1.0. They will be 0.0 while at rest. The horizontal axis is labeled x, and the vertical axis is labeled y.
 * <p/>
 * Triggers are represented as floats that range from 0.0 to 1.0. They will be at 0.0 while at rest.
 * <p/>
 * Buttons are boolean values. They will be true if the button is pressed, otherwise they will be false.
 * <p/>
 * The dpad is represented as 4 buttons, dpad_up, dpad_down, dpad_left, and dpad_right
 */
public class Gamepad implements RobocolParsable {
    /**
     * A gamepad with an ID equal to ID_UNASSOCIATED has not been associated with any device.
     */
    public static final int ID_UNASSOCIATED = -1;

    private static final int MAX_JOYSTICK_VALUE = 1;
    private static final short PAYLOAD_SIZE = 42;
    private static final short BUFFER_SIZE = PAYLOAD_SIZE + HEADER_LENGTH;

    private static Set<Integer> gamepadDevices = new HashSet<Integer>();
    private static Set<whitelistDevice> whitelistDevices;
    private final GamepadCallback gamepadCallback;
    /**
     * button a
     */
    public boolean a;
    /**
     * button b
     */
    public boolean b;
    /**
     * button back
     */
    public boolean back;
    /**
     * dpad down
     */
    public boolean dpad_down;
    /**
     * dpad left
     */
    public boolean dpad_left;
    /**
     * dpad right
     */
    public boolean dpad_right;
    /**
     * dpad up
     */
    public boolean dpad_up;
    /**
     * button guide - often the large button in the middle of the controller. The OS may capture this button before it is sent to the app; in which case you'll never receive it.
     */
    public boolean guide;
    /**
     * ID assigned to this gamepad by the OS. This value can change each time the device is plugged in
     */
    public int id = ID_UNASSOCIATED;
    /**
     * button left bumper
     */
    public boolean left_bumper;
    /**
     * left stick button
     */
    public boolean left_stick_button;
    /**
     * left analog stick horizontal axis
     */
    public float left_stick_x;
    /**
     * left analog stick vertical axis
     */
    public float left_stick_y;
    /**
     * left trigger
     */
    public float left_trigger;
    /**
     * button right bumper
     */
    public boolean right_bumper;
    /**
     * right stick button
     */
    public boolean right_stick_button;
    /**
     * right analog stick horizontal axis
     */
    public float right_stick_x;
    /**
     * right analog stick vertical axis
     */
    public float right_stick_y;
    /**
     * right trigger
     */
    public float right_trigger;
    /**
     * button start
     */
    public boolean start;
    /**
     * Relative timestamp of the last time an event was detected
     */
    public long timestamp;
    /**
     * Which user is this gamepad used by
     */
    public byte user = -1;
    /**
     * button x
     */
    public boolean x;
    /**
     * button y
     */
    public boolean y;
    /**
     * DPAD button will be considered pressed when the movement crosses this threshold
     */
    protected float dpadThreshold = 0.2f;
    /**
     * If the motion value is less than the threshold, the controller will be considered at rest
     */
    protected float joystickDeadzone = 0.2f;

    /**
     * Optional callback interface for monitoring changes due to MotionEvents and KeyEvents. This interface can be used to notify you if the gamepad changes due to either a KeyEvent or a MotionEvent. It does not notify you if the gamepad changes for other reasons.
     */
    public interface GamepadCallback {
        /**
         * This method will be called whenever the gamepad state has changed due to either a KeyEvent or a MotionEvent.
         *
         * @param gamepad device which state has changed
         */
        void gamepadChanged(Gamepad gamepad);
    }

    private static class whitelistDevice extends SimpleEntry<Integer, Integer> {
        public whitelistDevice(int vendorId, int productId) {
            super(vendorId, productId);
        }
    }

    public Gamepad() {
        this(null);
    }

    public Gamepad(GamepadCallback callback) {
        this.gamepadCallback = callback;
    }

    /**
     * Copy the state of a gamepad into this gamepad
     *
     * @param gamepad state to be copied from
     * @throws RobotCoreException if the copy fails - gamepad will be in an unknown state if this exception is thrown
     */
    public void copy(Gamepad gamepad) throws RobotCoreException {
        fromByteArray(gamepad.toByteArray());
    }

    /**
     * Reset this gamepad into its inital state
     */
    public void reset() {
        try {
            copy(new Gamepad());
        } catch (RobotCoreException e) {
            RobotLog.e("Gamepad library in an invalid state");
            throw new IllegalStateException("Gamepad library in an invalid state");
        }
    }

    /**
     * Set the joystick deadzone. Must be between 0 and 1.
     *
     * @param deadzone amount of joystick deadzone
     */
    public void setJoystickDeadzone(float deadzone) {
        if ((deadzone < 0) || (deadzone > MAX_JOYSTICK_VALUE)) {
            throw new IllegalArgumentException("deadzone cannot be greater than max joystick value");
        }
        this.joystickDeadzone = deadzone;
    }

    /**
     * Update the gamepad based on a MotionEvent
     *
     * @param event motion event
     */
    public void update(MotionEvent event) {
        this.id = event.getDeviceId();
        this.timestamp = event.getEventTime();
        this.left_stick_x = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_X));
        this.left_stick_y = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_Y));
        this.right_stick_x = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_Z));
        this.right_stick_y = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_RZ));
        this.left_trigger = event.getAxisValue(MotionEvent.AXIS_LTRIGGER);
        this.right_trigger = event.getAxisValue(MotionEvent.AXIS_RTRIGGER);
        this.dpad_down = event.getAxisValue(MotionEvent.AXIS_HAT_Y) > this.dpadThreshold;
        this.dpad_up = event.getAxisValue(MotionEvent.AXIS_HAT_Y) < (-this.dpadThreshold);
        this.dpad_right = event.getAxisValue(MotionEvent.AXIS_HAT_X) > this.dpadThreshold;
        this.dpad_left = event.getAxisValue(MotionEvent.AXIS_HAT_X) < (-this.dpadThreshold);
        callCallback();
    }

    /**
     * Update the gamepad based on a KeyEvent
     *
     * @param event key event
     */
    public void update(KeyEvent event) {
        this.id = event.getDeviceId();
        this.timestamp = event.getEventTime();
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            this.dpad_up = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            this.dpad_down = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            this.dpad_right = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            this.dpad_left = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_A) {
            this.a = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_B) {
            this.b = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_X) {
            this.x = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_Y) {
            this.y = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_MODE) {
            this.guide = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_START) {
            this.start = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.back = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_R1) {
            this.right_bumper = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_L1) {
            this.left_bumper = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_THUMBL) {
            this.left_stick_button = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_THUMBR) {
            this.right_stick_button = pressed(event);
        }
        callCallback();
    }

    public MsgType getRobocolMsgType() {
        return MsgType.GAMEPAD;
    }

    public byte[] toByteArray() throws RobotCoreException {
        ByteBuffer messageBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        try {
            messageBuffer.put(getRobocolMsgType().asByte());
            messageBuffer.putShort(PAYLOAD_SIZE);
            messageBuffer.put((byte) 2);
            messageBuffer.putInt(this.id);
            messageBuffer.putLong(this.timestamp).array();
            messageBuffer.putFloat(this.left_stick_x).array();
            messageBuffer.putFloat(this.left_stick_y).array();
            messageBuffer.putFloat(this.right_stick_x).array();
            messageBuffer.putFloat(this.right_stick_y).array();
            messageBuffer.putFloat(this.left_trigger).array();
            messageBuffer.putFloat(this.right_trigger).array();

            int buttons =
                    ((left_stick_button ? 1 : 0) << 14) +
                            ((right_stick_button ? 1 : 0) << 13) +
                            ((dpad_up ? 1 : 0) << 12) +
                            ((dpad_down ? 1 : 0) << 11) +
                            ((dpad_left ? 1 : 0) << 10) +
                            ((dpad_right ? 1 : 0) << 9) +
                            ((a ? 1 : 0) << 8) +
                            ((b ? 1 : 0) << 7) +
                            ((x ? 1 : 0) << 6) +
                            ((y ? 1 : 0) << 5) +
                            ((guide ? 1 : 0) << 4) +
                            ((start ? 1 : 0) << 3) +
                            ((back ? 1 : 0) << 2) +
                            ((left_bumper ? 1 : 0) << 1) +
                            (right_bumper ? 1 : 0);

            messageBuffer.putInt(buttons);
            messageBuffer.put(user);
        } catch (Exception e) {
            RobotLog.logStacktrace(e);
        }
        return messageBuffer.array();
    }

    public void fromByteArray(byte[] byteArray) throws RobotCoreException {
        if (byteArray.length < BUFFER_SIZE) {
            throw new RobotCoreException("Expected buffer of at least " + BUFFER_SIZE + " bytes, received " + byteArray.length);
        }
        ByteBuffer wrap = ByteBuffer.wrap(byteArray, HEADER_LENGTH, PAYLOAD_SIZE);
        byte version = wrap.get();
        if (version >= 1) {
            this.id = wrap.getInt();
            this.timestamp = wrap.getLong();
            this.left_stick_x = wrap.getFloat();
            this.left_stick_y = wrap.getFloat();
            this.right_stick_x = wrap.getFloat();
            this.right_stick_y = wrap.getFloat();
            this.left_trigger = wrap.getFloat();
            this.right_trigger = wrap.getFloat();
            int i = wrap.getInt();
            this.left_stick_button = (i & (1 << 14)) != 0;
            this.right_stick_button = (i & (1 << 13)) != 0;
            this.dpad_up = (i & (1 << 12)) != 0;
            this.dpad_down = (i & (1 << 11)) != 0;
            this.dpad_left = (i & (1 << 10)) != 0;
            this.dpad_right = (i & (1 << 9)) != 0;
            this.a = (i & (1 << 8)) != 0;
            this.b = (i & (1 << 7)) != 0;
            this.x = (i & (1 << 6)) != 0;
            this.y = (i & (1 << 5)) != 0;
            this.guide = (i & (1 << 4)) != 0;
            this.start = (i & (1 << 3)) != 0;
            this.back = (i & (1 << 2)) != 0;
            this.left_bumper = (i & (1 << 1)) != 0;
            this.right_bumper = (i & 1) != 0;
        }
        if (version >= 2) {
            this.user = wrap.get();
        }
        callCallback();
    }

    /**
     * Are all analog sticks and triggers in their rest position?
     *
     * @return true if all analog sticks and triggers are at rest; otherwise false
     */
    public boolean atRest() {
        return !((this.left_stick_x != 0) ||
                (this.left_stick_y != 0) ||
                (this.right_stick_x != 0) ||
                (this.right_stick_y != 0) ||
                (this.left_trigger != 0) ||
                (this.right_trigger != 0));
    }

    /**
     * Get the type of gamepad as a String. This method defaults to "Standard".
     *
     * @return gamepad type
     */
    public String type() {
        return "Standard";
    }

    /**
     * Display a summary of this gamepad, including the state of all buttons, analog sticks, and triggers
     *
     * @return a summary
     */
    @Override
    public String toString() {
        String str = "";
        if (dpad_up) {
            str += "dpad_up ";
        }
        if (dpad_down) {
            str += "dpad_down ";
        }
        if (dpad_left) {
            str += "dpad_left ";
        }
        if (dpad_right) {
            str += "dpad_right ";
        }
        if (a) {
            str += "a ";
        }
        if (b) {
            str += "b ";
        }
        if (x) {
            str += "x ";
        }
        if (y) {
            str += "y ";
        }
        if (guide) {
            str += "guide ";
        }
        if (start) {
            str += "start ";
        }
        if (back) {
            str += "back ";
        }
        if (left_bumper) {
            str += "left_bumper ";
        }
        if (right_bumper) {
            str += "right_bumper ";
        }
        if (left_stick_button) {
            str += "left stick button ";
        }
        if (right_stick_button) {
            str += "right stick button ";
        }
        return String.format("ID: %2d user: %2d lx: % 1.2f ly: % 1.2f rx: % 1.2f ry: % 1.2f lt: %1.2f rt: %1.2f %s",
                id, user, left_stick_x, left_stick_y, right_stick_x, right_stick_y, left_trigger, right_trigger, str);
    }

    protected float cleanMotionValues(float number) {
        if ((number < this.joystickDeadzone) && (number > (-this.joystickDeadzone))) {
            return 0;
        } else if (number > MAX_JOYSTICK_VALUE) {
            return MAX_JOYSTICK_VALUE;
        } else if (number < -MAX_JOYSTICK_VALUE) {
            return -MAX_JOYSTICK_VALUE;
        }

        if (number < 0) {
            Range.scale((double) number, this.joystickDeadzone, MAX_JOYSTICK_VALUE, 0, MAX_JOYSTICK_VALUE);
        } else if (number > 0) {
            Range.scale((double) number, -this.joystickDeadzone, -MAX_JOYSTICK_VALUE, 0, -MAX_JOYSTICK_VALUE);
        }

        return number;
    }

    protected boolean pressed(KeyEvent event) {
        return event.getAction() == 0;
    }

    protected void callCallback() {
        if (this.gamepadCallback != null) {
            this.gamepadCallback.gamepadChanged(this);
        }
    }

    /**
     * Add a whitelist filter for a specific device vendor/product ID.
     * <p/>
     * This adds a whitelist to the gamepad detection method. If a device has been added to the whitelist, then only devices that match the given vendor ID and product ID will be considered gamepads. This method can be called multiple times to add multiple devices to the whitelist.
     * <p/>
     * If no whitelist entries have been added, then the default OS detection methods will be used.
     *
     * @param vendorId  the vendor ID
     * @param productId the product ID
     */
    public static void enableWhitelistFilter(int vendorId, int productId) {
        if (whitelistDevices == null) {
            whitelistDevices = new HashSet<whitelistDevice>();
        }
        whitelistDevices.add(new whitelistDevice(vendorId, productId));
    }

    /**
     * Clear the device whitelist filter.
     */
    public static void clearWhitelistFilter() {
        whitelistDevices = null;
    }

    /**
     * Does this device ID belong to a gamepad device?
     *
     * @param deviceId device ID
     * @return true, if gamepad device; false otherwise
     */
    @TargetApi(19)
    public static synchronized boolean isGamepadDevice(int deviceId) {
        synchronized (Gamepad.class) {
            if (gamepadDevices.contains(Integer.valueOf(deviceId))) {
                return true; // deviceId is cached
            } else { // update cache to check for new devices
                gamepadDevices = new HashSet<Integer>();
                for (int id : InputDevice.getDeviceIds()) {
                    InputDevice device = InputDevice.getDevice(id);
                    int sources = device.getSources();
                    if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) ||
                            ((sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)) {
                        if (VERSION.SDK_INT < 19) {
                            gamepadDevices.add(id);
                        } else if ((whitelistDevices == null) || whitelistDevices.contains(new whitelistDevice(device.getVendorId(), device.getProductId()))) {
                            gamepadDevices.add(id);
                        }
                    }
                }
                // check updated cache for deviceId
                return gamepadDevices.contains(deviceId);
            }
        }
    }
}
