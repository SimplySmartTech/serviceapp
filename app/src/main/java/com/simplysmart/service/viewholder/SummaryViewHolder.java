package com.simplysmart.service.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplysmart.service.R;

/**
 * Created by shailendrapsp on 15/11/16.
 */

public class SummaryViewHolder extends RecyclerView.ViewHolder {

    public TextView sensorType, sensorNameAndValue, time;
    public ImageButton edit;
    public ImageView photo;

    public SummaryViewHolder(View itemView) {
        super(itemView);

        sensorType = (TextView) itemView.findViewById(R.id.type);
        sensorNameAndValue = (TextView) itemView.findViewById(R.id.name);
        time = (TextView) itemView.findViewById(R.id.time);
        edit = (ImageButton) itemView.findViewById(R.id.edit);
        photo = (ImageView) itemView.findViewById(R.id.photo);

    }

}
