package com.simplysmart.service.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.simplysmart.service.R;
import com.simplysmart.service.activity.AttendanceActivity;

/**
 * Created by shailendrapsp on 27/12/16.
 */

public class AlarmService extends IntentService {

    public AlarmService(){
        super(AlarmService.class.getName());
    }

    public AlarmService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sendNotification("Please take attendance. Ignore if already taken.");
    }

    private void sendNotification(String s) {
        NotificationManager alarmNotificationManager = (NotificationManager) this
                .getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, AttendanceActivity.class), 0);

        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle("Attendance").setSmallIcon(R.drawable.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(s))
                .setContentText(s);


        alamNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(1, alamNotificationBuilder.build());

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (alarmUri != null) {
            Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
            ringtone.play();
        }
    }
}
