package com.simplysmart.service.model.helpdesk;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by shekhar on 2/11/15.
 */
public class NewComplaint implements Parcelable {

    private String description;
    private String priority;
    private String of_type;

    private String resident_id;
    private String category_id;
    private String sub_category_id;

    private ArrayList<String> assets;

    private String unit_id;
    private String unit_info;

    private String parent_ticket;
    private boolean is_dependant;

    public NewComplaint() {
    }

    protected NewComplaint(Parcel in) {
        description = in.readString();
        priority = in.readString();
        of_type = in.readString();
        resident_id = in.readString();
        category_id = in.readString();
        sub_category_id = in.readString();
        assets = in.createStringArrayList();
        unit_id = in.readString();
        unit_info = in.readString();
        parent_ticket = in.readString();
        is_dependant = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(priority);
        dest.writeString(of_type);
        dest.writeString(resident_id);
        dest.writeString(category_id);
        dest.writeString(sub_category_id);
        dest.writeStringList(assets);
        dest.writeString(unit_id);
        dest.writeString(unit_info);
        dest.writeString(parent_ticket);
        dest.writeByte((byte) (is_dependant ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NewComplaint> CREATOR = new Creator<NewComplaint>() {
        @Override
        public NewComplaint createFromParcel(Parcel in) {
            return new NewComplaint(in);
        }

        @Override
        public NewComplaint[] newArray(int size) {
            return new NewComplaint[size];
        }
    };

    public String getUnit_info() {
        return unit_info;
    }

    public void setUnit_info(String unit_info) {
        this.unit_info = unit_info;
    }

    public String getParent_ticket() {
        return parent_ticket;
    }

    public void setParent_ticket(String parent_ticket) {
        this.parent_ticket = parent_ticket;
    }

    public boolean is_dependant() {
        return is_dependant;
    }

    public void setIs_dependant(boolean is_dependant) {
        this.is_dependant = is_dependant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getOf_type() {
        return of_type;
    }

    public void setOf_type(String of_type) {
        this.of_type = of_type;
    }

    public String getResident_id() {
        return resident_id;
    }

    public void setResident_id(String resident_id) {
        this.resident_id = resident_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getSub_category_id() {
        return sub_category_id;
    }

    public void setSub_category_id(String sub_category_id) {
        this.sub_category_id = sub_category_id;
    }

    public String getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(String unit_id) {
        this.unit_id = unit_id;
    }

    public ArrayList<String> getAssets() {
        return assets;
    }

    public void setAssets(ArrayList<String> assets) {
        this.assets = assets;
    }
}
