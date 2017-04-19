package com.simplysmart.service.activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.simplysmart.service.R;


public class AlertDialogComplaintInfo {

    Activity _Activity;
    public Dialog resource_Dialog;
    private Typeface textTypeface;

    public AlertDialogComplaintInfo(Activity _Activity) {
        this._Activity = _Activity;
        //textTypeface= Typeface.createFromAsset(_Activity.getAssets(), AppConstant.FONT_EUROSTILE_REGULAR_MID);

    }

    public void createCustomDialog() {

        resource_Dialog = new Dialog(_Activity);
        resource_Dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        resource_Dialog.setContentView(R.layout.dialog_complaint_assignee_info);
        resource_Dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView txtAssignTo = (TextView) resource_Dialog.findViewById(R.id.txt_assign_to);
        TextView assignTime = (TextView) resource_Dialog.findViewById(R.id.txt_assign_time);
        TextView assignName = (TextView) resource_Dialog.findViewById(R.id.txt_assignee_name);
        TextView assignContact = (TextView) resource_Dialog.findViewById(R.id.txt_contact_no);

        txtAssignTo.setTypeface(textTypeface);
        assignTime.setTypeface(textTypeface);
        assignName.setTypeface(textTypeface);
        assignContact.setTypeface(textTypeface);

        WindowManager.LayoutParams windowsLayoutParam = resource_Dialog.getWindow().getAttributes();
        windowsLayoutParam.dimAmount = (float) 0.4;

        resource_Dialog.getWindow().setAttributes(windowsLayoutParam);
        resource_Dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        resource_Dialog.setCancelable(true);
        resource_Dialog.show();
    }
}