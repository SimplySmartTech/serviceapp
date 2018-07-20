package com.simplysmart.service.model.matrix;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shekhar on 16/07/18.
 */

public class ValueInputType implements Parcelable {

    private String name;
    private String identifier;
    private String metric;
    private boolean bucket_system;

    protected ValueInputType(Parcel in) {
        name = in.readString();
        identifier = in.readString();
        metric = in.readString();
        bucket_system = in.readByte() != 0;
    }

    public static final Creator<ValueInputType> CREATOR = new Creator<ValueInputType>() {
        @Override
        public ValueInputType createFromParcel(Parcel in) {
            return new ValueInputType(in);
        }

        @Override
        public ValueInputType[] newArray(int size) {
            return new ValueInputType[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public boolean isBucket_system() {
        return bucket_system;
    }

    public void setBucket_system(boolean bucket_system) {
        this.bucket_system = bucket_system;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(identifier);
        dest.writeString(metric);
        dest.writeByte((byte) (bucket_system ? 1 : 0));
    }

    @Override
    public String toString() {
        return name;
    }
}
