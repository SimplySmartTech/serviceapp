package com.simplysmart.service.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by shekhar on 17/8/15.
 */
public class LoginResponse implements Parcelable {

    private String message;
    private boolean authenticated;
    private LoginData data;
    private String subdomain;
    private ArrayList<CompanyList> company_list;

    protected LoginResponse(Parcel in) {
        message = in.readString();
        authenticated = in.readByte() != 0;
        data = in.readParcelable(LoginData.class.getClassLoader());
        subdomain = in.readString();
        company_list = in.createTypedArrayList(CompanyList.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeByte((byte) (authenticated ? 1 : 0));
        dest.writeParcelable(data, flags);
        dest.writeString(subdomain);
        dest.writeTypedList(company_list);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LoginResponse> CREATOR = new Creator<LoginResponse>() {
        @Override
        public LoginResponse createFromParcel(Parcel in) {
            return new LoginResponse(in);
        }

        @Override
        public LoginResponse[] newArray(int size) {
            return new LoginResponse[size];
        }
    };

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LoginData getData() {
        return data;
    }

    public void setData(LoginData data) {
        this.data = data;
    }

    public ArrayList<CompanyList> getCompany_list() {
        return company_list;
    }

    public void setCompany_list(ArrayList<CompanyList> company_list) {
        this.company_list = company_list;
    }
}
