package com.simplysmart.service.model.matrix;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shekhar on 21/10/16.
 */
public class ReadingData implements Parcelable {

    private int local_id;
    private String utility_id;
    private String sensor_name;
    private String value;
    private String photographic_evidence_url;
    private String tare_weight;

    public ReadingData(){
    }

    protected ReadingData(Parcel in) {
        local_id = in.readInt();
        utility_id = in.readString();
        sensor_name = in.readString();
        value = in.readString();
        photographic_evidence_url = in.readString();
        tare_weight = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(local_id);
        dest.writeString(utility_id);
        dest.writeString(sensor_name);
        dest.writeString(value);
        dest.writeString(photographic_evidence_url);
        dest.writeString(tare_weight);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReadingData> CREATOR = new Creator<ReadingData>() {
        @Override
        public ReadingData createFromParcel(Parcel in) {
            return new ReadingData(in);
        }

        @Override
        public ReadingData[] newArray(int size) {
            return new ReadingData[size];
        }
    };

    public String getUtility_id() {
        return utility_id;
    }

    public void setUtility_id(String utility_id) {
        this.utility_id = utility_id;
    }

    public String getSensor_name() {
        return sensor_name;
    }

    public void setSensor_name(String sensor_name) {
        this.sensor_name = sensor_name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPhotographic_evidence_url() {
        return photographic_evidence_url;
    }

    public void setPhotographic_evidence_url(String photographic_evidence_url) {
        this.photographic_evidence_url = photographic_evidence_url;
    }

    public int getLocal_id() {
        return local_id;
    }

    public void setLocal_id(int local_id) {
        this.local_id = local_id;
    }

    public String getTare_weight() {
        return tare_weight;
    }

    public void setTare_weight(String tare_weight) {
        this.tare_weight = tare_weight;
    }
}
