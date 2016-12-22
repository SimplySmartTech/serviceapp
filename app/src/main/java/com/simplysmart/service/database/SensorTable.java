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

    @Column(name = "unit_id")
    public String site_name;

    @Column(name = "unit_id")
    public String metric;

    @Column(name = "unit_id")
    public String sensor_name;

    @Column(name = "unit_id")
    public String mandatory;

    @Column(name = "unit_id")
    public String unit;

    @Column(name = "unit_id")
    public String data_type;

    @Column(name = "unit_id")
    public String photographic_evidence;

    @Column(name = "unit_id")
    public String no_of_times;

    @Column(name = "unit_id")
    public String duration_unit;

    @Column(name = "unit_id")
    public String duration_type;

    @Column(name = "unit_id")
    public String utility_identifier;

    @Column(name = "unit_id")
    public String tooltip;

    @Column(name = "unit_id")
    public boolean tare_weight;

    @Column(name = "unit_id")
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
