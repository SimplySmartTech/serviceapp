package com.simplysmart.service.model.attendance;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shekhar on 26/12/16.
 */

public class Attendance implements Parcelable {

    private String image_url;
    private long time;
    private String coordinates;
    private String address;

    public Attendance() {

    }

    protected Attendance(Parcel in) {
        image_url = in.readString();
        time = in.readLong();
        coordinates = in.readString();
        address = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image_url);
        dest.writeLong(time);
        dest.writeString(coordinates);
        dest.writeString(address);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Attendance> CREATOR = new Creator<Attendance>() {
        @Override
        public Attendance createFromParcel(Parcel in) {
            return new Attendance(in);
        }

        @Override
        public Attendance[] newArray(int size) {
            return new Attendance[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
