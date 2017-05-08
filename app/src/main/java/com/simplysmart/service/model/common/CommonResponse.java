package com.simplysmart.service.model.common;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shekhar on 23/07/16.
 */
public class CommonResponse implements Parcelable {

    private String id;
    private String message;

    protected CommonResponse(Parcel in) {
        id = in.readString();
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CommonResponse> CREATOR = new Creator<CommonResponse>() {
        @Override
        public CommonResponse createFromParcel(Parcel in) {
            return new CommonResponse(in);
        }

        @Override
        public CommonResponse[] newArray(int size) {
            return new CommonResponse[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
