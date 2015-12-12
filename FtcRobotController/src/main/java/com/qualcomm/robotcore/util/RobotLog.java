package com.qualcomm.robotcore.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.qualcomm.robotcore.exception.RobotCoreException;
import java.io.File;

public class RobotLog {
    public static final String TAG = "RobotCore";
    private static String f416a;
    private static boolean f417b;

    /* renamed from: com.qualcomm.robotcore.util.RobotLog.1 */
    static class C00511 extends Thread {
        final /* synthetic */ String f411a;
        final /* synthetic */ String f412b;
        final /* synthetic */ int f413c;

        C00511(String str, String str2, String str3, int i) {
            super(str);
            this.f411a = str2;
            this.f412b = str3;
            this.f413c = i;
        }

        public void run() {
            try {
                String str = "UsbRequestJNI:S UsbRequest:S *:V";
                RobotLog.v("saving logcat to " + this.f411a);
                RunShellCommand runShellCommand = new RunShellCommand();
                RunShellCommand.killSpawnedProcess("logcat", this.f412b, runShellCommand);
                runShellCommand.run(String.format("logcat -f %s -r%d -n%d -v time %s", new Object[]{this.f411a, Integer.valueOf(this.f413c), Integer.valueOf(1), "UsbRequestJNI:S UsbRequest:S *:V"}));
            } catch (RobotCoreException e) {
                RobotLog.v("Error while writing log file to disk: " + e.toString());
            } finally {
                RobotLog.f417b = false;
            }
        }
    }

    /* renamed from: com.qualcomm.robotcore.util.RobotLog.2 */
    static class C00522 extends Thread {
        final /* synthetic */ String f414a;
        final /* synthetic */ String f415b;

        C00522(String str, String str2) {
            this.f414a = str;
            this.f415b = str2;
        }

        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            try {
                RobotLog.v("closing logcat file " + this.f414a);
                RunShellCommand.killSpawnedProcess("logcat", this.f415b, new RunShellCommand());
            } catch (RobotCoreException e2) {
                RobotLog.v("Unable to cancel writing log file to disk: " + e2.toString());
            }
        }
    }

    private RobotLog() {
    }

    static {
        f416a = "";
        f417b = false;
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
        if (f416a.isEmpty()) {
            f416a += message;
        }
    }

    public static void setGlobalErrorMsgAndThrow(String message, RobotCoreException e) throws RobotCoreException {
        setGlobalErrorMsg(message + "\n" + e.getMessage());
        throw e;
    }

    public static String getGlobalErrorMsg() {
        return f416a;
    }

    public static boolean hasGlobalErrorMsg() {
        return !f416a.isEmpty();
    }

    public static void clearGlobalErrorMsg() {
        f416a = "";
    }

    public static void logAndThrow(String errMsg) throws RobotCoreException {
        w(errMsg);
        throw new RobotCoreException(errMsg);
    }

    public static void writeLogcatToDisk(Context context, int fileSizeKb) {
        if (!f417b) {
            f417b = true;
            String str = "Logging Thread";
            new C00511(str, new File(getLogFilename(context)).getAbsolutePath(), context.getPackageName(), fileSizeKb).start();
        }
    }

    public static String getLogFilename(Context context) {
        return (Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getPackageName()) + ".logcat";
    }

    public static void cancelWriteLogcatToDisk(Context context) {
        String packageName = context.getPackageName();
        String absolutePath = new File(Environment.getExternalStorageDirectory(), packageName).getAbsolutePath();
        f417b = false;
        new C00522(absolutePath, packageName).start();
    }
}
