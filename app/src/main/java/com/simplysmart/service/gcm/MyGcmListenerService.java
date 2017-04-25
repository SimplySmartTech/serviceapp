package com.simplysmart.service.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.simplysmart.service.R;
import com.simplysmart.service.activity.ComplaintDetailScreenActivity;
import com.simplysmart.service.activity.SplashActivity;
import com.simplysmart.service.model.notification.PushNotificationData;
import com.simplysmart.service.model.notification.Notification;

/**
 * Created by shekhar on 17/11/15.
 */

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {

        Log.d(TAG, data.toString());
        String code = data.getString("code");
        String message = data.getString("data");
        Log.d(TAG, "Notification code: " + code);
        Log.d(TAG, "Message: " + message);

        sendNotification(code, message);
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String code, String message) {

        PushNotificationData response = new Gson().fromJson(message, PushNotificationData.class);
        Notification notification = response.getNotification();

        NotificationCompat.Builder notificationBuilder = null;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent broadcastIntent = new Intent("UpdateNotification");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);

        if (code.equalsIgnoreCase("1")) {

            Intent intent = new Intent(this, SplashActivity.class);
            intent.putExtra("UPDATED_FROM_PUSH", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(notification.getSubject())
                    .setContentText(notification.getDescription())
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

        } else if (code.equalsIgnoreCase("2")) {

            Intent i = new Intent("UpdateDetails");
            i.putExtra("complaint_id", notification.getNoticeable_id());
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);

            Intent intent = new Intent(this, ComplaintDetailScreenActivity.class);
            intent.putExtra("UPDATED_FROM_PUSH", true);
            intent.putExtra("complaint_id", response.getNotification().getNoticeable_id());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(notification.getSub_category() != null ? notification.getSub_category() : notification.getCategory())
                    .setContentText(notification.getSubject())
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

        } else if (code.equalsIgnoreCase("3")) {

            Intent i = new Intent("UpdateActivity");
            i.putExtra("UPDATED_ACTIVITY_INFO", response.getActivity());
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);

            Intent intent = new Intent(this, ComplaintDetailScreenActivity.class);
            intent.putExtra("UPDATED_FROM_PUSH", true);
            intent.putExtra("complaint_id", notification.getNoticeable_id());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(notification.getSubject())
                    .setContentText(notification.getDescription())
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}