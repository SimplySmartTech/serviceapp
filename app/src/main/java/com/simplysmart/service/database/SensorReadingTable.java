package com.simplysmart.service.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.simplysmart.service.model.matrix.ReadingData;

/**
 * Created by shailendrapsp on 2/11/16.
 */

@Table(name="SENSOR_READING")
public class SensorReadingTable extends Model {

    @Column(name="utility_id")
    public String utility_id;

    @Column(name="sensor_name")
    public String sensor_name;

    @Column(name="value")
    public String value;

    @Column(name="photographic_evidence_url")
    public String photographic_evidence_url;

    public SensorReadingTable(){}

    public SensorReadingTable(ReadingData data){
        this.utility_id = data.getUtility_id();
        this.sensor_name = data.getSensor_name();
        this.value = data.getValue();
        this.photographic_evidence_url = data.getPhotographic_evidence_url();
    }
}
