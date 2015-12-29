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

public class Gamepad implements RobocolParsable {
    public static final int ID_UNASSOCIATED = -1;

    private static final int MAX_JOYSTICK_VALUE = 1;
    private static final short PAYLOAD_SIZE = 42;
    private static final short BUFFER_SIZE = PAYLOAD_SIZE + HEADER_LENGTH;

    private static Set<Integer> gamepadDevices = new HashSet<Integer>();
    private static Set<whitelistDevice> whitelistDevices;
    public boolean a;
    public boolean b;
    public boolean back;
    private final GamepadCallback gamepadCallback;
    protected float dpadThreshold = 0.2f;
    public boolean dpad_down;
    public boolean dpad_left;
    public boolean dpad_right;
    public boolean dpad_up;
    public boolean guide;
    public int id = ID_UNASSOCIATED;
    protected float joystickDeadzone = 0.2f;
    public boolean left_bumper;
    public boolean left_stick_button;
    public float left_stick_x;
    public float left_stick_y;
    public float left_trigger;
    public boolean right_bumper;
    public boolean right_stick_button;
    public float right_stick_x;
    public float right_stick_y;
    public float right_trigger;
    public boolean start;
    public long timestamp;
    public byte user = -1;
    public boolean x;
    public boolean y;

    public interface GamepadCallback {
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

    public void copy(Gamepad gamepad) throws RobotCoreException {
        fromByteArray(gamepad.toByteArray());
    }

    public void reset() {
        try {
            copy(new Gamepad());
        } catch (RobotCoreException e) {
            RobotLog.e("Gamepad library in an invalid state");
            throw new IllegalStateException("Gamepad library in an invalid state");
        }
    }

    public void setJoystickDeadzone(float deadzone) {
        if ((deadzone < 0) || (deadzone >  MAX_JOYSTICK_VALUE)) {
            throw new IllegalArgumentException("deadzone cannot be greater than max joystick value");
        }
        this.joystickDeadzone = deadzone;
    }

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
        int i = 1;
        ByteBuffer messageBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        try {
            int i2;
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
            this.left_stick_button = (i & (1 <<14)) != 0;
            this.right_stick_button = (i & (1 <<13)) != 0;
            this.dpad_up = (i & (1 <<12)) != 0;
            this.dpad_down = (i & (1 <<11)) != 0;
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

    public boolean atRest() {
        return !((this.left_stick_x != 0) ||
                (this.left_stick_y != 0) ||
                (this.right_stick_x != 0) ||
                (this.right_stick_y != 0) ||
                (this.left_trigger != 0) ||
                (this.right_trigger != 0));
    }

    public String type() {
        return "Standard";
    }

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
                id,  user,  left_stick_x,  left_stick_y,  right_stick_x,  right_stick_y,  left_trigger,  right_trigger, str);
    }

    protected float cleanMotionValues(float number) {
        if ((number < this.joystickDeadzone) && (number > (-this.joystickDeadzone))) {
            return 0;
        } else if (number >  MAX_JOYSTICK_VALUE) {
            return  MAX_JOYSTICK_VALUE;
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

    public static void enableWhitelistFilter(int vendorId, int productId) {
        if (whitelistDevices == null) {
            whitelistDevices = new HashSet<whitelistDevice>();
        }
        whitelistDevices.add(new whitelistDevice(vendorId, productId));
    }

    public static void clearWhitelistFilter() {
        whitelistDevices = null;
    }

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
