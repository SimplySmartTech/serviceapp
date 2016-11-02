package com.simplysmart.service.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.View;

import com.google.gson.Gson;
import com.simplysmart.service.R;
import com.simplysmart.service.common.CommonMethod;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.custom.CustomProgressDialog;
import com.simplysmart.service.dialog.AlertDialogStandard;
import com.simplysmart.service.model.user.AccessPolicy;
import com.simplysmart.service.model.user.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shailendrapsp on 2/11/16.
 */
public class BaseFragment extends Fragment{
    private Dialog spinningDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spinningDialog = CustomProgressDialog.showProgressDialog(getContext());
        spinningDialog.setCancelable(false);
    }

    protected void onBackPressed(){

    }

    protected void displayMessage(String errorString) {
        showMyDialog(getContext().getString(R.string.app_name), errorString, getContext().getString(R.string.ok_button));
    }

    //show common alert dialog
    protected void showMyDialog(String title, String message, String positiveButton) {
        AlertDialogStandard newDialog = AlertDialogStandard.newInstance(title, message, "", positiveButton);
        newDialog.show(getActivity().getFragmentManager(), "show dialog");
    }

    protected void showSnackBar(View view, String message) {
        Snackbar.make(view, Html.fromHtml("<font color=\"#ffffff\">" + message + "</font>"), Snackbar.LENGTH_LONG).show();
        CommonMethod.hideKeyboard(getActivity());
    }

    protected void showSnackBar(View view, String message, boolean isSuccess) {

        Snackbar snack = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        View snackBarView = snack.getView();
        if (isSuccess) {
            snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.gal_color_pale_green));
        } else {
            snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.gal_color_amber));
        }
        snack.show();

        CommonMethod.hideKeyboard(getActivity());
    }

    protected void showActivitySpinner() {
        if (spinningDialog != null) spinningDialog.show();
    }

    protected void showActivitySpinner(String message) {
        CustomProgressDialog.setMessage(message);
        if (spinningDialog != null) spinningDialog.show();
    }


    protected void dismissActivitySpinner() {
        if (spinningDialog != null) spinningDialog.dismiss();
    }

    //Method used to parse error response from server
    protected String trimMessage(String json, String key) {
        String trimmedString;

        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return trimmedString;
    }

    protected void getUserInfo() {

        SharedPreferences UserInfo = getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        GlobalData.getInstance().setAuthToken(UserInfo.getString("auth_token", ""));
        GlobalData.getInstance().setApi_key(UserInfo.getString("api_key", ""));
        GlobalData.getInstance().setSubDomain(UserInfo.getString("subdomain", ""));
        GlobalData.getInstance().setRole_code(UserInfo.getString("role_code", ""));

        Gson gson = new Gson();
        String jsonUnitInfo = UserInfo.getString("unit_info", "");
        User residentData = gson.fromJson(jsonUnitInfo, User.class);

        String jsonAccessPolicy = UserInfo.getString("access_policy", "");
        AccessPolicy policy = gson.fromJson(jsonAccessPolicy, AccessPolicy.class);

        GlobalData.getInstance().setUnits(residentData.getUnits());
        GlobalData.getInstance().setAccessPolicy(policy);
    }
}
