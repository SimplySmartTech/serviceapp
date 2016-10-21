package com.simplysmart.service.model.user;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shekhar on 12/08/16.
 */
public class AccessPolicy implements Parcelable {

    private boolean electricity;
    private boolean water;
    private boolean helpdesk;
    private boolean planner;
    private boolean ewallet;
    private String background;
    private String logo_url;

    protected AccessPolicy(Parcel in) {
        electricity = in.readByte() != 0;
        water = in.readByte() != 0;
        helpdesk = in.readByte() != 0;
        planner = in.readByte() != 0;
        ewallet = in.readByte() != 0;
        background = in.readString();
        logo_url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (electricity ? 1 : 0));
        dest.writeByte((byte) (water ? 1 : 0));
        dest.writeByte((byte) (helpdesk ? 1 : 0));
        dest.writeByte((byte) (planner ? 1 : 0));
        dest.writeByte((byte) (ewallet ? 1 : 0));
        dest.writeString(background);
        dest.writeString(logo_url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AccessPolicy> CREATOR = new Creator<AccessPolicy>() {
        @Override
        public AccessPolicy createFromParcel(Parcel in) {
            return new AccessPolicy(in);
        }

        @Override
        public AccessPolicy[] newArray(int size) {
            return new AccessPolicy[size];
        }
    };

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public boolean isElectricity() {
        return electricity;
    }

    public void setElectricity(boolean electricity) {
        this.electricity = electricity;
    }

    public boolean isWater() {
        return water;
    }

    public void setWater(boolean water) {
        this.water = water;
    }

    public boolean isHelpdesk() {
        return helpdesk;
    }

    public void setHelpdesk(boolean helpdesk) {
        this.helpdesk = helpdesk;
    }

    public boolean isPlanner() {
        return planner;
    }

    public void setPlanner(boolean planner) {
        this.planner = planner;
    }

    public boolean isEwallet() {
        return ewallet;
    }

    public void setEwallet(boolean ewallet) {
        this.ewallet = ewallet;
    }
}
