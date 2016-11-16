package com.simplysmart.service.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.simplysmart.service.config.StringConstants;

/**
 * Created by shailendrapsp on 16/11/16.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (checkInternet(context)) {
            Toast.makeText(context, "Network Available : Broadcast recieved from OS", Toast.LENGTH_LONG).show();
            Intent i = new Intent(context, PhotoUploadService.class);
            i.putExtra(StringConstants.UPLOAD_IMAGE,true);
            context.startService(i);
        }
    }

    boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        return serviceManager.isNetworkAvailable();
    }

}
