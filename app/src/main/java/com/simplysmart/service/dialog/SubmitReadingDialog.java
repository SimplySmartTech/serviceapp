package com.simplysmart.service.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.simplysmart.service.R;
import com.simplysmart.service.activity.SummaryActivity;


public class SubmitReadingDialog extends DialogFragment implements View.OnClickListener {

    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_NEGATIVE_BUTTON = "negativeButton";
    private static final String KEY_POSITIVE_BUTTON = "positiveButton";

    public static SubmitReadingDialog newInstance(String title, String message, String negativeButton,
                                                  String positiveButton) {
        SubmitReadingDialog f = new SubmitReadingDialog();

        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_MESSAGE, message);
        args.putString(KEY_NEGATIVE_BUTTON, negativeButton);
        args.putString(KEY_POSITIVE_BUTTON, positiveButton);
        f.setArguments(args);

        return f;
    }

    public SubmitReadingDialog() {
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
        if(getArguments().getString(KEY_NEGATIVE_BUTTON).equalsIgnoreCase("")){
            dialogNegativeButton.setVisibility(View.GONE);
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    getActivity().finish();
                    return true;
                }
            });
        }else {
            dialogNegativeButton.setText(getArguments().getString(KEY_NEGATIVE_BUTTON));
        }
        dialogPositiveButton.setText(getArguments().getString(KEY_POSITIVE_BUTTON));

//		dialogTitle.setTypeface(textTypeface);
//		dialogMessage.setTypeface(textTypeface);
//		dialogNegativeButton.setTypeface(textTypeface);
//		dialogPositiveButton.setTypeface(textTypeface);

        dialogNegativeButton.setOnClickListener(this);
        dialogPositiveButton.setOnClickListener(this);

        builder.setView(dialogView);
        return builder.create();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.dialogButtonNegative) {
            dismiss();
        }

        if (v.getId() == R.id.dialogButtonPositive) {
            Intent i = new Intent(getActivity(), SummaryActivity.class);
            getActivity().startActivity(i);
            dismiss();
        }
    }

}