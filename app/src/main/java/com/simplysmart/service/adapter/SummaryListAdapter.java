package com.simplysmart.service.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.simplysmart.service.R;
import com.simplysmart.service.model.common.Summary;

import java.util.ArrayList;

/**
 * Created by shailendrapsp on 4/11/16.
 */

public class SummaryListAdapter extends BaseAdapter {

    private ArrayList<Summary> data;
    private Context mContext;

    public SummaryListAdapter(ArrayList<Summary> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.summary_list_item,parent,false);
            holder = new Holder();
            holder.name = (TextView)convertView.findViewById(R.id.name);
            holder.value = (TextView)convertView.findViewById(R.id.value);
            holder.time = (TextView)convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        }else{
            holder = (Holder)convertView.getTag();
        }

        holder.name.setText(data.get(position).getName());
        holder.value.setText(data.get(position).getValue());
        holder.time.setText(data.get(position).getTime());

        return convertView;
    }

    public class Holder{
        public TextView time,name,value;
    }
}
