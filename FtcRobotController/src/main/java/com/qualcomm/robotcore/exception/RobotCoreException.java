package com.qualcomm.robotcore.exception;

public class RobotCoreException extends Exception {
    private Exception parentException;

    public RobotCoreException(String message) {
        super(message);
        parentException = null;
    }

    public RobotCoreException(String message, Exception e) {
        super(message);
        parentException = e;
    }

    public boolean isChainedException() {
        return parentException != null;
    }

    public Exception getChainedException() {
        return parentException;
    }
}
