package com.simplysmart.service.model.matrix;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by shekhar on 20/10/16.
 * Updated by shekhar on 16/07/18.
 */
public class MatrixData implements Parcelable {

    private String type;
    private String utility_id;
    private String icon;
    private int order;
    private boolean mandatory;

    private ArrayList<SensorData> sensors;

    //New fields
    private String name;
    private String unit;
    private String identifier;
    private String metric;
    private boolean bucket_system;

    private ArrayList<VehicleType> vehicles;


    public MatrixData() {

    }

    protected MatrixData(Parcel in) {
        type = in.readString();
        utility_id = in.readString();
        icon = in.readString();
        order = in.readInt();
        mandatory = in.readByte() != 0;
        sensors = in.createTypedArrayList(SensorData.CREATOR);
        name = in.readString();
        unit = in.readString();
        identifier = in.readString();
        metric = in.readString();
        bucket_system = in.readByte() != 0;
        vehicles = in.createTypedArrayList(VehicleType.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(utility_id);
        dest.writeString(icon);
        dest.writeInt(order);
        dest.writeByte((byte) (mandatory ? 1 : 0));
        dest.writeTypedList(sensors);
        dest.writeString(name);
        dest.writeString(unit);
        dest.writeString(identifier);
        dest.writeString(metric);
        dest.writeByte((byte) (bucket_system ? 1 : 0));
        dest.writeTypedList(vehicles);
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

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

    public ArrayList<VehicleType> getVehicles() {
        return vehicles;
    }

    public void setVehicles(ArrayList<VehicleType> vehicles) {
        this.vehicles = vehicles;
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
}
