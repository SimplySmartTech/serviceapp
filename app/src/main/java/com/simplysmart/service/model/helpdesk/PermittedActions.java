package com.simplysmart.service.model.helpdesk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by omkar on 20/04/17.
 */

public class PermittedActions implements Parcelable {

    private String event;
    private boolean comment_required;

    protected PermittedActions(Parcel in) {
        event = in.readString();
        comment_required = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(event);
        dest.writeByte((byte) (comment_required ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PermittedActions> CREATOR = new Creator<PermittedActions>() {
        @Override
        public PermittedActions createFromParcel(Parcel in) {
            return new PermittedActions(in);
        }

        @Override
        public PermittedActions[] newArray(int size) {
            return new PermittedActions[size];
        }
    };

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }


    public boolean isComment_required() {
        return comment_required;
    }

    public void setComment_required(boolean comment_required) {
        this.comment_required = comment_required;
    }


}
