package com.simplysmart.service.model.helpdesk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shekhar on 12/8/15.
 */
public class HelpDeskResponse implements Parcelable {

    private HelpDeskData data;

    protected HelpDeskResponse(Parcel in) {
        data = in.readParcelable(HelpDeskData.class.getClassLoader());
    }

    public static final Creator<HelpDeskResponse> CREATOR = new Creator<HelpDeskResponse>() {
        @Override
        public HelpDeskResponse createFromParcel(Parcel in) {
            return new HelpDeskResponse(in);
        }

        @Override
        public HelpDeskResponse[] newArray(int size) {
            return new HelpDeskResponse[size];
        }
    };

    public HelpDeskData getData() {
        return data;
    }

    public void setData(HelpDeskData data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(data,flags);
    }


}
