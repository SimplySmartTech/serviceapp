package com.simplysmart.service.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.simplysmart.service.model.matrix.SensorData;

import java.util.List;

/**
 * Created by shailendrapsp on 22/12/16.
 */

@Table(name="sensor")
public class SensorTable extends Model {

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

    @Column(name = "tooltip")
    public String tooltip;

    @Column(name = "tare_weight")
    public boolean tare_weight;

    @Column(name = "isChecked")
    public boolean isChecked;

    public SensorTable(){
        super();
    }

    public SensorTable(SensorData sensorData){
        super();
        this.site_name = sensorData.getSite_name();
        this.metric = sensorData.getMetric();
        this.sensor_name=sensorData.getSensor_name();
        this.mandatory=sensorData.getMandatory();
        this.unit=sensorData.getUnit();
        this.data_type=sensorData.getData_type();
        this.photographic_evidence=sensorData.getPhotographic_evidence();
        this.no_of_times=sensorData.getNo_of_times();
        this.duration_unit=sensorData.getDuration_unit();
        this.duration_type=sensorData.getDuration_type();
        this.utility_identifier = sensorData.getUtility_identifier();

        if(sensorData.getTooltip()!=null) {
            this.tooltip = sensorData.getTooltip();
        }
    }

    public static SensorTable getSensorInfo(String utility_identifier,String sensor_name){
        return new Select()
                .from(SensorTable.class)
                .where("utility_identifier = ?",utility_identifier)
                .where("sensor_name = ?",sensor_name)
                .executeSingle();
    }

    public static SensorTable getSensorInfo(String utility_identifier){
        return new Select()
                .from(SensorTable.class)
                .where("utility_identifier = ?",utility_identifier)
                .executeSingle();
    }

    public static List<SensorTable> getSensorList(String utility_identifier){
        return new Select()
                .from(SensorTable.class)
                .where("utility_identifier = ?",utility_identifier)
                .execute();
    }

}
