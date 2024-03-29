package com.simplysmart.service.model.matrix;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by shekhar on 20/10/16.
 */

public class MatrixResponse implements Parcelable {

    private ArrayList<MatrixData> metrics;
    private ArrayList<TareWeight> tare_weights;

    protected MatrixResponse(Parcel in) {
        metrics = in.createTypedArrayList(MatrixData.CREATOR);
        tare_weights = in.createTypedArrayList(TareWeight.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(metrics);
        dest.writeTypedList(tare_weights);
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

    public ArrayList<MatrixData> getMetrics() {
        return metrics;
    }

    public void setMetrics(ArrayList<MatrixData> metrics) {
        this.metrics = metrics;
    }

    public ArrayList<MatrixData> getData() {
        return metrics;
    }

    public void setData(ArrayList<MatrixData> data) {
        this.metrics = data;
    }

    public ArrayList<TareWeight> getTare_weights() {
        return tare_weights;
    }

    public void setTare_weights(ArrayList<TareWeight> tare_weights) {
        this.tare_weights = tare_weights;
    }

}
