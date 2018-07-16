package com.simplysmart.service.model.matrix;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by shekhar on 20/10/16.
 * Updated by shekhar on 16/07/18.
 */
public class MatrixData implements Parcelable  {

    private String type;
    private String utility_id;
    private String icon;
    private int order;
    private boolean mandatory;

    private ArrayList<SensorData> sensors;

    //New fields
    private String name;
    private ArrayList<ValueInputType> sub_menu;
    private ArrayList<VehicleType> vehicles;

    public MatrixData(){

    }

    protected MatrixData(Parcel in) {
        type = in.readString();
        utility_id = in.readString();
        icon = in.readString();
        order = in.readInt();
        mandatory = in.readByte() != 0;
        sensors = in.createTypedArrayList(SensorData.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(utility_id);
        dest.writeString(icon);
        dest.writeInt(order);
        dest.writeByte((byte) (mandatory ? 1 : 0));
        dest.writeTypedList(sensors);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MatrixData> CREATOR = new Creator<MatrixData>() {
        @Override
        public MatrixData createFromParcel(Parcel in) {
            return new MatrixData(in);
        }

        @Override
        public MatrixData[] newArray(int size) {
            return new MatrixData[size];
        }
    };

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUtility_id() {
        return utility_id;
    }

    public void setUtility_id(String utility_id) {
        this.utility_id = utility_id;
    }

    public ArrayList<SensorData> getSensors() {
        return sensors;
    }

    public void setSensors(ArrayList<SensorData> sensors) {
        this.sensors = sensors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ValueInputType> getSub_menu() {
        return sub_menu;
    }

    public void setSub_menu(ArrayList<ValueInputType> sub_menu) {
        this.sub_menu = sub_menu;
    }

    public ArrayList<VehicleType> getVehicles() {
        return vehicles;
    }

    public void setVehicles(ArrayList<VehicleType> vehicles) {
        this.vehicles = vehicles;
    }
}
