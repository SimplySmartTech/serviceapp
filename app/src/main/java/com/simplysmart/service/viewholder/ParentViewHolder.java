package com.simplysmart.service.viewholder;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.simplysmart.service.R;

/**
 * Created by shailendrapsp on 18/11/16.
 */

public class ParentViewHolder extends RecyclerView.ViewHolder{

    public TextView sensor_type;
    public TextView unit;
    public LinearLayout parent;

    public ParentViewHolder(View itemView) {
        super(itemView);
        this.sensor_type = (TextView)itemView.findViewById(R.id.typeName);
        this.unit = (TextView) itemView.findViewById(R.id.unit);
        this.parent = (LinearLayout) itemView.findViewById(R.id.parentLayout);
    }
}
