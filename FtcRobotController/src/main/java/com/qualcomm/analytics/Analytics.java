package com.qualcomm.analytics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.Version;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Analytics extends BroadcastReceiver {
    public static final String DS_COMMAND_STRING = "update_ds";
    public static final String RC_COMMAND_STRING = "update_rc";
    public static final String DATA_COLLECTION_PATH = ".ftcdc";
    public static final String EXTERNAL_STORAGE_DIRECTORY_PATH = Environment.getExternalStorageDirectory() + "/";
    public static final String LAST_UPLOAD_DATE = "last_upload_date";
    public static final String MAX_DEVICES = "max_usb_devices";
    public static int MAX_ENTRIES_SIZE = 100;
    public static int TRIMMED_SIZE = 90;
    public static final String UUID_PATH = ".analytics_id";
    private static final Charset CHARSET = Charset.forName("UTF-8");

    static String qualcommServer = "https://ftcdc.qualcomm.com/DataApi";
    static long currentTime;
    static UUID uuid = null;
    static String libraryVersion;

    String commandString;
    Context context;
    SharedPreferences sharedPreferences;

    public Analytics(Context context, String commandString, HardwareMap map) {
        this.context = context;
        this.commandString = commandString;
        try {
            this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            currentTime = System.currentTimeMillis();
            libraryVersion = Version.getLibraryVersion();
            handleUUID(UUID_PATH);
            int calculateUsbDevices = calculateUsbDevices(map);
            if (this.sharedPreferences.getInt(MAX_DEVICES, 0) < calculateUsbDevices) {
                Editor edit = this.sharedPreferences.edit();
                edit.putInt(MAX_DEVICES, calculateUsbDevices);
                edit.apply();
            }
            handleData();
            register();
            RobotLog.i("Analytics has completed initialization.");
        } catch (Exception e) {
            clearAnalytics();
        }
    }

    public static class DataInfo implements Serializable {
        private final String data;
        protected int numUsages;

        public DataInfo(String date, int numUsages) {
            this.data = date;
            this.numUsages = numUsages;
        }

        public String encode() {
            String encodedDataInfo = "";
            try {
                encodedDataInfo = URLEncoder.encode(data, CHARSET.name()) + "," +
                        URLEncoder.encode(String.valueOf(numUsages), CHARSET.name());
            } catch (UnsupportedEncodingException e) {
                RobotLog.i("Analytics caught an UnsupportedEncodingException");
            }
            return encodedDataInfo;
        }
    }

    private class analyticsTask extends AsyncTask<Void, Void, Void> {
        final Analytics analytics;

        private analyticsTask(Analytics analytics) {
            this.analytics = analytics;
        }

        protected Void doInBackground(Void... params)  {
            if (this.analytics.isConnected()) {
                try {
                    URL url = new URL(Analytics.qualcommServer);
                    if (!Analytics.getDateFromTime(Analytics.currentTime).equals(Analytics.getDateFromTime(this.analytics.sharedPreferences.getLong(Analytics.LAST_UPLOAD_DATE, 0)))) {
                        String ping = Analytics.ping(url, this.analytics.encodeStat("cmd", "ping"));
                        CharSequence charSequence = "\"rc\": \"OK\"";
                        if (ping == null || !ping.contains(charSequence)) {
                            RobotLog.e("Analytics: Ping failed.");
                        } else {
                            RobotLog.i("Analytics ping succeeded.");
                            ping = Analytics.EXTERNAL_STORAGE_DIRECTORY_PATH + Analytics.DATA_COLLECTION_PATH;
                            ArrayList<DataInfo> readObjectsFromFile = this.analytics.readObjectsFromFile(ping);
                            if (readObjectsFromFile.size() >= Analytics.MAX_ENTRIES_SIZE) {
                                this.analytics.trimEntries(readObjectsFromFile);
                            }
                            String call = Analytics.call(url, this.analytics.getAnalyticsStats(Analytics.uuid.toString(), readObjectsFromFile, this.analytics.commandString));
                            if (call == null || !call.contains(charSequence)) {
                                RobotLog.e("Analytics: Upload failed.");
                            } else {
                                RobotLog.i("Analytics: Upload succeeded.");
                                Editor edit = this.analytics.sharedPreferences.edit();
                                edit.putLong(Analytics.LAST_UPLOAD_DATE, Analytics.currentTime);
                                edit.apply();
                                edit.putInt(Analytics.MAX_DEVICES, 0);
                                edit.apply();

                                File pingFile = new File(ping);
                                if(!pingFile.delete()) {
                                    RobotLog.i("Analytics: failed to close file " + pingFile);
                                }
                            }
                        }
                    }
                } catch (MalformedURLException urlException) {
                    RobotLog.e("Analytics encountered a malformed URL exception");
                } catch (Exception exception) {
                    RobotLog.i("Analytics encountered a problem during communication");
                    this.analytics.clearAnalytics();
                }
            }
            return null;
        }
    }

    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null && extras.containsKey("networkInfo") && ((NetworkInfo) extras.get("networkInfo")).getState().equals(State.CONNECTED)) {
            RobotLog.i("Analytics detected NetworkInfo.State.CONNECTED");
            communicateWithServer();
        }
    }

    public void unregister() {
        this.context.unregisterReceiver(this);
    }

    public void register() {
        this.context.registerReceiver(this, new IntentFilter("android.net.wifi.STATE_CHANGE"));
    }

    protected int calculateUsbDevices(HardwareMap map) {
        int size = map.legacyModule.size() + map.deviceInterfaceModule.size();
        Iterator it = map.servoController.iterator();
        int i = size;
        while (it.hasNext()) {
            if (Pattern.compile("(?i)usb").matcher(((ServoController) it.next()).getDeviceName()).matches()) {
                size = i + 1;
            } else {
                size = i;
            }
            i = size;
        }
        it = map.dcMotorController.iterator();
        while (it.hasNext()) {
            if (Pattern.compile("(?i)usb").matcher(((DcMotorController) it.next()).getDeviceName()).matches()) {
                i++;
            }
        }
        return i;
    }

    protected void handleData() throws IOException, ClassNotFoundException {
        String str = EXTERNAL_STORAGE_DIRECTORY_PATH + DATA_COLLECTION_PATH;
        if (new File(str).exists()) {
            ArrayList<DataInfo> updateExistingFile = updateExistingFile(str, getDateFromTime(currentTime));
            if (updateExistingFile.size() >= MAX_ENTRIES_SIZE) {
                trimEntries(updateExistingFile);
            }
            writeObjectsToFile(str, updateExistingFile);
            return;
        }
        createInitialFile(str);
    }

    protected void trimEntries(ArrayList<DataInfo> dataInfoArrayList) {
        dataInfoArrayList.subList(TRIMMED_SIZE, dataInfoArrayList.size()).clear();
    }

    protected ArrayList<DataInfo> updateExistingFile(String filepath, String date) throws ClassNotFoundException, IOException {
        ArrayList<DataInfo> readObjectsFromFile = readObjectsFromFile(filepath);
        DataInfo dataInfo = readObjectsFromFile.get(readObjectsFromFile.size() - 1);
        if (dataInfo.data.equalsIgnoreCase(date)) {
            dataInfo.numUsages++;
        } else {
            readObjectsFromFile.add(new DataInfo(date, 1));
        }
        return readObjectsFromFile;
    }

    protected ArrayList<DataInfo> readObjectsFromFile(String filepath) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(new File(filepath)));
        ArrayList<DataInfo> arrayList = new ArrayList<DataInfo>();
        Object obj = 1;
        while (obj != null) {
            try {
                arrayList.add((DataInfo) objectInputStream.readObject());
            } catch (EOFException e) {
                obj = null;
            }
        }
        objectInputStream.close();
        return arrayList;
    }

    protected void createInitialFile(String filepath) throws IOException {
        DataInfo dataInfo = new DataInfo(getDateFromTime(currentTime), 1);
        ArrayList<DataInfo> arrayList = new ArrayList<DataInfo>();
        arrayList.add(dataInfo);
        writeObjectsToFile(filepath, arrayList);
    }

    private void clearAnalytics() {
        RobotLog.i("Analytics is starting with a clean slate.");
        Editor edit = this.sharedPreferences.edit();
        edit.putLong(LAST_UPLOAD_DATE, 0);
        edit.apply();
        edit.putInt(MAX_DEVICES, 0);
        edit.apply();

        File data_collection_dir = new File(EXTERNAL_STORAGE_DIRECTORY_PATH + DATA_COLLECTION_PATH);

        if(!data_collection_dir.delete()) {
            RobotLog.i("Analytics failed to delete file " + data_collection_dir);
        }

        File uuid_dir = new File(EXTERNAL_STORAGE_DIRECTORY_PATH + UUID_PATH);

        if(!uuid_dir.delete()) {
            RobotLog.i("Analytics failed to delete file " + data_collection_dir);
        }
    }

    public void communicateWithServer() {
        new analyticsTask(this).execute();
    }

    public void handleUUID(String filename) {
        File file = new File(EXTERNAL_STORAGE_DIRECTORY_PATH + filename);
        if (!file.exists()) {
            uuid = UUID.randomUUID();
            handleCreateNewFile(EXTERNAL_STORAGE_DIRECTORY_PATH + filename, uuid.toString());
        }
        try {
            uuid = UUID.fromString(readFromFile(file));
        } catch (IllegalArgumentException e) {
            RobotLog.i("Analytics encountered an IllegalArgumentException");
            uuid = UUID.randomUUID();
            handleCreateNewFile(EXTERNAL_STORAGE_DIRECTORY_PATH + filename, uuid.toString());
        }
    }

    protected String readFromFile(File file) {
        String data = "";
        try {
            char[] fileBuffer = new char[4096];
            FileReader fileReader = new FileReader(file);
            int len = fileReader.read(fileBuffer);
            fileReader.close();
            data = new String(fileBuffer, 0, len).trim();
        } catch (FileNotFoundException e) {
            RobotLog.i("Analytics encountered a FileNotFoundException while trying to read a file.");
        } catch (IOException e2) {
            RobotLog.i("Analytics encountered an IOException while trying to read.");
        }
        return data;
    }

    protected void writeObjectsToFile(String filepath, ArrayList<DataInfo> info) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(filepath));

        for(DataInfo data : info) {
            objectOutputStream.writeObject(data);
        }

        objectOutputStream.close();
    }

    protected void handleCreateNewFile(String filepath, String data) {
        Writer bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filepath)), "utf-8"));
            bufferedWriter.write(data);
            bufferedWriter.close();
        } catch (IOException ioException) {
            RobotLog.i("Analytics encountered an IOException: " + ioException.toString());
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException ioException) {
                // do nothing
            }
        }
    }

    public static String getDateFromTime(long time) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date(time));
    }

    protected static UUID getUuid() {
        return uuid;
    }

    public String getAnalyticsStats(String user, ArrayList<DataInfo> dateInfoList, String commandString) {
        return getHardwareStats(user, commandString) + getDataInfoStats(dateInfoList);
    }

    private String getHardwareStats(String user, String commandString) {
        return encodeStat("cmd", commandString) + "&" +
                encodeStat("uuid", user) + "&" +
                encodeStat("device_hw", Build.MANUFACTURER) + "&" +
                encodeStat("device_ver", Build.MODEL) + "&" +
                encodeStat("chip_type", getChipType()) + "&" +
                encodeStat("sw_ver", libraryVersion) + "&" +
                encodeStat("max_dev", String.valueOf(this.sharedPreferences.getInt(MAX_DEVICES, 0))) + "&" +
                encodeStat("dc", "");
    }

    private String getDataInfoStats(ArrayList<DataInfo> dateInfoList) {
        String dataInfoStats = "";
        for(int i=0; i < dateInfoList.size(); i++) {
            if(i > 0 ) {
                dataInfoStats += ",";
            }
            dataInfoStats += dateInfoList.get(i).encode();
        }
        return dataInfoStats;
    }

    private String encodeStat(String statName, String statValue) {
        String encodedStat = "";
        try {
            encodedStat = URLEncoder.encode(statName, CHARSET.name()) + "=" +
                    URLEncoder.encode(statValue, CHARSET.name());
        } catch (UnsupportedEncodingException e) {
            RobotLog.i("Analytics caught an UnsupportedEncodingException");
        }
        return encodedStat;
    }

    private String getChipType() {
        String cpu = "CPU implementer".toLowerCase();
        String hardware = "Hardware".toLowerCase();

        String chipType = null;

        Map<String, String> hashMap = new HashMap<String, String>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/cpuinfo"));
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                String[] split = line.toLowerCase().split(":");
                if (split.length >= 2) {
                    hashMap.put(split[0].trim(), split[1].trim());
                }
            }
            bufferedReader.close();
            chipType = hashMap.get(cpu) + " " + hashMap.get(hardware);
        } catch (FileNotFoundException fileException) {
            RobotLog.i("Analytics encountered a FileNotFoundException while looking for CPU info");
        } catch (IOException ioException) {
            RobotLog.i("Analytics encountered an IOException while looking for CPU info");
        } finally {
            if (chipType == null || chipType.isEmpty()) {
                chipType = "UNKNOWN";
            }
        }
        return chipType;
    }

    public boolean isConnected() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) this.context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String ping(URL baseUrl, String data) {
        return call(baseUrl, data);
    }

    public static String call(URL url, String data) {
        String str = "";
        if (url == null || data == null) {
            return null;
        }
        try {
            HttpsURLConnection httpsURLConnection;
            long currentTimeMillis = System.currentTimeMillis();
            if (url.getProtocol().toLowerCase().equals("https")) {
                m6c();
                httpsURLConnection = (HttpsURLConnection) url.openConnection();

                HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };

                httpsURLConnection.setHostnameVerifier(hostnameVerifier);
            } else {
                httpsURLConnection = (HttpsURLConnection) url.openConnection();
            }
            httpsURLConnection.setDoOutput(true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
            outputStreamWriter.write(data);
            outputStreamWriter.flush();
            outputStreamWriter.close();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));

            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    str = str + readLine;
                } else {
                    bufferedReader.close();
                    RobotLog.i("Analytics took: " + (System.currentTimeMillis() - currentTimeMillis) + "ms");
                    return str;
                }
            }
        } catch (IOException e2) {
            RobotLog.i("Analytics Failed to process command.");
            return null;
        }
    }

    private static void m6c() {
        X509TrustManager trustManager = new X509TrustManager() {

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        };

        TrustManager[] trustManagerArr = new TrustManager[]{trustManager};
        try {
            SSLContext instance = SSLContext.getInstance("TLS");
            instance.init(null, trustManagerArr, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(instance.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}