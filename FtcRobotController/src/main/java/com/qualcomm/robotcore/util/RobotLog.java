package com.qualcomm.robotcore.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.qualcomm.robotcore.exception.RobotCoreException;
import java.io.File;

public class RobotLog {
    public static final String TAG = "RobotCore";
    private static String globalErrorMessage;
    private static boolean writeLock;

    private RobotLog() {
    }

    static {
        globalErrorMessage = "";
        writeLock = false;
    }

    public static void v(String message) {
        Log.v(TAG, message);
    }

    public static void d(String message) {
        Log.d(TAG, message);
    }

    public static void i(String message) {
        Log.i(TAG, message);
    }

    public static void w(String message) {
        Log.w(TAG, message);
    }

    public static void e(String message) {
        Log.e(TAG, message);
    }

    public static void logStacktrace(Exception e) {
        e(e.toString());
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            e(stackTraceElement.toString());
        }
    }

    public static void logStacktrace(RobotCoreException e) {
        e(e.toString());
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            e(stackTraceElement.toString());
        }
        if (e.isChainedException()) {
            e("Exception chained from:");
            if (e.getChainedException() instanceof RobotCoreException) {
                logStacktrace((RobotCoreException) e.getChainedException());
            } else {
                logStacktrace(e.getChainedException());
            }
        }
    }

    public static void setGlobalErrorMsg(String message) {
        if (globalErrorMessage.isEmpty()) {
            globalErrorMessage += message;
        }
    }

    public static void setGlobalErrorMsgAndThrow(String message, RobotCoreException e) throws RobotCoreException {
        setGlobalErrorMsg(message + "\n" + e.getMessage());
        throw e;
    }

    public static String getGlobalErrorMsg() {
        return globalErrorMessage;
    }

    public static boolean hasGlobalErrorMsg() {
        return !globalErrorMessage.isEmpty();
    }

    public static void clearGlobalErrorMsg() {
        globalErrorMessage = "";
    }

    public static void logAndThrow(String errMsg) throws RobotCoreException {
        w(errMsg);
        throw new RobotCoreException(errMsg);
    }

    public static void writeLogcatToDisk(Context context, final int fileSizeKb) {
        if (!writeLock) {
            writeLock = true;

            final String filename = new File(getLogFilename(context)).getAbsolutePath();
            final String packageName = context.getPackageName();

            Thread writeThread = new Thread() {
                public void run() {
                    try {
                        String usbRequest = "UsbRequestJNI:S UsbRequest:S *:V";
                        RobotLog.v("saving logcat to " + filename);
                        RunShellCommand runShellCommand = new RunShellCommand();
                        RunShellCommand.killSpawnedProcess("logcat", packageName, runShellCommand);
                        runShellCommand.run(String.format("logcat -f %s -r%d -n%d -v time %s", filename, fileSizeKb, 1, usbRequest));
                    } catch (RobotCoreException robotCoreException) {
                        RobotLog.v("Error while writing log file to disk: " + robotCoreException.toString());
                    } finally {
                        RobotLog.writeLock = false;
                    }
                }
            };
            writeThread.start();
        }
    }

    public static String getLogFilename(Context context) {
        return (Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getPackageName()) + ".logcat";
    }

    public static void cancelWriteLogcatToDisk(Context context) {
        final String packageName = context.getPackageName();
        final String absolutePath = new File(Environment.getExternalStorageDirectory(), packageName).getAbsolutePath();
        writeLock = false;

        Thread cancelWriteThread = new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // do nothing
                }
                try {
                    RobotLog.v("closing logcat file " + absolutePath);
                    RunShellCommand.killSpawnedProcess("logcat", packageName, new RunShellCommand());
                } catch (RobotCoreException e2) {
                    RobotLog.v("Unable to cancel writing log file to disk: " + e2.toString());
                }
            }
        };
        cancelWriteThread.start();
    }
}
