package com.simplysmart.service.adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.simplysmart.service.R;
import com.simplysmart.service.activity.ImageViewActivity;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.database.ReadingTable;
import com.simplysmart.service.dialog.EditDialog;
import com.simplysmart.service.model.matrix.Summary;
import com.simplysmart.service.viewholder.SummaryHeaderViewHolder;
import com.simplysmart.service.viewholder.SummaryViewHolder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by shailendrapsp on 4/11/16.
 */

public class SummaryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_HEADER = 1;
    private final int TYPE_ITEM = 2;
    private ArrayList<Summary> data;
    private Context mContext;
    private Typeface textTypeface;
    private FragmentManager fragmentManager;
    private SimpleDateFormat sdf;
    private boolean yesterday;

    public SummaryListAdapter(ArrayList<Summary> data, Context mContext, FragmentManager fragmentManager, boolean yesterday) {
        this.data = data;
        this.mContext = mContext;
        this.sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        this.textTypeface = Typeface.createFromAsset(mContext.getAssets(), "fontawesome-webfont.ttf");
        this.fragmentManager = fragmentManager;
        this.yesterday = yesterday;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_ITEM) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.summary_list_item, parent, false);
            return new SummaryViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.summary_list_header, parent, false);
            return new SummaryHeaderViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Summary summary = data.get(position);
        if (summary.isHeader()) {

            SummaryHeaderViewHolder viewHolder = (SummaryHeaderViewHolder) holder;
            String logo = "&#x" + data.get(position).getValue() + ";";
            String type = summary.getName();
            type = type.toUpperCase();

            viewHolder.logo.setText(Html.fromHtml(logo));
            viewHolder.logo.setTypeface(textTypeface);
            viewHolder.type.setText(type);
            if (position == 0) {
//                viewHolder.line.setVisibility(View.GONE);
            }

            if (yesterday) {
                ((SummaryHeaderViewHolder) holder).parentLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bw_color_very_light_grey));
            } else {
                ((SummaryHeaderViewHolder) holder).parentLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bw_color_white));
            }

        } else {
            SummaryViewHolder viewHolder = (SummaryViewHolder) holder;
            boolean imageFound = true;
            File image = null;
            try {
                image = new File(summary.getLocalPhotoUrl());
            } catch (Exception e) {
                imageFound = false;
            }

            viewHolder.sensorName.setText(summary.getName());
            viewHolder.sensorValue.setText(summary.getValue());
            viewHolder.time.setText(summary.getTime());
            final long time = summary.getTimestamp();

            if (imageFound) {
                setPic(viewHolder.photo, image);
                viewHolder.photo.setAlpha(1.0f);
                viewHolder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ImageViewActivity.class);
                        intent.putExtra(StringConstants.PHOTO_PATH, summary.getLocalPhotoUrl());
                        intent.putExtra(StringConstants.ALLOW_NEW_IMAGE, false);
                        mContext.startActivity(intent);
                    }
                });
            } else {
                setPic(viewHolder.photo, null);
            }

            viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReadingTable readingTable = ReadingTable.getReading(time);
                    showEditDialog(readingTable, holder.getAdapterPosition());
                }
            });

            if (summary.getLocalPhotoUrl() != null && !summary.getLocalPhotoUrl().equals("") && !summary.isUploaded()) {
                if (NetworkUtilities.isInternet(mContext)) {
                    viewHolder.uploadImageBar.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.uploadImageBar.setVisibility(View.GONE);
                }
            } else {
                viewHolder.uploadImageBar.setVisibility(View.GONE);
            }

            if (yesterday) {
                ((SummaryViewHolder) holder).parentLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bw_color_very_light_grey));
            } else {
                ((SummaryViewHolder) holder).parentLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bw_color_white));
            }

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void setPic(ImageView view, File image) {
        view.setVisibility(View.VISIBLE);
        Picasso.with(mContext).load(image)
                .placeholder(R.drawable.photo_default)
                .noFade()
                .fit().centerCrop()
//                .resize(32,32)
                .error(R.drawable.photo_default).into(view);

    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position).isHeader()) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    private void showEditDialog(ReadingTable readingTable, int position) {
        EditDialog newDialog = EditDialog.newInstance(readingTable, position, false);
        newDialog.show(fragmentManager, "show dialog");
    }

    public ArrayList<Summary> getData() {
        return data;
    }

    public void setData(ArrayList<Summary> data) {
        this.data = data;
    }

    private boolean dateBeforeToday(long date) {
        Calendar c = Calendar.getInstance();
        String current = sdf.format(c.getTimeInMillis());
        String reading = sdf.format(date);
        Date currentDate = null;
        Date readingDate = null;
        try {
            currentDate = sdf.parse(current);
            readingDate = sdf.parse(reading);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        if (readingDate.before(currentDate)) {
            return true;
        } else {
            return false;
        }
    }
}
