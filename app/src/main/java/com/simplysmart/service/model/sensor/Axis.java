package com.simplysmart.service.model.sensor;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shekhar on 06/01/17.
 */
public class Axis implements Parcelable {

    private String x;
    private String y;

    protected Axis(Parcel in) {
        x = in.readString();
        y = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(x);
        dest.writeString(y);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Axis> CREATOR = new Creator<Axis>() {
        @Override
        public Axis createFromParcel(Parcel in) {
            return new Axis(in);
        }

        @Override
        public Axis[] newArray(int size) {
            return new Axis[size];
        }
    };

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }
}
