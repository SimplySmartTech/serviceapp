package com.simplysmart.service.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.ServiceGenerator;
import com.simplysmart.service.database.FinalReadingTable;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.interfaces.SubmitCompleteInterface;
import com.simplysmart.service.model.matrix.AllReadingsData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shailendrapsp on 5/12/16.
 */

public class ReadingSubmitService extends Service implements SubmitCompleteInterface {

    private List<FinalReadingTable> finalReadingTables;

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

        finalReadingTables = FinalReadingTable.getReadingsToSubmit();

        if(finalReadingTables!=null && finalReadingTables.size()>0) {
                Gson gson = new Gson();
                AllReadingsData allReadingsData = gson.fromJson(finalReadingTables.get(0).reading, AllReadingsData.class);
                submitData(allReadingsData, finalReadingTables.get(0));
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void submitData(AllReadingsData allReadingsData, final FinalReadingTable finalReadingData) {
        if (NetworkUtilities.isInternet(this)) {
            ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
            Call<JsonObject> submitAllReadings = apiInterface.submitAllReadings(GlobalData.getInstance().getSubDomain(), allReadingsData);
            submitAllReadings.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        onSubmitComplete(finalReadingData);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }
    }


    @Override
    public void onSubmitComplete(FinalReadingTable finalReadingTable) {
        finalReadingTable.delete();
        finalReadingTables = FinalReadingTable.getReadingsToSubmit();
        if(finalReadingTables!=null && finalReadingTables.size()>0){
            Gson gson = new Gson();
            AllReadingsData allReadingsData = gson.fromJson(finalReadingTables.get(0).reading, AllReadingsData.class);
            submitData(allReadingsData, finalReadingTables.get(0));
        }
    }
}
