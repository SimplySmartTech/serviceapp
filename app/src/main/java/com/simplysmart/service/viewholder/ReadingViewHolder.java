package com.simplysmart.service.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplysmart.service.R;

/**
 * Created by shailendrapsp on 15/11/16.
 */

public class ReadingViewHolder extends RecyclerView.ViewHolder {

    public TextView reading, time, edit;
    public ImageView photo;
    public CardView cardView;

    public ReadingViewHolder(View itemView) {
        super(itemView);
        reading = (TextView) itemView.findViewById(R.id.reading);
        time = (TextView) itemView.findViewById(R.id.time);
        edit = (TextView) itemView.findViewById(R.id.edit);
        photo = (ImageView) itemView.findViewById(R.id.photo);
        cardView = (CardView) itemView.findViewById(R.id.parentLayout);
    }
}
