package com.simplysmart.service.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by shekhar on 17/8/15.
 */
public class User implements Parcelable {

    private String id;
    private String name;
    private String username;
    private String email;
    private String role_code;
    private String api_key;
    private String auth_token;
    private ArrayList<Unit> units;

    private Company company;

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        username = in.readString();
        email = in.readString();
        role_code = in.readString();
        api_key = in.readString();
        auth_token = in.readString();
        units = in.createTypedArrayList(Unit.CREATOR);
        company = in.readParcelable(Company.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(role_code);
        dest.writeString(api_key);
        dest.writeString(auth_token);
        dest.writeTypedList(units);
        dest.writeParcelable(company, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole_code() {
        return role_code;
    }

    public void setRole_code(String role_code) {
        this.role_code = role_code;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public void setUnits(ArrayList<Unit> units) {
        this.units = units;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}