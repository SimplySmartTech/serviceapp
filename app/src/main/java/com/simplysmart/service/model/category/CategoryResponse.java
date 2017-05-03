package com.simplysmart.service.model.category;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by shekhar on 02/05/17.
 */

public class CategoryResponse implements Parcelable{

    private ArrayList<Category> categories;

    protected CategoryResponse(Parcel in) {
        categories = in.createTypedArrayList(Category.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(categories);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CategoryResponse> CREATOR = new Creator<CategoryResponse>() {
        @Override
        public CategoryResponse createFromParcel(Parcel in) {
            return new CategoryResponse(in);
        }

        @Override
        public CategoryResponse[] newArray(int size) {
            return new CategoryResponse[size];
        }
    };

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }
}
