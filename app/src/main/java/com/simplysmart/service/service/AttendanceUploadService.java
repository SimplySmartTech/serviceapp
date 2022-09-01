package com.simplysmart.service.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.google.gson.JsonObject;
import com.simplysmart.service.aws.AWSConstants;
import com.simplysmart.service.aws.Util;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.ServiceGenerator;
import com.simplysmart.service.database.AttendanceTable;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.model.attendance.Attendance;
import com.simplysmart.service.model.attendance.AttendanceList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;

/**
 * Created by shekhar on 27/12/16.
 */

public class AttendanceUploadService extends Service {
    private TransferUtility transferUtility;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        DebugLog.d("Reached Attendance upload service");
        transferUtility = Util.getTransferUtility(getApplicationContext());

//        List<AttendanceTable> attendanceTables = AttendanceTable.getAttendanceToSubmit();
//
//        if (attendanceTables != null && attendanceTables.size() > 0) {
//
//            DebugLog.d("size of attendance tables" + attendanceTables.size());
//            ArrayList<Attendance> attendanceList = new ArrayList<>();
//
//            for (int i = 0; i < attendanceTables.size(); i++) {
//                Attendance attendance = new Attendance();
//                attendance.setTime(attendanceTables.get(i).timestamp);
//                attendance.setImage_url(attendanceTables.get(i).image_url);
//                attendance.setCoordinates(attendanceTables.get(i).coordinates);
//                attendance.setAddress(attendanceTables.get(i).address);
//                attendanceList.add(attendance);
//            }
//            AttendanceList al = new AttendanceList();
//            al.setAttendances(attendanceList);
//
//            if (NetworkUtilities.isInternet(getApplicationContext())) {
//                sendAttendance(al);
//            }
//        } else {
//            DebugLog.d("size of attendance tables" + 0);
//        }

        List<AttendanceTable> attendances = AttendanceTable.getAttendances();
        if (attendances != null && attendances.size() > 0) {
            DebugLog.d("size of photos to upload " + attendances.size());

            for (AttendanceTable table : attendances) {
                if (NetworkUtilities.isInternet(this)) {
                    beginUpload(table);
                }
            }
        } else {
            DebugLog.d("size of photos to upload " + attendances.size());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void beginUpload(AttendanceTable table) {
        DebugLog.d("Local photo url:" + table.local_photo_url);
        String filePath = table.local_photo_url;
        try {
            try {
                File file = new File(filePath);
                TransferObserver observer = transferUtility.upload(
                        AWSConstants.BUCKET_NAME,
                        AWSConstants.PATH_FOLDER + file.getName(),
                        file, CannedAccessControlList.PublicRead);

                observer.setTransferListener(new UploadListener(table, file.getName()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class UploadListener implements TransferListener {
        private AttendanceTable attendanceTable;
        private String fileName;

        UploadListener(AttendanceTable attendanceTable, String fileName) {
            this.attendanceTable = attendanceTable;
            this.fileName = fileName;
        }

        @Override
        public void onError(int id, Exception e) {
            Log.e(TAG, "Error during upload: " + id, e);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d", id, bytesTotal, bytesCurrent));
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
            Log.d(TAG, "onStateChanged: " + id + ", " + newState);

            if (newState == TransferState.COMPLETED) {

                String url = AWSConstants.S3_URL + AWSConstants.BUCKET_NAME + "/" + AWSConstants.PATH_FOLDER + fileName;
                DebugLog.d("URL :::: " + url);
                onUploadComplete(attendanceTable, url);
            }
        }
    }

    private void onUploadComplete(AttendanceTable attendanceTable, String url) {
        attendanceTable.image_url = url;
        attendanceTable.submitted = true;
        attendanceTable.save();
        sendAttendance(attendanceTable);
    }

    private void sendAttendanceToServer(final AttendanceList attendanceList) {

        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<JsonObject> sendInterfaces = apiInterface.sendAttendances(GlobalData.getInstance().getSubDomain(), attendanceList);
        sendInterfaces.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    markAsSynced(attendanceList);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void markAsSynced(AttendanceList attendanceList) {
        for (int i = 0; i < attendanceList.getAttendances().size(); i++) {
            AttendanceTable at = AttendanceTable.getTable(attendanceList.getAttendances().get(i).getTime());
            at.synched = true;
        }
    }

    private void sendAttendance(AttendanceTable attendanceTable) {

        Attendance attendance = new Attendance();
        attendance.setImage_url(attendanceTable.image_url);
        attendance.setTime(attendanceTable.timestamp);
        attendance.setCoordinates(attendanceTable.coordinates);
        attendance.setAddress(attendanceTable.address);

        ArrayList<Attendance> attendanceArrayList = new ArrayList<>();
        attendanceArrayList.add(attendance);

        AttendanceList attendances = new AttendanceList();
        attendances.setAttendances(attendanceArrayList);

        if (NetworkUtilities.isInternet(this)) {
            sendAttendanceToServer(attendances);
        }
    }
}