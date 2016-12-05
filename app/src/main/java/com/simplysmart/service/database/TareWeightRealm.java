package com.simplysmart.service.database;

import com.simplysmart.service.model.matrix.TareWeight;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by shailendrapsp on 17/11/16.
 */

public class TareWeightRealm extends RealmObject {
    private String unit_id;
    private String name;
    private String value;
    private String info;

    public TareWeightRealm() {
        super();
    }

    public TareWeightRealm(TareWeight tareWeight, String unit_id) {
        super();
        this.name = tareWeight.getName();
        this.value = tareWeight.getValue();
        this.info = tareWeight.getInfo();
        this.unit_id = unit_id;
    }

    public static RealmResults<TareWeightRealm> getTareWeights(String unit_id) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(TareWeightRealm.class).equalTo("unit_id", unit_id).findAll();
    }

    public static boolean alreadyExists(TareWeight tareWeight) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<TareWeightRealm> results = realm
                .where(TareWeightRealm.class)
                .equalTo("name", tareWeight.getName())
                .equalTo("value", tareWeight.getValue())
                .equalTo("info", tareWeight.getInfo())
                .findAll();

        return results.size() > 0;
    }

    public static void deleteAll() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<TareWeightRealm> tareWeightRealms = realm.where(TareWeightRealm.class).findAll();
        realm.beginTransaction();
        tareWeightRealms.deleteAllFromRealm();
        realm.commitTransaction();
    }

    public String getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(String unit_id) {
        this.unit_id = unit_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
