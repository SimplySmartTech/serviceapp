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
import com.simplysmart.service.database.AttendanceTable;
import com.simplysmart.service.viewholder.AttendanceItemViewHolder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by shailendrapsp on 27/12/16.
 */

public class AttendanceListAdapter extends RecyclerView.Adapter<AttendanceItemViewHolder> {
    private Context mContext;
    private List<AttendanceTable> attendanceList;

    public AttendanceListAdapter(Context mContext, List<AttendanceTable> attendanceList) {
        this.mContext = mContext;
        this.attendanceList = attendanceList;
    }

    @Override
    public AttendanceItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AttendanceItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_attendance_item, parent, false));
    }

    @Override
    public void onBindViewHolder(AttendanceItemViewHolder holder, int position) {
        final AttendanceTable table = attendanceList.get(position);
        int pos = position;
        holder.date.setText(getDate(table));
        holder.time.setText(getTime(table));
        File image = new File(table.local_photo_url);
        setPic(holder.view_pic, image);

        holder.view_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImageViewActivity.class);
                intent.putExtra(StringConstants.PHOTO_PATH, table.local_photo_url);
                intent.putExtra(StringConstants.ALLOW_NEW_IMAGE, false);
                mContext.startActivity(intent);
            }
        });

    }

    private String getTime(AttendanceTable table) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(table.timestamp);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        String h = "";
        String m = "";
        if (hour < 10) {
            h = "0" + hour;
        } else {
            h = "" + hour;
        }

        if (minute < 10) {
            m = "0" + minute;
        } else {
            m = "" + minute;
        }

        return "" + h + ":" + m+" hrs";
    }

    private String getDate(AttendanceTable table) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(table.timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(table.timestamp);
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
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
