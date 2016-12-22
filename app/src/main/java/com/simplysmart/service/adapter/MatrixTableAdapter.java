package com.simplysmart.service.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.simplysmart.service.R;
import com.simplysmart.service.activity.InputFormActivity;
import com.simplysmart.service.activity.SensorListActivity;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.database.MatrixTable;
import com.simplysmart.service.model.matrix.MatrixData;
import com.simplysmart.service.viewholder.ParentViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shailendrapsp on 22/12/16.
 */

public class MatrixTableAdapter extends RecyclerView.Adapter<ParentViewHolder> {
    private Context mContext;
    private List<MatrixTable> matrixData;
    private final LayoutInflater inflater;
//    private Typeface textTypeface;

    public MatrixTableAdapter(Context context, List<MatrixTable> data) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        matrixData = data;

//        textTypeface = Typeface.createFromAsset(mContext.getAssets(), "fontawesome-webfont.ttf");
    }

    @Override
    public ParentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.matrix_list_row_parent, parent, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ParentViewHolder holder, int position) {

        final MatrixTable data = matrixData.get(position);
        String sensorName = "";
        if (data.type != null) {
            sensorName = data.type;
        }

        String unit = "";
        String tooltip = "";
        String unicodeUnit = "";
        boolean isUnicode = false;
        for (int i = 0; i < unit.length(); i++) {
            if (unit.contains("\\")) {
                isUnicode = true;
            }
        }

        if (tooltip.equals("")) {
            holder.unit.setVisibility(View.GONE);
            holder.sensor_type.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        } else {
            holder.unit.setText(tooltip);
        }

        if (isUnicode) {
            holder.sensor_type.setText(sensorName + "( " + "\u00B0" + " C)");
        } else {
            holder.sensor_type.setText(sensorName + " (" + unit + ")");
        }

        holder.sensor_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return matrixData.size();
    }
}
