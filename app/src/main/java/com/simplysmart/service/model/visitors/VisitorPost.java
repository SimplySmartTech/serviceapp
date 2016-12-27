package com.simplysmart.service.model.visitors;

import java.util.ArrayList;

/**
 * Created by shailendrapsp on 27/12/16.
 */

public class VisitorPost {
    private String subdomain;
    private ArrayList<Visitors> visitors;

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public ArrayList<Visitors> getVisitors() {
        return visitors;
    }

    public void setVisitors(ArrayList<Visitors> visitors) {
        this.visitors = visitors;
    }
}
