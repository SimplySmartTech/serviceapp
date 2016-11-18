package com.simplysmart.service.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.simplysmart.service.R;
import com.simplysmart.service.adapter.SensorListAdapter;
import com.simplysmart.service.common.CommonMethod;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.ErrorUtils;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.ServiceGenerator;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.custom_views.CustomGridLayoutManager;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.gcm.QuickstartPreferences;
import com.simplysmart.service.gcm.RegistrationIntentService;
import com.simplysmart.service.model.common.APIError;
import com.simplysmart.service.model.matrix.MatrixData;
import com.simplysmart.service.model.matrix.SensorData;
import com.simplysmart.service.model.user.AccessPolicy;
import com.simplysmart.service.model.user.LoginRequest;
import com.simplysmart.service.model.user.LoginResponse;
import com.simplysmart.service.model.user.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SensorListActivity extends BaseActivity {

    private MatrixData data;
    ArrayList<SensorData> sensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (GlobalData.getInstance().getSelectedUnit() != null && GlobalData.getInstance().getSelectedUnit().equals("")) {
            getSupportActionBar().setTitle(GlobalData.getInstance().getSelectedUnit());
        } else {
            getSupportActionBar().setTitle("Mailhem");
        }

        if(getIntent()!=null && getIntent().getExtras()!=null) {
            data = getIntent().getParcelableExtra(StringConstants.METRIC_DATA);
            if(data!=null){
                if(data.getSensors()!=null && data.getSensors().size()>0){
                    sensors = data.getSensors();
                    initializeWidgets();
                }
            }
        }

    }

    private void initializeWidgets() {
        RecyclerView sensorList = (RecyclerView)findViewById(R.id.sensorList);
        SensorListAdapter adapter = new SensorListAdapter(SensorListActivity.this,sensors);
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(SensorListActivity.this,2);
        sensorList.setLayoutManager(gridLayoutManager);
        sensorList.setAdapter(adapter);
    }

    @Override
    protected int getStatusBarColor() {
        return 0;
    }


}
