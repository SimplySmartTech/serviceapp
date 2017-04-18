package com.simplysmart.service.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.activeandroid.query.Delete;
import com.simplysmart.service.R;
import com.simplysmart.service.database.AttendanceTable;
import com.simplysmart.service.database.FinalReadingTable;
import com.simplysmart.service.database.MatrixTable;
import com.simplysmart.service.database.ReadingTable;
import com.simplysmart.service.database.SensorTable;
import com.simplysmart.service.database.TareWeightTable;
import com.simplysmart.service.database.VisitorTable;


public class SplashActivity extends Activity {

    private boolean isLogin;
    private static final int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);

        //Used for force fully reset application (logout forcefully) set flag true for reset user
        resetApplication(true);

        switchToNextActivity();
    }

    private void switchToNextActivity() {
        SharedPreferences UserInfo = this.getSharedPreferences("UserInfo", MODE_PRIVATE);
        isLogin = UserInfo.getBoolean("isLogin", false);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent i;
                if (isLogin) {
                    i = new Intent(SplashActivity.this, MainActivity_V2.class);
                } else {
                    i = new Intent(SplashActivity.this, LoginActivity.class);
                }
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void resetApplication(boolean reset) {
        SharedPreferences ResetUserPreferences = this.getSharedPreferences("ResetUserPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ResetUserPreferences.edit();
        boolean logoutUser = ResetUserPreferences.getBoolean("logoutUser", true);

        if (reset) {
            if (logoutUser) {
                SharedPreferences UserInfo = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor userInfoEdit = UserInfo.edit();
                userInfoEdit.clear().apply();
                editor.putBoolean("logoutUser", false).apply();

                new Delete().from(MatrixTable.class).execute();
                new Delete().from(SensorTable.class).execute();
                new Delete().from(ReadingTable.class).execute();
                new Delete().from(FinalReadingTable.class).execute();
                new Delete().from(TareWeightTable.class).execute();
                new Delete().from(AttendanceTable.class).execute();
                new Delete().from(VisitorTable.class).execute();
            }
        } else {
            editor.remove("logoutUser").apply();
        }
    }

}