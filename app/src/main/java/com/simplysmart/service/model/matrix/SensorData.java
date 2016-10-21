package com.simplysmart.service.model.matrix;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shekhar on 20/10/16.
 */
public class SensorData implements Parcelable {

    private String name;
    private boolean mandatory;
    private String unit;
    private String data_type;
    private boolean photographic_evidence;
    private String no_of_times;
    private String duration_unit;
    private String duration_type;


    protected SensorData(Parcel in) {
        name = in.readString();
        mandatory = in.readByte() != 0;
        unit = in.readString();
        data_type = in.readString();
        photographic_evidence = in.readByte() != 0;
        no_of_times = in.readString();
        duration_unit = in.readString();
        duration_type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeByte((byte) (mandatory ? 1 : 0));
        dest.writeString(unit);
        dest.writeString(data_type);
        dest.writeByte((byte) (photographic_evidence ? 1 : 0));
        dest.writeString(no_of_times);
        dest.writeString(duration_unit);
        dest.writeString(duration_type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SensorData> CREATOR = new Creator<SensorData>() {
        @Override
        public SensorData createFromParcel(Parcel in) {
            return new SensorData(in);
        }

        @Override
        public SensorData[] newArray(int size) {
            return new SensorData[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public boolean isPhotographic_evidence() {
        return photographic_evidence;
    }

    public void setPhotographic_evidence(boolean photographic_evidence) {
        this.photographic_evidence = photographic_evidence;
    }

    public String getNo_of_times() {
        return no_of_times;
    }

    public void setNo_of_times(String no_of_times) {
        this.no_of_times = no_of_times;
    }

    public String getDuration_unit() {
        return duration_unit;
    }

    public void setDuration_unit(String duration_unit) {
        this.duration_unit = duration_unit;
    }

    public String getDuration_type() {
        return duration_type;
    }

    public void setDuration_type(String duration_type) {
        this.duration_type = duration_type;
    }
}
