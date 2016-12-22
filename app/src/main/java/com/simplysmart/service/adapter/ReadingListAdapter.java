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
import com.simplysmart.service.database.ReadingTable;
import com.simplysmart.service.dialog.EditDialog;
import com.simplysmart.service.viewholder.ReadingViewHolder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by shailendrapsp on 4/11/16.
 */

public class ReadingListAdapter extends RecyclerView.Adapter<ReadingViewHolder> {
    private List<ReadingTable> readingsList;
    private Context mContext;
    private FragmentManager fragmentManager;

    public ReadingListAdapter(List<ReadingTable> readingsList, Context mContext, FragmentManager fragmentManager) {
        this.readingsList = readingsList;
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;

        Collections.sort(readingsList, new Comparator<ReadingTable>() {
            @Override
            public int compare(ReadingTable lhs, ReadingTable rhs) {
                return (int)(rhs.timestamp-lhs.timestamp);
            }
        });
    }

    @Override
    public ReadingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.reading_list_item, parent, false);
        return new ReadingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ReadingViewHolder holder, int position) {
        final ReadingTable readingTable = readingsList.get(position);
        String reading = readingTable.value + "  " + readingTable.unit;
        boolean imageFound = true;
        File image = null;
        try {
            image = new File(readingTable.local_photo_url);
        } catch (Exception e) {
            imageFound = false;
        }

        holder.reading.setText(reading);
        holder.time.setText(readingTable.date);

        if (imageFound) {
            setPic(holder.photo, image);
        } else {
            setPic(holder.photo, null);
        }

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(readingTable, holder.getAdapterPosition());
            }
        });

        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (readingTable.local_photo_url != null && !readingTable.local_photo_url.equals("")) {
                    Intent i = new Intent(mContext, ImageViewActivity.class);
                    i.putExtra(StringConstants.PHOTO_PATH, readingTable.local_photo_url);
                    mContext.startActivity(i);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return readingsList.size();
    }

    public void addElement(ReadingTable readingTable) {
        readingsList.add(readingTable);

        Collections.sort(readingsList, new Comparator<ReadingTable>() {
            @Override
            public int compare(ReadingTable lhs, ReadingTable rhs) {
                return (int) (rhs.timestamp - lhs.timestamp);
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

    private void showEditDialog(ReadingTable readingTable, int position) {
        EditDialog newDialog = EditDialog.newInstance(readingTable, position, true);
        newDialog.show(fragmentManager, "show dialog");
    }

    public List<ReadingTable> getReadingsList() {
        return readingsList;
    }

    public void setReadingsList(List<ReadingTable> readingsList) {
        this.readingsList = readingsList;
    }
}
