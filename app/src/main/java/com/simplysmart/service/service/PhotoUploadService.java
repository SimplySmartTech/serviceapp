package com.simplysmart.service.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
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
import com.simplysmart.service.database.ReadingTable;

import java.io.File;
import java.util.List;

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
        List<ReadingTable> readingTableList = ReadingTable.getReadings(false);
        for (ReadingTable readingTable : readingTableList) {
            if (readingTable.local_photo_url != null && !readingTable.local_photo_url.equalsIgnoreCase("")) {
                DebugLog.d("Local photo url : " + readingTable.local_photo_url);
                beginUpload(readingTable);
            }
        }
    }

    private void uploadImage(String unit_id) {
        List<ReadingTable> readingTableList = ReadingTable.getReadings(unit_id, false);
        for (ReadingTable reading : readingTableList) {
            if (reading.local_photo_url != null && !reading.local_photo_url.equals("")) {
                beginUpload(reading);
            } else {
                sendUploadCompleteBroadcast();
            }
        }
    }

    private void beginUpload(ReadingTable readingTable) {
        Log.d("Local photo url:", readingTable.local_photo_url);
        String filePath = readingTable.local_photo_url;
        try {
            try {
                File file = new File(filePath);
                TransferObserver observer = transferUtility.upload(
                        AWSConstants.BUCKET_NAME,
                        AWSConstants.PATH_FOLDER + file.getName(),
                        file, CannedAccessControlList.PublicRead);

                observer.setTransferListener(new UploadListener(readingTable.timestamp, file.getName(), readingTable));
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

    public void onUploadComplete(ReadingTable readingTable, String aws_url) {
        readingTable.photographic_evidence_url = aws_url;
        readingTable.uploadedImage = true;
        readingTable.save();

        DebugLog.d("URL:" + readingTable.photographic_evidence_url);
        sendImageUploadBroadcast(readingTable);
    }

    class UploadListener implements TransferListener {

        private String fileName;
        private long timestamp;
        private ReadingTable rdr;

        UploadListener(long timestamp, String fileName, ReadingTable rdr) {
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

    private void sendImageUploadBroadcast(ReadingTable rdr) {
        Intent i = new Intent("imageUploadComplete");
        i.putExtra(StringConstants.TIMESTAMP, rdr.timestamp);
        DebugLog.d("Sending image upload complete broadcast " + rdr.photographic_evidence_url);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
    }
}
