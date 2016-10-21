package com.simplysmart.service.model.matrix;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by shekhar on 20/10/16.
 */

public class MatrixResponse implements Parcelable {

    private ArrayList<MatrixData> data;

    protected MatrixResponse(Parcel in) {
        data = in.createTypedArrayList(MatrixData.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MatrixResponse> CREATOR = new Creator<MatrixResponse>() {
        @Override
        public MatrixResponse createFromParcel(Parcel in) {
            return new MatrixResponse(in);
        }

        @Override
        public MatrixResponse[] newArray(int size) {
            return new MatrixResponse[size];
        }
    };

    public ArrayList<MatrixData> getData() {
        return data;
    }

    public void setData(ArrayList<MatrixData> data) {
        this.data = data;
    }
}
