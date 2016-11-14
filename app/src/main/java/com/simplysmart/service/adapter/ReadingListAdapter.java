package com.simplysmart.service.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplysmart.service.R;
import com.simplysmart.service.activity.InputFormActivity;
import com.simplysmart.service.database.ReadingDataRealm;
import com.simplysmart.service.viewholder.ReadingViewHolder;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.EventListener;

/**
 * Created by shailendrapsp on 4/11/16.
 */

public class ReadingListAdapter extends RecyclerView.Adapter<ReadingViewHolder> {
    private ArrayList<ReadingDataRealm> readingsList;
    private Context mContext;

    public ReadingListAdapter(ArrayList<ReadingDataRealm> readingsList, Context mContext) {
        this.readingsList = readingsList;
        this.mContext = mContext;
    }

    @Override
    public ReadingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.reading_list_item,parent,false);
        return new ReadingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReadingViewHolder holder, int position) {
        ReadingDataRealm readingDataRealm = readingsList.get(readingsList.size()-position-1);
        String reading = readingDataRealm.getValue()+ "  " + readingDataRealm.getUnit();
        boolean imageFound = true;
        File image = null;
        try {
            image = new File(readingsList.get(position).getLocal_photo_url());
        }catch (Exception e){
            imageFound = false;
        }

        holder.reading.setText(reading);
        holder.time.setText(readingDataRealm.getDate());

        if(imageFound) {
            setPic(holder.photo, image);
            holder.photo.setAlpha(1.0f);
        }

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return readingsList.size();
    }

    public void addElement(ReadingDataRealm dataRealm){
        readingsList.add(dataRealm);
    }

    private void setPic(ImageView view,File image) {
        view.setVisibility(View.VISIBLE);
        Picasso.with(mContext).load(image)
                .placeholder(R.drawable.ic_menu_slideshow)
                .noFade()
                .resize(64,64)
                .error(R.drawable.ic_menu_slideshow).into(view);

    }

}
