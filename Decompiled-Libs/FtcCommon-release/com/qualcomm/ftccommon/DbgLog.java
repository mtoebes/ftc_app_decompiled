package com.qualcomm.ftccommon;

import android.util.Log;

public class DbgLog {
    public static final String ERROR_PREPEND = "### ERROR: ";
    public static final String TAG = "FIRST";

    private DbgLog() {
    }

    public static void msg(String message) {
        Log.i(TAG, message);
    }

    public static void error(String message) {
        Log.e(TAG, ERROR_PREPEND + message);
    }

    public static void logStacktrace(Exception e) {
        msg(e.toString());
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            msg(stackTraceElement.toString());
        }
    }
}
