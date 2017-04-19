package com.simplysmart.service.model.helpdesk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shekhar on 24/11/15.
 */
public class ComplaintChatResponse implements Parcelable {

    private ComplaintChat activity;

    protected ComplaintChatResponse(Parcel in) {
        activity = in.readParcelable(ComplaintChat.class.getClassLoader());
    }

    public static final Creator<ComplaintChatResponse> CREATOR = new Creator<ComplaintChatResponse>() {
        @Override
        public ComplaintChatResponse createFromParcel(Parcel in) {
            return new ComplaintChatResponse(in);
        }

        @Override
        public ComplaintChatResponse[] newArray(int size) {
            return new ComplaintChatResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(activity,flags);
    }

    public ComplaintChat getActivity() {
        return activity;
    }

    public void setActivity(ComplaintChat activity) {
        this.activity = activity;
    }
}
