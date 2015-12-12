package com.qualcomm.robotcore.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import com.qualcomm.robotcore.BuildConfig;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class ExtractAssets {
    private static final String f379a;

    static {
        f379a = ExtractAssets.class.getSimpleName();
    }

    public static ArrayList<String> ExtractToStorage(Context context, ArrayList<String> files, boolean useInternalStorage) throws IOException {
        if (!useInternalStorage) {
            if (!"mounted".equals(Environment.getExternalStorageState())) {
                throw new IOException("External Storage not accessible");
            }
        }
        ArrayList<String> arrayList = new ArrayList();
        Iterator it = files.iterator();
        while (it.hasNext()) {
            m220a(context, (String) it.next(), useInternalStorage, arrayList);
            if (arrayList != null) {
                Log.d(f379a, "got " + arrayList.size() + " elements");
            }
        }
        return arrayList;
    }

    private static ArrayList<String> m220a(Context context, String str, boolean z, ArrayList<String> arrayList) {
        String[] list;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        Log.d(f379a, "Extracting assests for " + str);
        AssetManager assets = context.getAssets();
        try {
            list = assets.list(str);
        } catch (IOException e) {
            e.printStackTrace();
            list = null;
        }
        if (list.length == 0) {
            try {
                inputStream = assets.open(str);

                    File filesDir;
                    Log.d(f379a, "File: " + str + " opened for streaming");
                    if (!str.startsWith(File.separator)) {
                        str = File.separator + str;
                    }
                    if (z) {
                        filesDir = context.getFilesDir();
                    } else {
                        filesDir = context.getExternalFilesDir(null);
                    }
                    String concat = filesDir.getPath().concat(str);
                    if (arrayList == null || !arrayList.contains(concat)) {
                        int lastIndexOf = concat.lastIndexOf(File.separatorChar);
                        String substring = concat.substring(0, lastIndexOf);
                        String substring2 = concat.substring(lastIndexOf, concat.length());
                        File file = new File(substring);
                        if (file.mkdirs()) {
                            Log.d(f379a, "Dir created " + substring);
                        }
                        fileOutputStream = new FileOutputStream(new File(file, substring2));
                        if (fileOutputStream != null) {
                                byte[] bArr = new byte[1024];
                                while (true) {
                                    int read = inputStream.read(bArr);
                                    if (read != -1) {
                                        fileOutputStream.write(bArr, 0, read);
                                    } else {
                                        break;
                                    }
                                }
                                if (arrayList != null) {
                                    arrayList.add(concat);
                                }
                        }
                        if (arrayList != null) {
                            arrayList.add(concat);
                        }
                    } else {
                        Log.e(f379a, "Ignoring Duplicate entry for " + concat);
                    }
            } catch (IOException e7) {
                Log.d(f379a, "File: " + str + " doesn't exist");
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    Log.d(f379a, "Unable to close in stream");
                    e.printStackTrace();
                }

                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (Exception e) {
                    Log.d(f379a, "Unable to close out stream");
                    e.printStackTrace();
                }
            }
            return arrayList;
        }
        if (!(str.equals(BuildConfig.VERSION_NAME) || str.endsWith(File.separator))) {
            str = str.concat(File.separator);
        }
        for (String concat2 : list) {
            m220a(context, str.concat(concat2), z, arrayList);
        }
        return arrayList;
    }
}
