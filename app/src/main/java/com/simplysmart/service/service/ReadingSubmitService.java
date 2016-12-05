package com.simplysmart.service.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.ServiceGenerator;
import com.simplysmart.service.database.FinalReadingData;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.interfaces.SubmitCompleteInterface;
import com.simplysmart.service.model.matrix.AllReadingsData;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shailendrapsp on 5/12/16.
 */

public class ReadingSubmitService extends Service implements SubmitCompleteInterface {

    private RealmList<FinalReadingData> finalReadingDatas;
    RealmResults<FinalReadingData> results;
    public ReadingSubmitService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        finalReadingDatas = new RealmList<>();
        results = Realm.getDefaultInstance().where(FinalReadingData.class).findAll();
        DebugLog.d(results.size()+"");
        if(results.size()>0) {
            for (int i = 0; i < results.size(); i++) {
                finalReadingDatas.add(results.get(i));
            }
        }

        if(finalReadingDatas!=null && finalReadingDatas.size()>0) {
            Gson gson = new Gson();
            AllReadingsData allReadingsData = gson.fromJson(finalReadingDatas.get(0).getJsonToSend(), AllReadingsData.class);
            submitData(allReadingsData, finalReadingDatas.get(0));
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void submitData(AllReadingsData allReadingsData, final FinalReadingData finalReadingData) {
        if (NetworkUtilities.isInternet(this)) {
            ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
            Call<JsonObject> submitAllReadings = apiInterface.submitAllReadings(GlobalData.getInstance().getSubDomain(), allReadingsData);
            submitAllReadings.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        onSubmitComplete();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }
    }


    @Override
    public void onSubmitComplete() {
        finalReadingDatas.remove(0);
        if(finalReadingDatas!=null && finalReadingDatas.size()>0){
            Gson gson = new Gson();
            AllReadingsData allReadingsData = gson.fromJson(finalReadingDatas.get(0).getJsonToSend(), AllReadingsData.class);
            submitData(allReadingsData, finalReadingDatas.get(0));
        }else {
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            results.deleteAllFromRealm();
            realm.commitTransaction();
        }
    }
}
