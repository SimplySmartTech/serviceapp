package com.simplysmart.service.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.simplysmart.service.R;
import com.simplysmart.service.activity.InputFormActivity;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.database.SensorTable;
import com.simplysmart.service.viewholder.ChildViewHolder;

import java.util.List;

/**
 * Created by shekhar on 20/10/16.
 */

public class SensorListAdapter extends RecyclerView.Adapter<ChildViewHolder> {

    private Context mContext;
    private List<SensorTable> sensorData;
    private Typeface textTypeface;

    public SensorListAdapter(Context context, List<SensorTable> data) {
        mContext = context;
        sensorData = data;
    }

    @Override
    public ChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.matrix_list_row_child, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChildViewHolder holder, final int position) {
        final SensorTable data = sensorData.get(position);
        String sensorName ="";
        String tooltip = "";
        String unit;
        if (data != null && data.sensor_name != null) {
            sensorName = data.sensor_name;
        }

        if (data != null && data.unit != null) {
            unit = data.unit;
        } else {
            unit = "";
        }

        if (data!=null && data.tooltip != null && !data.tooltip.equalsIgnoreCase("")) {
            tooltip = data.tooltip;
            holder.unit.setText(tooltip);
        } else {
            holder.unit.setVisibility(View.GONE);
            holder.sensor_name.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }


        if (unit.contains("\\")) {
            holder.sensor_name.setText(sensorName + "( " + "\u00B0" + " C)");
        } else {
            holder.sensor_name.setText(sensorName + "( " + unit + ")");
        }

        final String finalSensorName = sensorName;
        holder.sensor_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, InputFormActivity.class);
                i.putExtra(StringConstants.UTILITY_ID, data.utility_identifier);
                i.putExtra(StringConstants.SENSOR_NAME, finalSensorName);
                mContext.startActivity(i);
            }
        });

        holder.unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, InputFormActivity.class);
                i.putExtra(StringConstants.UTILITY_ID, data.utility_identifier);
                i.putExtra(StringConstants.SENSOR_NAME, finalSensorName);
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sensorData.size();
    }
}
