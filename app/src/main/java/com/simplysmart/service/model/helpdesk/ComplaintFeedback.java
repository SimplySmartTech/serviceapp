package com.simplysmart.service.model.helpdesk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shekhar on 25/02/16.
 */
public class ComplaintFeedback implements Parcelable {

    private String state_action;
    private String closed_reason;
    private String is_resident_satisfied;

    public ComplaintFeedback(){

    }

    protected ComplaintFeedback(Parcel in) {
        state_action = in.readString();
        closed_reason = in.readString();
        is_resident_satisfied = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(state_action);
        dest.writeString(closed_reason);
        dest.writeString(is_resident_satisfied);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ComplaintFeedback> CREATOR = new Creator<ComplaintFeedback>() {
        @Override
        public ComplaintFeedback createFromParcel(Parcel in) {
            return new ComplaintFeedback(in);
        }

        @Override
        public ComplaintFeedback[] newArray(int size) {
            return new ComplaintFeedback[size];
        }
    };

    public String getState_action() {
        return state_action;
    }

    public void setState_action(String state_action) {
        this.state_action = state_action;
    }

    public String getClosed_reason() {
        return closed_reason;
    }

    public void setClosed_reason(String closed_reason) {
        this.closed_reason = closed_reason;
    }

    public String getIs_resident_satisfied() {
        return is_resident_satisfied;
    }

    public void setIs_resident_satisfied(String is_resident_satisfied) {
        this.is_resident_satisfied = is_resident_satisfied;
    }
}
