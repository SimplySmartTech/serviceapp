package com.simplysmart.service.model.user;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shekhar on 10/8/15.
 */
public class Unit implements Parcelable {

    private String info;
    private String name;
    private String building;
    private String id;

    protected Unit(Parcel in) {
        info = in.readString();
        name = in.readString();
        building = in.readString();
        id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(info);
        dest.writeString(name);
        dest.writeString(building);
        dest.writeString(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Unit> CREATOR = new Creator<Unit>() {
        @Override
        public Unit createFromParcel(Parcel in) {
            return new Unit(in);
        }

        @Override
        public Unit[] newArray(int size) {
            return new Unit[size];
        }
    };

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
