package com.simplysmart.service.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.simplysmart.service.R;
import com.simplysmart.service.config.ErrorUtils;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.ServiceGenerator;
import com.simplysmart.service.database.FinalReadingData;
import com.simplysmart.service.database.MatrixDataRealm;
import com.simplysmart.service.dialog.AlertDialogStandard;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.model.common.APIError;
import com.simplysmart.service.model.matrix.AllReadingsData;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shailendrapsp on 5/12/16.
 */

public class ReadingSubmitService extends Service {

    public ReadingSubmitService(){
        super();
    }

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
            AllReadingsData allReadingsData = gson.fromJson(finalReadingDatas.get(i).getJsonToSend(),AllReadingsData.class);
            submitData(allReadingsData,finalReadingDatas.get(i));
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
                        Toast.makeText(getApplicationContext(),"Data successfully submitted.",Toast.LENGTH_SHORT).show();
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        finalReadingData.deleteFromRealm();
                        realm.commitTransaction();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }
    }
}
