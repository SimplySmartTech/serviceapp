package com.simplysmart.service.model.helpdesk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shekhar on 25/02/16.
 */
public class ComplaintFeedbackResponse implements Parcelable {

    private String message;
    private String id;

    protected ComplaintFeedbackResponse(Parcel in) {
        message = in.readString();
        id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ComplaintFeedbackResponse> CREATOR = new Creator<ComplaintFeedbackResponse>() {
        @Override
        public ComplaintFeedbackResponse createFromParcel(Parcel in) {
            return new ComplaintFeedbackResponse(in);
        }

        @Override
        public ComplaintFeedbackResponse[] newArray(int size) {
            return new ComplaintFeedbackResponse[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
