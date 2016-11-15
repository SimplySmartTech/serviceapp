package com.simplysmart.service.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.simplysmart.service.R;

/**
 * Created by shailendrapsp on 15/11/16.
 */

public class SummaryHeaderViewHolder extends RecyclerView.ViewHolder {

    public TextView logo,type;
    public View line;
    public SummaryHeaderViewHolder(View itemView) {
        super(itemView);
        logo = (TextView)itemView.findViewById(R.id.logo);
        type = (TextView)itemView.findViewById(R.id.type);
        line = itemView.findViewById(R.id.topSeparator);
    }
}
