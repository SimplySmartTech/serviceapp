package com.simplysmart.service.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.simplysmart.service.R;
import com.simplysmart.service.model.helpdesk.ComplaintChat;
import com.simplysmart.service.util.ParseDateFormat;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;

public class CommentListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ComplaintChat> complaintChats;

    public CommentListAdapter(Context context, ArrayList<ComplaintChat> complaintChats) {
        this.mContext = context;
        this.complaintChats = complaintChats;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();

            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.custom_list_row_complaint_comment, parent, false);

            viewHolder.botsworth_chat_bubble = (RelativeLayout) convertView.findViewById(R.id.botsworth_chat_bubble);
            viewHolder.resident_chat_bubble = (RelativeLayout) convertView.findViewById(R.id.resident_chat_bubble);

            viewHolder.botsworthComment = (TextView) convertView.findViewById(R.id.txt_comment_botswoth);
            viewHolder.botsworthTime = (TextView) convertView.findViewById(R.id.txt_time_botswoth);
            viewHolder.residentComment = (TextView) convertView.findViewById(R.id.txt_comment_resident);
            viewHolder.residentTime = (TextView) convertView.findViewById(R.id.txt_time_resident);

            viewHolder.activityImage = (ImageView) convertView.findViewById(R.id.activityImage);
            viewHolder.activityImageResident = (ImageView) convertView.findViewById(R.id.activityImageResident);

            viewHolder.loadingImage = (LinearLayout) convertView.findViewById(R.id.loadingImage);
            viewHolder.loadingImageResident = (LinearLayout) convertView.findViewById(R.id.loadingImageResident);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.activityImage.setTag(position);
        viewHolder.activityImageResident.setTag(position);

        if (complaintChats.get(position).getResource_type() != null &&
                complaintChats.get(position).getResource_type().trim().equalsIgnoreCase("User")) {
            viewHolder.resident_chat_bubble.setVisibility(View.VISIBLE);
            viewHolder.botsworth_chat_bubble.setVisibility(View.GONE);

            if (complaintChats.get(position).getText() != null) {
                viewHolder.residentComment.setText(Html.fromHtml(complaintChats.get(position).getText()));
            } else {
                viewHolder.residentComment.setText("----");
            }

            try {
                viewHolder.residentTime.setText(ParseDateFormat.changeDateFormat(complaintChats.get(position).getCreated_at()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (complaintChats.get(position).getImage_url() != null && !complaintChats.get(position).getImage_url().equalsIgnoreCase("")) {
                viewHolder.activityImageResident.setVisibility(View.VISIBLE);
                viewHolder.loadingImageResident.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(complaintChats.get(position).getImage_url())
                        .error(R.drawable.loading_border).into(viewHolder.activityImageResident);
            } else {
                viewHolder.activityImageResident.setVisibility(View.GONE);
                viewHolder.loadingImageResident.setVisibility(View.GONE);
            }

        } else {
            viewHolder.resident_chat_bubble.setVisibility(View.GONE);
            viewHolder.botsworth_chat_bubble.setVisibility(View.VISIBLE);

            if (complaintChats.get(position).getText() != null) {
                viewHolder.botsworthComment.setText(Html.fromHtml(complaintChats.get(position).getText()));
            } else {
                viewHolder.botsworthComment.setText("----");
            }
            try {
                viewHolder.botsworthTime.setText(ParseDateFormat.changeDateFormat(complaintChats.get(position).getCreated_at()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (complaintChats.get(position).getImage_url() != null && !complaintChats.get(position).getImage_url().equalsIgnoreCase("")) {
                viewHolder.activityImage.setVisibility(View.VISIBLE);
                viewHolder.loadingImage.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(complaintChats.get(position).getImage_url())
                        .error(R.drawable.loading_border).into(viewHolder.activityImage);
            } else {
                viewHolder.activityImage.setVisibility(View.GONE);
                viewHolder.loadingImage.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    private static class ViewHolder {
        private RelativeLayout botsworth_chat_bubble, resident_chat_bubble;
        private TextView botsworthComment, residentComment;
        private TextView botsworthTime, residentTime;

        private ImageView activityImage, activityImageResident;
        private LinearLayout loadingImageResident, loadingImage;

    }

    public void addData(ArrayList<ComplaintChat> data) {
        complaintChats.addAll(data);
    }

    public ArrayList<ComplaintChat> getData() {
        return complaintChats;
    }

}