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
    OnClickListener f264a;
    private ArrayList<String> f265b;
    private Context f266c;
    private Utility f267d;

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcLoadFileActivity.1 */
    class C00701 implements View.OnClickListener {
        final /* synthetic */ FtcLoadFileActivity f261a;

        C00701(FtcLoadFileActivity ftcLoadFileActivity) {
            this.f261a = ftcLoadFileActivity;
        }

        public void onClick(View view) {
            Builder buildBuilder = this.f261a.f267d.buildBuilder("Available files", "These are the files the Hardware Wizard was able to find. You can edit each file by clicking the edit button. The 'Activate' button will set that file as the current configuration file, which will be used to start the robot.");
            buildBuilder.setPositiveButton("Ok", this.f261a.f264a);
            AlertDialog create = buildBuilder.create();
            create.show();
            ((TextView) create.findViewById(16908299)).setTextSize(14.0f);
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcLoadFileActivity.2 */
    class C00712 implements View.OnClickListener {
        final /* synthetic */ FtcLoadFileActivity f262a;

        C00712(FtcLoadFileActivity ftcLoadFileActivity) {
            this.f262a = ftcLoadFileActivity;
        }

        public void onClick(View view) {
            Builder buildBuilder = this.f262a.f267d.buildBuilder("AutoConfigure", "This is the fastest way to get a new machine up and running. The AutoConfigure tool will automatically enter some default names for devices. AutoConfigure expects certain devices.  If there are other devices attached, the AutoConfigure tool will not name them.");
            buildBuilder.setPositiveButton("Ok", this.f262a.f264a);
            AlertDialog create = buildBuilder.create();
            create.show();
            ((TextView) create.findViewById(16908299)).setTextSize(14.0f);
        }
    }

    /* renamed from: com.qualcomm.ftccommon.configuration.FtcLoadFileActivity.3 */
    class C00723 implements OnClickListener {
        final /* synthetic */ FtcLoadFileActivity f263a;

        C00723(FtcLoadFileActivity ftcLoadFileActivity) {
            this.f263a = ftcLoadFileActivity;
        }

        public void onClick(DialogInterface dialog, int button) {
        }
    }

    public FtcLoadFileActivity() {
        this.f265b = new ArrayList();
        this.f264a = new C00723(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        this.f266c = this;
        this.f267d = new Utility(this);
        this.f267d.createConfigFolder();
        m192a();
    }

    protected void onStart() {
        super.onStart();
        this.f265b = this.f267d.getXMLFiles();
        m193b();
        this.f267d.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        m194c();
    }

    private void m192a() {
        ((Button) findViewById(R.id.files_holder).findViewById(R.id.info_btn)).setOnClickListener(new C00701(this));
        ((Button) findViewById(R.id.autoconfigure_holder).findViewById(R.id.info_btn)).setOnClickListener(new C00712(this));
    }

    private void m193b() {
        if (this.f265b.size() == 0) {
            this.f267d.setOrangeText("No files found!", "In order to proceed, you must create a new file", R.id.empty_filelist, R.layout.orange_warning, R.id.orangetext0, R.id.orangetext1);
            return;
        }
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.empty_filelist);
        viewGroup.removeAllViews();
        viewGroup.setVisibility(8);
    }

    private void m194c() {
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.inclusionlayout);
        viewGroup.removeAllViews();
        Iterator it = this.f265b.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            View inflate = LayoutInflater.from(this).inflate(R.layout.file_info, null);
            viewGroup.addView(inflate);
            ((TextView) inflate.findViewById(R.id.filename_editText)).setText(str);
        }
    }

    public void new_button(View v) {
        this.f267d.saveToPreferences("No current file!", R.string.pref_hardware_config_filename);
        startActivity(new Intent(this.f266c, FtcConfigurationActivity.class));
    }

    public void file_edit_button(View v) {
        this.f267d.saveToPreferences(m191a(v, true), R.string.pref_hardware_config_filename);
        startActivity(new Intent(this.f266c, FtcConfigurationActivity.class));
    }

    public void file_activate_button(View v) {
        this.f267d.saveToPreferences(m191a(v, false), R.string.pref_hardware_config_filename);
        this.f267d.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
    }

    public void file_delete_button(View v) {
        String a = m191a(v, true);
        File file = new File(Utility.CONFIG_FILES_DIR + a);
        if (file.exists()) {
            file.delete();
        } else {
            this.f267d.complainToast("That file does not exist: " + a, this.f266c);
            DbgLog.error("Tried to delete a file that does not exist: " + a);
        }
        this.f265b = this.f267d.getXMLFiles();
        this.f267d.saveToPreferences("No current file!", R.string.pref_hardware_config_filename);
        this.f267d.updateHeader("No current file!", R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
        m194c();
    }

    private String m191a(View view, boolean z) {
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
        String filenameFromPrefs = this.f267d.getFilenameFromPrefs(R.string.pref_hardware_config_filename, "No current file!");
        Intent intent = new Intent();
        intent.putExtra(CONFIGURE_FILENAME, filenameFromPrefs);
        setResult(-1, intent);
        finish();
    }
}
