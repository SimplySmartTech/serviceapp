package com.simplysmart.service.model.helpdesk;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by shekhar on 12/8/15.
 */
public class HelpDeskData implements Parcelable {

    private Integer total;
    private ArrayList<ComplaintLists> complaints;
    private Complaint complaint;

    public Complaint getComplaint() {
        return complaint;
    }

    public void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }

    public ArrayList<ComplaintLists> getComplaints() {
        return complaints;
    }

    public void setComplaints(ArrayList<ComplaintLists> complaints) {
        this.complaints = complaints;
    }

    protected HelpDeskData(Parcel in) {
        complaints = in.createTypedArrayList(ComplaintLists.CREATOR);
    }

    public static final Creator<HelpDeskData> CREATOR = new Creator<HelpDeskData>() {
        @Override
        public HelpDeskData createFromParcel(Parcel in) {
            return new HelpDeskData(in);
        }

        @Override
        public HelpDeskData[] newArray(int size) {
            return new HelpDeskData[size];
        }
    };

    public ArrayList<ComplaintLists> getComplaintLists() {
        return complaints;
    }

    public void setComplaintLists(ArrayList<ComplaintLists> complaints) {
        this.complaints = complaints;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(complaints);
        dest.writeParcelable(complaint,flags);
    }


    public boolean hasComplaints() {
        return complaints != null && complaints.size() > 0;

    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }


}