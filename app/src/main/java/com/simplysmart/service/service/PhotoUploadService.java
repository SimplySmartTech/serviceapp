package com.simplysmart.service.service;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.simplysmart.service.activity.InputFormActivity;
import com.simplysmart.service.aws.AWSConstants;
import com.simplysmart.service.aws.Util;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.database.ImageUploadObjectRealm;
import com.simplysmart.service.database.MatrixDataRealm;
import com.simplysmart.service.database.ReadingDataRealm;

import java.io.File;
import java.util.Queue;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static android.content.ContentValues.TAG;

/**
 * Created by shailendrapsp on 7/11/16.
 */

public class PhotoUploadService extends Service {

    private TransferUtility transferUtility;
    private int count = 0;

    public PhotoUploadService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(getApplicationContext(), "Network Available : UploadingPhoto", Toast.LENGTH_LONG).show();
        Log.d("TAG","Reached photo upload service.");
        transferUtility = Util.getTransferUtility(getApplicationContext());
        uploadImage();

        return super.onStartCommand(intent, flags, startId);
    }


    private void uploadImage() {
        Toast.makeText(getApplicationContext(), "Network Available : uploadImage", Toast.LENGTH_LONG).show();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ReadingDataRealm> readingsList = realm.where(ReadingDataRealm.class).findAll();

        for (ReadingDataRealm reading : readingsList) {
            if (!reading.isUploadedImage() && !reading.getLocal_photo_url().equals("") && reading.getLocal_photo_url()!=null) {
                beginUpload(reading);
            }
        }
    }

    private void beginUpload(ReadingDataRealm readingDataRealm) {
        String filePath = readingDataRealm.getLocal_photo_url();
        if (filePath == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            File file = new File(filePath);
            TransferObserver observer = transferUtility.upload(
                    AWSConstants.BUCKET_NAME,
                    AWSConstants.PATH_FOLDER + file.getName(),
                    file, CannedAccessControlList.PublicRead);

            observer.setTransferListener(new UploadListener(readingDataRealm.getTimestamp(), file.getName()));
            count++;
            Toast.makeText(getApplicationContext(), "Network Available : Set for uploading"+filePath, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendUploadCompleteBroadcast() {
        Intent i = new Intent("uploadComplete");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
    }

    class UploadListener implements TransferListener {

        private String fileName;
        private long timestamp;

        UploadListener(long timestamp, String fileName) {
            this.fileName = fileName;
            this.timestamp = timestamp;
        }

        @Override
        public void onError(int id, Exception e) {
            Log.e(TAG, "Error during upload: " + id, e);
            count--;
            if(count==0){
                sendUploadCompleteBroadcast();
            }
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d", id, bytesTotal, bytesCurrent));
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
            Log.d(TAG, "onStateChanged: " + id + ", " + newState);

            if (newState == TransferState.COMPLETED) {

                String url = AWSConstants.S3_URL
                        + AWSConstants.BUCKET_NAME + "/"
                        + AWSConstants.PATH_FOLDER
                        + fileName;

                DebugLog.d("URL :::: " + url);

                Realm realm = Realm.getDefaultInstance();
                ReadingDataRealm reading = realm.where(ReadingDataRealm.class).equalTo("timestamp",timestamp).findFirst();

                realm.beginTransaction();
                reading.setUploadedImage(true);
                reading.setPhotographic_evidence_url(url);
                realm.commitTransaction();

                count --;
                Log.d("COUNT : ", count+"");
                if(count==0){
                    sendUploadCompleteBroadcast();
                }

            }
        }
    }
}

