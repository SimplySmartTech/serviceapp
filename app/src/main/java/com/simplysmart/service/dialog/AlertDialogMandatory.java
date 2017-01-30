package com.simplysmart.service.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.simplysmart.service.R;
import com.simplysmart.service.interfaces.MandatoryReading;


public class AlertDialogMandatory extends DialogFragment implements View.OnClickListener {

    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_NEGATIVE_BUTTON = "negativeButton";
    private static final String KEY_POSITIVE_BUTTON = "positiveButton";

    public static AlertDialogMandatory newInstance(String title, String message, String negativeButton,
                                                  String positiveButton) {
        AlertDialogMandatory f = new AlertDialogMandatory();

        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_MESSAGE, message);
        args.putString(KEY_NEGATIVE_BUTTON, negativeButton);
        args.putString(KEY_POSITIVE_BUTTON, positiveButton);
        f.setArguments(args);

        return f;
    }

    public AlertDialogMandatory() {
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

//		Typeface textTypeface = Typeface.createFromAsset(getActivity().getAssets(), ApplicationConstant.FONT_EUROSTILE_REGULAR_MID);
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

//		dialogTitle.setTypeface(textTypeface);
//		dialogMessage.setTypeface(textTypeface);
//		dialogNegativeButton.setTypeface(textTypeface);
//		dialogPositiveButton.setTypeface(textTypeface);

        dialogNegativeButton.setVisibility(View.GONE);
        dialogPositiveButton.setOnClickListener(this);

        builder.setView(dialogView);
        return builder.create();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.dialogButtonPositive) {
            MandatoryReading mandatoryListener = (MandatoryReading) getActivity();
            mandatoryListener.continueAhead();
            dismiss();
        }
    }



}