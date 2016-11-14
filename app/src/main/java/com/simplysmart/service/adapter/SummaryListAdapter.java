package com.simplysmart.service.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplysmart.service.R;
import com.simplysmart.service.model.matrix.Summary;
import com.simplysmart.service.viewholder.SummaryViewHolder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by shailendrapsp on 4/11/16.
 */

public class SummaryListAdapter extends RecyclerView.Adapter<SummaryViewHolder> {

    private ArrayList<Summary> data;
    private Context mContext;

    public SummaryListAdapter(ArrayList<Summary> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public SummaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.summary_list_item,parent,false);
        return new SummaryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SummaryViewHolder holder, int position) {
        Summary summary = data.get(position);
        String reading = summary.getName()+"  "+summary.getValue();
        boolean imageFound = true;
        File image = null;
        try {
            image = new File(summary.getLocalPhotoUrl());
        }catch (Exception e){
            imageFound = false;
        }

        holder.sensorType.setText(summary.getType());
        holder.sensorNameAndValue.setText(reading);
        holder.time.setText(summary.getTime());

        if(imageFound){
            setPic(holder.photo,image);
        }

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void setPic(ImageView view, File image) {
        view.setVisibility(View.VISIBLE);
        Picasso.with(mContext).load(image)
                .placeholder(R.drawable.ic_menu_slideshow)
                .noFade()
                .resize(64,64)
                .error(R.drawable.ic_menu_slideshow).into(view);

    }
}
