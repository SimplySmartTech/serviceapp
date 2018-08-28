package com.simplysmart.service.model.matrix;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shekhar on 17/07/18.
 */
public class MatrixReadingData implements Parcelable {

    private String subdomain;
    private String site_id;
    private String reading;
    private String identifier;
    private String timestamp;
    private String unit;

    private String sensor_name;

    public MatrixReadingData() {

    }

    protected MatrixReadingData(Parcel in) {
        subdomain = in.readString();
        site_id = in.readString();
        reading = in.readString();
        identifier = in.readString();
        timestamp = in.readString();
        unit = in.readString();
        sensor_name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subdomain);
        dest.writeString(site_id);
        dest.writeString(reading);
        dest.writeString(identifier);
        dest.writeString(timestamp);
        dest.writeString(unit);
        dest.writeString(sensor_name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MatrixReadingData> CREATOR = new Creator<MatrixReadingData>() {
        @Override
        public MatrixReadingData createFromParcel(Parcel in) {
            return new MatrixReadingData(in);
        }

        @Override
        public MatrixReadingData[] newArray(int size) {
            return new MatrixReadingData[size];
        }
    };

    public String getSensor_name() {
        return sensor_name;
    }

    public void setSensor_name(String sensor_name) {
        this.sensor_name = sensor_name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getReading() {
        return reading;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}