package com.simplysmart.service.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simplysmart.service.R;

/**
 * Created by shailendrapsp on 18/11/16.
 */

public class ChildViewHolder extends RecyclerView.ViewHolder {

    public TextView sensor_name;
    public LinearLayout parent;

    public ChildViewHolder(View itemView) {
        super(itemView);
        this.sensor_name = (TextView)itemView.findViewById(R.id.sensorName);
        this.parent = (LinearLayout)itemView.findViewById(R.id.parentLayout);
    }
}
