package com.simplysmart.service.model.helpdesk;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by omkar on 19/04/17.
 */

public class ComplaintsResponse implements Parcelable {

    ArrayList<ComplaintData> complaints;

    public ArrayList<ComplaintData> getComplaints() {
        return complaints;
    }

    public void setComplaints(ArrayList<ComplaintData> complaints) {
        this.complaints = complaints;
    }

    public static Creator<ComplaintsResponse> getCREATOR() {
        return CREATOR;
    }

    protected ComplaintsResponse(Parcel in) {
        complaints = in.createTypedArrayList(ComplaintData.CREATOR);
    }

    public static final Creator<ComplaintsResponse> CREATOR = new Creator<ComplaintsResponse>() {
        @Override
        public ComplaintsResponse createFromParcel(Parcel in) {
            return new ComplaintsResponse(in);
        }

        @Override
        public ComplaintsResponse[] newArray(int size) {
            return new ComplaintsResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(complaints);
    }
}
