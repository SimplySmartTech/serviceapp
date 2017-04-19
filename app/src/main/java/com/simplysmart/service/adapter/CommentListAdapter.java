package com.simplysmart.service.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.simplysmart.service.R;
import com.simplysmart.service.model.helpdesk.ComplaintChat;
import com.simplysmart.service.util.ParseDateFormat;

import java.text.ParseException;
import java.util.ArrayList;

public class CommentListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ComplaintChat> complaintChats;

    public CommentListAdapter(Context context, ArrayList<ComplaintChat> complaintChats) {
        this.mContext = context;
        this.complaintChats=complaintChats;
    }

    @Override
    public int getCount() {
        return complaintChats.size();
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

            viewHolder = new ViewHolder();

            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.custom_list_row_complaint_comment, parent, false);

            viewHolder.botsworth_chat_bubble=(RelativeLayout)convertView.findViewById(R.id.botsworth_chat_bubble);
            viewHolder.resident_chat_bubble=(RelativeLayout)convertView.findViewById(R.id.resident_chat_bubble);

            viewHolder.botsworthComment=(TextView)convertView.findViewById(R.id.txt_comment_botswoth);
            viewHolder.botsworthTime=(TextView)convertView.findViewById(R.id.txt_time_botswoth);
            viewHolder.residentComment=(TextView)convertView.findViewById(R.id.txt_comment_resident);
            viewHolder.residentTime=(TextView)convertView.findViewById(R.id.txt_time_resident);


            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (complaintChats.get(position).getResource_type()!=null &&
                complaintChats.get(position).getResource_type().trim().equalsIgnoreCase("Resident")){
            viewHolder.resident_chat_bubble.setVisibility(View.VISIBLE);
            viewHolder.botsworth_chat_bubble.setVisibility(View.GONE);

            viewHolder.residentComment.setText(Html.fromHtml(complaintChats.get(position).getText()));
            try {
                viewHolder.residentTime.setText(ParseDateFormat.changeDateFormat(complaintChats.get(position).getCreated_at()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }else{
            viewHolder.resident_chat_bubble.setVisibility(View.GONE);
            viewHolder.botsworth_chat_bubble.setVisibility(View.VISIBLE);

            viewHolder.botsworthComment.setText(Html.fromHtml(complaintChats.get(position).getText()));
            try {
                viewHolder.botsworthTime.setText(ParseDateFormat.changeDateFormat(complaintChats.get(position).getCreated_at()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return convertView;
    }

    private static class ViewHolder {
        private RelativeLayout botsworth_chat_bubble,resident_chat_bubble;
        private TextView botsworthComment,residentComment;
        private TextView botsworthTime, residentTime;

    }

    public void addData(ArrayList<ComplaintChat> data){
        complaintChats.addAll(data);
    }

    public ArrayList<ComplaintChat> getData(){
        return complaintChats;
    }

}