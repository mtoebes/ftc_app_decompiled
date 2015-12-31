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

import android.widget.TextView;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Util {
    public static String ASCII_RECORD_SEPARATOR = "\u001e";
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
