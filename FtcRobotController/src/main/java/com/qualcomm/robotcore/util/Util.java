package com.qualcomm.robotcore.util;

import android.widget.TextView;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Util {
    public static String ASCII_RECORD_SEPARATOR = "\u001e";
    public static final String LOWERCASE_ALPHA_NUM_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";

    public static String getRandomString(int stringLength, String charSet) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < stringLength; i++) {
            stringBuilder.append(charSet.charAt(random.nextInt(charSet.length())));
        }
        return stringBuilder.toString();
    }

    public static void sortFilesByName(File[] files) {
        Arrays.sort(files, new Comparator<File>(){
            public int compare(File f1, File f2){
                return f1.getName().compareTo(f2.getName());
            }
        });
    }

    public static void updateTextView(TextView textView, String msg) {
        final TextView postTextview = textView;
        final String postMessage = msg;

        if (textView != null) {
            textView.post(new Runnable() {
                public void run() {
                    postTextview.setText(postMessage);
                }
            });
        }
    }

    public static byte[] concatenateByteArrays(byte[] first, byte[] second) {
        byte[] buffer = new byte[(first.length + second.length)];
        System.arraycopy(first, 0, buffer, 0, first.length);
        System.arraycopy(second, 0, buffer, first.length, second.length);
        return buffer;
    }
}
