package com.simplysmart.service.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

import com.simplysmart.service.config.StringConstants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by shailendrapsp on 16/11/16.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (checkInternet(context)) {
            Intent i = new Intent(context, PhotoUploadService.class);
            i.putExtra(StringConstants.USE_UNIT, false);
            context.startService(i);

            Intent i2 = new Intent(context, ReadingSubmitService.class);
            context.startService(i2);

            Intent i3 = new Intent("uploadStarted");
            LocalBroadcastManager.getInstance(context).sendBroadcast(i3);

            Intent i4 = new Intent(context,CheckAttendanceTimeService.class);
            context.startService(i4);

            Intent i5 = new Intent(context,AttendanceUploadService.class);
            context.startService(i5);
        }
    }

    boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        return serviceManager.isNetworkAvailable();
    }

}
