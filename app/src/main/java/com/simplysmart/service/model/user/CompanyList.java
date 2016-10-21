package com.simplysmart.service.model.user;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shekhar on 11/08/16.
 */
public class CompanyList implements Parcelable {

    private String name;
    private String subdomain;
    private String logo_url;

    protected CompanyList(Parcel in) {
        name = in.readString();
        subdomain = in.readString();
        logo_url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(subdomain);
        dest.writeString(logo_url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CompanyList> CREATOR = new Creator<CompanyList>() {
        @Override
        public CompanyList createFromParcel(Parcel in) {
            return new CompanyList(in);
        }

        @Override
        public CompanyList[] newArray(int size) {
            return new CompanyList[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }
}
