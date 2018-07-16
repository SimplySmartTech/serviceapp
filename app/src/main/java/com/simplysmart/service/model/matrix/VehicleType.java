package com.simplysmart.service.model.matrix;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shekhar on 16/07/18.
 */

public class VehicleType implements Parcelable {

    private String name;
    private double capacity;

    protected VehicleType(Parcel in) {
        name = in.readString();
        capacity = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(capacity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VehicleType> CREATOR = new Creator<VehicleType>() {
        @Override
        public VehicleType createFromParcel(Parcel in) {
            return new VehicleType(in);
        }

        @Override
        public VehicleType[] newArray(int size) {
            return new VehicleType[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }
}
