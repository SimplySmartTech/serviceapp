package com.simplysmart.service.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.activeandroid.query.Delete;
import com.google.gson.Gson;
import com.simplysmart.service.R;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.database.AttendanceTable;
import com.simplysmart.service.database.FinalReadingTable;
import com.simplysmart.service.database.MatrixTable;
import com.simplysmart.service.database.ReadingTable;
import com.simplysmart.service.database.SensorTable;
import com.simplysmart.service.database.TareWeightTable;
import com.simplysmart.service.database.VisitorTable;
import com.simplysmart.service.model.user.AccessPolicy;
import com.simplysmart.service.model.user.User;

import java.util.HashMap;


public class SplashActivity extends Activity {

    private boolean isLogin;
    private static final int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);

        //Used for force fully reset application (logout forcefully) set flag true for reset user
        resetApplication(false);

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
                    i = navigateToDefaultScreen();
                } else {
                    i = new Intent(SplashActivity.this, LoginActivity.class);
                }
                if (i != null) {
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    private void resetApplication(boolean reset) {
        SharedPreferences ResetUserPreferences = this.getSharedPreferences("ResetUserPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ResetUserPreferences.edit();
        boolean logoutUser = ResetUserPreferences.getBoolean("logoutUser", true);

        if (reset) {
            if (logoutUser) {
                try {
                    SharedPreferences UserInfo = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor userInfoEdit = UserInfo.edit();
                    userInfoEdit.clear().apply();
                    editor.putBoolean("logoutUser", false).apply();

                    try {
                        new Delete().from(MatrixTable.class).execute();
                        new Delete().from(SensorTable.class).execute();
                        new Delete().from(ReadingTable.class).execute();
                        new Delete().from(FinalReadingTable.class).execute();
                        new Delete().from(TareWeightTable.class).execute();
                        new Delete().from(AttendanceTable.class).execute();
                        new Delete().from(VisitorTable.class).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            editor.remove("logoutUser").apply();
        }
    }

    private Intent navigateToDefaultScreen() {

        Intent intent = null;
        HashMap<String, Class> menuCLickList = new HashMap<>();
        menuCLickList.put("metrics", MainActivity.class);
        menuCLickList.put("notification", null);
        menuCLickList.put("helpdesk", HelpDeskScreenActivity.class);
        menuCLickList.put("sensors", SiteSensorsScreenActivity.class);
        menuCLickList.put("attendance", AttendanceActivity.class);
        menuCLickList.put("visitors", VisitorActivity.class);
        menuCLickList.put("status", SiteStatusScreenActivity.class);

        SharedPreferences UserInfo = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        GlobalData.getInstance().setSubDomain(UserInfo.getString("subdomain", ""));

        Gson gson = new Gson();
        String jsonUnitInfo = UserInfo.getString("unit_info", "");
        User residentData = gson.fromJson(jsonUnitInfo, User.class);

        GlobalData.getInstance().setAccessPolicy(residentData.getPolicy());

        for (AccessPolicy accessPolicy : residentData.getPolicy()) {
            if (menuCLickList.containsKey(accessPolicy.getName().toLowerCase()) && accessPolicy.isDefaultVal()) {
                intent = new Intent(SplashActivity.this, menuCLickList.get(accessPolicy.getName().toLowerCase()));
            }
        }
        return intent;
    }

}