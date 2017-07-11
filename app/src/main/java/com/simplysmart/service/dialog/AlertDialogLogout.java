package com.simplysmart.service.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.simplysmart.service.R;
import com.simplysmart.service.callback.ApiCallback;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.interfaces.LogoutListener;
import com.simplysmart.service.model.common.CommonResponse;
import com.simplysmart.service.request.CreateRequest;


public class AlertDialogLogout extends DialogFragment implements View.OnClickListener {

    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_NEGATIVE_BUTTON = "negativeButton";
    private static final String KEY_POSITIVE_BUTTON = "positiveButton";

    public static AlertDialogLogout newInstance(String title, String message, String negativeButton,
                                                String positiveButton) {
        AlertDialogLogout f = new AlertDialogLogout();

        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_MESSAGE, message);
        args.putString(KEY_NEGATIVE_BUTTON, negativeButton);
        args.putString(KEY_POSITIVE_BUTTON, positiveButton);
        f.setArguments(args);

        return f;
    }

    public AlertDialogLogout() {
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
            clearUserSessionData();
            postLogoutRequest();
            GlobalData.mGlobalData = null;
        }
    }

    private void postLogoutRequest() {

        try {
            CreateRequest.getInstance().logoutRequestWithSubDomain(new ApiCallback<CommonResponse>() {
                @Override
                public void onSuccess(CommonResponse response) {}

                @Override
                public void onFailure(String error) {}
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearUserSessionData() {

        try {
            SharedPreferences userInfo = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            userInfo.edit().clear().apply();

            SharedPreferences gcmToken = PreferenceManager.getDefaultSharedPreferences(getActivity());
            gcmToken.edit().clear().apply();

            LogoutListener logoutListener = (LogoutListener) getActivity();
            logoutListener.logoutUser();
            dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

