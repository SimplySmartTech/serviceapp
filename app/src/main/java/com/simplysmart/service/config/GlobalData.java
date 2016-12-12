package com.simplysmart.service.config;

import android.app.Application;
import android.content.Context;

import com.simplysmart.service.model.user.AccessPolicy;
import com.simplysmart.service.model.user.Unit;

import java.util.ArrayList;

import io.realm.Realm;


/**
 * Created by shekhar on 17/8/15.
 */
public class GlobalData extends Application {

    public static GlobalData mGlobalData;

    private String authToken;
    private String api_key;
    private String subDomain;
    private String userId;
    private String role_code;
    private String selectedUnitId;
    private String selectedUnit;

    private ArrayList<Unit> units;
    private AccessPolicy accessPolicy;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

    public AccessPolicy getAccessPolicy() {
        return accessPolicy;
    }

    public void setAccessPolicy(AccessPolicy accessPolicy) {
        this.accessPolicy = accessPolicy;
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public void setUnits(ArrayList<Unit> units) {
        this.units = units;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getSubDomain() {
        return subDomain;
    }

    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    public String getApiKey() {
        return this.api_key;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRole_code() {
        return role_code;
    }

    public void setRole_code(String role_code) {
        this.role_code = role_code;
    }

    public String getSelectedUnitId() {
        return selectedUnitId;
    }

    public void setSelectedUnitId(String selectedUnitId) {
        this.selectedUnitId = selectedUnitId;
    }

    public String getSelectedUnit() {
        return selectedUnit;
    }

    public void setSelectedUnit(String selectedUnit) {
        this.selectedUnit = selectedUnit;
    }

    public static GlobalData getInstance() {

        if (mGlobalData == null) {
            mGlobalData = new GlobalData();
        }
        return mGlobalData;
    }

}