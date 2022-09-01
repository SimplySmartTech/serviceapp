package com.simplysmart.service.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.simplysmart.service.R;
import com.simplysmart.service.activity.InputReadingFormActivity;
import com.simplysmart.service.activity.MatrixSensorListActivity;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.database.MatrixTable;
import com.simplysmart.service.database.SensorTable;
import com.simplysmart.service.viewholder.ParentViewHolder;

import java.util.List;

/**
 * Created by shailendrapsp on 22/12/16.
 */

public class MatrixTableAdapter extends RecyclerView.Adapter<ParentViewHolder> {
    private Context mContext;
    private List<MatrixTable> matrixData;
    private boolean backdated;

    public MatrixTableAdapter(Context context, List<MatrixTable> data,boolean backdated) {
        mContext = context;
        matrixData = data;
        this.backdated = backdated;
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

        final List<SensorTable> sensorTable = SensorTable.getSensorList(data.utility_id);
        String unit = sensorTable.get(0).unit;

        String tooltip = "";
        if (sensorTable.get(0).tooltip != null && sensorTable.get(0).tooltip.equalsIgnoreCase("")) {
            tooltip = sensorTable.get(0).tooltip;
        }

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
                if (sensorTable.size() > 1) {
                    Intent i = new Intent(mContext, MatrixSensorListActivity.class);
                    i.putExtra(StringConstants.UTILITY_ID, data.utility_id);
                    i.putExtra(StringConstants.BACKDATA,backdated);
                    mContext.startActivity(i);
                } else {
                    Intent i = new Intent(mContext, InputReadingFormActivity.class);
                    i.putExtra(StringConstants.UTILITY_ID, data.utility_id);
                    i.putExtra(StringConstants.SENSOR_NAME, sensorTable.get(0).sensor_name);
                    i.putExtra(StringConstants.BACKDATA,backdated);
                    mContext.startActivity(i);
                }
            }
        });

        holder.unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sensorTable != null && sensorTable.size() > 1) {
                    Intent i = new Intent(mContext, MatrixSensorListActivity.class);
                    i.putExtra(StringConstants.UTILITY_ID, data.utility_id);
                    mContext.startActivity(i);
                } else {
                    Intent i = new Intent(mContext, InputReadingFormActivity.class);
                    i.putExtra(StringConstants.UTILITY_ID, data.utility_id);
                    i.putExtra(StringConstants.SENSOR_NAME, sensorTable.get(0).sensor_name);
                    mContext.startActivity(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return matrixData.size();
    }

    public boolean isBackdated() {
        return backdated;
    }

    public void setBackdated(boolean backdated) {
        this.backdated = backdated;
    }
}
