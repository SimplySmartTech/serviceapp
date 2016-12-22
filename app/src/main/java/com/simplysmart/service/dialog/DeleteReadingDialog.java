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
import com.simplysmart.service.database.ReadingTable;
import com.simplysmart.service.interfaces.EditDialogListener;

public class DeleteReadingDialog extends DialogFragment implements View.OnClickListener {

    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_NEGATIVE_BUTTON = "negativeButton";
    private static final String KEY_POSITIVE_BUTTON = "positiveButton";
    private int position;
    private String utility_id;
    private String sensor_name;
    private long timestamp;

    public static DeleteReadingDialog newInstance(String title, String message, String negativeButton,
                                                  String positiveButton, int position, ReadingTable readingTable) {
        DeleteReadingDialog f = new DeleteReadingDialog();

        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_MESSAGE, message);
        args.putString(KEY_NEGATIVE_BUTTON, negativeButton);
        args.putString(KEY_POSITIVE_BUTTON, positiveButton);
        args.putInt(StringConstants.EDIT, position);
        args.putString(StringConstants.UTILITY_ID,readingTable.utility_id);
        args.putString(StringConstants.SENSOR_NAME,readingTable.sensor_name);
        args.putLong(StringConstants.TIMESTAMP,readingTable.timestamp);
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
        this.utility_id = getArguments().getString(StringConstants.UTILITY_ID);
        this.sensor_name = getArguments().getString(StringConstants.SENSOR_NAME);
        this.timestamp = getArguments().getLong(StringConstants.TIMESTAMP);

        dialogNegativeButton.setOnClickListener(this);
        dialogPositiveButton.setOnClickListener(this);

        builder.setView(dialogView);
        return builder.create();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.dialogButtonNegative) {
            EditDialogListener editDialogListener = (EditDialogListener) getActivity();
            editDialogListener.updateResult(StringConstants.NO_NEW_VALUE, position, "");
            dismiss();
        }

        if (v.getId() == R.id.dialogButtonPositive) {

            ReadingTable readingTable = ReadingTable.getReading(utility_id,sensor_name,timestamp);
            readingTable.delete();

            EditDialogListener editDialogListener = (EditDialogListener) getActivity();
            editDialogListener.updateResult(StringConstants.VALUE_DELETED, position, "");
            dismiss();
        }
    }

}