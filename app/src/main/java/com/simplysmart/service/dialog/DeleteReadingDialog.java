package com.simplysmart.service.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.simplysmart.service.R;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.database.ReadingDataRealm;
import com.simplysmart.service.interfaces.EditDialogListener;

import io.realm.Realm;


public class DeleteReadingDialog extends DialogFragment implements View.OnClickListener {

    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_NEGATIVE_BUTTON = "negativeButton";
    private static final String KEY_POSITIVE_BUTTON = "positiveButton";
    private int position ;
    private ReadingDataRealm readingDataRealm;

    public static DeleteReadingDialog newInstance(String title, String message, String negativeButton,
                                                  String positiveButton, int position, ReadingDataRealm readingDataRealm) {
        DeleteReadingDialog f = new DeleteReadingDialog();

        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_MESSAGE, message);
        args.putString(KEY_NEGATIVE_BUTTON, negativeButton);
        args.putString(KEY_POSITIVE_BUTTON, positiveButton);
        args.putInt(StringConstants.EDIT,position);
        args.putParcelable(StringConstants.READING_DATA,readingDataRealm);
        f.setArguments(args);

        return f;
    }

    public DeleteReadingDialog() {
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
        dialogNegativeButton.setText(getArguments().getString(KEY_NEGATIVE_BUTTON));
        dialogPositiveButton.setText(getArguments().getString(KEY_POSITIVE_BUTTON));
        this.position = getArguments().getInt(StringConstants.EDIT);
        this.readingDataRealm = getArguments().getParcelable(StringConstants.READING_DATA);

        dialogNegativeButton.setOnClickListener(this);
        dialogPositiveButton.setOnClickListener(this);

        builder.setView(dialogView);
        return builder.create();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.dialogButtonNegative) {
            EditDialogListener editDialogListener = (EditDialogListener)getActivity();
            editDialogListener.updateResult(StringConstants.NO_NEW_VALUE,position,"");
            dismiss();
        }
        if (v.getId() == R.id.dialogButtonPositive) {

            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            readingDataRealm.deleteFromRealm();
            realm.commitTransaction();

            EditDialogListener editDialogListener = (EditDialogListener)getActivity();
            editDialogListener.updateResult(StringConstants.VALUE_DELETED,position,"");
            dismiss();
        }
    }

}