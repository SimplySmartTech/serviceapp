package com.simplysmart.service.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.google.gson.JsonObject;
import com.simplysmart.service.adapter.VisitorListAdapter;
import com.simplysmart.service.aws.AWSConstants;
import com.simplysmart.service.aws.Util;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.ServiceGenerator;
import com.simplysmart.service.database.VisitorTable;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.model.visitors.VisitorPost;
import com.simplysmart.service.model.visitors.Visitors;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by shailendrapsp on 27/12/16.
 */

public class VisitorInfoUploadService extends Service {
    private TransferUtility transferUtility;
    private int count = 0;
    private ArrayList<String> imageUrls;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        transferUtility = Util.getTransferUtility(getApplicationContext());
        imageUrls = new ArrayList<>();
        List<VisitorTable> visitors = VisitorTable.getVisitorTables();
        if(visitors!=null && visitors.size()>0) {
            for (VisitorTable visitor : visitors) {
                String imageUrls = visitor.local_image_urls;
                DebugLog.d(imageUrls);
                ArrayList<String> urlList = new ArrayList<>();
                int prevPos = 0;
                for (int j = 0; j < imageUrls.length(); j++) {
                    if (imageUrls.charAt(j) == ',') {
                        urlList.add(imageUrls.substring(prevPos, j));
                        prevPos = j + 1;
                    }
                }

                if(urlList.size()>0){
                    count = urlList.size();
                    uploadImages(urlList, visitor);
                }else {
                    sendVisitorList(visitor);
                }

                break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void uploadImages(ArrayList<String> imageUrls, VisitorTable table) {
        for (String url : imageUrls) {
            DebugLog.d(url);
            beginUpload(url, table);
        }
    }

    private void beginUpload(String url, VisitorTable table) {
        DebugLog.d("Local photo url:" + url);
        String filePath = url;
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

    class UploadListener implements TransferListener {

        private VisitorTable visitorTable;
        private String fileName;

        UploadListener(VisitorTable visitorTable, String fileName) {
            this.visitorTable = visitorTable;
            this.fileName = fileName;
        }

        @Override
        public void onError(int id, Exception e) {
            Log.e(TAG, "Error during upload: " + id, e);
            count--;
            if (count == 0) {
                sendVisitorList(visitorTable);
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

                imageUrls.add(url);
                count--;
                if (count == 0) {
                    sendVisitorList(visitorTable);
                }
            }
        }

    }

    private void sendVisitorList(final VisitorTable visitorTable) {
        final Visitors visitors = new Visitors();
        visitors.setTime(visitorTable.timestamp);
        visitors.setDetails(visitorTable.details);
        visitors.setImage_urls(imageUrls);
        visitors.setNumber_of_person(visitorTable.num_of_person);

        ArrayList<Visitors> v = new ArrayList<>();
        v.add(visitors);
        VisitorPost post = new VisitorPost();
        post.setSubdomain(GlobalData.getInstance().getSubDomain());
        post.setVisitors(v);

        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<JsonObject> sendVisitors = apiInterface.sendVisitors(GlobalData.getInstance().getSubDomain(), post);
        sendVisitors.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    visitorTable.synched = true;
                    visitorTable.save();
                    deleteIfNotRequired(visitorTable);
                    Intent i = new Intent(getApplicationContext(),VisitorInfoUploadService.class);
                    startService(i);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void deleteIfNotRequired(VisitorTable visitorTable) {
        long tableTimestamp = visitorTable.timestamp;
        long currentTimestamp = Calendar.getInstance().getTimeInMillis();

        if((currentTimestamp-tableTimestamp)>(604800000L)){
            visitorTable.delete();
        }
    }
}
