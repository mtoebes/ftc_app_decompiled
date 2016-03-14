package com.qualcomm.ftccommon;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.widget.Toast;

public class FtcRobotControllerSettingsActivity extends Activity {

    public static class SettingsFragment extends PreferenceFragment {
        OnPreferenceClickListener f37a;

        /* renamed from: com.qualcomm.ftccommon.FtcRobotControllerSettingsActivity.SettingsFragment.1 */
        class C00081 implements OnPreferenceClickListener {
            final /* synthetic */ SettingsFragment f33a;

            C00081(SettingsFragment settingsFragment) {
                this.f33a = settingsFragment;
            }

            public boolean onPreferenceClick(Preference preference) {
                try {
                    this.f33a.startActivity(this.f33a.getActivity().getPackageManager().getLaunchIntentForPackage(LaunchActivityConstantsList.ZTE_WIFI_CHANNEL_EDITOR_PACKAGE));
                } catch (NullPointerException e) {
                    Toast.makeText(this.f33a.getActivity(), "Unable to launch ZTE WifiChannelEditor", 0).show();
                }
                return true;
            }
        }

        /* renamed from: com.qualcomm.ftccommon.FtcRobotControllerSettingsActivity.SettingsFragment.2 */
        class C00092 implements OnPreferenceClickListener {
            final /* synthetic */ SettingsFragment f34a;

            C00092(SettingsFragment settingsFragment) {
                this.f34a = settingsFragment;
            }

            public boolean onPreferenceClick(Preference preference) {
                this.f34a.startActivity(new Intent(preference.getIntent().getAction()));
                return true;
            }
        }

        /* renamed from: com.qualcomm.ftccommon.FtcRobotControllerSettingsActivity.SettingsFragment.3 */
        class C00103 implements OnPreferenceClickListener {
            final /* synthetic */ SettingsFragment f35a;

            C00103(SettingsFragment settingsFragment) {
                this.f35a = settingsFragment;
            }

            public boolean onPreferenceClick(Preference preference) {
                this.f35a.startActivity(new Intent("android.settings.SETTINGS"));
                return true;
            }
        }

        /* renamed from: com.qualcomm.ftccommon.FtcRobotControllerSettingsActivity.SettingsFragment.4 */
        class C00114 implements OnPreferenceClickListener {
            final /* synthetic */ SettingsFragment f36a;

            C00114(SettingsFragment settingsFragment) {
                this.f36a = settingsFragment;
            }

            public boolean onPreferenceClick(Preference preference) {
                this.f36a.startActivityForResult(new Intent(preference.getIntent().getAction()), 3);
                return true;
            }
        }

        public SettingsFragment() {
            this.f37a = new C00114(this);
        }

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            findPreference(getString(R.string.pref_launch_configure)).setOnPreferenceClickListener(this.f37a);
            findPreference(getString(R.string.pref_launch_autoconfigure)).setOnPreferenceClickListener(this.f37a);
            if (Build.MANUFACTURER.equalsIgnoreCase(Device.MANUFACTURER_ZTE) && Build.MODEL.equalsIgnoreCase(Device.MODEL_ZTE_SPEED)) {
                findPreference(getString(R.string.pref_launch_settings)).setOnPreferenceClickListener(new C00081(this));
            } else {
                findPreference(getString(R.string.pref_launch_settings)).setOnPreferenceClickListener(new C00092(this));
            }
            if (Build.MODEL.equals(Device.MODEL_FOXDA_FL7007)) {
                findPreference(getString(R.string.pref_launch_settings)).setOnPreferenceClickListener(new C00103(this));
            }
        }

        public void onActivityResult(int request, int result, Intent intent) {
            if (request == 3 && result == -1) {
                getActivity().setResult(-1, intent);
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(16908290, new SettingsFragment()).commit();
    }
}
