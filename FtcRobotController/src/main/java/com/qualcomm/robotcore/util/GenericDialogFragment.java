package com.qualcomm.robotcore.util;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class GenericDialogFragment extends DialogFragment {
    public static final String ARGUMENT_DIALOG_MESSAGE = "dialogMsg";
    private static final String ERROR_MESSAGE = "App error condition!";
    private static final String POSITIVE_BUTTON_TEXT = "OK";

    class GenericOnClickListener implements OnClickListener {
        final GenericDialogFragment genericDialogFragment;

        GenericOnClickListener(GenericDialogFragment genericDialogFragment) {
            this.genericDialogFragment = genericDialogFragment;
        }

        public void onClick(DialogInterface dialog, int id) {
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CharSequence string = getArguments().getString(ARGUMENT_DIALOG_MESSAGE, ERROR_MESSAGE);
        Builder builder = new Builder(getActivity());
        builder.setMessage(string).setPositiveButton(POSITIVE_BUTTON_TEXT, new GenericOnClickListener(this));
        return builder.create();
    }
}
