package com.simplysmart.service.model.sensor;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by shekhar on 02/01/17.
 */

public class SensorReadingGraphResponse implements Parcelable {

    private ArrayList<ArrayList<String>> data;
    private String reading_unit;
    private int total;
    private Axis axis;

    protected SensorReadingGraphResponse(Parcel in) {
        reading_unit = in.readString();
        total = in.readInt();
        axis = in.readParcelable(Axis.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reading_unit);
        dest.writeInt(total);
        dest.writeParcelable(axis, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SensorReadingGraphResponse> CREATOR = new Creator<SensorReadingGraphResponse>() {
        @Override
        public SensorReadingGraphResponse createFromParcel(Parcel in) {
            return new SensorReadingGraphResponse(in);
        }

        @Override
        public SensorReadingGraphResponse[] newArray(int size) {
            return new SensorReadingGraphResponse[size];
        }
    };

    public Axis getAxis() {
        return axis;
    }

    public void setAxis(Axis axis) {
        this.axis = axis;
    }

    public String getReading_unit() {
        return reading_unit;
    }

    public void setReading_unit(String reading_unit) {
        this.reading_unit = reading_unit;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ArrayList<ArrayList<String>> getData() {
        return data;
    }

    public void setData(ArrayList<ArrayList<String>> data) {
        this.data = data;
    }
}
