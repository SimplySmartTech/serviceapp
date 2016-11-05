package com.simplysmart.service.database;

import com.simplysmart.service.model.matrix.MatrixData;
import com.simplysmart.service.model.matrix.SensorData;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by shailendrapsp on 3/11/16.
 */

public class MatrixDataRealm extends RealmObject {
    private String type;
    private String utility_id;
    private String icon;
    private RealmList<SensorDataRealm> sensors;

    public MatrixDataRealm(){
        super();
    }


    public MatrixDataRealm(MatrixData matrixData) {
        super();
        this.type = matrixData.getType();
        this.utility_id = matrixData.getUtility_id();
        this.icon = matrixData.getIcon();
    }

    public static boolean alreadyExists(String utility_id){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<MatrixDataRealm> results = realm
                .where(MatrixDataRealm.class)
                .equalTo("utility_id",utility_id)
                .findAll();
        if(results.size()>0){
            return true;
        }else {
            return false;
        }
    }

    public static RealmList<MatrixDataRealm> getAll(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<MatrixDataRealm> results = realm
                .where(MatrixDataRealm.class)
                .findAll();

        RealmList<MatrixDataRealm> list = new RealmList<>();
        if(results.size()>0){
            for(int i =0;i<results.size();i++){
                list.add(results.get(i));
            }
        }
        return list;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUtility_id() {
        return utility_id;
    }

    public void setUtility_id(String utility_id) {
        this.utility_id = utility_id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public RealmList<SensorDataRealm> getSensors() {
        return sensors;
    }

    public void setSensors(RealmList<SensorDataRealm> sensors) {
        this.sensors = sensors;
    }
}
