package com.simplysmart.service.model.helpdesk;

/**
 * Created by shekhar on 2/11/15.
 */
public class ComplaintRequest {

    private NewComplaint complaint;

    public NewComplaint getComplaint() {
        return complaint;
    }

    public void setComplaint(NewComplaint newComplaint) {
        this.complaint = newComplaint;
    }


    @Override
    public String toString() {
        return "ComplaintRequest{" +
                "complaint=" + complaint +
                '}';
    }
}
