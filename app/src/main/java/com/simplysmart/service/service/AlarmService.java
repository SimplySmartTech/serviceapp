package com.simplysmart.service.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.simplysmart.service.R;
import com.simplysmart.service.activity.AttendanceActivity;

/**
 * Created by shekhar on 27/12/16.
 */

public class AlarmService extends IntentService {

    public AlarmService() {
        super(AlarmService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sendNotification("Please take attendance. Ignore if already taken.");
    }

    private void sendNotification(String s) {

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager alarmNotificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, AttendanceActivity.class), 0);

        NotificationCompat.Builder alarmNotificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Attendance")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(s))
                .setSound(alarmUri)
                .setLargeIcon(icon)
                .setContentText(s);

        alarmNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(1, alarmNotificationBuilder.build());
    }
}
