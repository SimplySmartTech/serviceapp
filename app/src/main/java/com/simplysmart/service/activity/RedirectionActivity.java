package com.simplysmart.service.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.GlobalData;


public class RedirectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DebugLog.d("AAAAAAA   Start");
        SharedPreferences UserInfo = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        GlobalData.getInstance().setSubDomain(UserInfo.getString("subdomain", ""));

        Intent i;
        if (GlobalData.getInstance().getSubDomain().equalsIgnoreCase("mailhem")) {
            i = new Intent(this, MainActivity.class);
        } else {
            i = new Intent(this, SiteSensorsScreenActivity.class);
        }
        DebugLog.d("AAAAAAA   End");
        startActivity(i);
        finish();
    }
}