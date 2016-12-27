package com.simplysmart.service.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.simplysmart.service.R;
import com.simplysmart.service.activity.AttendanceActivity;
import com.simplysmart.service.activity.ImageViewActivity;
import com.simplysmart.service.activity.InputFormActivity;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.database.AttendanceTable;
import com.simplysmart.service.interfaces.TakePicInterface;
import com.simplysmart.service.viewholder.PhotoViewHolder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by shailendrapsp on 26/12/16.
 */

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoViewHolder> {
    private Context mContext;
    private List<AttendanceTable> attendanceList;

    public PhotoListAdapter(Context mContext, List<AttendanceTable> attendanceList) {
        this.mContext = mContext;
        this.attendanceList = attendanceList;
        if(attendanceList.size()>1){
            Collections.sort(attendanceList, new Comparator<AttendanceTable>() {
                @Override
                public int compare(AttendanceTable lhs, AttendanceTable rhs) {
                    return (int)(lhs.timestamp-rhs.timestamp);
                }
            });
        }
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_attendance_item, parent, false);
        return new PhotoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        final AttendanceTable table = attendanceList.get(position);
        final int pos = position;
        if (pos == 0) {
            holder.take_pic_layout.setVisibility(View.VISIBLE);
            if(table.local_photo_url.equalsIgnoreCase("")){
                holder.pic_layout.setVisibility(View.GONE);
            }else {
                setPic(holder.view_pic,table.local_photo_url);
            }
        }else {
            holder.take_pic_layout.setVisibility(View.GONE);
            holder.pic_layout.setVisibility(View.VISIBLE);
            setPic(holder.view_pic,table.local_photo_url);
        }

    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    private void setPic(ImageView view, String imageUrl) {
        File image = new File(imageUrl);

        if (image.exists()) {
            Picasso.with(mContext).load(image)
                    .placeholder(R.drawable.ic_photo_black_48dp)
                    .noFade()
                    .fit().centerCrop()
//                    .resize(48, 48)
                    .error(R.drawable.ic_menu_slideshow).into(view);
            view.setAlpha(1.0f);
            view.setVisibility(View.VISIBLE);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    public void addElement(AttendanceTable attendanceTable,int position){
        attendanceList.set(position,attendanceTable);
        notifyItemInserted(position);
    }
}
