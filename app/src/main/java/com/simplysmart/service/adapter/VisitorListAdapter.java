package com.simplysmart.service.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simplysmart.service.R;
import com.simplysmart.service.database.VisitorTable;
import com.simplysmart.service.viewholder.VisitorListItemViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by shekhar on 30/12/16.
 */

public class VisitorListAdapter extends RecyclerView.Adapter<VisitorListItemViewHolder> {

    private Context mContext;
    private List<VisitorTable> visitorTables;

    public VisitorListAdapter(Context mContext, List<VisitorTable> visitorTables) {
        this.mContext = mContext;
        this.visitorTables = visitorTables;
    }

    @Override
    public VisitorListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_visitor_list_item, parent, false);
        return new VisitorListItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(VisitorListItemViewHolder holder, int position) {
        VisitorTable visitorTable = visitorTables.get(position);

        if (visitorTable.num_of_person > 1) {
            holder.noOfPersons.setText(visitorTable.num_of_person + " People");
        } else {
            holder.noOfPersons.setText(visitorTable.num_of_person + " Person");
        }

        holder.description.setText(visitorTable.details);
        holder.time.setText(getDateForTime(visitorTable.timestamp));

        ArrayList<String> urlList = new ArrayList<>();
        int prevPos = 0;
        for (int j = 0; j < visitorTable.local_image_urls.length(); j++) {
            if (visitorTable.local_image_urls.charAt(j) == ',') {
                urlList.add(visitorTable.local_image_urls.substring(prevPos, j));
                prevPos = j + 1;
            }
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 6);
        holder.recyclerView.setLayoutManager(gridLayoutManager);
        PhotoListAdapter visitorListAdapter = new PhotoListAdapter(mContext, urlList);
        holder.recyclerView.setAdapter(visitorListAdapter);
    }

    private String getDateForTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.getDefault());
        return sdf.format(timestamp);
    }

    @Override
    public int getItemCount() {
        return visitorTables.size();
    }
}
