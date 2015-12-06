package com.qualcomm.robotcore.hardware;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.SPI_SLAVE_CMD;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.Command;
import com.qualcomm.robotcore.robocol.RobocolParsable;
import com.qualcomm.robotcore.util.Dimmer;
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
        if (deadzone < 0.0f || deadzone > Dimmer.MAXIMUM_BRIGHTNESS) {
            throw new IllegalArgumentException("deadzone cannot be greater than max joystick value");
        }
        joystickDeadzone = deadzone;
    }

    public void update(MotionEvent event) {
        id = event.getDeviceId();
        timestamp = event.getEventTime();
        left_stick_x = cleanMotionValues(event.getAxisValue(0));
        left_stick_y = cleanMotionValues(event.getAxisValue(1));
        right_stick_x = cleanMotionValues(event.getAxisValue(11));
        right_stick_y = cleanMotionValues(event.getAxisValue(14));
        left_trigger = event.getAxisValue(17);
        right_trigger = event.getAxisValue(18);
        dpad_down = event.getAxisValue(16) > dpadThreshold;
        dpad_up = (event.getAxisValue(16) < (-dpadThreshold));
        dpad_right = (event.getAxisValue(15) > dpadThreshold);
        dpad_left = (event.getAxisValue(15) < (-dpadThreshold));
        callCallback();
    }

    public void update(KeyEvent event) {
        id = event.getDeviceId();
        timestamp = event.getEventTime();
        int keyCode = event.getKeyCode();
        if (keyCode == 19) {
            dpad_up = pressed(event);
        } else if (keyCode == 20) {
            dpad_down = pressed(event);
        } else if (keyCode == 22) {
            dpad_right = pressed(event);
        } else if (keyCode == 21) {
            dpad_left = pressed(event);
        } else if (keyCode == 96) {
            a = pressed(event);
        } else if (keyCode == 97) {
            b = pressed(event);
        } else if (keyCode == 99) {
            x = pressed(event);
        } else if (keyCode == 100) {
            y = pressed(event);
        } else if (keyCode == 110) {
            guide = pressed(event);
        } else if (keyCode == 108) {
            start = pressed(event);
        } else if (keyCode == 4) {
            back = pressed(event);
        } else if (keyCode == 103) {
            right_bumper = pressed(event);
        } else if (keyCode == 102) {
            left_bumper = pressed(event);
        } else if (keyCode == 106) {
            left_stick_button = pressed(event);
        } else if (keyCode == 107) {
            right_stick_button = pressed(event);
        }
        callCallback();
    }

    public MsgType getRobocolMsgType() {
        return MsgType.GAMEPAD;
    }

    public byte[] toByteArray() throws RobotCoreException {
        ByteBuffer allocate = ByteBuffer.allocate(45);
        try {
            allocate.put(getRobocolMsgType().asByte());
            allocate.putShort((short) 42);
            allocate.put((byte) 2);
            allocate.putInt(id);
            allocate.putLong(timestamp).array();
            allocate.putFloat(left_stick_x).array();
            allocate.putFloat(left_stick_y).array();
            allocate.putFloat(right_stick_x).array();
            allocate.putFloat(right_stick_y).array();
            allocate.putFloat(left_trigger).array();
            allocate.putFloat(right_trigger).array();

            int bytes = ((left_stick_button ? 1 : 0) << 14) +
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
                    ((right_bumper ? 1 : 0));

            allocate.putInt(bytes);
            allocate.put(user);
        } catch (Exception e) {
            RobotLog.logStacktrace(e);
        }
        return allocate.array();
    }

    public void fromByteArray(byte[] byteArray) throws RobotCoreException {
        if (byteArray.length < 45) {
            throw new RobotCoreException("Expected buffer of at least 45 bytes, received " + byteArray.length);
        }
        ByteBuffer wrap = ByteBuffer.wrap(byteArray, 3, 42);
        byte wrapb = wrap.get();
        if (wrapb >= (byte) 1) {
            id = wrap.getInt();
            timestamp = wrap.getLong();
            left_stick_x = wrap.getFloat();
            left_stick_y = wrap.getFloat();
            right_stick_x = wrap.getFloat();
            right_stick_y = wrap.getFloat();
            left_trigger = wrap.getFloat();
            right_trigger = wrap.getFloat();

            int bytes = wrap.getInt();
            left_stick_button = ((bytes & (1<<14)) != 0);
            right_stick_button = ((bytes & (1<<13)) != 0);
            dpad_up = ((bytes & (1<<12)) != 0);
            dpad_down = ((bytes & (1<<11)) != 0);
            dpad_left = ((bytes & (1<<10)) != 0);
            dpad_right = ((bytes & (1<<9)) != 0);
            a = ((bytes & (1<<8)) != 0);
            b = ((bytes & (1<<7)) != 0);
            x = ((bytes & (1<<6)) != 0);
            y = ((bytes & (1<<5)) != 0);
            guide = ((bytes & (1<<4)) != 0);
            start = ((bytes & (1<<3)) != 0);
            back = ((bytes & (1<<2)) != 0);
            left_bumper = ((bytes & (1<<1)) != 0);
            right_bumper = ((bytes & 1) != 0);
        }
        if (wrapb >= 2) {
            user = wrap.get();
        }
        callCallback();
    }

    public boolean atRest() {
        return left_stick_x == 0.0f &&
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
            str = str + "dpad_up ";
        }
        if (dpad_down) {
            str = str + "dpad_down ";
        }
        if (dpad_left) {
            str = str + "dpad_left ";
        }
        if (dpad_right) {
            str = str + "dpad_right ";
        }
        if (a) {
            str = str + "a ";
        }
        if (b) {
            str = str + "b ";
        }
        if (x) {
            str = str + "x ";
        }
        if (y) {
            str = str + "y ";
        }
        if (guide) {
            str = str + "guide ";
        }
        if (start) {
            str = str + "start ";
        }
        if (back) {
            str = str + "back ";
        }
        if (left_bumper) {
            str = str + "left_bumper ";
        }
        if (right_bumper) {
            str = str + "right_bumper ";
        }
        if (left_stick_button) {
            str = str + "left stick button ";
        }
        if (right_stick_button) {
            str = str + "right stick button ";
        }
        return String.format("ID: %2d user: %2d lx: % 1.2f ly: % 1.2f rx: % 1.2f ry: % 1.2f lt: %1.2f rt: %1.2f %s",
                id, user, left_stick_x, left_stick_y, right_stick_x, right_stick_y, left_trigger, right_trigger, str);
    }

    protected float cleanMotionValues(float number) {
        if (number < joystickDeadzone && number > (-joystickDeadzone)) {
            return 0.0f;
        }
        if (number > Dimmer.MAXIMUM_BRIGHTNESS) {
            return Dimmer.MAXIMUM_BRIGHTNESS;
        }
        if (number < -1.0f) {
            return -1.0f;
        }
        if (number < 0.0f) {
            Range.scale((double) number, (double) joystickDeadzone, Servo.MAX_POSITION, 0.0d, Servo.MAX_POSITION);
        }
        if (number <= 0.0f) {
            return number;
        }
        Range.scale((double) number, (double) (-joystickDeadzone), -1.0d, 0.0d, -1.0d);
        return number;
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
