package com.simplysmart.service.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplysmart.service.R;
import com.simplysmart.service.activity.InputFormActivity;
import com.simplysmart.service.database.ReadingDataRealm;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by shailendrapsp on 4/11/16.
 */

public class ReadingListAdapter extends BaseAdapter {
    private ArrayList<ReadingDataRealm> readingsList;
    private Context mContext;

    public ReadingListAdapter(ArrayList<ReadingDataRealm> readingsList, Context mContext) {
        this.readingsList = readingsList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return readingsList.size();
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
            holder = new Holder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.reading_list_item,parent,false);
            holder.edit = (ImageButton)convertView.findViewById(R.id.edit);
            holder.photo = (ImageView)convertView.findViewById(R.id.photo);
            holder.reading = (TextView)convertView.findViewById(R.id.reading);
            holder.time = (TextView)convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        }else{
            holder = (Holder)convertView.getTag();
        }

        String value = readingsList.get(position).getValue()+"_"+readingsList.get(position).getUnit();
        holder.reading.setText(value);
        holder.time.setText(readingsList.get(position).getDate());
        setPic(holder.photo,readingsList.get(position).getLocal_photo_url());

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return convertView;
    }

    public void addElement(ReadingDataRealm dataRealm){
        readingsList.add(dataRealm);
        notifyDataSetChanged();
    }

    public class Holder{
        public ImageButton edit;
        public ImageView photo;
        public TextView time,reading;
    }

    private void setPic(ImageView view,String uri) {
        view.setImageBitmap(BitmapFactory.decodeFile(uri));
    }




}
