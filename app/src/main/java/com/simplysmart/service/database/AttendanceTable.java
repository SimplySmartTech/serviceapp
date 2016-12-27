package com.simplysmart.service.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.simplysmart.service.model.attendance.Attendance;

import java.util.List;

/**
 * Created by shailendrapsp on 26/12/16.
 */

@Table(name = "attendance")
public class AttendanceTable extends Model {
    @Column(name = "local_photo_url")
    public String local_photo_url;

    @Column(name = "image_url")
    public String image_url;

    @Column(name = "timestamp")
    public long timestamp;

    @Column(name = "submitted")
    public boolean submitted;

    @Column(name = "synched")
    public boolean synched;

    public AttendanceTable(){
        super();
    }

    public static List<AttendanceTable> getAttendances(){
        return new Select()
                .from(AttendanceTable.class)
                .where("submitted = ?",false)
                .where("synched = ?",false)
                .execute();
    }

    public static List<AttendanceTable> getAttendanceToSubmit(){
        return new Select()
                .from(AttendanceTable.class)
                .where("submitted = ?",true)
                .where("synched = ?",false)
                .execute();
    }

    public static List<AttendanceTable> getAllAttendances(){
        return new Select()
                .from(AttendanceTable.class)
                .execute();
    }

    public static AttendanceTable getTable(long timestamp){
        return new Select()
                .from(AttendanceTable.class)
                .where("timestamp = ?",timestamp)
                .executeSingle();
    }
}
