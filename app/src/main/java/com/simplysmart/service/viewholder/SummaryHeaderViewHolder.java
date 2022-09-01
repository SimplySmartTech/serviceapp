package com.simplysmart.service.viewholder;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.simplysmart.service.R;

/**
 * Created by shailendrapsp on 15/11/16.
 */

public class SummaryHeaderViewHolder extends RecyclerView.ViewHolder {

    public TextView logo, type;
    public LinearLayout parentLayout;

    public SummaryHeaderViewHolder(View itemView) {
        super(itemView);
        parentLayout = (LinearLayout) itemView.findViewById(R.id.parentLayout);
        logo = (TextView) itemView.findViewById(R.id.logo);
        type = (TextView) itemView.findViewById(R.id.type);
    }
}
