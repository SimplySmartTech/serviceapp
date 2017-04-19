package com.simplysmart.service.model.helpdesk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chandrashekhar on 12/8/15.
 */
public class ComplaintDetailResponse implements Parcelable {

    private HelpDeskData data;

    protected ComplaintDetailResponse(Parcel in) {
        data = in.readParcelable(HelpDeskData.class.getClassLoader());
    }

    public static final Creator<ComplaintDetailResponse> CREATOR = new Creator<ComplaintDetailResponse>() {
        @Override
        public ComplaintDetailResponse createFromParcel(Parcel in) {
            return new ComplaintDetailResponse(in);
        }

        @Override
        public ComplaintDetailResponse[] newArray(int size) {
            return new ComplaintDetailResponse[size];
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
