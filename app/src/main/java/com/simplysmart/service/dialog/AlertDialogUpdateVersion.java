package com.simplysmart.service.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.simplysmart.service.R;


public class AlertDialogUpdateVersion extends DialogFragment implements View.OnClickListener {

    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String NEGATIVE_BUTTON = "negativeButton";
    private static final String POSITIVE_BUTTON = "positiveButton";

    public static AlertDialogUpdateVersion newInstance(String title, String message, String negativeButton, String positiveButton) {
        AlertDialogUpdateVersion f = new AlertDialogUpdateVersion();

        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_MESSAGE, message);
        args.putString(NEGATIVE_BUTTON, negativeButton);
        args.putString(POSITIVE_BUTTON, positiveButton);
        f.setArguments(args);

        return f;
    }

    public AlertDialogUpdateVersion() {
    }

    @SuppressLint("InflateParams")
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
        dialogNegativeButton.setText(getArguments().getString(NEGATIVE_BUTTON));
        dialogPositiveButton.setText(getArguments().getString(POSITIVE_BUTTON));

        dialogPositiveButton.setOnClickListener(this);
        dialogNegativeButton.setVisibility(View.GONE);
        dialogMessage.setLineSpacing(2.0f,1.25f);

        builder.setView(dialogView);

        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
                return true;
            }
        });

        return builder.create();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.dialogButtonNegative) {
            dismiss();
        } else if (v.getId() == R.id.dialogButtonPositive) {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("market://details?id=" + getActivity().getPackageName()));
                startActivity(i);
                dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}