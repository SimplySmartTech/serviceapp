package com.simplysmart.service.model.helpdesk;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


/**
 * Created by omkar on 19/04/17.
 */

public class ComplaintData implements Parcelable {

    private String aasm_state;

    private String department_id;

    private boolean escalated;

    private String unit_info;

    @SerializedName("id")
    private String complaintId;

    private String registered_at;

    public String getAasm_state() {
        return aasm_state;
    }

    public void setAasm_state(String aasm_state) {
        this.aasm_state = aasm_state;
    }

    public String getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(String department_id) {
        this.department_id = department_id;
    }

    public boolean isEscalated() {
        return escalated;
    }

    public void setEscalated(boolean escalated) {
        this.escalated = escalated;
    }

    public String getUnit_info() {
        return unit_info;
    }

    public void setUnit_info(String unit_info) {
        this.unit_info = unit_info;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getRegistered_at() {
        return registered_at;
    }

    public void setRegistered_at(String registered_at) {
        this.registered_at = registered_at;
    }

    public static Creator<ComplaintData> getCREATOR() {
        return CREATOR;
    }

    public ComplaintData(){

    }


    protected ComplaintData(Parcel in) {
        aasm_state = in.readString();
        department_id = in.readString();
        escalated = in.readByte() != 0;
        unit_info = in.readString();
        complaintId = in.readString();
        registered_at = in.readString();
    }

    public static final Creator<ComplaintData> CREATOR = new Creator<ComplaintData>() {
        @Override
        public ComplaintData createFromParcel(Parcel in) {
            return new ComplaintData(in);
        }

        @Override
        public ComplaintData[] newArray(int size) {
            return new ComplaintData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(aasm_state);
        dest.writeString(department_id);
        dest.writeByte((byte) (escalated ? 1 : 0));
        dest.writeString(unit_info);
        dest.writeString(complaintId);
        dest.writeString(registered_at);
    }
}
