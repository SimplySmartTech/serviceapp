package com.simplysmart.service.model.helpdesk;

import android.os.Parcel;
import android.os.Parcelable;

import com.simplysmart.service.model.user.User;


/**
 * Created by shekhar on 24/11/15.
 */
public class ComplaintChat implements Parcelable {

    private String id;
    private String created_at;
    private String text;
    private String resource_type;
    private User resource;
    private String image_url;


    protected ComplaintChat(Parcel in) {
        id = in.readString();
        created_at = in.readString();
        text = in.readString();
        resource_type = in.readString();
        resource = in.readParcelable(User.class.getClassLoader());
        image_url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(created_at);
        dest.writeString(text);
        dest.writeString(resource_type);
        dest.writeParcelable(resource, flags);
        dest.writeString(image_url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ComplaintChat> CREATOR = new Creator<ComplaintChat>() {
        @Override
        public ComplaintChat createFromParcel(Parcel in) {
            return new ComplaintChat(in);
        }

        @Override
        public ComplaintChat[] newArray(int size) {
            return new ComplaintChat[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getResource_type() {
        return resource_type;
    }

    public void setResource_type(String resource_type) {
        this.resource_type = resource_type;
    }

    public User getResource() {
        return resource;
    }

    public void setResource(User resource) {
        this.resource = resource;
    }
}
