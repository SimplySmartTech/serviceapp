package com.simplysmart.service.model.helpdesk;

import android.os.Parcel;
import android.os.Parcelable;

import com.simplysmart.service.model.user.Unit;
import com.simplysmart.service.model.user.User;

import java.util.ArrayList;

/**
 * Created by shekhar on 24/11/15.
 */
public class Complaint implements Parcelable {

    private String aasm_state;
    private String created_at;
    private String description;
    private String number;
    private String of_type;
    private String unit_info;
    private String category_name;
    private String sub_category_name;
    private String priority;
    private String state_action;
    private String assigned_name;
    private String id;
    private String category_short_name;

    private ArrayList<ComplaintChat> sorted_activities;
    private ArrayList<PermittedActions> permitted_events;

    private String resolved_reason;
    private String blocked_reason;
    private String rejected_reason;
    private String closed_reason;

    private Unit unit;

    private User resident;

    public Complaint() {

    }

    protected Complaint(Parcel in) {
        aasm_state = in.readString();
        created_at = in.readString();
        description = in.readString();
        number = in.readString();
        of_type = in.readString();
        unit_info = in.readString();
        category_name = in.readString();
        sub_category_name = in.readString();
        priority = in.readString();
        state_action = in.readString();
        assigned_name = in.readString();
        id = in.readString();
        category_short_name = in.readString();
        sorted_activities = in.createTypedArrayList(ComplaintChat.CREATOR);
        permitted_events = in.createTypedArrayList(PermittedActions.CREATOR);
        resolved_reason = in.readString();
        blocked_reason = in.readString();
        rejected_reason = in.readString();
        closed_reason = in.readString();
        unit = in.readParcelable(Unit.class.getClassLoader());
        resident = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(aasm_state);
        dest.writeString(created_at);
        dest.writeString(description);
        dest.writeString(number);
        dest.writeString(of_type);
        dest.writeString(unit_info);
        dest.writeString(category_name);
        dest.writeString(sub_category_name);
        dest.writeString(priority);
        dest.writeString(state_action);
        dest.writeString(assigned_name);
        dest.writeString(id);
        dest.writeString(category_short_name);
        dest.writeTypedList(sorted_activities);
        dest.writeTypedList(permitted_events);
        dest.writeString(resolved_reason);
        dest.writeString(blocked_reason);
        dest.writeString(rejected_reason);
        dest.writeString(closed_reason);
        dest.writeParcelable(unit, flags);
        dest.writeParcelable(resident, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Complaint> CREATOR = new Creator<Complaint>() {
        @Override
        public Complaint createFromParcel(Parcel in) {
            return new Complaint(in);
        }

        @Override
        public Complaint[] newArray(int size) {
            return new Complaint[size];
        }
    };

    public String getCategory_short_name() {
        return category_short_name;
    }

    public void setCategory_short_name(String category_short_name) {
        this.category_short_name = category_short_name;
    }

    public User getResident() {
        return resident;
    }

    public void setResident(User resident) {
        this.resident = resident;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getAasm_state() {
        return aasm_state;
    }

    public void setAasm_state(String aasm_state) {
        this.aasm_state = aasm_state;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOf_type() {
        return of_type;
    }

    public void setOf_type(String of_type) {
        this.of_type = of_type;
    }

    public String getUnit_info() {
        return unit_info;
    }

    public void setUnit_info(String unit_info) {
        this.unit_info = unit_info;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getSub_category_name() {
        return sub_category_name;
    }

    public void setSub_category_name(String sub_category_name) {
        this.sub_category_name = sub_category_name;
    }

    public String getAssigned_name() {
        return assigned_name;
    }

    public void setAssigned_name(String assigned_name) {
        this.assigned_name = assigned_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<ComplaintChat> getSorted_activities() {
        return sorted_activities;
    }

    public void setSorted_activities(ArrayList<ComplaintChat> sorted_activities) {
        this.sorted_activities = sorted_activities;
    }

    public ArrayList<PermittedActions> getPermittedActions() {
        return permitted_events;
    }

    public void setPermittedActions(ArrayList<PermittedActions> permittedActions) {
        this.permitted_events = permittedActions;
    }


    public String getState_action() {
        return state_action;
    }

    public void setState_action(String state_action) {
        this.state_action = state_action;
    }

    public ArrayList<PermittedActions> getPermitted_events() {
        return permitted_events;
    }

    public void setPermitted_events(ArrayList<PermittedActions> permitted_events) {
        this.permitted_events = permitted_events;
    }

    public String getResolved_reason() {
        return resolved_reason;
    }

    public void setResolved_reason(String resolved_reason) {
        this.resolved_reason = resolved_reason;
    }

    public String getBlocked_reason() {
        return blocked_reason;
    }

    public void setBlocked_reason(String blocked_reason) {
        this.blocked_reason = blocked_reason;
    }

    public String getRejected_reason() {
        return rejected_reason;
    }

    public void setRejected_reason(String rejected_reason) {
        this.rejected_reason = rejected_reason;
    }

    public String getClosed_reason() {
        return closed_reason;
    }

    public void setClosed_reason(String closed_reason) {
        this.closed_reason = closed_reason;
    }
}
