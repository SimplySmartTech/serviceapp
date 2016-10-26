package com.simplysmart.service.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplysmart.service.R;
import com.simplysmart.service.model.matrix.MatrixData;

import java.util.ArrayList;

/**
 * Created by shekhar on 20/10/16.
 */

public class MatrixListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private ArrayList<MatrixData> matrixData;
    private final LayoutInflater inflater;

    public MatrixListAdapter(Context context, ArrayList<MatrixData> data) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        matrixData = data;
    }

    @Override
    public int getGroupCount() {
        return matrixData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return matrixData.get(groupPosition).getSensors().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        GroupHolder holder;
        if (convertView == null) {
            holder = new GroupHolder();
            convertView = inflater.inflate(R.layout.matrix_list_row_parent, parent, false);
            holder.typeName = (TextView) convertView.findViewById(R.id.typeName);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }

        if (matrixData.get(groupPosition).getType() != null) {
            holder.typeName.setText(matrixData.get(groupPosition).getType());
        } else {
            holder.typeName.setText("");
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ChildHolder holder;
        if (convertView == null) {
            holder = new ChildHolder();
            convertView = inflater.inflate(R.layout.matrix_list_row_child, parent, false);
            holder.sensorName = (TextView) convertView.findViewById(R.id.sensorName);
            holder.checkDoneImage = (ImageView) convertView.findViewById(R.id.checkDoneImage);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }

        if (matrixData.get(groupPosition).getSensors().get(childPosition).getSensor_name() != null) {
            holder.sensorName.setText(matrixData.get(groupPosition).getSensors().get(childPosition).getSensor_name());
        } else {
            holder.sensorName.setText("");
        }

        if (matrixData.get(groupPosition).getSensors().get(childPosition).isChecked()) {
            holder.checkDoneImage.setImageResource(R.drawable.tick_green);
        } else {
            holder.checkDoneImage.setImageResource(R.drawable.tick_grey);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    private static class ChildHolder {
        private TextView sensorName;
        private ImageView checkDoneImage;
    }

    private static class GroupHolder {
        private TextView typeName;
    }
}
