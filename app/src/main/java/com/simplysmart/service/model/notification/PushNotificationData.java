package com.simplysmart.service.model.notification;

import android.os.Parcel;
import android.os.Parcelable;

import com.simplysmart.service.model.helpdesk.Complaint;
import com.simplysmart.service.model.helpdesk.ComplaintChat;


/**
 * Created by shekhar on 24/04/17.
 */

public class PushNotificationData implements Parcelable{

    private Notification notification;
    private ComplaintChat activity;
    private Complaint complaint;

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public ComplaintChat getActivity() {
        return activity;
    }

    public void setActivity(ComplaintChat activity) {
        this.activity = activity;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }

    protected PushNotificationData(Parcel in) {
        notification = in.readParcelable(Notification.class.getClassLoader());
        activity = in.readParcelable(ComplaintChat.class.getClassLoader());
        complaint = in.readParcelable(Complaint.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(notification, flags);
        dest.writeParcelable(activity, flags);
        dest.writeParcelable(complaint, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PushNotificationData> CREATOR = new Creator<PushNotificationData>() {
        @Override
        public PushNotificationData createFromParcel(Parcel in) {
            return new PushNotificationData(in);
        }

        @Override
        public PushNotificationData[] newArray(int size) {
            return new PushNotificationData[size];
        }
    };
}
