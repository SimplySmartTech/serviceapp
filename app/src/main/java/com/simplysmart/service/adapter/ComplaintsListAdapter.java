package com.simplysmart.service.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.simplysmart.service.R;
import com.simplysmart.service.model.helpdesk.ComplaintLists;
import com.simplysmart.service.viewholder.ComplaintViewHolder;

import java.util.ArrayList;

/**
 * Created by omkar on 19/04/17.
 */

public class ComplaintsListAdapter extends RecyclerView.Adapter<ComplaintViewHolder>  {

    Context mContext;
    ArrayList<ComplaintLists> mComplaintDataList;

    public ComplaintsListAdapter(){

    }

    public ComplaintsListAdapter(Context context, ArrayList<ComplaintLists> complaintDataList){
        mContext = context;
        mComplaintDataList = complaintDataList;

    }

    @Override
    public ComplaintViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.complaint_list_row,parent,false);
        return new ComplaintViewHolder(view, mContext, mComplaintDataList);
    }

    @Override
    public void onBindViewHolder(ComplaintViewHolder holder, int position) {
        ComplaintLists complaintData = mComplaintDataList.get(position);
       // holder.complaintNumberTextView.setText(complaintData.getNumber());

        holder.helpdesk_complaint_no.setText(complaintData.getNumber());
        holder.helpdesk_sub_category.setText(complaintData.getComplaint_category_name());


    }

    @Override
    public int getItemCount() {
        return mComplaintDataList.size();
    }
}
