package com.qualcomm.robotcore.exception;

public class RobotCoreException extends Exception {
    private Exception exception;

    public RobotCoreException(String message) {
        super(message);
    }

    public RobotCoreException(String message, Exception exception) {
        super(message);
        this.exception = exception;
    }

    public boolean isChainedException() {
        return this.exception != null;
    }

    public Exception getChainedException() {
        return this.exception;
    }
}
