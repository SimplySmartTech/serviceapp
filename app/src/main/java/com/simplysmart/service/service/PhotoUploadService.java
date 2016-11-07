package com.simplysmart.service.service;

import android.app.IntentService;
import android.content.Intent;

import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.database.ImageUploadObjectRealm;

import java.util.Queue;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by shailendrapsp on 7/11/16.
 */

public class PhotoUploadService extends IntentService{
    private String utility_id;
    private String sensor_name;
    private String localUrl;
    private long timeStamp;

    RealmList<ImageUploadObjectRealm> uploadList;

    public PhotoUploadService(){
        super(PhotoUploadService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        utility_id = intent.getStringExtra(StringConstants.UTILITY_ID);
        sensor_name = intent.getStringExtra(StringConstants.SENSOR_NAME);
        localUrl = intent.getStringExtra(StringConstants.LOCAL_IMAGE_URL);
        timeStamp = intent.getLongExtra(StringConstants.TIMESTAMP,0);

        uploadList = new RealmList<>();
        uploadList = ImageUploadObjectRealm.getAllImagesToUpload();

    }

    private void uploadImage() {
        if(uploadList.size()>0){

        }
    }


}
