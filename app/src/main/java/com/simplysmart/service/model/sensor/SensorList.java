package com.simplysmart.service.model.sensor;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by shekhar on 6/12/16.
 */

public class SensorList implements Parcelable {

    private ArrayList<SensorItem> data;
    private String date;

    protected SensorList(Parcel in) {
        data = in.createTypedArrayList(SensorItem.CREATOR);
        date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(data);
        dest.writeString(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SensorList> CREATOR = new Creator<SensorList>() {
        @Override
        public SensorList createFromParcel(Parcel in) {
            return new SensorList(in);
        }

        @Override
        public SensorList[] newArray(int size) {
            return new SensorList[size];
        }
    };

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<SensorItem> getData() {
        return data;
    }

    public void setData(ArrayList<SensorItem> data) {
        this.data = data;
    }
}
