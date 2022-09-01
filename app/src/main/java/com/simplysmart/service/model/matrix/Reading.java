package com.simplysmart.service.model.matrix;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shailendrapsp on 4/11/16.
 */

public class Reading implements Parcelable{
    private String tare_weight;
    private String value;
    private String photographic_evidence_url;
    private long timestamp;
    private String remark;
    private long updatedAt = 0;

    public Reading(){}


    protected Reading(Parcel in) {
        tare_weight = in.readString();
        value = in.readString();
        photographic_evidence_url = in.readString();
        timestamp = in.readLong();
        remark = in.readString();
        updatedAt = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tare_weight);
        dest.writeString(value);
        dest.writeString(photographic_evidence_url);
        dest.writeLong(timestamp);
        dest.writeString(remark);
        dest.writeLong(updatedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Reading> CREATOR = new Creator<Reading>() {
        @Override
        public Reading createFromParcel(Parcel in) {
            return new Reading(in);
        }

        @Override
        public Reading[] newArray(int size) {
            return new Reading[size];
        }
    };

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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTare_weight() {
        return tare_weight;
    }

    public void setTare_weight(String tare_weight) {
        this.tare_weight = tare_weight;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
