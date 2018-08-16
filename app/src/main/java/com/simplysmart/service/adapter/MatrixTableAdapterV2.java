package com.simplysmart.service.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simplysmart.service.R;
import com.simplysmart.service.activity.InputReadingFormActivityV2;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.dialog.AlertDialogStandard;
import com.simplysmart.service.model.matrix.MatrixData;
import com.simplysmart.service.viewholder.ParentViewHolder;

import java.util.ArrayList;

/**
 * Created by shekhar on 16/07/18.
 */

public class MatrixTableAdapterV2 extends RecyclerView.Adapter<ParentViewHolder> {
    private Context mContext;
    private ArrayList<MatrixData> matrixData;
    private boolean backdated;

    public MatrixTableAdapterV2(Context context, ArrayList<MatrixData> data, boolean backdated) {
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
    public void onBindViewHolder(ParentViewHolder holder, final int position) {

        final MatrixData data = matrixData.get(position);

        holder.typeName.setText(data.getName());
        holder.unit.setText(data.getUnit());

        holder.typeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == matrixData.size() - 1) {
                    AlertDialogStandard.newInstance(mContext.getString(R.string.app_name), "Add New Sensor", "", "OK")
                            .show(((Activity) mContext).getFragmentManager(), "add new");
                } else {
                    switchToInputReadingScreen(data);
                }
            }
        });

        holder.unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == matrixData.size() - 1) {
                    AlertDialogStandard.newInstance(mContext.getString(R.string.app_name), "Add New Sensor", "", "OK")
                            .show(((Activity) mContext).getFragmentManager(), "add new");
                } else {
                    switchToInputReadingScreen(data);
                }
            }
        });
    }

    private void switchToInputReadingScreen(MatrixData data) {
        Intent intent = new Intent(mContext, InputReadingFormActivityV2.class);
        intent.putExtra(StringConstants.METRIC_DATA, data);
        mContext.startActivity(intent);
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
