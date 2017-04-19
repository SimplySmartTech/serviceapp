package com.simplysmart.service.model.helpdesk;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by shekhar on 24/11/15.
 */
public class Complaint implements Parcelable {

    private String aasm_state,created_at,description,number,of_type,unit_info,category_name,sub_category_name;
    private String assigned_name,id;
    private ArrayList<ComplaintChat> sorted_activities;

    protected Complaint(Parcel in) {
        aasm_state = in.readString();
        created_at = in.readString();
        description = in.readString();
        number = in.readString();
        of_type = in.readString();
        unit_info = in.readString();
        category_name = in.readString();
        sub_category_name = in.readString();
        assigned_name = in.readString();
        id = in.readString();
        sorted_activities = in.createTypedArrayList(ComplaintChat.CREATOR);
    }

    public static final Creator<Complaint> CREATOR = new Creator<Complaint>() {
        @Override
        public Complaint createFromParcel(Parcel in) {
            return new Complaint(in);
        }

        @Override
        public Complaint[] newArray(int size) {
            return new Complaint[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(aasm_state);
        dest.writeString(created_at);
        dest.writeString(description);
        dest.writeString(number);
        dest.writeString(of_type);
        dest.writeString(unit_info);
        dest.writeString(category_name);
        dest.writeString(sub_category_name);
        dest.writeString(assigned_name);
        dest.writeString(id);
        dest.writeList(sorted_activities);
    }

    public String getAasm_state() {
        return aasm_state;
    }

    public void setAasm_state(String aasm_state) {
        this.aasm_state = aasm_state;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOf_type() {
        return of_type;
    }

    public void setOf_type(String of_type) {
        this.of_type = of_type;
    }

    public String getUnit_info() {
        return unit_info;
    }

    public void setUnit_info(String unit_info) {
        this.unit_info = unit_info;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getSub_category_name() {
        return sub_category_name;
    }

    public void setSub_category_name(String sub_category_name) {
        this.sub_category_name = sub_category_name;
    }

    public String getAssigned_name() {
        return assigned_name;
    }

    public void setAssigned_name(String assigned_name) {
        this.assigned_name = assigned_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<ComplaintChat> getSorted_activities() {
        return sorted_activities;
    }

    public void setSorted_activities(ArrayList<ComplaintChat> sorted_activities) {
        this.sorted_activities = sorted_activities;
    }
}
