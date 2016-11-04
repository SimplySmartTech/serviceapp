package com.simplysmart.service.model.matrix;

import java.util.ArrayList;

/**
 * Created by shailendrapsp on 4/11/16.
 */

public class Metric {
    private String type;
    private String utility_id;
    private ArrayList<Sensor> sensors;

    public String getUtility_id() {
        return utility_id;
    }

    public void setUtility_id(String utility_id) {
        this.utility_id = utility_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(ArrayList<Sensor> sensors) {
        this.sensors = sensors;
    }
}
