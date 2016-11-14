package com.simplysmart.service.database;

import com.amazonaws.services.dynamodbv2.model.Select;
import com.simplysmart.service.model.matrix.ReadingData;

import java.io.File;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by shailendrapsp on 3/11/16.
 */

public class ReadingDataRealm extends RealmObject {

    private String utility_id;
    private String sensor_name;
    private String value;
    private String photographic_evidence_url;
    private String local_photo_url;
    private String date;
    private String unit;
    private long timestamp;
    private int id;
    private boolean uploadedImage;

    public ReadingDataRealm() {
        super();
    }

    public ReadingDataRealm(ReadingData readingData) {
        super();
        setData(readingData);
    }

    public void setData(ReadingData readingData) {
        this.utility_id = readingData.getUtility_id();
        this.sensor_name = readingData.getSensor_name();
        this.value = readingData.getValue();
        this.photographic_evidence_url = readingData.getPhotographic_evidence_url();
    }

    public static RealmList<ReadingDataRealm> findExistingReading(String utility_id, String sensor_name) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ReadingDataRealm> results = realm
                .where(ReadingDataRealm.class)
                .equalTo("utility_id", utility_id)
                .equalTo("sensor_name", sensor_name)
                .findAll();


        RealmList<ReadingDataRealm> realmList = new RealmList<>();
        for (int i = 0; i < results.size(); i++) {
            realmList.add(results.get(i));
        }
        return realmList;

    }

    public static RealmList<ReadingDataRealm> findAllForThisSensor(String utility_id, String sensor_name) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ReadingDataRealm> results = realm
                .where(ReadingDataRealm.class)
                .equalTo("utility_id", utility_id)
                .equalTo("sensor_name", sensor_name)
                .findAll();

        if (results.size() > 0) {
            RealmList<ReadingDataRealm> realmList = new RealmList<>();
            for (int i = 0; i < results.size(); i++) {
                realmList.add(results.get(i));
            }
            return realmList;
        } else {
            return null;
        }
    }


    public String getUtility_id() {
        return utility_id;
    }

    public void setUtility_id(String utility_id) {
        this.utility_id = utility_id;
    }

    public String getSensor_name() {
        return sensor_name;
    }

    public void setSensor_name(String sensor_name) {
        this.sensor_name = sensor_name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPhotographic_evidence_url() {
        return photographic_evidence_url;
    }

    public void setPhotographic_evidence_url(String photographic_evidence_url) {
        this.photographic_evidence_url = photographic_evidence_url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocal_photo_url() {
        return local_photo_url;
    }

    public void setLocal_photo_url(String local_photo_url) {
        this.local_photo_url = local_photo_url;
    }

    public boolean isUploadedImage() {
        return uploadedImage;
    }

    public void setUploadedImage(boolean uploadedImage) {
        this.uploadedImage = uploadedImage;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
