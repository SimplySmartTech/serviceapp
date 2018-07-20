package com.simplysmart.service.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.simplysmart.service.R;
import com.simplysmart.service.database.ReadingTable;
import com.simplysmart.service.dialog.EditDialog;
import com.simplysmart.service.model.matrix.MatrixReadingData;
import com.simplysmart.service.util.ParseDateFormat;
import com.simplysmart.service.viewholder.ReadingViewHolder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by shekhar on 19/07/18.
 */

public class ReadingListAdapterV2 extends RecyclerView.Adapter<ReadingViewHolder> {
    private List<MatrixReadingData> readingsList;
    private Context mContext;
    private FragmentManager fragmentManager;
    private SimpleDateFormat sdf;

    public ReadingListAdapterV2(List<MatrixReadingData> readingsList, Context mContext, FragmentManager fragmentManager) {
        this.readingsList = readingsList;
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
        this.sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Collections.sort(readingsList, new Comparator<MatrixReadingData>() {
            @Override
            public int compare(MatrixReadingData lhs, MatrixReadingData rhs) {
                return (int) (Long.parseLong(rhs.getTimestamp()) - Long.parseLong(lhs.getTimestamp()));
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
        final MatrixReadingData readingTable = readingsList.get(position);

        DecimalFormat formatter = new DecimalFormat();
        formatter.setDecimalSeparatorAlwaysShown(false);

        String reading = (formatter.format(Double.parseDouble(readingTable.getReading()))) + " " + (readingTable.getUnit() != null ? readingTable.getUnit() : "");

//        boolean imageFound = true;
//        File image = null;
//        try {
//            image = new File(readingTable.local_photo_url);
//        } catch (Exception e) {
//            imageFound = false;
//        }

        holder.reading.setText(reading);

//        String date = readingTable.date;
//        if (readingTable.updated_at != 0 && readingTable.remark != null && !readingTable.remark.equalsIgnoreCase("")) {
//            date += "  (Edited)";
//        }
//
//        if (dateBeforeToday(readingTable.timestamp)) {
//            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.bw_color_very_light_grey));
//        } else {
//            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.bw_color_white));
//        }
//
        holder.time.setText(ParseDateFormat.getDate(Long.parseLong(readingTable.getTimestamp())));

//        if (imageFound) {
//            setPic(holder.photo, image);
//        } else {
//            setPic(holder.photo, null);
//        }
        holder.edit.setVisibility(View.GONE);
//        holder.edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showEditDialog(readingTable, holder.getAdapterPosition());
//            }
//        });
//
//        holder.photo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (readingTable.local_photo_url != null && !readingTable.local_photo_url.equals("")) {
//                    Intent i = new Intent(mContext, ImageViewActivity.class);
//                    i.putExtra(StringConstants.PHOTO_PATH, readingTable.local_photo_url);
//                    mContext.startActivity(i);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return readingsList.size();
    }

    public int addElement(MatrixReadingData readingTable) {
        readingsList.add(readingTable);

        Collections.sort(readingsList, new Comparator<MatrixReadingData>() {
            @Override
            public int compare(MatrixReadingData lhs, MatrixReadingData rhs) {
                return (int) (Long.parseLong(rhs.getTimestamp()) - Long.parseLong(lhs.getTimestamp()));
            }
        });

        notifyItemInserted(readingsList.indexOf(readingTable));
        return readingsList.indexOf(readingTable);
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

    public List<MatrixReadingData> getReadingsList() {
        return readingsList;
    }

    public void setReadingsList(List<MatrixReadingData> readingsList) {
        this.readingsList = readingsList;
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
