/*
 * Copyright (c) 2014, 2015 Qualcomm Technologies Inc
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * (subject to the limitations in the disclaimer below) provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Qualcomm Technologies Inc nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.qualcomm.robotcore.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.qualcomm.robotcore.exception.RobotCoreException;

import java.io.File;

public class RobotLog {
    public static final String TAG = "RobotCore";
    private static String globalErrorMessage = "";
    private static boolean writeLock;

    static class writeLogThread extends Thread {
        final String filePath;
        final String packageName;
        final int fileSizeKb;

        writeLogThread(String str, String filePath, String packageName, int fileSizeKb) {
            super(str);
            this.filePath = filePath;
            this.packageName = packageName;
            this.fileSizeKb = fileSizeKb;
        }

        public void run() {
            try {
                RobotLog.v("saving logcat to " + this.filePath);
                RunShellCommand runShellCommand = new RunShellCommand();
                RunShellCommand.killSpawnedProcess("logcat", this.packageName, runShellCommand);
                runShellCommand.run(String.format("logcat -f %s -r%d -n%d -v time %s", this.filePath, this.fileSizeKb, 1, "UsbRequestJNI:S UsbRequest:S *:V"));
            } catch (RobotCoreException e) {
                RobotLog.v("Error while writing log file to disk: " + e);
            } finally {
                RobotLog.writeLock = false;
            }
        }
    }

    static class CancelLogThread extends Thread {
        final String filePath;
        final String packageName;

        CancelLogThread(String filePath, String packageName) {
            this.filePath = filePath;
            this.packageName = packageName;
        }

        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            try {
                RobotLog.v("closing logcat file " + this.filePath);
                RunShellCommand.killSpawnedProcess("logcat", this.packageName, new RunShellCommand());
            } catch (RobotCoreException e2) {
                RobotLog.v("Unable to cancel writing log file to disk: " + e2);
            }
        }
    }

    private RobotLog() {
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

    public static void writeLogcatToDisk(Context context, int fileSizeKb) {
        if (!writeLock) {
            writeLock = true;
            new writeLogThread("Logging Thread", new File(getLogFilename(context)).getAbsolutePath(), context.getPackageName(), fileSizeKb).start();
        }
    }

    public static String getLogFilename(Context context) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getPackageName() + ".logcat";
    }

    public static void cancelWriteLogcatToDisk(Context context) {
        String packageName = context.getPackageName();
        String absolutePath = new File(Environment.getExternalStorageDirectory(), packageName).getAbsolutePath();
        writeLock = false;
        new CancelLogThread(absolutePath, packageName).start();
    }
}
