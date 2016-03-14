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
        OnPreferenceClickListener f50a;

        /* renamed from: com.qualcomm.ftccommon.FtcRobotControllerSettingsActivity.SettingsFragment.1 */
        class C00131 implements OnPreferenceClickListener {
            final /* synthetic */ SettingsFragment f46a;

            C00131(SettingsFragment settingsFragment) {
                this.f46a = settingsFragment;
            }

            public boolean onPreferenceClick(Preference preference) {
                try {
                    this.f46a.startActivity(this.f46a.getActivity().getPackageManager().getLaunchIntentForPackage(LaunchActivityConstantsList.ZTE_WIFI_CHANNEL_EDITOR_PACKAGE));
                } catch (NullPointerException e) {
                    Toast.makeText(this.f46a.getActivity(), "Unable to launch ZTE WifiChannelEditor", 0).show();
                }
                return true;
            }
        }

        /* renamed from: com.qualcomm.ftccommon.FtcRobotControllerSettingsActivity.SettingsFragment.2 */
        class C00142 implements OnPreferenceClickListener {
            final /* synthetic */ SettingsFragment f47a;

            C00142(SettingsFragment settingsFragment) {
                this.f47a = settingsFragment;
            }

            public boolean onPreferenceClick(Preference preference) {
                this.f47a.startActivity(new Intent(preference.getIntent().getAction()));
                return true;
            }
        }

        /* renamed from: com.qualcomm.ftccommon.FtcRobotControllerSettingsActivity.SettingsFragment.3 */
        class C00153 implements OnPreferenceClickListener {
            final /* synthetic */ SettingsFragment f48a;

            C00153(SettingsFragment settingsFragment) {
                this.f48a = settingsFragment;
            }

            public boolean onPreferenceClick(Preference preference) {
                this.f48a.startActivity(new Intent("android.settings.SETTINGS"));
                return true;
            }
        }

        /* renamed from: com.qualcomm.ftccommon.FtcRobotControllerSettingsActivity.SettingsFragment.4 */
        class C00164 implements OnPreferenceClickListener {
            final /* synthetic */ SettingsFragment f49a;

            C00164(SettingsFragment settingsFragment) {
                this.f49a = settingsFragment;
            }

            public boolean onPreferenceClick(Preference preference) {
                this.f49a.startActivityForResult(new Intent(preference.getIntent().getAction()), 3);
                return true;
            }
        }

        public SettingsFragment() {
            this.f50a = new C00164(this);
        }

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            findPreference(getString(R.string.pref_launch_configure)).setOnPreferenceClickListener(this.f50a);
            findPreference(getString(R.string.pref_launch_autoconfigure)).setOnPreferenceClickListener(this.f50a);
            if (Build.MANUFACTURER.equalsIgnoreCase(Device.MANUFACTURER_ZTE) && Build.MODEL.equalsIgnoreCase(Device.MODEL_ZTE_SPEED)) {
                findPreference(getString(R.string.pref_launch_settings)).setOnPreferenceClickListener(new C00131(this));
            } else {
                findPreference(getString(R.string.pref_launch_settings)).setOnPreferenceClickListener(new C00142(this));
            }
            if (Build.MODEL.equals(Device.MODEL_FOXDA_FL7007)) {
                findPreference(getString(R.string.pref_launch_settings)).setOnPreferenceClickListener(new C00153(this));
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
