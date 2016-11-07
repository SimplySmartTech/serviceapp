package com.simplysmart.service.database;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by shailendrapsp on 7/11/16.
 */

public class ImageUploadObjectRealm extends RealmObject{
    private String localUrlPath;
    private String utility_id;
    private String sensor_name;
    private long timestamp;

    public ImageUploadObjectRealm() {
        super();
    }

    public static RealmList<ImageUploadObjectRealm> getAllImagesToUpload(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ImageUploadObjectRealm> results = realm.where(ImageUploadObjectRealm.class).findAll();
        RealmList<ImageUploadObjectRealm> list = new RealmList<>();
        if(results.size()>0){
            for(int i=0;i<results.size();i++){
                list.add(results.get(i));
            }
        }
        return list;
    }

    public String getLocalUrlPath() {
        return localUrlPath;
    }

    public void setLocalUrlPath(String localUrlPath) {
        this.localUrlPath = localUrlPath;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
