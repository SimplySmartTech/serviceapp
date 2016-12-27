package com.simplysmart.service.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simplysmart.service.R;

/**
 * Created by shailendrapsp on 26/12/16.
 */

public class PhotoViewHolder extends RecyclerView.ViewHolder {
    public ImageView take_pic,view_pic;
    public CardView take_pic_layout,pic_layout;
    public TextView submit,edit;

    public PhotoViewHolder(View itemView) {
        super(itemView);
        view_pic = (ImageView)itemView.findViewById(R.id.view_pic);
        take_pic = (ImageView)itemView.findViewById(R.id.take_pic);
        take_pic_layout = (CardView)itemView.findViewById(R.id.take_pic_layout);
        pic_layout = (CardView)itemView.findViewById(R.id.pic_layout);
        submit = (TextView)itemView.findViewById(R.id.submit);
        edit = (TextView)itemView.findViewById(R.id.edit);
    }
}
