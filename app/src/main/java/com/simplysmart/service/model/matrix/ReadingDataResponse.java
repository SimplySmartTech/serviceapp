package com.simplysmart.service.model.matrix;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by shekhar on 21/10/16.
 */
public class ReadingDataResponse implements Parcelable {

    private ArrayList<MatrixReadingData> readings;

    public ReadingDataResponse() {

    }

    public ReadingDataResponse(ArrayList<MatrixReadingData> readings) {
        this.readings = readings;
    }

    public ReadingDataResponse(Parcel in) {
        readings = in.createTypedArrayList(MatrixReadingData.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(readings);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReadingDataResponse> CREATOR = new Creator<ReadingDataResponse>() {
        @Override
        public ReadingDataResponse createFromParcel(Parcel in) {
            return new ReadingDataResponse(in);
        }

        @Override
        public ReadingDataResponse[] newArray(int size) {
            return new ReadingDataResponse[size];
        }
    };

    public ArrayList<MatrixReadingData> getReadings() {
        return readings;
    }

    public void setReadings(ArrayList<MatrixReadingData> readings) {
        this.readings = readings;
    }
}
