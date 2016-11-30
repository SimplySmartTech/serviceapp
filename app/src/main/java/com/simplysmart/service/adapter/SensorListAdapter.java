package com.simplysmart.service.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simplysmart.service.R;
import com.simplysmart.service.activity.InputFormActivity;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.model.matrix.SensorData;
import com.simplysmart.service.viewholder.ChildViewHolder;

import java.util.ArrayList;

/**
 * Created by shekhar on 20/10/16.
 */

public class SensorListAdapter extends RecyclerView.Adapter<ChildViewHolder> {

    private Context mContext;
    private ArrayList<SensorData> sensorData;
    private Typeface textTypeface;

    public SensorListAdapter(Context context, ArrayList<SensorData> data) {
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
        final SensorData data = sensorData.get(position);
        String sensorName= "";
        String unit;
        if(data!=null && data.getSensor_name()!=null) {
             sensorName = data.getSensor_name();
        }
        if(data!=null && data.getUnit()!=null) {
            unit = data.getUnit();
        }else{
            unit = "";
        }
        holder.sensor_name.setText(sensorName+"( "+unit+")");

        holder.sensor_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, InputFormActivity.class);
                i.putExtra(StringConstants.SENSOR_DATA, data);
                mContext.startActivity(i);
            }
        });

        holder.unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, InputFormActivity.class);
                i.putExtra(StringConstants.SENSOR_DATA, data);
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sensorData.size();
    }
}
