package com.simplysmart.service.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.simplysmart.service.R;

/**
 * Created by shailendrapsp on 30/12/16.
 */

public class VisitorListItemViewHolder extends RecyclerView.ViewHolder {
    public TextView noOfPersons,description,time;
    public RecyclerView recyclerView;

    public VisitorListItemViewHolder(View itemView) {
        super(itemView);
        time = (TextView)itemView.findViewById(R.id.time);
        noOfPersons = (TextView)itemView.findViewById(R.id.noOfPersons);
        description = (TextView)itemView.findViewById(R.id.description);
        recyclerView = (RecyclerView) itemView.findViewById(R.id.photos);
    }
}
