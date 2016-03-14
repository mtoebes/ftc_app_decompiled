package com.qualcomm.robotcore.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.qualcomm.robotcore.BuildConfig;
import com.qualcomm.robotcore.exception.RobotCoreException;
import java.io.File;
import java.util.WeakHashMap;

public class RobotLog {
    public static final String TAG = "RobotCore";
    private static String f415a;
    private static final Object f416b;
    private static String f417c;
    private static WeakHashMap<GlobalWarningSource, Integer> f418d;
    private static boolean f419e;

    /* renamed from: com.qualcomm.robotcore.util.RobotLog.1 */
    static class C00561 extends Thread {
        final /* synthetic */ String f410a;
        final /* synthetic */ String f411b;
        final /* synthetic */ int f412c;

        C00561(String str, String str2, String str3, int i) {
            this.f410a = str2;
            this.f411b = str3;
            this.f412c = i;
            super(str);
        }

        public void run() {
            try {
                String str = "UsbRequestJNI:S UsbRequest:S *:V";
                RobotLog.m254v("saving logcat to " + this.f410a);
                RunShellCommand runShellCommand = new RunShellCommand();
                RunShellCommand.killSpawnedProcess("logcat", this.f411b, runShellCommand);
                runShellCommand.run(String.format("logcat -f %s -r%d -n%d -v time %s", new Object[]{this.f410a, Integer.valueOf(this.f412c), Integer.valueOf(1), "UsbRequestJNI:S UsbRequest:S *:V"}));
            } catch (RobotCoreException e) {
                RobotLog.m254v("Error while writing log file to disk: " + e.toString());
            } finally {
                RobotLog.f419e = false;
            }
        }
    }

    /* renamed from: com.qualcomm.robotcore.util.RobotLog.2 */
    static class C00572 extends Thread {
        final /* synthetic */ String f413a;
        final /* synthetic */ String f414b;

        C00572(String str, String str2) {
            this.f413a = str;
            this.f414b = str2;
        }

        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            try {
                RobotLog.m254v("closing logcat file " + this.f413a);
                RunShellCommand.killSpawnedProcess("logcat", this.f414b, new RunShellCommand());
            } catch (RobotCoreException e2) {
                RobotLog.m254v("Unable to cancel writing log file to disk: " + e2.toString());
            }
        }
    }

    private RobotLog() {
    }

    static {
        f415a = BuildConfig.VERSION_NAME;
        f416b = new Object();
        f417c = BuildConfig.VERSION_NAME;
        f418d = new WeakHashMap();
        f419e = false;
    }

    public static void m255v(String format, Object... args) {
        m254v(String.format(format, args));
    }

    public static void m254v(String message) {
        Log.v(TAG, message);
    }

    public static void m249d(String format, Object... args) {
        m248d(String.format(format, args));
    }

    public static void m248d(String message) {
        Log.d(TAG, message);
    }

    public static void m253i(String format, Object... args) {
        m252i(String.format(format, args));
    }

    public static void m252i(String message) {
        Log.i(TAG, message);
    }

    public static void m257w(String format, Object... args) {
        m256w(String.format(format, args));
    }

    public static void m256w(String message) {
        Log.w(TAG, message);
    }

    public static void m251e(String format, Object... args) {
        m250e(String.format(format, args));
    }

    public static void m250e(String message) {
        Log.e(TAG, message);
    }

    public static void logStacktrace(Exception e) {
        m250e(e.toString());
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            m250e(stackTraceElement.toString());
        }
    }

    public static void logStacktrace(RobotCoreException e) {
        m250e(e.toString());
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            m250e(stackTraceElement.toString());
        }
        if (e.isChainedException()) {
            m250e("Exception chained from:");
            if (e.getChainedException() instanceof RobotCoreException) {
                logStacktrace((RobotCoreException) e.getChainedException());
            } else {
                logStacktrace(e.getChainedException());
            }
        }
    }

    public static void setGlobalErrorMsg(String message) {
        if (f415a.isEmpty()) {
            f415a += message;
        }
    }

    public static void setGlobalErrorMsg(String format, Object... args) {
        setGlobalErrorMsg(String.format(format, args));
    }

    public static void setGlobalWarningMessage(String message) {
        synchronized (f416b) {
            if (f417c.isEmpty()) {
                f417c += message;
            }
        }
    }

    public static void setGlobalWarningMessage(String format, Object... args) {
        setGlobalWarningMessage(String.format(format, args));
    }

    public static void registerGlobalWarningSource(GlobalWarningSource globalWarningSource) {
        synchronized (f416b) {
            f418d.put(globalWarningSource, Integer.valueOf(1));
        }
    }

    public static void unregisterGlobalWarningSource(GlobalWarningSource globalWarningSource) {
        synchronized (f416b) {
            f418d.remove(globalWarningSource);
        }
    }

    public static void setGlobalErrorMsgAndThrow(RobotCoreException e, String message) throws RobotCoreException {
        setGlobalErrorMsg(message + ": " + e.getMessage());
        throw e;
    }

    public static String getGlobalErrorMsg() {
        return f415a;
    }

    public static String getGlobalWarningMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        synchronized (f416b) {
            if (!f417c.isEmpty()) {
                stringBuilder.append(f417c);
            }
            for (GlobalWarningSource globalWarning : f418d.keySet()) {
                String globalWarning2 = globalWarning.getGlobalWarning();
                if (!(globalWarning2 == null || globalWarning2.isEmpty())) {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append("; ");
                    }
                    stringBuilder.append(globalWarning2);
                }
            }
        }
        return stringBuilder.toString();
    }

    public static boolean hasGlobalErrorMsg() {
        return !getGlobalErrorMsg().isEmpty();
    }

    public static boolean hasGlobalWarningMsg() {
        return !getGlobalWarningMessage().isEmpty();
    }

    public static void clearGlobalErrorMsg() {
        f415a = BuildConfig.VERSION_NAME;
    }

    public static void clearGlobalWarningMsg() {
        synchronized (f416b) {
            f417c = BuildConfig.VERSION_NAME;
        }
    }

    public static void logAndThrow(String errMsg) throws RobotCoreException {
        m256w(errMsg);
        throw new RobotCoreException(errMsg);
    }

    public static void writeLogcatToDisk(Context context, int fileSizeKb) {
        if (!f419e) {
            f419e = true;
            String str = "Logging Thread";
            new C00561(str, new File(getLogFilename(context)).getAbsolutePath(), context.getPackageName(), fileSizeKb).start();
        }
    }

    public static String getLogFilename(Context context) {
        return (Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getPackageName()) + ".logcat";
    }

    public static void cancelWriteLogcatToDisk(Context context) {
        String packageName = context.getPackageName();
        String absolutePath = new File(Environment.getExternalStorageDirectory(), packageName).getAbsolutePath();
        f419e = false;
        new C00572(absolutePath, packageName).start();
    }
}
