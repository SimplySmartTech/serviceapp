package com.simplysmart.service.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplysmart.service.R;
import com.simplysmart.service.activity.InputFormActivity;
import com.simplysmart.service.activity.SensorListActivity;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.model.matrix.MatrixData;
import com.simplysmart.service.viewholder.ParentViewHolder;

import java.util.ArrayList;

/**
 * Created by shekhar on 20/10/16.
 */

public class MatrixListAdapter extends RecyclerView.Adapter<ParentViewHolder> {

    private Context mContext;
    private ArrayList<MatrixData> matrixData;
    private final LayoutInflater inflater;
//    private Typeface textTypeface;

    public MatrixListAdapter(Context context, ArrayList<MatrixData> data) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        matrixData = data;

//        textTypeface = Typeface.createFromAsset(mContext.getAssets(), "fontawesome-webfont.ttf");
    }

    @Override
    public ParentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.matrix_list_row_parent,parent,false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ParentViewHolder holder, int position) {

        final MatrixData data = matrixData.get(position);
        if(data.getType()!=null) {
            holder.sensor_type.setText(data.getType());
        }
        String unit;
        if(data.getSensors()!=null && data.getSensors().get(0)!=null && data.getSensors().get(0).getUnit()!=null) {
            unit = data.getSensors().get(0).getUnit();
        }else {
            unit = "";
        }
        holder.unit.setText(unit);

        holder.sensor_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.getSensors()!=null && data.getSensors().size()>1) {
                    Intent i = new Intent(mContext, SensorListActivity.class);
                    i.putExtra(StringConstants.METRIC_DATA, data);
                    mContext.startActivity(i);
                }else{
                    Intent i = new Intent(mContext, InputFormActivity.class);
                    i.putExtra(StringConstants.SENSOR_DATA,data.getSensors().get(0));
                    mContext.startActivity(i);
                }
            }
        });

        holder.unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.getSensors()!=null && data.getSensors().size()>1) {
                    Intent i = new Intent(mContext, SensorListActivity.class);
                    i.putExtra(StringConstants.METRIC_DATA, data);
                    mContext.startActivity(i);
                }else{
                    Intent i = new Intent(mContext, InputFormActivity.class);
                    i.putExtra(StringConstants.SENSOR_DATA,data.getSensors().get(0));
                    mContext.startActivity(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return matrixData.size();
    }
}
