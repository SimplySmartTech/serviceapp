package com.simplysmart.service.fcm;

import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.simplysmart.service.gcm.QuickstartPreferences;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.e(TAG, "Token: " + token);
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(QuickstartPreferences.GCM_TOKEN,token).apply();

        //upload the token to your server
        //you have to store in preferences
    }

}