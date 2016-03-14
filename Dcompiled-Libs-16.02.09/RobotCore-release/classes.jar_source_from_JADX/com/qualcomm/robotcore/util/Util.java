package com.qualcomm.robotcore.util;

import android.widget.TextView;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Util {
    public static String ASCII_RECORD_SEPARATOR = null;
    public static final String LOWERCASE_ALPHA_NUM_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";

    /* renamed from: com.qualcomm.robotcore.util.Util.1 */
    static class C00581 implements Comparator<File> {
        C00581() {
        }

        public /* synthetic */ int compare(Object obj, Object obj2) {
            return m259a((File) obj, (File) obj2);
        }

        public int m259a(File file, File file2) {
            return file.getName().compareTo(file2.getName());
        }
    }

    /* renamed from: com.qualcomm.robotcore.util.Util.2 */
    static class C00592 implements Runnable {
        final /* synthetic */ TextView f429a;
        final /* synthetic */ String f430b;

        C00592(TextView textView, String str) {
            this.f429a = textView;
            this.f430b = str;
        }

        public void run() {
            this.f429a.setText(this.f430b);
        }
    }

    static {
        ASCII_RECORD_SEPARATOR = "\u001e";
    }

    public static String getRandomString(int stringLength, String charSet) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < stringLength; i++) {
            stringBuilder.append(charSet.charAt(random.nextInt(charSet.length())));
        }
        return stringBuilder.toString();
    }

    public static void sortFilesByName(File[] files) {
        Arrays.sort(files, new C00581());
    }

    public static void updateTextView(TextView textView, String msg) {
        if (textView != null) {
            textView.post(new C00592(textView, msg));
        }
    }

    public static byte[] concatenateByteArrays(byte[] first, byte[] second) {
        Object obj = new byte[(first.length + second.length)];
        System.arraycopy(first, 0, obj, 0, first.length);
        System.arraycopy(second, 0, obj, first.length, second.length);
        return obj;
    }

    public static void logThreadLifeCycle(String name, Runnable runnable) {
        try {
            Thread.currentThread().setName(name);
            RobotLog.m254v(String.format("thread: '%s' starting...", new Object[]{name}));
            runnable.run();
            RobotLog.m254v(String.format("thread: ...terminating '%s'", new Object[]{name}));
        } catch (Throwable th) {
            RobotLog.m254v(String.format("thread: ...terminating '%s'", new Object[]{name}));
        }
    }
}
