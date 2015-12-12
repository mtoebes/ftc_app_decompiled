package com.qualcomm.robotcore.util;

import android.widget.TextView;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Util {
    public static String ASCII_RECORD_SEPARATOR;
    public static final String LOWERCASE_ALPHA_NUM_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";

    static class fileComparator implements Comparator<File> {

        public int compare(File obj, File obj2) {
            return obj.getName().compareTo(obj2.getName());
        }
    }

    static class updateTextViewRunnable implements Runnable {
        final TextView textView;
        final String message;

        updateTextViewRunnable(TextView textView, String str) {
            this.textView = textView;
            this.message = str;
        }

        public void run() {
            this.textView.setText(this.message);
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
        Arrays.sort(files, new fileComparator());
    }

    public static void updateTextView(TextView textView, String msg) {
        if (textView != null) {
            textView.post(new updateTextViewRunnable(textView, msg));
        }
    }

    public static byte[] concatenateByteArrays(byte[] first, byte[] second) {
        byte[] buffer = new byte[(first.length + second.length)];
        System.arraycopy(first, 0, buffer, 0, first.length);
        System.arraycopy(second, 0, buffer, first.length, second.length);
        return buffer;
    }
}
