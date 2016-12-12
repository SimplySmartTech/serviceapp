package com.simplysmart.service.database;

import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.model.matrix.MatrixData;
import com.simplysmart.service.model.matrix.SensorData;

import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by shailendrapsp on 3/11/16.
 */

public class MatrixDataRealm extends RealmObject {
    private String unit_id;
    private String type;
    private String utility_id;
    private String icon;
    private int order;
    private boolean mandatory;
    private RealmList<SensorDataRealm> sensors;

    public MatrixDataRealm() {
        super();
    }


    public MatrixDataRealm(MatrixData matrixData) {
        super();
        this.type = matrixData.getType();
        this.utility_id = matrixData.getUtility_id();
        this.icon = matrixData.getIcon();
        this.order = matrixData.getOrder();
        this.mandatory = matrixData.isMandatory();
        this.setUnit_id(GlobalData.getInstance().getSelectedUnitId());
    }

    public static boolean alreadyExists(String utility_id) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<MatrixDataRealm> results = realm
                .where(MatrixDataRealm.class)
                .equalTo("unit_id", GlobalData.getInstance().getSelectedUnitId())
                .equalTo("utility_id", utility_id)
                .findAll();
        if (results.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static ArrayList<MatrixData> getAll() {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<MatrixData> list = new ArrayList<>();

        RealmResults<MatrixDataRealm> result = realm
                .where(MatrixDataRealm.class)
                .equalTo("unit_id", GlobalData.getInstance().getSelectedUnitId())
                .findAll();
        if (result.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
                MatrixData matrixData = new MatrixData();
                matrixData.setIcon(result.get(i).getIcon());
                matrixData.setType(result.get(i).getType());
                matrixData.setUtility_id(result.get(i).getUtility_id());
                ArrayList<SensorData> sensors = new ArrayList<>();
                for (int j = 0; j < result.get(i).getSensors().size(); j++) {
                    SensorData sensorData = new SensorData(result.get(i).getSensors().get(j));
                    sensors.add(sensorData);
                }
                matrixData.setSensors(sensors);
                list.add(matrixData);
            }
        }
        return list;
    }

    public static boolean removeUnitData(String unit_id) {
        Realm realm = Realm.getDefaultInstance();

        RealmResults<MatrixDataRealm> results = realm
                .where(MatrixDataRealm.class)
                .equalTo("unit_id", unit_id)
                .findAll();

        for (MatrixDataRealm data : results) {
            RealmList<SensorDataRealm> sensorList = SensorDataRealm.getForUtilityId(data.getUtility_id());
            for (SensorDataRealm sensor : sensorList) {
                deleteReadings(sensor.getUtility_identifier(), sensor.getSensor_name());
            }
            deleteSensors(data.getUtility_id());
        }

        deleteMatrix(GlobalData.getInstance().getSelectedUnitId());

        return true;
    }

    private static void deleteReadings(final String utility_id, final String sensor_name) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<ReadingDataRealm> results = realm
                .where(ReadingDataRealm.class)
                .equalTo("utility_id", utility_id)
                .equalTo("sensor_name", sensor_name)
                .findAll();

        for (ReadingDataRealm dataRealm : results) {
            try {
                if (dataRealm.getLocal_photo_url() != null && !dataRealm.getLocal_photo_url().equals("")) {
                    File file = new File(dataRealm.getLocal_photo_url());
                    if (file.exists()) {
                        try {
                            file.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        results.deleteAllFromRealm();
        realm.commitTransaction();
    }


    private static void deleteSensors(final String utility_id) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<SensorDataRealm> results = realm
                .where(SensorDataRealm.class)
                .equalTo("utility_identifier", utility_id)
                .findAll();
        results.deleteAllFromRealm();
        realm.commitTransaction();
    }

    private static void deleteMatrix(final String unit_id) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<MatrixDataRealm> results = realm
                .where(MatrixDataRealm.class)
                .equalTo("unit_id", unit_id)
                .findAll();
        results.deleteAllFromRealm();
        realm.commitTransaction();
    }

    public void setData(MatrixDataRealm matrixData){
        this.type = matrixData.getType();
        this.utility_id = matrixData.getUtility_id();
        this.icon = matrixData.getIcon();
        this.order = matrixData.getOrder();
        this.mandatory = matrixData.isMandatory();
        this.setUnit_id(GlobalData.getInstance().getSelectedUnitId());
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

    public String getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(String unit_id) {
        this.unit_id = unit_id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
}
