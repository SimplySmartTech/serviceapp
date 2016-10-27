package com.simplysmart.service.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
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
    private Typeface textTypeface;

    public MatrixListAdapter(Context context, ArrayList<MatrixData> data) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        matrixData = data;

        textTypeface = Typeface.createFromAsset(mContext.getAssets(), "fontawesome-webfont.ttf");
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
            holder.iconLabel = (TextView) convertView.findViewById(R.id.iconLabel);
            holder.clickArrow = (ImageView) convertView.findViewById(R.id.clickArrow);
            holder.topSeparator = (View)convertView.findViewById(R.id.topSeparator);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }

        if (matrixData.get(groupPosition).getType() != null) {
            holder.typeName.setText(matrixData.get(groupPosition).getType().toUpperCase());
        } else {
            holder.typeName.setText("");
        }

        if (isExpanded) {
            holder.clickArrow.setImageResource(R.drawable.gal_ic_arrow_up);
            holder.iconLabel.setTextColor(mContext.getResources().getColor(R.color.accent));
            holder.typeName.setTextColor(mContext.getResources().getColor(R.color.accent));
            holder.topSeparator.setVisibility(View.VISIBLE);
        } else {
            holder.clickArrow.setImageResource(R.drawable.gal_ic_arrow_down);
            holder.iconLabel.setTextColor(mContext.getResources().getColor(R.color.bw_color_black));
            holder.typeName.setTextColor(mContext.getResources().getColor(R.color.bw_color_black));
            holder.topSeparator.setVisibility(View.GONE);
        }

        if (matrixData.get(groupPosition).getIcon() != null && !matrixData.get(groupPosition).getIcon().equalsIgnoreCase("")) {
            String unicode = "&#x" + matrixData.get(groupPosition).getIcon() + ";";
            holder.iconLabel.setText(Html.fromHtml(unicode));
            holder.iconLabel.setTypeface(textTypeface);
        } else {
            holder.iconLabel.setText("");
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
            holder.bottomSeparator = (View) convertView.findViewById(R.id.bottomSeparator);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }

        if (matrixData.get(groupPosition).getSensors().get(childPosition).getSensor_name() != null) {
            holder.sensorName.setText(matrixData.get(groupPosition).getSensors().get(childPosition).getSensor_name());
        } else {
            holder.sensorName.setText("");
        }

        if(isLastChild){
            holder.bottomSeparator.setVisibility(View.VISIBLE);
        }else {
            holder.bottomSeparator.setVisibility(View.GONE);
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
        private View bottomSeparator;
    }

    private static class GroupHolder {
        private TextView typeName;
        private TextView iconLabel;
        private ImageView clickArrow;
        private View topSeparator;
    }
}
