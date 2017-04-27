package com.simplysmart.service.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.simplysmart.service.R;

public class CreateComplaintActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_complaint);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Update");
    }

    @Override
    protected int getStatusBarColor() {
        return 0;
    }
}
