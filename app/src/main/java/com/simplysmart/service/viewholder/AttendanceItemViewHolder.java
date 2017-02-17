package com.simplysmart.service.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplysmart.service.R;

/**
 * Created by shekhar on 27/12/16.
 */

public class AttendanceItemViewHolder extends RecyclerView.ViewHolder {

    public TextView date, time;
    public ImageView view_pic;
    public ImageView locationIcon;
    public TextView address;

    public AttendanceItemViewHolder(View itemView) {
        super(itemView);
        date = (TextView) itemView.findViewById(R.id.date);
        time = (TextView) itemView.findViewById(R.id.time);
        view_pic = (ImageView) itemView.findViewById(R.id.view_pic);
        locationIcon = (ImageView) itemView.findViewById(R.id.locationIcon);
        address = (TextView) itemView.findViewById(R.id.address);
    }
}
