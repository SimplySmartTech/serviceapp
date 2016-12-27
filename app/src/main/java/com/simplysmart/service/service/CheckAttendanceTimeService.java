package com.simplysmart.service.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.gson.JsonObject;
import com.simplysmart.service.aws.Util;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.ServiceGenerator;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.model.attendance.AttendanceAt;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shailendrapsp on 26/12/16.
 */

public class CheckAttendanceTimeService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(c.getTimeInMillis());
        if(!preferences.getString(StringConstants.PREV_DATE,"").equalsIgnoreCase(currentDate)){
            getNewAttendanceTime();
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString(StringConstants.PREV_DATE,currentDate);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void getNewAttendanceTime() {
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<AttendanceAt> getAttendance = apiInterface.getAttendanceTime(GlobalData.getInstance().getSubDomain());
        getAttendance.enqueue(new Callback<AttendanceAt>() {
            @Override
            public void onResponse(Call<AttendanceAt> call, Response<AttendanceAt> response) {
                if(response.isSuccessful() && response.body().getAttendance_at()!=null) {
                    saveNewAttendanceTime(response);
                }
            }

            @Override
            public void onFailure(Call<AttendanceAt> call, Throwable t) {

            }
        });
    }

    private void saveNewAttendanceTime(Response<AttendanceAt> response) {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("UserInfo",MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(StringConstants.ATTENDANCE_AT,response.body().getAttendance_at());
    }
}
