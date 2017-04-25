package com.simplysmart.service.model.notification;

import android.os.Parcel;
import android.os.Parcelable;

import com.simplysmart.service.model.user.Unit;


/**
 * Created by shekhar on 10/8/15.
 */
public class Notification implements Parcelable {

    private String category = "";
    private String sub_category = "";
    private String created_at;
    private String description;
    private String subject;
    private boolean unread;
    private String id;
    private Unit unit;
    private String noticeable_id;
    private String noticeable_type;

    protected Notification(Parcel in) {
        category = in.readString();
        sub_category = in.readString();
        created_at = in.readString();
        description = in.readString();
        subject = in.readString();
        unread = in.readByte() != 0;
        id = in.readString();
        unit = in.readParcelable(Unit.class.getClassLoader());
        noticeable_id = in.readString();
        noticeable_type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(category);
        dest.writeString(sub_category);
        dest.writeString(created_at);
        dest.writeString(description);
        dest.writeString(subject);
        dest.writeByte((byte) (unread ? 1 : 0));
        dest.writeString(id);
        dest.writeParcelable(unit, flags);
        dest.writeString(noticeable_id);
        dest.writeString(noticeable_type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    public String getNoticeable_id() {
        return noticeable_id;
    }

    public void setNoticeable_id(String noticeable_id) {
        this.noticeable_id = noticeable_id;
    }

    public String getNoticeable_type() {
        return noticeable_type;
    }

    public void setNoticeable_type(String noticeable_type) {
        this.noticeable_type = noticeable_type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSub_category() {
        return sub_category;
    }

    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
}
