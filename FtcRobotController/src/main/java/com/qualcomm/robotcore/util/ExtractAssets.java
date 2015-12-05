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
        InputStream open;
        InputStream inputStream;
        Throwable th;
        Throwable th2;
        FileOutputStream fileOutputStream = null;
        Log.d(f379a, "Extracting assests for " + str);
        AssetManager assets = context.getAssets();
        try {
            list = assets.list(str);
        } catch (IOException e) {
            e.printStackTrace();
            list = null;
        }
        FileOutputStream fileOutputStream2 = null;
        if (list.length == 0) {
            try {
                open = assets.open(str);
                try {
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
                        FileOutputStream fileOutputStream3 = new FileOutputStream(new File(file, substring2));
                        if (fileOutputStream3 != null) {
                            try {
                                byte[] bArr = new byte[1024];
                                while (true) {
                                    int read = open.read(bArr);
                                    if (read != -1) {
                                        fileOutputStream3.write(bArr, 0, read);
                                    }
                                }
                                fileOutputStream3.close();
                                if (arrayList != null) {
                                    arrayList.add(concat);
                                }
                                if (open != null) {
                                    try {
                                        open.close();
                                    } catch (IOException e2) {
                                        Log.d(f379a, "Unable to close in stream");
                                        e2.printStackTrace();
                                    }
                                    if (fileOutputStream3 != null) {
                                        try {
                                            fileOutputStream3.close();
                                        } catch (IOException e3) {
                                            Log.d(f379a, "Unable to close out stream");
                                            e3.printStackTrace();
                                        }
                                    }
                                }
                            } catch (IOException e4) {
                                fileOutputStream = fileOutputStream3;
                                inputStream = open;
                                try {
                                    Log.d(f379a, "File: " + str + " doesn't exist");
                                    if (inputStream != null) {
                                        try {
                                            inputStream.close();
                                        } catch (IOException e32) {
                                            Log.d(f379a, "Unable to close in stream");
                                            e32.printStackTrace();
                                        }
                                        if (fileOutputStream != null) {
                                            try {
                                                fileOutputStream.close();
                                            } catch (IOException e322) {
                                                Log.d(f379a, "Unable to close out stream");
                                                e322.printStackTrace();
                                            }
                                        }
                                    }
                                    return arrayList;
                                } catch (Throwable th3) {
                                    th = th3;
                                    open = inputStream;
                                    th2 = th;
                                    if (open != null) {
                                        try {
                                            open.close();
                                        } catch (IOException e5) {
                                            Log.d(f379a, "Unable to close in stream");
                                            e5.printStackTrace();
                                        }
                                        if (fileOutputStream != null) {
                                            try {
                                                fileOutputStream.close();
                                            } catch (IOException e22) {
                                                Log.d(f379a, "Unable to close out stream");
                                                e22.printStackTrace();
                                            }
                                        }
                                    }
                                    throw th2;
                                }
                            } catch (Throwable th4) {
                                th = th4;
                                fileOutputStream = fileOutputStream3;
                                th2 = th;
                                if (open != null) {
                                    open.close();
                                    if (fileOutputStream != null) {
                                        fileOutputStream.close();
                                    }
                                }
                                throw th2;
                            }
                        }
                        break;
                        fileOutputStream3.close();
                        if (arrayList != null) {
                            arrayList.add(concat);
                        }
                        if (open != null) {
                            open.close();
                            if (fileOutputStream3 != null) {
                                fileOutputStream3.close();
                            }
                        }
                    } else {
                        Log.e(f379a, "Ignoring Duplicate entry for " + concat);
                        if (open != null) {
                            try {
                                open.close();
                            } catch (IOException e3222) {
                                Log.d(f379a, "Unable to close in stream");
                                e3222.printStackTrace();
                            }
                            if (null != null) {
                                try {
                                    fileOutputStream2.close();
                                } catch (IOException e32222) {
                                    Log.d(f379a, "Unable to close out stream");
                                    e32222.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (IOException e6) {
                    inputStream = open;
                    Log.d(f379a, "File: " + str + " doesn't exist");
                    if (inputStream != null) {
                        inputStream.close();
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                    }
                    return arrayList;
                } catch (Throwable th5) {
                    th2 = th5;
                    if (open != null) {
                        open.close();
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                    }
                    throw th2;
                }
            } catch (IOException e7) {
                inputStream = null;
                Log.d(f379a, "File: " + str + " doesn't exist");
                if (inputStream != null) {
                    inputStream.close();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                }
                return arrayList;
            } catch (Throwable th6) {
                th2 = th6;
                open = null;
                if (open != null) {
                    open.close();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                }
                throw th2;
            }
        }
        if (!(str.equals("") || str.endsWith(File.separator))) {
            str = str.concat(File.separator);
        }
        for (String concat2 : list) {
            m220a(context, str.concat(concat2), z, arrayList);
        }
        return arrayList;
    }
}
