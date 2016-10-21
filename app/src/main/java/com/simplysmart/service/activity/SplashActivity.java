package com.simplysmart.service.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.simplysmart.service.R;


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
        isLogin = UserInfo.getBoolean("isLogin", false);

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
                    i = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    i = new Intent(SplashActivity.this, LoginActivity.class);
                }
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}