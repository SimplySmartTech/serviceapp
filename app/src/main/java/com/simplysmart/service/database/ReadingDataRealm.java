package com.simplysmart.service.database;

import android.os.Parcel;
import android.os.Parcelable;

import com.simplysmart.service.model.matrix.ReadingData;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by shailendrapsp on 3/11/16.
 */

public class ReadingDataRealm extends RealmObject implements Parcelable {

    private String utility_id;
    private String sensor_name;
    private String value;
    private String photographic_evidence_url;
    private String local_photo_url;
    private String date;
    private String unit;
    private String tare_weight;
    private long timestamp;
    private String unit_id;
    private boolean uploadedImage;

    public ReadingDataRealm() {
        super();
    }

    public ReadingDataRealm(ReadingData readingData) {
        super();
        setData(readingData);
    }

    protected ReadingDataRealm(Parcel in) {
        utility_id = in.readString();
        sensor_name = in.readString();
        value = in.readString();
        photographic_evidence_url = in.readString();
        local_photo_url = in.readString();
        date = in.readString();
        unit = in.readString();
        tare_weight = in.readString();
        timestamp = in.readLong();
        unit_id = in.readString();
        uploadedImage = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(utility_id);
        dest.writeString(sensor_name);
        dest.writeString(value);
        dest.writeString(photographic_evidence_url);
        dest.writeString(local_photo_url);
        dest.writeString(date);
        dest.writeString(unit);
        dest.writeString(tare_weight);
        dest.writeLong(timestamp);
        dest.writeString(unit_id);
        dest.writeByte((byte) (uploadedImage ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReadingDataRealm> CREATOR = new Creator<ReadingDataRealm>() {
        @Override
        public ReadingDataRealm createFromParcel(Parcel in) {
            return new ReadingDataRealm(in);
        }

        @Override
        public ReadingDataRealm[] newArray(int size) {
            return new ReadingDataRealm[size];
        }
    };

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

    public static void deleteAllReadings(String unit_id) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ReadingDataRealm> results = realm.where(ReadingDataRealm.class).equalTo("unit_id", unit_id).findAll();
        realm.beginTransaction();
        results.deleteAllFromRealm();
        realm.commitTransaction();
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

    public String getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(String unit_id) {
        this.unit_id = unit_id;
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

    public String getTare_weight() {
        return tare_weight;
    }

    public void setTare_weight(String tare_weight) {
        this.tare_weight = tare_weight;
    }
}
