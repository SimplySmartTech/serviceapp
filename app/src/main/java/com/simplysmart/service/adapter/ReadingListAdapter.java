package com.simplysmart.service.adapter;

import android.app.FragmentManager;
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
import com.simplysmart.service.database.ReadingDataRealm;
import com.simplysmart.service.dialog.EditDialog;
import com.simplysmart.service.viewholder.ReadingViewHolder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by shailendrapsp on 4/11/16.
 */

public class ReadingListAdapter extends RecyclerView.Adapter<ReadingViewHolder> {
    private ArrayList<ReadingDataRealm> readingsList;
    private Context mContext;
    private FragmentManager fragmentManager;

    public ReadingListAdapter(ArrayList<ReadingDataRealm> readingsList, Context mContext, FragmentManager fragmentManager) {
        this.readingsList = readingsList;
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ReadingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.reading_list_item, parent, false);
        return new ReadingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ReadingViewHolder holder, int position) {
        final ReadingDataRealm readingDataRealm = readingsList.get(position);
        String reading = readingDataRealm.getValue() + "  " + readingDataRealm.getUnit();
        boolean imageFound = true;
        File image = null;
        try {
            image = new File(readingsList.get(position).getLocal_photo_url());
        } catch (Exception e) {
            imageFound = false;
        }

        holder.reading.setText(reading);
        holder.time.setText(readingDataRealm.getDate());

        if (imageFound) {
            setPic(holder.photo, image);
        } else {
            setPic(holder.photo, null);
        }

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(readingDataRealm, holder.getAdapterPosition());
            }
        });

        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(readingDataRealm.getLocal_photo_url()!=null && !readingDataRealm.getLocal_photo_url().equals("")) {
                    Intent i = new Intent(mContext, ImageViewActivity.class);
                    i.putExtra(StringConstants.PHOTO_PATH, readingDataRealm.getLocal_photo_url());
                    mContext.startActivity(i);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return readingsList.size();
    }

    public void addElement(ReadingDataRealm dataRealm) {
        readingsList.add(dataRealm);
        Collections.sort(readingsList, new Comparator<ReadingDataRealm>() {
            @Override
            public int compare(ReadingDataRealm o1, ReadingDataRealm o2) {
                return (int) (o2.getTimestamp() - o1.getTimestamp());
            }
        });
        notifyItemInserted(0);
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

    private void showEditDialog(ReadingDataRealm readingDataRealm, int position) {
        EditDialog newDialog = EditDialog.newInstance(readingDataRealm, position, true);
        newDialog.show(fragmentManager, "show dialog");
    }

    public ArrayList<ReadingDataRealm> getReadingsList() {
        return readingsList;
    }

    public void setReadingsList(ArrayList<ReadingDataRealm> readingsList) {
        this.readingsList = readingsList;
    }
}
