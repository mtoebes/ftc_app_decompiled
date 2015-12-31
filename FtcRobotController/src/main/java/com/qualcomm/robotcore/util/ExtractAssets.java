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
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ExtractAssets {
    private static final String TAG = ExtractAssets.class.getSimpleName();

    public static ArrayList<String> ExtractToStorage(Context context, ArrayList<String> files, boolean useInternalStorage) throws IOException {
        if (!useInternalStorage) {
            if (!"mounted".equals(Environment.getExternalStorageState())) {
                throw new IOException("External Storage not accessible");
            }
        }
        ArrayList<String> arrayList = new ArrayList<String>();
        for (String file : files) {
            ExtractAndCopy(context, file, useInternalStorage, arrayList);
            Log.d(TAG, "got " + arrayList.size() + " elements");
        }
        return arrayList;
    }

    private static ArrayList<String> ExtractAndCopy(Context context, String sourceFilePath, boolean useInternalStorage, ArrayList<String> fileList) {
        String[] ipFiles = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        Log.d(TAG, "Extracting assets for " + sourceFilePath);
        AssetManager assets = context.getAssets();
        try {
            ipFiles = assets.list(sourceFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ipFiles == null) {
            return fileList;
        }

        if (ipFiles.length == 0) {
            try {
                inputStream = assets.open(sourceFilePath);

                File filesDir;
                Log.d(TAG, "File: " + sourceFilePath + " opened for streaming");
                if (!sourceFilePath.startsWith(File.separator)) {
                    sourceFilePath = File.separator + sourceFilePath;
                }
                if (useInternalStorage) {
                    filesDir = context.getFilesDir();
                } else {
                    filesDir = context.getExternalFilesDir(null);
                }

                if (filesDir == null) {
                    return fileList;
                }

                String outFile = filesDir.getPath() + sourceFilePath;

                if ((fileList == null) || !(fileList.contains(outFile))) {
                    int lastIndexOf = outFile.lastIndexOf(File.separatorChar);
                    String dirName = outFile.substring(0, lastIndexOf);
                    String fileName = outFile.substring(lastIndexOf, outFile.length());
                    File file = new File(dirName);
                    if (file.mkdirs()) {
                        Log.d(TAG, "Dir created " + dirName);
                    }
                    outputStream = new FileOutputStream(new File(file, fileName));
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, read);
                    }
                    if (fileList != null) {
                        fileList.add(outFile);
                    }
                } else {
                    Log.e(TAG, "Ignoring Duplicate entry for " + outFile);
                }
            } catch (IOException e7) {
                Log.d(TAG, "File: " + sourceFilePath + " doesn't exist");
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Unable to close in stream");
                    e.printStackTrace();
                }

                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Unable to close out stream");
                    e.printStackTrace();
                }
            }
            return fileList;
        } else {
            if (!("".equals(sourceFilePath) || sourceFilePath.endsWith(File.separator))) {
                sourceFilePath = sourceFilePath + File.separator;
            }
            for (String ipFile : ipFiles) {
                ExtractAndCopy(context, sourceFilePath + ipFile, useInternalStorage, fileList);
            }
            return fileList;
        }
    }
}
