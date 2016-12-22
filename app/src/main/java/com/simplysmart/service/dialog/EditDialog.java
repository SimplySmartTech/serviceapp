package com.simplysmart.service.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.simplysmart.service.R;
import com.simplysmart.service.common.CommonMethod;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.database.ReadingTable;
import com.simplysmart.service.interfaces.EditDialogListener;

import java.util.Calendar;

public class EditDialog extends DialogFragment {

    private static String utility_id = "UTILITY_ID";
    private static String sensor_name = "SENSOR_NAME";
    private static String timestamp = "TIMESTAMP";
    private static String position = "POSITION";
    private boolean edit = false;

    private ReadingTable readingTable;

    public static EditDialog newInstance(ReadingTable readingTable, int pos, boolean edit) {
        EditDialog f = new EditDialog();

        Bundle args = new Bundle();
        args.putString(utility_id, readingTable.utility_id);
        args.putString(sensor_name, readingTable.sensor_name);
        args.putLong(timestamp, readingTable.timestamp);
        args.putInt(position, pos);
        args.putBoolean(StringConstants.EDIT, edit);
        f.setArguments(args);

        return f;
    }

    public EditDialog() {

    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_edit_reading, null);

        TextView unit = (TextView) dialogView.findViewById(R.id.unit);
        final EditText newReading = (EditText) dialogView.findViewById(R.id.reading);
        Button dialogNegativeButton = (Button) dialogView.findViewById(R.id.dialogButtonNegative);
        final Button dialogPositiveButton = (Button) dialogView.findViewById(R.id.dialogButtonPositive);
        ImageView close = (ImageView) dialogView.findViewById(R.id.close);
        final Button backButton = (Button) dialogView.findViewById(R.id.backButton);
        final EditText remarks = (EditText) dialogView.findViewById(R.id.remarks);
        final TextView remark_mandatory_text = (TextView) dialogView.findViewById(R.id.enter_remark);

        final RelativeLayout readingLayout = (RelativeLayout) dialogView.findViewById(R.id.readingLayout);
        final RelativeLayout remarksLayout = (RelativeLayout) dialogView.findViewById(R.id.remarksLayout);

        Bundle bundle = getArguments();
        String utilityId = bundle.getString(utility_id);
        String unitId = GlobalData.getInstance().getSelectedUnitId();
        String sensorName = bundle.getString(sensor_name);
        long time = bundle.getLong(timestamp);
        final int pos = bundle.getInt(position);
        this.edit = bundle.getBoolean(StringConstants.EDIT);

        readingTable = ReadingTable.getReading(time);

        remarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    remark_mandatory_text.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dialogNegativeButton.setText("DELETE");
        dialogNegativeButton.setTextColor(Color.RED);
        unit.setText(readingTable.unit);
        newReading.setText(readingTable.value);
        newReading.setCursorVisible(false);

        dialogPositiveButton.setText("NEXT");

        newReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newReading.setSelection(newReading.getText().length());
                newReading.setCursorVisible(true);
            }
        });

        newReading.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            newReading.setCursorVisible(false);
                            CommonMethod.hideKeyboard(getActivity());
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        if (!edit) {
            dialogNegativeButton.setVisibility(View.INVISIBLE);
        }

        dialogPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogPositiveButton.getText().toString().equalsIgnoreCase("NEXT")) {
                    addRemarks();
                } else if (!newReading.getText().equals("") && remarks.getText() != null && !remarks.getText().toString().equalsIgnoreCase("")) {
                    String string = newReading.getText().toString();
                    try {
                        readingTable.value = string;
                        readingTable.remark = remarks.getText().toString();
                        readingTable.updated_at = Calendar.getInstance().getTimeInMillis();
                        readingTable.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    EditDialogListener editDialogListener = (EditDialogListener) getActivity();
                    editDialogListener.updateResult(StringConstants.NEW_VALUE, pos, string + " " + readingTable.unit);
                    dismiss();
                }else {
                    remark_mandatory_text.setVisibility(View.VISIBLE);
                }
            }

            private void addRemarks() {
                readingLayout.setVisibility(View.GONE);
                backButton.setVisibility(View.VISIBLE);
                remarksLayout.setVisibility(View.VISIBLE);
                dialogPositiveButton.setText("UPDATE");
            }
        });

        dialogNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    readingTable.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                EditDialogListener editDialogListener = (EditDialogListener) getActivity();
                editDialogListener.updateResult(StringConstants.VALUE_DELETED, pos, "");
                dismiss();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remarksLayout.setVisibility(View.GONE);
                readingLayout.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.GONE);
                dialogPositiveButton.setText("NEXT");
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditDialogListener editDialogListener = (EditDialogListener) getActivity();
                editDialogListener.updateResult(StringConstants.NO_NEW_VALUE,pos,"");
                dismiss();
            }
        });

        builder.setView(dialogView);

        // Makes dialog non cancelable but resolves problem of edit swipe getting frozen.
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                EditDialogListener editDialogListener = (EditDialogListener) getActivity();
                editDialogListener.updateResult(StringConstants.NO_NEW_VALUE,pos,"");
                dismiss();
                return true;
            }
        });

        return builder.create();
    }
}