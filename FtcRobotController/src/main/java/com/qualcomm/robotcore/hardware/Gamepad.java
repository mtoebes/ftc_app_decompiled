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
    private static Set<Integer> f236d;
    private static Set<C0034a> f237e;
    public boolean f238a;
    public boolean f239b;
    public boolean back;
    private final GamepadCallback f240c;
    protected float dpadThreshold;
    public boolean dpad_down;
    public boolean dpad_left;
    public boolean dpad_right;
    public boolean dpad_up;
    public boolean guide;
    public int id;
    protected float joystickDeadzone;
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
    public byte user;
    public boolean f241x;
    public boolean f242y;

    public interface GamepadCallback {
        void gamepadChanged(Gamepad gamepad);
    }

    /* renamed from: com.qualcomm.robotcore.hardware.Gamepad.a */
    private static class C0034a extends SimpleEntry<Integer, Integer> {
        public C0034a(int i, int i2) {
            super(Integer.valueOf(i), Integer.valueOf(i2));
        }
    }

    static {
        f236d = new HashSet();
        f237e = null;
    }

    public Gamepad() {
        this(null);
    }

    public Gamepad(GamepadCallback callback) {
        this.left_stick_x = 0.0f;
        this.left_stick_y = 0.0f;
        this.right_stick_x = 0.0f;
        this.right_stick_y = 0.0f;
        this.dpad_up = false;
        this.dpad_down = false;
        this.dpad_left = false;
        this.dpad_right = false;
        this.f238a = false;
        this.f239b = false;
        this.f241x = false;
        this.f242y = false;
        this.guide = false;
        this.start = false;
        this.back = false;
        this.left_bumper = false;
        this.right_bumper = false;
        this.left_stick_button = false;
        this.right_stick_button = false;
        this.left_trigger = 0.0f;
        this.right_trigger = 0.0f;
        this.user = (byte) -1;
        this.id = ID_UNASSOCIATED;
        this.timestamp = 0;
        this.dpadThreshold = 0.2f;
        this.joystickDeadzone = 0.2f;
        this.f240c = callback;
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
        if (event.getAxisValue(16) < (-this.dpadThreshold)) {
            z = true;
        } else {
            z = false;
        }
        this.dpad_up = z;
        if (event.getAxisValue(15) > this.dpadThreshold) {
            z = true;
        } else {
            z = false;
        }
        this.dpad_right = z;
        if (event.getAxisValue(15) >= (-this.dpadThreshold)) {
            z2 = false;
        }
        this.dpad_left = z2;
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
            this.f238a = pressed(event);
        } else if (keyCode == 97) {
            this.f239b = pressed(event);
        } else if (keyCode == 99) {
            this.f241x = pressed(event);
        } else if (keyCode == 100) {
            this.f242y = pressed(event);
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
            int i3 = ((this.left_stick_button ? 1 : 0) + 0) << 1;
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
            if (this.f238a) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            i3 = (i2 + i3) << 1;
            if (this.f239b) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            i3 = (i2 + i3) << 1;
            if (this.f241x) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            i3 = (i2 + i3) << 1;
            if (this.f242y) {
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
            if ((i & 8192) != 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.right_stick_button = z2;
            if ((i & 4096) != 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.dpad_up = z2;
            if ((i & 2048) != 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.dpad_down = z2;
            if ((i & 1024) != 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.dpad_left = z2;
            if ((i & 512) != 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.dpad_right = z2;
            if ((i & Command.MAX_COMMAND_LENGTH) != 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.f238a = z2;
            if ((i & SPI_SLAVE_CMD.SPI_MASTER_TRANSFER) != 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.f239b = z2;
            if ((i & 64) != 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.f241x = z2;
            if ((i & 32) != 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.f242y = z2;
            if ((i & 16) != 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.guide = z2;
            if ((i & 8) != 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.start = z2;
            if ((i & 4) != 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.back = z2;
            if ((i & 2) != 0) {
                z2 = true;
            } else {
                z2 = false;
            }
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
        return this.left_stick_x == 0.0f && this.left_stick_y == 0.0f && this.right_stick_x == 0.0f && this.right_stick_y == 0.0f && this.left_trigger == 0.0f && this.right_trigger == 0.0f;
    }

    public String type() {
        return "Standard";
    }

    public String toString() {
        String str = new String();
        if (this.dpad_up) {
            str = str + "dpad_up ";
        }
        if (this.dpad_down) {
            str = str + "dpad_down ";
        }
        if (this.dpad_left) {
            str = str + "dpad_left ";
        }
        if (this.dpad_right) {
            str = str + "dpad_right ";
        }
        if (this.f238a) {
            str = str + "a ";
        }
        if (this.f239b) {
            str = str + "b ";
        }
        if (this.f241x) {
            str = str + "x ";
        }
        if (this.f242y) {
            str = str + "y ";
        }
        if (this.guide) {
            str = str + "guide ";
        }
        if (this.start) {
            str = str + "start ";
        }
        if (this.back) {
            str = str + "back ";
        }
        if (this.left_bumper) {
            str = str + "left_bumper ";
        }
        if (this.right_bumper) {
            str = str + "right_bumper ";
        }
        if (this.left_stick_button) {
            str = str + "left stick button ";
        }
        if (this.right_stick_button) {
            str = str + "right stick button ";
        }
        return String.format("ID: %2d user: %2d lx: % 1.2f ly: % 1.2f rx: % 1.2f ry: % 1.2f lt: %1.2f rt: %1.2f %s", new Object[]{Integer.valueOf(this.id), Byte.valueOf(this.user), Float.valueOf(this.left_stick_x), Float.valueOf(this.left_stick_y), Float.valueOf(this.right_stick_x), Float.valueOf(this.right_stick_y), Float.valueOf(this.left_trigger), Float.valueOf(this.right_trigger), str});
    }

    protected float cleanMotionValues(float number) {
        if (number < this.joystickDeadzone && number > (-this.joystickDeadzone)) {
            return 0.0f;
        }
        if (number > Dimmer.MAXIMUM_BRIGHTNESS) {
            return Dimmer.MAXIMUM_BRIGHTNESS;
        }
        if (number < -1.0f) {
            return -1.0f;
        }
        if (number < 0.0f) {
            Range.scale((double) number, (double) this.joystickDeadzone, Servo.MAX_POSITION, 0.0d, Servo.MAX_POSITION);
        }
        if (number <= 0.0f) {
            return number;
        }
        Range.scale((double) number, (double) (-this.joystickDeadzone), -1.0d, 0.0d, -1.0d);
        return number;
    }

    protected boolean pressed(KeyEvent event) {
        return event.getAction() == 0;
    }

    protected void callCallback() {
        if (this.f240c != null) {
            this.f240c.gamepadChanged(this);
        }
    }

    public static void enableWhitelistFilter(int vendorId, int productId) {
        if (f237e == null) {
            f237e = new HashSet();
        }
        f237e.add(new C0034a(vendorId, productId));
    }

    public static void clearWhitelistFilter() {
        f237e = null;
    }

    @TargetApi(19)
    public static synchronized boolean isGamepadDevice(int deviceId) {
        boolean z = true;
        synchronized (Gamepad.class) {
            if (!f236d.contains(Integer.valueOf(deviceId))) {
                f236d = new HashSet();
                for (int i : InputDevice.getDeviceIds()) {
                    InputDevice device = InputDevice.getDevice(i);
                    int sources = device.getSources();
                    if ((sources & 1025) == 1025 || (sources & 16777232) == 16777232) {
                        if (VERSION.SDK_INT < 19) {
                            f236d.add(Integer.valueOf(i));
                        } else if (f237e == null || f237e.contains(new C0034a(device.getVendorId(), device.getProductId()))) {
                            f236d.add(Integer.valueOf(i));
                        }
                    }
                }
                if (!f236d.contains(Integer.valueOf(deviceId))) {
                    z = false;
                }
            }
        }
        return z;
    }
}
