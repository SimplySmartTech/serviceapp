package com.simplysmart.service.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.simplysmart.service.R;

/**
 * Created by shailendrapsp on 15/11/16.
 */

public class SummaryViewHolder extends RecyclerView.ViewHolder {

    public TextView sensorValue, sensorName, time, edit;
    public ImageView photo;
    public ProgressBar uploadImageBar;
    public LinearLayout parentLayout;

    public SummaryViewHolder(View itemView) {
        super(itemView);

        sensorName = (TextView) itemView.findViewById(R.id.sensorName);
        sensorValue = (TextView) itemView.findViewById(R.id.sensorValue);
        time = (TextView) itemView.findViewById(R.id.time);
        edit = (TextView) itemView.findViewById(R.id.edit);
        photo = (ImageView) itemView.findViewById(R.id.photo);
        uploadImageBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        parentLayout = (LinearLayout) itemView.findViewById(R.id.parentLayout);
    }

}
