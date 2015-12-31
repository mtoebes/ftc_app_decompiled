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
