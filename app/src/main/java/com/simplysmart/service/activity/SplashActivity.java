package com.simplysmart.service.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.simplysmart.service.R;
import com.simplysmart.service.config.GlobalData;

import io.realm.Realm;


/**
 * Created by Shekhar on 4/8/15.
 * Splash screen play landing app logo video.
 */
public class SplashActivity extends Activity {

    private boolean isLogin;
    private static final int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);

        SharedPreferences UserInfo = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor userInfoEdit = UserInfo.edit();
        isLogin = UserInfo.getBoolean("isLogin", false);

        if (UserInfo.getBoolean("DataChanged", true)) {
            userInfoEdit.putBoolean("DataChanged", false).apply();
            Realm realm = Realm.getDefaultInstance();
            try {
                realm.close();
                Realm.deleteRealm(realm.getConfiguration());
            } catch (Exception e) {
                e.printStackTrace();
            }
            logout();
        }else {
            switchToNextActivity();
        }
    }

    private void switchToNextActivity() {
        SharedPreferences UserInfo = this.getSharedPreferences("UserInfo", MODE_PRIVATE);
        isLogin = UserInfo.getBoolean("isLogin", false);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent i;
                if (isLogin) {
                    i = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    i = new Intent(SplashActivity.this, LoginActivity.class);
                }
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    public void logout() {
        if (GlobalData.getInstance().getUnits() != null && GlobalData.getInstance().getUnits().size() > 0) {
            for (int i = 0; i < GlobalData.getInstance().getUnits().size(); i++) {
                Realm realm = Realm.getDefaultInstance();
                realm.close();
                Realm.deleteRealm(realm.getConfiguration());
//                removeLocalData(GlobalData.getInstance().getUnits().get(i).getId());
            }
        }

        handleAuthorizationFailed();
    }

    public void handleAuthorizationFailed() {
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

}