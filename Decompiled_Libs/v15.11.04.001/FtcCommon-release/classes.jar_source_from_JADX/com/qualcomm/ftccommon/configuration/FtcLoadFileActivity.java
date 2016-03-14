package com.qualcomm.ftccommon.configuration;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.ftccommon.R;
import com.qualcomm.robotcore.hardware.configuration.Utility;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class FtcLoadFileActivity extends Activity {
    public static final String CONFIGURE_FILENAME = "CONFIGURE_FILENAME";
    OnClickListener f248a;
    private ArrayList<String> f249b;
    private Context f250c;
    private Utility f251d;

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcLoadFileActivity.1 */
    class C00621 implements View.OnClickListener {
        final /* synthetic */ FtcLoadFileActivity f245a;

        C00621(FtcLoadFileActivity ftcLoadFileActivity) {
            this.f245a = ftcLoadFileActivity;
        }

        public void onClick(View view) {
            Builder buildBuilder = this.f245a.f251d.buildBuilder("Available files", "These are the files the Hardware Wizard was able to find. You can edit each file by clicking the edit button. The 'Activate' button will set that file as the current configuration file, which will be used to start the robot.");
            buildBuilder.setPositiveButton("Ok", this.f245a.f248a);
            AlertDialog create = buildBuilder.create();
            create.show();
            ((TextView) create.findViewById(16908299)).setTextSize(14.0f);
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcLoadFileActivity.2 */
    class C00632 implements View.OnClickListener {
        final /* synthetic */ FtcLoadFileActivity f246a;

        C00632(FtcLoadFileActivity ftcLoadFileActivity) {
            this.f246a = ftcLoadFileActivity;
        }

        public void onClick(View view) {
            Builder buildBuilder = this.f246a.f251d.buildBuilder("AutoConfigure", "This is the fastest way to get a new machine up and running. The AutoConfigure tool will automatically enter some default names for devices. AutoConfigure expects certain devices.  If there are other devices attached, the AutoConfigure tool will not name them.");
            buildBuilder.setPositiveButton("Ok", this.f246a.f248a);
            AlertDialog create = buildBuilder.create();
            create.show();
            ((TextView) create.findViewById(16908299)).setTextSize(14.0f);
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcLoadFileActivity.3 */
    class C00643 implements OnClickListener {
        final /* synthetic */ FtcLoadFileActivity f247a;

        C00643(FtcLoadFileActivity ftcLoadFileActivity) {
            this.f247a = ftcLoadFileActivity;
        }

        public void onClick(DialogInterface dialog, int button) {
        }
    }

    public FtcLoadFileActivity() {
        this.f249b = new ArrayList();
        this.f248a = new C00643(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        this.f250c = this;
        this.f251d = new Utility(this);
        this.f251d.createConfigFolder();
        m181a();
    }

    protected void onStart() {
        super.onStart();
        this.f249b = this.f251d.getXMLFiles();
        m182b();
        this.f251d.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        m183c();
    }

    private void m181a() {
        ((Button) findViewById(R.id.files_holder).findViewById(R.id.info_btn)).setOnClickListener(new C00621(this));
        ((Button) findViewById(R.id.autoconfigure_holder).findViewById(R.id.info_btn)).setOnClickListener(new C00632(this));
    }

    private void m182b() {
        if (this.f249b.size() == 0) {
            this.f251d.setOrangeText("No files found!", "In order to proceed, you must create a new file", R.id.empty_filelist, R.layout.orange_warning, R.id.orangetext0, R.id.orangetext1);
            return;
        }
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.empty_filelist);
        viewGroup.removeAllViews();
        viewGroup.setVisibility(8);
    }

    private void m183c() {
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.inclusionlayout);
        viewGroup.removeAllViews();
        Iterator it = this.f249b.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            View inflate = LayoutInflater.from(this).inflate(R.layout.file_info, null);
            viewGroup.addView(inflate);
            ((TextView) inflate.findViewById(R.id.filename_editText)).setText(str);
        }
    }

    public void new_button(View v) {
        this.f251d.saveToPreferences("No current file!", R.string.pref_hardware_config_filename);
        startActivity(new Intent(this.f250c, FtcConfigurationActivity.class));
    }

    public void file_edit_button(View v) {
        this.f251d.saveToPreferences(m180a(v, true), R.string.pref_hardware_config_filename);
        startActivity(new Intent(this.f250c, FtcConfigurationActivity.class));
    }

    public void file_activate_button(View v) {
        this.f251d.saveToPreferences(m180a(v, false), R.string.pref_hardware_config_filename);
        this.f251d.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
    }

    public void file_delete_button(View v) {
        String a = m180a(v, true);
        File file = new File(Utility.CONFIG_FILES_DIR + a);
        if (file.exists()) {
            file.delete();
        } else {
            this.f251d.complainToast("That file does not exist: " + a, this.f250c);
            DbgLog.error("Tried to delete a file that does not exist: " + a);
        }
        this.f249b = this.f251d.getXMLFiles();
        this.f251d.saveToPreferences("No current file!", R.string.pref_hardware_config_filename);
        this.f251d.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        m183c();
    }

    private String m180a(View view, boolean z) {
        String charSequence = ((TextView) ((LinearLayout) ((LinearLayout) view.getParent()).getParent()).findViewById(R.id.filename_editText)).getText().toString();
        if (z) {
            return charSequence + ".xml";
        }
        return charSequence;
    }

    public void launch_autoConfigure(View v) {
        startActivity(new Intent(getBaseContext(), AutoConfigureActivity.class));
    }

    public void onBackPressed() {
        String filenameFromPrefs = this.f251d.getFilenameFromPrefs(R.string.pref_hardware_config_filename, "No current file!");
        Intent intent = new Intent();
        intent.putExtra(CONFIGURE_FILENAME, filenameFromPrefs);
        setResult(-1, intent);
        finish();
    }
}
