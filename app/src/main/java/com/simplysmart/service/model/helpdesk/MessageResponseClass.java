package com.simplysmart.service.model.helpdesk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by omkar on 20/04/17.
 */

public class MessageResponseClass implements Parcelable {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    protected MessageResponseClass(Parcel in) {
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MessageResponseClass> CREATOR = new Creator<MessageResponseClass>() {
        @Override
        public MessageResponseClass createFromParcel(Parcel in) {
            return new MessageResponseClass(in);
        }

        @Override
        public MessageResponseClass[] newArray(int size) {
            return new MessageResponseClass[size];
        }
    };
}
