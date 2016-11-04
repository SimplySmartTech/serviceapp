package com.simplysmart.service.model.matrix;

import java.util.ArrayList;

/**
 * Created by shailendrapsp on 4/11/16.
 */

public class Sensor {
    private String sensor_name;
    private String utility_id;
    private ArrayList<Reading> readings;

    public String getSensor_name() {
        return sensor_name;
    }

    public void setSensor_name(String sensor_name) {
        this.sensor_name = sensor_name;
    }

    public String getUtility_id() {
        return utility_id;
    }

    public void setUtility_id(String utility_id) {
        this.utility_id = utility_id;
    }

    public ArrayList<Reading> getReadings() {
        return readings;
    }

    public void setReadings(ArrayList<Reading> readings) {
        this.readings = readings;
    }
}
