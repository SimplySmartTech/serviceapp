package com.simplysmart.service.model.sensor;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shekhar on 6/12/16.
 */

public class SensorItem implements Parcelable {

    private String last_reading;
    private String last_reading_at;

    private String cumulative_today;
    private String cumulative_yesterday;

    private String unit_name;
    private String meter_no;
    private String site_name;
    private String sensor_name;
    private String description;

    private String unit = "";
    private String type;
    private int priority;
    private String key;

    private boolean inactive;
    private boolean active;
    private boolean unreachable;


    private String battery_voltage;

    protected SensorItem(Parcel in) {
        last_reading = in.readString();
        last_reading_at = in.readString();
        cumulative_today = in.readString();
        cumulative_yesterday = in.readString();
        unit_name = in.readString();
        meter_no = in.readString();
        site_name = in.readString();
        sensor_name = in.readString();
        description = in.readString();
        unit = in.readString();
        type = in.readString();
        priority = in.readInt();
        key = in.readString();
        inactive = in.readByte() != 0;
        active = in.readByte() != 0;
        unreachable = in.readByte() != 0;
        battery_voltage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(last_reading);
        dest.writeString(last_reading_at);
        dest.writeString(cumulative_today);
        dest.writeString(cumulative_yesterday);
        dest.writeString(unit_name);
        dest.writeString(meter_no);
        dest.writeString(site_name);
        dest.writeString(sensor_name);
        dest.writeString(description);
        dest.writeString(unit);
        dest.writeString(type);
        dest.writeInt(priority);
        dest.writeString(key);
        dest.writeByte((byte) (inactive ? 1 : 0));
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeByte((byte) (unreachable ? 1 : 0));
        dest.writeString(battery_voltage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SensorItem> CREATOR = new Creator<SensorItem>() {
        @Override
        public SensorItem createFromParcel(Parcel in) {
            return new SensorItem(in);
        }

        @Override
        public SensorItem[] newArray(int size) {
            return new SensorItem[size];
        }
    };

    public String getLast_reading() {
        return last_reading;
    }

    public void setLast_reading(String last_reading) {
        this.last_reading = last_reading;
    }

    public String getLast_reading_at() {
        return last_reading_at;
    }

    public void setLast_reading_at(String last_reading_at) {
        this.last_reading_at = last_reading_at;
    }

    public String getCumulative_today() {
        return cumulative_today;
    }

    public void setCumulative_today(String cumulative_today) {
        this.cumulative_today = cumulative_today;
    }

    public String getCumulative_yesterday() {
        return cumulative_yesterday;
    }

    public void setCumulative_yesterday(String cumulative_yesterday) {
        this.cumulative_yesterday = cumulative_yesterday;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public String getMeter_no() {
        return meter_no;
    }

    public void setMeter_no(String meter_no) {
        this.meter_no = meter_no;
    }

    public String getSite_name() {
        return site_name;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }

    public String getSensor_name() {
        return sensor_name;
    }

    public void setSensor_name(String sensor_name) {
        this.sensor_name = sensor_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isUnreachable() {
        return unreachable;
    }

    public void setUnreachable(boolean unreachable) {
        this.unreachable = unreachable;
    }

    public String getBattery_voltage() {
        return battery_voltage;
    }

    public void setBattery_voltage(String battery_voltage) {
        this.battery_voltage = battery_voltage;
    }
}
