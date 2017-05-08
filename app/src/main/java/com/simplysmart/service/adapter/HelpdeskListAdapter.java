package com.simplysmart.service.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.simplysmart.service.R;
import com.simplysmart.service.model.helpdesk.ComplaintLists;

import java.util.ArrayList;
import java.util.HashMap;

public class HelpdeskListAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<ComplaintLists> complaintLists;
    private HashMap<String, Integer> priorityColorMap = new HashMap<>();

    public HelpdeskListAdapter(Context context, ArrayList<ComplaintLists> complaintLists) {
        this.mContext = context;
        this.complaintLists = complaintLists;
        priorityColorMap.put("Regular", R.drawable.circle_priority_regular);
        priorityColorMap.put("High", R.drawable.circle_priority_high);
        priorityColorMap.put("Medium", R.drawable.circle_priority_medium);
        priorityColorMap.put("Low", R.drawable.circle_priority_low);
    }

    @Override
    public int getCount() {
        return (complaintLists.size());
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {

            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.complaint_list_row, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.helpdesk_logo = (TextView) convertView.findViewById(R.id.helpdesk_logo);
            viewHolder.helpdesk_sub_category = (TextView) convertView.findViewById(R.id.helpdesk_sub_category);
            viewHolder.helpdesk_complaint_no = (TextView) convertView.findViewById(R.id.helpdesk_complaint_no);
            viewHolder.helpdesk_desc = (TextView) convertView.findViewById(R.id.helpdesk_desc);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.helpdesk_complaint_no.setText("# " + complaintLists.get(position).getNumber());
        viewHolder.helpdesk_sub_category.setText(complaintLists.get(position).getSub_category_name());
        viewHolder.helpdesk_desc.setText(complaintLists.get(position).getDescription());
        viewHolder.helpdesk_logo.setText(complaintLists.get(position).getCategory_short_name());

        if (priorityColorMap.containsKey(complaintLists.get(position).getPriority())) {
            viewHolder.helpdesk_logo.setBackgroundResource(priorityColorMap.get(complaintLists.get(position).getPriority()));
        }

        return convertView;
    }

    static class ViewHolder {

        TextView helpdesk_logo, helpdesk_sub_category, helpdesk_complaint_no, helpdesk_desc;

    }


    public void clearData() {
        complaintLists.clear();
        notifyDataSetChanged();
    }

    public void addData(ArrayList<ComplaintLists> data) {
        complaintLists.addAll(data);
    }

    public ArrayList<ComplaintLists> getData() {
        return complaintLists;
    }

}