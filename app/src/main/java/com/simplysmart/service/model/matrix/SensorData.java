package com.simplysmart.service.model.matrix;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shekhar on 20/10/16.
 */
public class SensorData implements Parcelable {

    private String site_name;
    private String metric;
    private String sensor_name;
    private String mandatory;
    private String unit;
    private String placeholder;
    private String data_type;
    private String photographic_evidence;
    private String no_of_times;
    private String duration_unit;
    private String duration_type;
    private String utility_identifier;
    private String tooltip;
    private boolean tare_weight;
    private boolean isChecked;

    public SensorData(){}

    protected SensorData(Parcel in) {
        site_name = in.readString();
        metric = in.readString();
        sensor_name = in.readString();
        mandatory = in.readString();
        unit = in.readString();
        placeholder = in.readString();
        data_type = in.readString();
        photographic_evidence = in.readString();
        no_of_times = in.readString();
        duration_unit = in.readString();
        duration_type = in.readString();
        utility_identifier = in.readString();
        tooltip = in.readString();
        tare_weight = in.readByte() != 0;
        isChecked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(site_name);
        dest.writeString(metric);
        dest.writeString(sensor_name);
        dest.writeString(mandatory);
        dest.writeString(unit);
        dest.writeString(placeholder);
        dest.writeString(data_type);
        dest.writeString(photographic_evidence);
        dest.writeString(no_of_times);
        dest.writeString(duration_unit);
        dest.writeString(duration_type);
        dest.writeString(utility_identifier);
        dest.writeString(tooltip);
        dest.writeByte((byte) (tare_weight ? 1 : 0));
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SensorData> CREATOR = new Creator<SensorData>() {
        @Override
        public SensorData createFromParcel(Parcel in) {
            return new SensorData(in);
        }

        @Override
        public SensorData[] newArray(int size) {
            return new SensorData[size];
        }
    };

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getSite_name() {
        return site_name;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getSensor_name() {
        return sensor_name;
    }

    public void setSensor_name(String sensor_name) {
        this.sensor_name = sensor_name;
    }

    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public String getPhotographic_evidence() {
        return photographic_evidence;
    }

    public void setPhotographic_evidence(String photographic_evidence) {
        this.photographic_evidence = photographic_evidence;
    }

    public String getNo_of_times() {
        return no_of_times;
    }

    public void setNo_of_times(String no_of_times) {
        this.no_of_times = no_of_times;
    }

    public String getDuration_unit() {
        return duration_unit;
    }

    public void setDuration_unit(String duration_unit) {
        this.duration_unit = duration_unit;
    }

    public String getDuration_type() {
        return duration_type;
    }

    public void setDuration_type(String duration_type) {
        this.duration_type = duration_type;
    }

    public String getUtility_identifier() {
        return utility_identifier;
    }

    public void setUtility_identifier(String utility_identifier) {
        this.utility_identifier = utility_identifier;
    }

    public boolean isTare_weight() {
        return tare_weight;
    }

    public void setTare_weight(boolean tare_weight) {
        this.tare_weight = tare_weight;
    }
}
