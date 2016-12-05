package com.simplysmart.service.database;

import com.simplysmart.service.model.matrix.SensorData;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by shailendrapsp on 3/11/16.
 */

public class SensorDataRealm extends RealmObject {
    private String site_name;
    private String metric;
    private String sensor_name;
    private String mandatory;
    private String unit;
    private String data_type;
    private String photographic_evidence;
    private String no_of_times;
    private String duration_unit;
    private String duration_type;
    private String utility_identifier;
    private boolean tare_weight;
    private boolean isChecked;

    public SensorDataRealm() {
    }

    public SensorDataRealm(SensorData sensorData) {
        super();
        setData(sensorData);
    }

    public void setData(SensorData sensorData) {
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
        this.tare_weight = sensorData.isTare_weight();
    }

    public static boolean alreadyExists(String sensor_name, String utility_identifier) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<SensorDataRealm> results = realm
                .where(SensorDataRealm.class)
                .equalTo("sensor_name", sensor_name)
                .equalTo("utility_identifier", utility_identifier)
                .findAll();
        if (results.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static RealmList<SensorDataRealm> getForUtilityId(String utility_id) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<SensorDataRealm> results = realm
                .where(SensorDataRealm.class)
                .equalTo("utility_identifier", utility_id)
                .findAll();

        RealmList<SensorDataRealm> list = new RealmList<>();

        if (results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {
                list.add(results.get(i));
            }
        }
        return list;
    }


    public String getSite_name() {
        return site_name;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getSensor_name() {
        return sensor_name;
    }

    public void setSensor_name(String sensor_name) {
        this.sensor_name = sensor_name;
    }

    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public String getPhotographic_evidence() {
        return photographic_evidence;
    }

    public void setPhotographic_evidence(String photographic_evidence) {
        this.photographic_evidence = photographic_evidence;
    }

    public String getNo_of_times() {
        return no_of_times;
    }

    public void setNo_of_times(String no_of_times) {
        this.no_of_times = no_of_times;
    }

    public String getDuration_unit() {
        return duration_unit;
    }

    public void setDuration_unit(String duration_unit) {
        this.duration_unit = duration_unit;
    }

    public String getDuration_type() {
        return duration_type;
    }

    public void setDuration_type(String duration_type) {
        this.duration_type = duration_type;
    }

    public String getUtility_identifier() {
        return utility_identifier;
    }

    public void setUtility_identifier(String utility_identifier) {
        this.utility_identifier = utility_identifier;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isTare_weight() {
        return tare_weight;
    }

    public void setTare_weight(boolean tare_weight) {
        this.tare_weight = tare_weight;
    }
}
