package com.simplysmart.service.model.visitors;

import java.util.ArrayList;

/**
 * Created by shailendrapsp on 27/12/16.
 */

public class Visitors {
    private ArrayList<String> image_urls;
    private String details;
    private int number_of_person;
    private long time;

    public ArrayList<String> getImage_urls() {
        return image_urls;
    }

    public void setImage_urls(ArrayList<String> image_urls) {
        this.image_urls = image_urls;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getNumber_of_person() {
        return number_of_person;
    }

    public void setNumber_of_person(int number_of_person) {
        this.number_of_person = number_of_person;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
