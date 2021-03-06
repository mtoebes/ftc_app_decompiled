package com.qualcomm.ftccommon;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.ScrollView;
import android.widget.TextView;
import com.qualcomm.robotcore.util.RobotLog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

public class ViewLogsActivity extends Activity {
    public static final String FILENAME = "Filename";
    TextView f88a;
    int f89b;
    String f90c;

    /* renamed from: com.qualcomm.ftccommon.ViewLogsActivity.1 */
    class C00301 implements Runnable {
        final /* synthetic */ ScrollView f85a;
        final /* synthetic */ ViewLogsActivity f86b;

        C00301(ViewLogsActivity viewLogsActivity, ScrollView scrollView) {
            this.f86b = viewLogsActivity;
            this.f85a = scrollView;
        }

        public void run() {
            this.f85a.fullScroll(130);
        }
    }

    /* renamed from: com.qualcomm.ftccommon.ViewLogsActivity.2 */
    class C00312 implements Runnable {
        final /* synthetic */ ViewLogsActivity f87a;

        C00312(ViewLogsActivity viewLogsActivity) {
            this.f87a = viewLogsActivity;
        }

        public void run() {
            try {
                this.f87a.f88a.setText(this.f87a.m45a(this.f87a.readNLines(this.f87a.f89b)));
            } catch (IOException e) {
                RobotLog.e(e.toString());
                this.f87a.f88a.setText("File not found: " + this.f87a.f90c);
            }
        }
    }

    public ViewLogsActivity() {
        this.f89b = 300;
        this.f90c = " ";
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_logs);
        this.f88a = (TextView) findViewById(R.id.textAdbLogs);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.post(new C00301(this, scrollView));
    }

    protected void onStart() {
        super.onStart();
        Serializable serializableExtra = getIntent().getSerializableExtra(FILENAME);
        if (serializableExtra != null) {
            this.f90c = (String) serializableExtra;
        }
        runOnUiThread(new C00312(this));
    }

    public String readNLines(int n) throws IOException {
        int i = 0;
        Environment.getExternalStorageDirectory();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(this.f90c)));
        String[] strArr = new String[n];
        int i2 = 0;
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                break;
            }
            strArr[i2 % strArr.length] = readLine;
            i2++;
        }
        int i3 = i2 - n;
        if (i3 >= 0) {
            i = i3;
        }
        int i4 = i;
        String str = BuildConfig.VERSION_NAME;
        i3 = i4;
        while (i3 < i2) {
            i3++;
            str = str + strArr[i3 % strArr.length] + "\n";
        }
        i2 = str.lastIndexOf("--------- beginning");
        if (i2 < 0) {
            return str;
        }
        return str.substring(i2);
    }

    private Spannable m45a(String str) {
        int i = 0;
        Spannable spannableString = new SpannableString(str);
        String[] split = str.split("\\n");
        int length = split.length;
        int i2 = 0;
        while (i < length) {
            String str2 = split[i];
            if (str2.contains("E/RobotCore") || str2.contains(DbgLog.ERROR_PREPEND)) {
                spannableString.setSpan(new ForegroundColorSpan(-65536), i2, str2.length() + i2, 33);
            }
            i2 = (i2 + str2.length()) + 1;
            i++;
        }
        return spannableString;
    }
}
