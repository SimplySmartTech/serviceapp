package com.simplysmart.service.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.simplysmart.service.R;
import com.simplysmart.service.activity.ImageViewActivity;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.viewholder.AttendanceItemViewHolder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by shekhar on 27/12/16.
 */

public class VisitorImageListAdapter extends RecyclerView.Adapter<AttendanceItemViewHolder> {
    private Context mContext;
    private ArrayList<String> imageUrls;

    public VisitorImageListAdapter(Context mContext, ArrayList<String> imageUrls) {
        this.mContext = mContext;
        this.imageUrls = imageUrls;
    }

    @Override
    public AttendanceItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AttendanceItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_attendance_item,parent,false));
    }

    @Override
    public void onBindViewHolder(AttendanceItemViewHolder holder, int position) {
        final String imageUrl = imageUrls.get(position);
        holder.date.setVisibility(View.GONE);
        holder.time.setVisibility(View.GONE);
        File image = new File(imageUrl);
        setPic(holder.view_pic,image);

        holder.view_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImageViewActivity.class);
                intent.putExtra(StringConstants.PHOTO_PATH, imageUrl);
                intent.putExtra(StringConstants.ALLOW_NEW_IMAGE, false);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    private void setPic(ImageView view, File image) {
        view.setVisibility(View.VISIBLE);
        Picasso.with(mContext).load(image)
                .placeholder(R.drawable.photo_default)
                .noFade()
                .fit().centerCrop()
//                .resize(48, 48)
                .error(R.drawable.photo_default).into(view);

    }
}
