package com.qualcomm.robotcore.robot;

import com.qualcomm.robotcore.util.RobotLog;

public enum RobotState {
    NOT_STARTED(0),
    INIT(1),
    RUNNING(2),
    STOPPED(3),
    EMERGENCY_STOP(4),
    DROPPED_CONNECTION(5);
    
    private static final RobotState[] VALUES = values();
    private int state;

    RobotState(int state) {
        this.state = state;
    }

    public byte asByte() {
        return (byte) this.state;
    }

    public static RobotState fromByte(byte b) {
        RobotState robotState = NOT_STARTED;
        try {
            return VALUES[b];
        } catch (ArrayIndexOutOfBoundsException e) {
            RobotLog.w(String.format("Cannot convert %d to RobotState: %s", b, e.toString()));
            return robotState;
        }
    }
}
