package com.simplysmart.service.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.simplysmart.service.aws.AWSConstants;
import com.simplysmart.service.aws.Util;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.database.ReadingDataRealm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import io.realm.Realm;
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

        transferUtility = Util.getTransferUtility(getApplicationContext());
        try {
            if (intent != null && intent.getExtras() != null) {
                boolean useUnitId = intent.getBooleanExtra(StringConstants.USE_UNIT, false);
                if (useUnitId) {
                    uploadImage(intent.getStringExtra(StringConstants.UNIT_ID));
                } else {
                    uploadImage();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void sendUploadCompleteBroadcast() {
        Intent i = new Intent("uploadComplete");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
    }

    private void uploadImage() {
        //TODO : Remove toast.
//        Toast.makeText(getApplicationContext(), "Network Available : uploadImage", Toast.LENGTH_LONG).show();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ReadingDataRealm> readingsList = realm
                .where(ReadingDataRealm.class)
                .equalTo("uploadedImage", false)
                .findAll();

        for (ReadingDataRealm reading : readingsList) {
            if (reading.getLocal_photo_url() != null && !reading.getLocal_photo_url().equals("")) {
                DebugLog.d("Got photo URL : " + reading.getLocal_photo_url());
                beginUpload(reading);
            } else {
                DebugLog.d("No photo URL");
            }
        }
    }

    private void uploadImage(String unit_id) {
        //TODO : Remove toast.
//        Toast.makeText(getApplicationContext(), "Network Available : uploadImage", Toast.LENGTH_LONG).show();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ReadingDataRealm> readingsList = realm
                .where(ReadingDataRealm.class)
                .equalTo("uploadedImage", false)
                .equalTo("unit_id", unit_id)
                .findAll();

        for (ReadingDataRealm reading : readingsList) {
            if (reading.getLocal_photo_url() != null && !reading.getLocal_photo_url().equals("")) {
                beginUpload(reading);
            } else {
                sendUploadCompleteBroadcast();
            }
        }
    }

    private void beginUpload(ReadingDataRealm readingDataRealm) {
        Log.d("Local photo url:", readingDataRealm.getLocal_photo_url());
        String filePath = readingDataRealm.getLocal_photo_url();
        try {
            try {
                File file = new File(filePath);
                TransferObserver observer = transferUtility.upload(
                        AWSConstants.BUCKET_NAME,
                        AWSConstants.PATH_FOLDER + file.getName(),
                        file, CannedAccessControlList.PublicRead);

                observer.setTransferListener(new UploadListener(readingDataRealm.getTimestamp(), file.getName(), readingDataRealm));
                count++;
//            Toast.makeText(getApplicationContext(), "Network Available : Set for uploading"+filePath, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            count--;
            if (count == 0) {
                sendUploadCompleteBroadcast();
            }
        }
    }

    public void onUploadComplete(ReadingDataRealm readingDataRealm, String aws_url) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        readingDataRealm.setPhotographic_evidence_url(aws_url);
        readingDataRealm.setUploadedImage(true);
        realm.commitTransaction();
        DebugLog.d("URL:" + readingDataRealm.getPhotographic_evidence_url());

        sendImageUploadBroadcast(readingDataRealm);
    }

    class UploadListener implements TransferListener {

        private String fileName;
        private long timestamp;
        private ReadingDataRealm rdr;

        UploadListener(long timestamp, String fileName, ReadingDataRealm rdr) {
            this.fileName = fileName;
            this.timestamp = timestamp;
            this.rdr = rdr;
        }

        @Override
        public void onError(int id, Exception e) {
            Log.e(TAG, "Error during upload: " + id, e);
            count--;
            if (count == 0) {
                sendUploadCompleteBroadcast();
            }

            sendImageUploadBroadcast(rdr);
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

                count--;
                Log.d("COUNT : ", count + "");
                if (count == 0) {
                    sendUploadCompleteBroadcast();
                }
                onUploadComplete(rdr, url);
            }
        }
    }

    private void sendImageUploadBroadcast(ReadingDataRealm rdr) {
        Intent i = new Intent("imageUploadComplete");
        i.putExtra(StringConstants.READING_DATA,rdr);
        DebugLog.d("Sending image upload complete broadcast "+rdr.getPhotographic_evidence_url());
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
    }
}
