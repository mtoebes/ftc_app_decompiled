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
    public static final int ID_UNASSOCIATED = -1;
    private static Set<Integer> gamepadDevices;
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

    static {
        gamepadDevices = new HashSet<Integer>();
        whitelistDevices = null;
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
        if ((deadzone < 0) || (deadzone >  1)) {
            throw new IllegalArgumentException("deadzone cannot be greater than max joystick value");
        }
        this.joystickDeadzone = deadzone;
    }

    public void update(MotionEvent event) {
        boolean z;
        boolean z2 = true;
        this.id = event.getDeviceId();
        this.timestamp = event.getEventTime();
        this.left_stick_x = cleanMotionValues(event.getAxisValue(0));
        this.left_stick_y = cleanMotionValues(event.getAxisValue(1));
        this.right_stick_x = cleanMotionValues(event.getAxisValue(11));
        this.right_stick_y = cleanMotionValues(event.getAxisValue(14));
        this.left_trigger = event.getAxisValue(17);
        this.right_trigger = event.getAxisValue(18);
        this.dpad_down = event.getAxisValue(16) > this.dpadThreshold;
        this.dpad_up = event.getAxisValue(16) < (-this.dpadThreshold);
        this.dpad_right = event.getAxisValue(15) > this.dpadThreshold;
        this.dpad_left = event.getAxisValue(15) < (-this.dpadThreshold);
        callCallback();
    }

    public void update(KeyEvent event) {
        this.id = event.getDeviceId();
        this.timestamp = event.getEventTime();
        int keyCode = event.getKeyCode();
        if (keyCode == 19) {
            this.dpad_up = pressed(event);
        } else if (keyCode == 20) {
            this.dpad_down = pressed(event);
        } else if (keyCode == 22) {
            this.dpad_right = pressed(event);
        } else if (keyCode == 21) {
            this.dpad_left = pressed(event);
        } else if (keyCode == 96) {
            this.a = pressed(event);
        } else if (keyCode == 97) {
            this.b = pressed(event);
        } else if (keyCode == 99) {
            this.x = pressed(event);
        } else if (keyCode == 100) {
            this.y = pressed(event);
        } else if (keyCode == 110) {
            this.guide = pressed(event);
        } else if (keyCode == 108) {
            this.start = pressed(event);
        } else if (keyCode == 4) {
            this.back = pressed(event);
        } else if (keyCode == 103) {
            this.right_bumper = pressed(event);
        } else if (keyCode == 102) {
            this.left_bumper = pressed(event);
        } else if (keyCode == 106) {
            this.left_stick_button = pressed(event);
        } else if (keyCode == 107) {
            this.right_stick_button = pressed(event);
        }
        callCallback();
    }

    public MsgType getRobocolMsgType() {
        return MsgType.GAMEPAD;
    }

    public byte[] toByteArray() throws RobotCoreException {
        int i = 1;
        ByteBuffer allocate = ByteBuffer.allocate(45);
        try {
            int i2;
            allocate.put(getRobocolMsgType().asByte());
            allocate.putShort((short) 42);
            allocate.put((byte) 2);
            allocate.putInt(this.id);
            allocate.putLong(this.timestamp).array();
            allocate.putFloat(this.left_stick_x).array();
            allocate.putFloat(this.left_stick_y).array();
            allocate.putFloat(this.right_stick_x).array();
            allocate.putFloat(this.right_stick_y).array();
            allocate.putFloat(this.left_trigger).array();
            allocate.putFloat(this.right_trigger).array();
            int i3 = ((this.left_stick_button ? 1 : 0)) << 1;
            if (this.right_stick_button) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            i3 = (i2 + i3) << 1;
            if (this.dpad_up) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            i3 = (i2 + i3) << 1;
            if (this.dpad_down) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            i3 = (i2 + i3) << 1;
            if (this.dpad_left) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            i3 = (i2 + i3) << 1;
            if (this.dpad_right) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            i3 = (i2 + i3) << 1;
            if (this.a) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            i3 = (i2 + i3) << 1;
            if (this.b) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            i3 = (i2 + i3) << 1;
            if (this.x) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            i3 = (i2 + i3) << 1;
            if (this.y) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            i3 = (i2 + i3) << 1;
            if (this.guide) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            i3 = (i2 + i3) << 1;
            if (this.start) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            i3 = (i2 + i3) << 1;
            if (this.back) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            i3 = (i2 + i3) << 1;
            if (this.left_bumper) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            i2 = (i2 + i3) << 1;
            if (!this.right_bumper) {
                i = 0;
            }
            allocate.putInt(i + i2);
            allocate.put(this.user);
        } catch (Exception e) {
            RobotLog.logStacktrace(e);
        }
        return allocate.array();
    }

    public void fromByteArray(byte[] byteArray) throws RobotCoreException {
        boolean z = true;
        if (byteArray.length < 45) {
            throw new RobotCoreException("Expected buffer of at least 45 bytes, received " + byteArray.length);
        }
        ByteBuffer wrap = ByteBuffer.wrap(byteArray, 3, 42);
        byte b = wrap.get();
        if (b >= (byte) 1) {
            boolean z2;
            this.id = wrap.getInt();
            this.timestamp = wrap.getLong();
            this.left_stick_x = wrap.getFloat();
            this.left_stick_y = wrap.getFloat();
            this.right_stick_x = wrap.getFloat();
            this.right_stick_y = wrap.getFloat();
            this.left_trigger = wrap.getFloat();
            this.right_trigger = wrap.getFloat();
            int i = wrap.getInt();
            this.left_stick_button = (i & D2xxManager.FTDI_BREAK_ON) != 0;
            z2 = (i & 8192) != 0;
            this.right_stick_button = z2;
            z2 = (i & 4096) != 0;
            this.dpad_up = z2;
            z2 = (i & 2048) != 0;
            this.dpad_down = z2;
            z2 = (i & 1024) != 0;
            this.dpad_left = z2;
            z2 = (i & 512) != 0;
            this.dpad_right = z2;
            z2 = (i & Command.MAX_COMMAND_LENGTH) != 0;
            this.a = z2;
            z2 = (i & SPI_SLAVE_CMD.SPI_MASTER_TRANSFER) != 0;
            this.b = z2;
            z2 = (i & 64) != 0;
            this.x = z2;
            z2 = (i & 32) != 0;
            this.y = z2;
            z2 = (i & 16) != 0;
            this.guide = z2;
            z2 = (i & 8) != 0;
            this.start = z2;
            z2 = (i & 4) != 0;
            this.back = z2;
            z2 = (i & 2) != 0;
            this.left_bumper = z2;
            if ((i & 1) == 0) {
                z = false;
            }
            this.right_bumper = z;
        }
        if (b >= 2) {
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
        } else if (number >  1) {
            return  1;
        } else if (number < -1) {
            return -1;
        }

        if (number < 0) {
            Range.scale((double) number, this.joystickDeadzone, 1, 0, 1);
        } else if (number > 0) {
            Range.scale((double) number, -this.joystickDeadzone, -1, 0, -1);
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
        boolean z = true;
        synchronized (Gamepad.class) {
            if (!gamepadDevices.contains(Integer.valueOf(deviceId))) {
                gamepadDevices = new HashSet<Integer>();
                for (int i : InputDevice.getDeviceIds()) {
                    InputDevice device = InputDevice.getDevice(i);
                    int sources = device.getSources();
                    if (((sources & 1025) == 1025) || ((sources & 16777232) == 16777232)) {
                        if (VERSION.SDK_INT < 19) {
                            gamepadDevices.add(i);
                        } else if ((whitelistDevices == null) || whitelistDevices.contains(new whitelistDevice(device.getVendorId(), device.getProductId()))) {
                            gamepadDevices.add(i);
                        }
                    }
                }
                if (!gamepadDevices.contains(Integer.valueOf(deviceId))) {
                    z = false;
                }
            }
        }
        return z;
    }
}
