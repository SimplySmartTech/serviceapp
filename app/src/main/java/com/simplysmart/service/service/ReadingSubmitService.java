package com.simplysmart.service.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.simplysmart.service.database.FinalReadingData;
import com.simplysmart.service.model.matrix.AllReadingsData;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by shailendrapsp on 5/12/16.
 */

public class ReadingSubmitService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RealmResults<FinalReadingData> finalReadingDatas = Realm.getDefaultInstance().where(FinalReadingData.class).findAll();
        for(int i=0;i<finalReadingDatas.size();i++){
            Gson gson = new Gson();
//            JsonObject jsonObject = gson.toJson(finalReadingDatas.get(i).getJsonToSend());
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
