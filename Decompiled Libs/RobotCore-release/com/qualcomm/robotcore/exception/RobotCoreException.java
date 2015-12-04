package com.qualcomm.robotcore.exception;

public class RobotCoreException extends Exception {
    private Exception f220a;

    public RobotCoreException(String message) {
        super(message);
        this.f220a = null;
    }

    public RobotCoreException(String message, Exception e) {
        super(message);
        this.f220a = null;
        this.f220a = e;
    }

    public boolean isChainedException() {
        return this.f220a != null;
    }

    public Exception getChainedException() {
        return this.f220a;
    }
}
