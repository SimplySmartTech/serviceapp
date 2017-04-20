package com.simplysmart.service.model.helpdesk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by omkar on 20/04/17.
 */

public class ComplaintUpdateRequest implements Parcelable {
    public static Creator<ComplaintUpdateRequest> getCREATOR() {
        return CREATOR;
    }

    private Complaint complaint;


    public ComplaintUpdateRequest(){

    }


    protected ComplaintUpdateRequest(Parcel in) {
        complaint = in.readParcelable(Complaint.class.getClassLoader());
    }

    public static final Creator<ComplaintUpdateRequest> CREATOR = new Creator<ComplaintUpdateRequest>() {
        @Override
        public ComplaintUpdateRequest createFromParcel(Parcel in) {
            return new ComplaintUpdateRequest(in);
        }

        @Override
        public ComplaintUpdateRequest[] newArray(int size) {
            return new ComplaintUpdateRequest[size];
        }
    };

    public Complaint getComplaint() {
        return complaint;
    }

    public void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(complaint, flags);
    }
}
