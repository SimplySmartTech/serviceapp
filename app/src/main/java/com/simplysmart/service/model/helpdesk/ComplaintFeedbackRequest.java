package com.simplysmart.service.model.helpdesk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shekhar on 25/02/16.
 */
public class ComplaintFeedbackRequest implements Parcelable {

   private ComplaintFeedback complaint;

    public ComplaintFeedbackRequest(){

    }

    protected ComplaintFeedbackRequest(Parcel in) {
        complaint = in.readParcelable(ComplaintFeedback.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(complaint, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ComplaintFeedbackRequest> CREATOR = new Creator<ComplaintFeedbackRequest>() {
        @Override
        public ComplaintFeedbackRequest createFromParcel(Parcel in) {
            return new ComplaintFeedbackRequest(in);
        }

        @Override
        public ComplaintFeedbackRequest[] newArray(int size) {
            return new ComplaintFeedbackRequest[size];
        }
    };

    public ComplaintFeedback getComplaint() {
        return complaint;
    }

    public void setComplaint(ComplaintFeedback complaint) {
        this.complaint = complaint;
    }
}
