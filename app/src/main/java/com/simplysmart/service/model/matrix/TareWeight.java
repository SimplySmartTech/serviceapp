package com.simplysmart.service.model.matrix;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shailendrapsp on 17/11/16.
 */

public class TareWeight implements Parcelable {
    private String name;
    private String value;
    private String info;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    protected TareWeight(Parcel in) {
        name = in.readString();
        value = in.readString();
        info = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(value);
        dest.writeString(info);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TareWeight> CREATOR = new Creator<TareWeight>() {
        @Override
        public TareWeight createFromParcel(Parcel in) {
            return new TareWeight(in);
        }

        @Override
        public TareWeight[] newArray(int size) {
            return new TareWeight[size];
        }
    };
}
