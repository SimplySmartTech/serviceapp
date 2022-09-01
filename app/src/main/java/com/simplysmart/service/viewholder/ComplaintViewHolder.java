package com.simplysmart.service.viewholder;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.simplysmart.service.R;
import com.simplysmart.service.model.helpdesk.ComplaintLists;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by omkar on 19/04/17.
 */

public class ComplaintViewHolder extends RecyclerView.ViewHolder {
    private final Context mContext;
    private final ArrayList<ComplaintLists> complaintLists;
    private final HashMap<String, Integer> hashCategoryLogo = new HashMap<>();
    private Typeface textTypeface,textBoldTypeface;
    public TextView helpdesk_logo, helpdesk_sub_category, helpdesk_complaint_no, helpdesk_desc, helpdesk_count;


    public ComplaintViewHolder(View itemView, Context mContext, ArrayList<ComplaintLists> complaintLists) {
        super(itemView);
        this.mContext = mContext;
//        this.complaintNumberTextView = (TextView) itemView.findViewById(R.id.complaintNumber);
//        this.complaintListLayout = (LinearLayout) itemView.findViewById(R.id.complaint_parent_layout);
        this.helpdesk_sub_category = (TextView) itemView.findViewById(R.id.helpdesk_sub_category);
        this.helpdesk_complaint_no = (TextView) itemView.findViewById(R.id.helpdesk_complaint_no);


        this.complaintLists = complaintLists;
    }
}
