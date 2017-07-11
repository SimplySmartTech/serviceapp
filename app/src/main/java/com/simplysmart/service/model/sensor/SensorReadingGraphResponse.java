package com.simplysmart.service.model.sensor;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by shekhar on 02/01/17.
 */

public class SensorReadingGraphResponse {

    private String reading_unit;
    private int total;
    private Axis axis;

    private LinkedHashMap<String, ArrayList<ArrayList<String>>> data;


    public String getReading_unit() {
        return reading_unit;
    }

    public void setReading_unit(String reading_unit) {
        this.reading_unit = reading_unit;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Axis getAxis() {
        return axis;
    }

    public void setAxis(Axis axis) {
        this.axis = axis;
    }

    public LinkedHashMap<String, ArrayList<ArrayList<String>>> getData() {
        return data;
    }

    public void setData(LinkedHashMap<String, ArrayList<ArrayList<String>>> data) {
        this.data = data;
    }
}
