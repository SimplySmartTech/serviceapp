package com.simplysmart.service.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by shailendrapsp on 22/12/16.
 */

@Table(name = "reading")
public class ReadingTable extends Model {

    @Column(name = "utility_id")
    public String utility_id;

    @Column(name = "sensor_name")
    public String sensor_name;

    @Column(name = "value")
    public String value;

    @Column(name = "photographic_evidence_url")
    public String photographic_evidence_url;

    @Column(name = "local_photo_url")
    public String local_photo_url;

    @Column(name = "date")
    public String date;

    @Column(name = "unit")
    public String unit;

    @Column(name = "tare_weight")
    public String tare_weight;

    @Column(name = "timestamp")
    public long timestamp;

    @Column(name = "unit_id")
    public String unit_id;

    @Column(name = "uploadedImage")
    public boolean uploadedImage;

    @Column(name = "remark")
    public String remark;

    @Column(name = "updated_at")
    public long updated_at;

    @Column(name = "date_of_reading")
    public String date_of_reading;

    public ReadingTable() {
        super();
    }

    public static List<ReadingTable> getReadings(String utility_id, String sensor_name) {
        return new Select()
                .from(ReadingTable.class)
                .where("utility_id = ?", utility_id)
                .where("sensor_name = ?", sensor_name)
                .execute();
    }

    public static List<ReadingTable> getReadings(String utility_id, String sensor_name, String date) {
        return new Select()
                .from(ReadingTable.class)
                .where("utility_id = ?", utility_id)
                .where("sensor_name = ?", sensor_name)
                .where("date_of_reading = ?", date)
                .execute();
    }

    public static ReadingTable getReading(String utility_id, String sensor_name, long timestamp) {
        return new Select()
                .from(ReadingTable.class)
                .where("utility_id = ?", utility_id)
                .where("sensor_name = ?", sensor_name)
                .where("timestamp = ?", timestamp)
                .executeSingle();
    }

    public static ReadingTable getReading(long timestamp) {
        return new Select()
                .from(ReadingTable.class)
                .where("timestamp = ?", timestamp)
                .executeSingle();
    }

    public static List<ReadingTable> getReadings(boolean imagesUploaded) {
        return new Select()
                .from(ReadingTable.class)
                .where("uploadedImage = ?", imagesUploaded)
                .execute();
    }

    public static List<ReadingTable> getReadings(String unit_id, boolean imagesUploaded) {
        return new Select()
                .from(ReadingTable.class)
                .where("unit_id = ?", unit_id)
                .where("uploadedImage = ?", imagesUploaded)
                .execute();
    }

    public static List<ReadingTable> getAllReadings(String unit_id) {
        return new Select()
                .from(ReadingTable.class)
                .where("unit_id = ?", unit_id)
                .execute();
    }

    public static List<ReadingTable> getAllReadingInPhone(){
        return new Select()
                .from(ReadingTable.class)
                .execute();
    }
}
