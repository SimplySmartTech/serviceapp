package com.simplysmart.service.model.user;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shekhar on 20/10/16.
 */
public class Company implements Parcelable {

    private String subdomain;
    private String name;
    private String logo_url;

    protected Company(Parcel in) {
        subdomain = in.readString();
        name = in.readString();
        logo_url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subdomain);
        dest.writeString(name);
        dest.writeString(logo_url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Company> CREATOR = new Creator<Company>() {
        @Override
        public Company createFromParcel(Parcel in) {
            return new Company(in);
        }

        @Override
        public Company[] newArray(int size) {
            return new Company[size];
        }
    };

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }
}
