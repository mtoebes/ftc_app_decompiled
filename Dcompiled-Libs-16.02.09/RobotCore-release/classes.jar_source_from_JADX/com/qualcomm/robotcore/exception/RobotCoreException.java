package com.qualcomm.robotcore.exception;

public class RobotCoreException extends Exception {
    private Exception f214a;

    public RobotCoreException(String message) {
        super(message);
        this.f214a = null;
    }

    public RobotCoreException(String format, Object... args) {
        super(String.format(format, args));
        this.f214a = null;
    }

    public static RobotCoreException createChained(Exception e, String format, Object... args) {
        RobotCoreException robotCoreException = new RobotCoreException(format, args);
        robotCoreException.f214a = e;
        return robotCoreException;
    }

    public boolean isChainedException() {
        return this.f214a != null;
    }

    public Exception getChainedException() {
        return this.f214a;
    }
}
