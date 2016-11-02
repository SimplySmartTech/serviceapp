package com.simplysmart.service.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.simplysmart.service.model.matrix.MatrixData;
import com.simplysmart.service.model.matrix.SensorData;

import java.util.List;

/**
 * Created by shailendrapsp on 2/11/16.
 */

@Table(name = "SENSOR_DATA")
public class SensorDataTable extends Model {

    @Column(name = "site_name")
    public String site_name;

    @Column(name = "metric")
    public String metric;

    @Column(name = "sensor_name")
    public String sensor_name;

    @Column(name = "mandatory")
    public String mandatory;

    @Column(name = "unit")
    public String unit;

    @Column(name = "data_type")
    public String data_type;

    @Column(name = "photographic_evidence")
    public String photographic_evidence;

    @Column(name = "no_of_times")
    public String no_of_times;

    @Column(name = "duration_unit")
    public String duration_unit;

    @Column(name = "duration_type")
    public String duration_type;

    @Column(name = "utility_identifier")
    public String utility_identifier;

    @Column(name = "isChecked")
    public boolean isChecked;

    public SensorDataTable() { }

    public SensorDataTable(SensorData sensorData){
        this.site_name = sensorData.getSite_name();
        this.metric = sensorData.getMetric();
        this.sensor_name = sensorData.getSensor_name();
        this.mandatory = sensorData.getMandatory();
        this.unit = sensorData.getUnit();
        this.data_type = sensorData.getData_type();
        this.photographic_evidence = sensorData.getPhotographic_evidence();
        this.no_of_times = sensorData.getNo_of_times();
        this.duration_unit = sensorData.getDuration_unit();
        this.duration_type = sensorData.getDuration_type();
        this.utility_identifier = sensorData.getUtility_identifier();
        this.isChecked = sensorData.isChecked();
    }

    public List<SensorDataTable> getSensors(String utility_id){
        return new Select()
                .from(SensorDataTable.class)
                .where("utility_identifier = ?",utility_id)
                .execute();
    }
}
