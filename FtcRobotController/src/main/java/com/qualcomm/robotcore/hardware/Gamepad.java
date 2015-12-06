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
    public boolean a = false;
    public boolean b = false;
    public boolean back = false;
    public boolean dpad_down = false;
    public boolean dpad_left = false;
    public boolean dpad_right = false;
    public boolean dpad_up = false;
    protected float dpadThreshold = 0.2f;
    public boolean guide = false;
    public int id = ID_UNASSOCIATED;
    public static final int ID_UNASSOCIATED = -1;
    protected float joystickDeadzone = 0.2f;
    public boolean left_bumper = false;
    public boolean left_stick_button = false;
    public float left_stick_x = 0.0f;
    public float left_stick_y = 0.0f;
    public float left_trigger = 0.0f;
    public boolean right_bumper = false;
    public boolean right_stick_button = false;
    public float right_stick_x = 0.0f;
    public float right_stick_y = 0.0f;
    public float right_trigger = 0.0f;
    public boolean start = false;
    public long timestamp = 0;
    public byte user = (byte) -1;
    public boolean x = false;
    public boolean y = false;

    private final GamepadCallback gamepadCallback;
    private static Set<Integer> gamepadDevices = new HashSet<Integer>();
    private static Set<whitelistDevice> whitelistDevices = null;

    private static float MAX_MOTION_VALUE = 1.0f;

    private static short DATA_LENGTH = 42;
    private static short HEADER_LENGTH = RobocolParsable.HEADER_LENGTH;
    private static short BUFFER_SIZE = (short) (DATA_LENGTH + HEADER_LENGTH);
    private static byte MESSAGE_VERSION = 2;

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
        gamepadCallback = callback;
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
        if (deadzone < 0 || deadzone > MAX_MOTION_VALUE) {
            throw new IllegalArgumentException("deadzone cannot be greater than max joystick value");
        }
        joystickDeadzone = deadzone;
    }

    public void update(MotionEvent event) {
        id = event.getDeviceId();
        timestamp = event.getEventTime();
        left_stick_x = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_X));
        left_stick_y = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_Y));
        right_stick_x = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_Z));
        right_stick_y = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_RZ));
        left_trigger = event.getAxisValue(MotionEvent.AXIS_LTRIGGER);
        right_trigger = event.getAxisValue(MotionEvent.AXIS_RTRIGGER);
        dpad_down = event.getAxisValue(MotionEvent.AXIS_HAT_Y) > dpadThreshold;
        dpad_up = (event.getAxisValue(MotionEvent.AXIS_HAT_Y) < (-dpadThreshold));
        dpad_right = (event.getAxisValue(MotionEvent.AXIS_HAT_X) > dpadThreshold);
        dpad_left = (event.getAxisValue(MotionEvent.AXIS_HAT_X) < (-dpadThreshold));
        callCallback();
    }

    public void update(KeyEvent event) {
        id = event.getDeviceId();
        timestamp = event.getEventTime();
        int keyCode = event.getKeyCode();

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            dpad_up = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            dpad_down = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            dpad_right = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            dpad_left = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_A) {
            a = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_B) {
            b = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_X) {
            x = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_Y) {
            y = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_MODE) {
            guide = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_START) {
            start = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            back = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_R1) {
            right_bumper = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_L1) {
            left_bumper = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_THUMBL) {
            left_stick_button = pressed(event);
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_THUMBR) {
            right_stick_button = pressed(event);
        }

        callCallback();
    }

    public MsgType getRobocolMsgType() {
        return MsgType.GAMEPAD;
    }

    public byte[] toByteArray() throws RobotCoreException {
        ByteBuffer message = ByteBuffer.allocate(BUFFER_SIZE);
        try {
            message.put(getRobocolMsgType().asByte());
            message.putShort(DATA_LENGTH);
            message.put(MESSAGE_VERSION);
            message.putInt(id);
            message.putLong(timestamp).array();
            message.putFloat(left_stick_x).array();
            message.putFloat(left_stick_y).array();
            message.putFloat(right_stick_x).array();
            message.putFloat(right_stick_y).array();
            message.putFloat(left_trigger).array();
            message.putFloat(right_trigger).array();

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
            message.putInt(buttons);
            message.put(user);
        } catch (Exception e) {
            RobotLog.logStacktrace(e);
        }
        return message.array();
    }

    public void fromByteArray(byte[] byteArray) throws RobotCoreException {
        if (byteArray.length < BUFFER_SIZE) {
            throw new RobotCoreException("Expected buffer of at least " + BUFFER_SIZE + " bytes, received " + byteArray.length);
        }
        ByteBuffer message = ByteBuffer.wrap(byteArray, HEADER_LENGTH, DATA_LENGTH);
        byte version = message.get();
        if (version >= 1) {
            id = message.getInt();
            timestamp = message.getLong();
            left_stick_x = message.getFloat();
            left_stick_y = message.getFloat();
            right_stick_x = message.getFloat();
            right_stick_y = message.getFloat();
            left_trigger = message.getFloat();
            right_trigger = message.getFloat();

            int buttons = message.getInt();
            left_stick_button = ((buttons & (1<<14)) != 0);
            right_stick_button = ((buttons & (1<<13)) != 0);
            dpad_up = ((buttons & (1<<12)) != 0);
            dpad_down = ((buttons & (1<<11)) != 0);
            dpad_left = ((buttons & (1<<10)) != 0);
            dpad_right = ((buttons & (1<<9)) != 0);
            a = ((buttons & (1<<8)) != 0);
            b = ((buttons & (1<<7)) != 0);
            x = ((buttons & (1<<6)) != 0);
            y = ((buttons & (1<<5)) != 0);
            guide = ((buttons & (1<<4)) != 0);
            start = ((buttons & (1<<3)) != 0);
            back = ((buttons & (1<<2)) != 0);
            left_bumper = ((buttons & (1<<1)) != 0);
            right_bumper = ((buttons & 1) != 0);
        }
        if (version >= 2) {
            user = message.get();
        }
        callCallback();
    }

    public boolean atRest() {
        return
                left_stick_x == 0.0f &&
                left_stick_y == 0.0f &&
                right_stick_x == 0.0f &&
                right_stick_y == 0.0f &&
                left_trigger == 0.0f &&
                right_trigger == 0.0f;
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
                id, user, left_stick_x, left_stick_y, right_stick_x, right_stick_y, left_trigger, right_trigger, str);
    }

    protected float cleanMotionValues(float number) {
        if (number < joystickDeadzone && number > (-joystickDeadzone)) {
            return 0;
        } else if (number > MAX_MOTION_VALUE) {
            return MAX_MOTION_VALUE;
        } else if (number < -MAX_MOTION_VALUE) {
            return -MAX_MOTION_VALUE;
        } else if (number < 0) {
            return (float) Range.scale((double) number, (double) joystickDeadzone, Servo.MAX_POSITION, 0, Servo.MAX_POSITION);
        } else if (number > 0) {
            return (float) Range.scale((double) number, (double) (-joystickDeadzone), -MAX_MOTION_VALUE, 0, -MAX_MOTION_VALUE);
        } else {
            return number;
        }
    }

    protected boolean pressed(KeyEvent event) {
        return event.getAction() == 0;
    }

    protected void callCallback() {
        if (gamepadCallback != null) {
            gamepadCallback.gamepadChanged(this);
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
            // check cache for deviceId
            if (gamepadDevices.contains(Integer.valueOf(deviceId))) {
                return true;
            } else {
                // update cache to check for new devices
                gamepadDevices = new HashSet<Integer>();
                for (int id : InputDevice.getDeviceIds()) {
                    InputDevice device = InputDevice.getDevice(id);
                    int sources = device.getSources();
                    if ((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD || (sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK) {
                        if (VERSION.SDK_INT < 19) {
                            gamepadDevices.add(id);
                        } else if (whitelistDevices == null || whitelistDevices.contains(new whitelistDevice(device.getVendorId(), device.getProductId()))) {
                            gamepadDevices.add(id);
                        }
                    }
                }
                // check updated cache for deviceId
                return gamepadDevices.contains(Integer.valueOf(deviceId));
            }
        }
    }
}
