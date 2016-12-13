package com.simplysmart.service.adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.simplysmart.service.database.ReadingDataRealm;
import com.simplysmart.service.dialog.EditDialog;
import com.simplysmart.service.model.matrix.Summary;
import com.simplysmart.service.viewholder.SummaryHeaderViewHolder;
import com.simplysmart.service.viewholder.SummaryViewHolder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;

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

    public SummaryListAdapter(ArrayList<Summary> data, Context mContext, FragmentManager fragmentManager) {
        this.data = data;
        this.mContext = mContext;
        this.textTypeface = Typeface.createFromAsset(mContext.getAssets(), "fontawesome-webfont.ttf");
        this.fragmentManager = fragmentManager;
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
                    Realm realm = Realm.getDefaultInstance();
                    ReadingDataRealm readingDataRealm = realm
                            .where(ReadingDataRealm.class)
                            .equalTo("timestamp", time)
                            .findFirst();
                    showEditDialog(readingDataRealm, holder.getAdapterPosition());
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

    private void showEditDialog(ReadingDataRealm readingDataRealm, int position) {
        EditDialog newDialog = EditDialog.newInstance(readingDataRealm, position, false);
        newDialog.show(fragmentManager, "show dialog");
    }

    public ArrayList<Summary> getData() {
        return data;
    }

    public void setData(ArrayList<Summary> data) {
        this.data = data;
    }
}
