package com.simplysmart.service.model.category;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by shekhar on 27/04/17.
 */

public class Category implements Parcelable {

    private String name;
    private String id;
    private String department_id;
    private String of_type;
    private String short_name;
    private String department_name;
    private ArrayList<SubCategory> sub_categories;

    protected Category(Parcel in) {
        name = in.readString();
        id = in.readString();
        department_id = in.readString();
        of_type = in.readString();
        short_name = in.readString();
        department_name = in.readString();
        sub_categories = in.createTypedArrayList(SubCategory.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(department_id);
        dest.writeString(of_type);
        dest.writeString(short_name);
        dest.writeString(department_name);
        dest.writeTypedList(sub_categories);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(String department_id) {
        this.department_id = department_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOf_type() {
        return of_type;
    }

    public void setOf_type(String of_type) {
        this.of_type = of_type;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getDepartment_name() {
        return department_name;
    }

    public void setDepartment_name(String department_name) {
        this.department_name = department_name;
    }

    public ArrayList<SubCategory> getSub_categories() {
        return sub_categories;
    }

    public void setSub_categories(ArrayList<SubCategory> sub_categories) {
        this.sub_categories = sub_categories;
    }

    @Override
    public String toString() {
        return this.name; // What to display in the Spinner list.
    }
}
