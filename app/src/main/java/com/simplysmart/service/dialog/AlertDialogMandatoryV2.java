package com.simplysmart.service.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.simplysmart.service.R;

public class AlertDialogMandatoryV2 extends DialogFragment {

    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_NEGATIVE_BUTTON = "negativeButton";
    private static final String KEY_POSITIVE_BUTTON = "positiveButton";

    public static AlertDialogMandatoryV2 newInstance(String title, String message, String negativeButton,
                                                     String positiveButton) {
        AlertDialogMandatoryV2 f = new AlertDialogMandatoryV2();

        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_MESSAGE, message);
        args.putString(KEY_NEGATIVE_BUTTON, negativeButton);
        args.putString(KEY_POSITIVE_BUTTON, positiveButton);
        f.setArguments(args);

        return f;
    }

    public AlertDialogMandatoryV2() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_alert_dialog_standard, null);

        TextView dialogTitle = (TextView) dialogView.findViewById(R.id.dialogTitle);
        TextView dialogMessage = (TextView) dialogView.findViewById(R.id.dialogMessage);
        Button dialogNegativeButton = (Button) dialogView.findViewById(R.id.dialogButtonNegative);
        Button dialogPositiveButton = (Button) dialogView.findViewById(R.id.dialogButtonPositive);

        dialogTitle.setText(getArguments().getString(KEY_TITLE));
        dialogMessage.setText(getArguments().getString(KEY_MESSAGE));
        dialogNegativeButton.setText(getArguments().getString(KEY_NEGATIVE_BUTTON));
        dialogPositiveButton.setText(getArguments().getString(KEY_POSITIVE_BUTTON));

        dialogNegativeButton.setVisibility(View.GONE);
        dialogPositiveButton.setVisibility(View.GONE);

        builder.setView(dialogView);

        builder.setPositiveButton(R.string.ok_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                    }
                }
        )
                .setNegativeButton("", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
                    }
                })
                .create();

        return builder.create();
    }
}