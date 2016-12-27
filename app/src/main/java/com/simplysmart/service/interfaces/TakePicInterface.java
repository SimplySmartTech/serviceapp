package com.simplysmart.service.interfaces;

import com.simplysmart.service.database.AttendanceTable;

/**
 * Created by shailendrapsp on 26/12/16.
 */

public interface TakePicInterface {
    void takePic(int type, AttendanceTable attendanceTable,int position);
}
