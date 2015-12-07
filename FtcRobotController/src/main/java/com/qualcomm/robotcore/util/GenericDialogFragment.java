package com.qualcomm.robotcore.util;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class GenericDialogFragment extends DialogFragment {
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String defaultDialogMsg = "App error condition!";
        CharSequence string = getArguments().getString("dialogMsg", defaultDialogMsg);
        Builder builder = new Builder(getActivity());
        builder.setMessage(string).setPositiveButton("OK",
                new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) { }
        });
        return builder.create();
    }
}
