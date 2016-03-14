package com.qualcomm.ftccommon;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.Version;
import com.qualcomm.robotcore.wifi.WifiDirectAssistant;

public class AboutActivity extends Activity {
    WifiDirectAssistant f1a;

    /* renamed from: com.qualcomm.ftccommon.AboutActivity.1 */
    class C00001 extends ArrayAdapter<Item> {
        final /* synthetic */ AboutActivity f0a;

        C00001(AboutActivity aboutActivity, Context context, int i, int i2) {
            this.f0a = aboutActivity;
            super(context, i, i2);
        }

        public /* synthetic */ Object getItem(int i) {
            return m3a(i);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = (TextView) view.findViewById(16908308);
            TextView textView2 = (TextView) view.findViewById(16908309);
            Item a = m3a(position);
            textView.setText(a.title);
            textView2.setText(a.info);
            return view;
        }

        public int getCount() {
            return 3;
        }

        public Item m3a(int i) {
            switch (i) {
                case 0:
                    return m0a();
                case BuildConfig.VERSION_CODE /*1*/:
                    return m1b();
                case 2:
                    return m2c();
                default:
                    return new Item();
            }
        }

        private Item m0a() {
            Item item = new Item();
            item.title = "App Version";
            try {
                item.info = this.f0a.getPackageManager().getPackageInfo(this.f0a.getPackageName(), 0).versionName;
            } catch (NameNotFoundException e) {
                item.info = e.getMessage();
            }
            return item;
        }

        private Item m1b() {
            Item item = new Item();
            item.title = "Library Version";
            item.info = Version.getLibraryVersion();
            return item;
        }

        private Item m2c() {
            Item item = new Item();
            item.title = "Wifi Direct Information";
            item.info = "unavailable";
            StringBuilder stringBuilder = new StringBuilder();
            if (this.f0a.f1a != null && this.f0a.f1a.isEnabled()) {
                stringBuilder.append("Name: ").append(this.f0a.f1a.getDeviceName());
                if (this.f0a.f1a.isGroupOwner()) {
                    stringBuilder.append("\nIP Address").append(this.f0a.f1a.getGroupOwnerAddress().getHostAddress());
                    stringBuilder.append("\nPassphrase: ").append(this.f0a.f1a.getPassphrase());
                    stringBuilder.append("\nGroup Owner");
                } else if (this.f0a.f1a.isConnected()) {
                    stringBuilder.append("\nGroup Owner: ").append(this.f0a.f1a.getGroupOwnerName());
                    stringBuilder.append("\nConnected");
                } else {
                    stringBuilder.append("\nNo connection information");
                }
                item.info = stringBuilder.toString();
            }
            return item;
        }
    }

    public static class Item {
        public String info;
        public String title;

        public Item() {
            this.title = BuildConfig.VERSION_NAME;
            this.info = BuildConfig.VERSION_NAME;
        }
    }

    public AboutActivity() {
        this.f1a = null;
    }

    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_about);
        ListView listView = (ListView) findViewById(R.id.aboutList);
        try {
            this.f1a = WifiDirectAssistant.getWifiDirectAssistant(null);
            this.f1a.enable();
        } catch (NullPointerException e) {
            RobotLog.i("Cannot start Wifi Direct Assistant");
            this.f1a = null;
        }
        listView.setAdapter(new C00001(this, this, 17367044, 16908308));
    }

    protected void onStop() {
        super.onStop();
        if (this.f1a != null) {
            this.f1a.disable();
        }
    }
}
