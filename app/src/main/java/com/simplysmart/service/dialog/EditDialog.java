package com.simplysmart.service.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.simplysmart.service.R;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.database.ReadingDataRealm;
import com.simplysmart.service.model.matrix.ReadingData;

import io.realm.Realm;
import io.realm.RealmResults;


public class EditDialog extends DialogFragment {

    private static String utility_id = "UTILITY_ID";
    private static String sensor_name = "SENSOR_NAME";
    private static String timestamp = "TIMESTAMP";
    private static String position = "POSITION";

    public static EditDialog newInstance(ReadingDataRealm readingDataRealm, int pos) {
        EditDialog f = new EditDialog();

        Bundle args = new Bundle();
        args.putString(utility_id, readingDataRealm.getUtility_id());
        args.putString(sensor_name, readingDataRealm.getSensor_name());
        args.putLong(timestamp, readingDataRealm.getTimestamp());
        args.putInt(position,pos);
        f.setArguments(args);

        return f;
    }

    public EditDialog(){

    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

//		Typeface textTypeface = Typeface.createFromAsset(getActivity().getAssets(), ApplicationConstant.FONT_EUROSTILE_REGULAR_MID);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_edit_reading, null);

        TextView unit = (TextView) dialogView.findViewById(R.id.unit);
        final EditText newReading = (EditText) dialogView.findViewById(R.id.reading);
        Button dialogNegativeButton = (Button) dialogView.findViewById(R.id.dialogButtonNegative);
        Button dialogPositiveButton = (Button) dialogView.findViewById(R.id.dialogButtonPositive);

        Bundle bundle = getArguments();
        String utilityId = bundle.getString(utility_id);
        String unitId = GlobalData.getInstance().getSelectedUnitId();
        String sensorName = bundle.getString(sensor_name);
        long time = bundle.getLong(timestamp);
        final int pos = bundle.getInt(position);


        final Realm realm = Realm.getDefaultInstance();
        final ReadingDataRealm readingDataRealm = realm
                .where(ReadingDataRealm.class)
                .equalTo("utility_id",utilityId)
                .equalTo("sensor_name",sensorName)
                .equalTo("timestamp",time)
                .findFirst();

        unit.setText(readingDataRealm.getUnit());
        newReading.setText(readingDataRealm.getValue());

        dialogPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!newReading.getText().equals("")){
                    String string = newReading.getText().toString();
                    try {
                        realm.beginTransaction();
                        readingDataRealm.setValue(string);
                        realm.commitTransaction();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    EditDialogListener editDialogListener = (EditDialogListener)getActivity();
                    editDialogListener.updateResult(true,pos,string+" "+readingDataRealm.getUnit());
                    dismiss();
                }else{
                    EditDialogListener editDialogListener = (EditDialogListener)getActivity();
                    editDialogListener.updateResult(false,pos,"");
                    dismiss();
                }
            }
        });

        dialogNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        builder.setView(dialogView);
        return builder.create();
    }

    public interface EditDialogListener {
        void updateResult(boolean done,int position,String value);
    }

}