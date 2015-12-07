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
        for(String file : files) {
            ExtractAndCopy(context, file, useInternalStorage, arrayList);
            Log.d(TAG, "got " + arrayList.size() + " elements");
        }
        return arrayList;
    }

    private static ArrayList<String> ExtractAndCopy(Context context, String sourceFilePath, boolean useInternalStorage, ArrayList<String> fileList) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        Log.d(TAG, "Extracting assests for " + sourceFilePath);
        AssetManager assets = context.getAssets();

        String[] ipFiles = null;
        try {
            ipFiles = assets.list(sourceFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(ipFiles == null) {
            return fileList;
        }

        if (ipFiles.length == 0) {
            try {
                inputStream = assets.open(sourceFilePath);
                Log.d(TAG, "File: " + sourceFilePath + " opened for streaming");

                // Add "/" to filename if missing
                if (!sourceFilePath.startsWith(File.separator)) {
                    sourceFilePath = File.separator + sourceFilePath;
                }

                File filesDir;
                if (useInternalStorage) {
                    filesDir = context.getFilesDir();
                } else {
                    filesDir = context.getExternalFilesDir(null);
                }

                String outFile = filesDir.getPath().concat(sourceFilePath);

                // outFile already exists
                if(fileList != null && fileList.contains(sourceFilePath)) {
                    Log.e(TAG, "Ignoring Duplicate entry for " + outFile);
                    return fileList;
                }

                // Get the directory path and filename
                int dirPathEnd = outFile.lastIndexOf(File.separatorChar);
                String dirName = outFile.substring(0, dirPathEnd);
                String filename = outFile.substring(dirPathEnd, outFile.length());

                File outputFileDir = new File(dirName);
                if (outputFileDir.mkdirs()) {
                    Log.d(TAG, "Dir created " + dirName);
                }

                File outputFile = new File(outputFileDir, filename);
                outputStream = new FileOutputStream(outputFile);

                // Read from input stream and write to output stream
                byte[] readBuf = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(readBuf)) != -1) {
                    outputStream.write(readBuf, 0, bytesRead);
                }

                if (fileList != null) {
                    fileList.add(outFile);
                }
            } catch (IOException exception) {
                Log.d(TAG, "File: " + sourceFilePath + " doesn't exist");
            } catch (NullPointerException exception) {
                exception.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException exception) {
                    Log.d(TAG, "Unable to close in stream");
                }
                try {
                    if(outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException exception) {
                    Log.d(TAG, "Unable to close out stream");
                }
            }
        } else { // Recurse over children
            if (!(sourceFilePath.equals("") || sourceFilePath.endsWith(File.separator))) {
                sourceFilePath = sourceFilePath.concat(File.separator);
            }
            for (String ipFile : ipFiles) {
                sourceFilePath = sourceFilePath.concat(ipFile);
                ExtractAndCopy(context, sourceFilePath, useInternalStorage, fileList);
            }
        }
        return fileList;
    }
}
