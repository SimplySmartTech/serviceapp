package com.simplysmart.service.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.google.gson.Gson;
import com.simplysmart.service.R;
import com.simplysmart.service.adapter.MatrixListAdapter;
import com.simplysmart.service.config.ErrorUtils;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.ServiceGenerator;
import com.simplysmart.service.database.MatrixDataRealm;
import com.simplysmart.service.database.SensorDataRealm;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.model.common.APIError;
import com.simplysmart.service.model.matrix.MatrixData;
import com.simplysmart.service.model.matrix.MatrixResponse;
import com.simplysmart.service.model.matrix.SensorData;
import com.simplysmart.service.model.user.AccessPolicy;
import com.simplysmart.service.model.user.Unit;
import com.simplysmart.service.model.user.User;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;
import io.realm.internal.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    private ExpandableListView matrixList;
    private MatrixListAdapter matrixListAdapter;
    private MatrixResponse matrixResponse;
    private int lastExpandedPosition = -1;

    private boolean savedToDisk = false;
    private ArrayList<MatrixData> adapterData;

    private ArrayList<Unit> units;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUserInfo();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Service");

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        Menu menu = navigationView.getMenu();
        units = GlobalData.getInstance().getUnits();
        for(int i=0;i<units.size();i++){
            menu.add(i,i,i,units.get(i).getName().toUpperCase());
        }


        matrixList = (ExpandableListView) findViewById(R.id.matrixList);

        if (NetworkUtilities.isInternet(this)) {
            getMatrixRequest(GlobalData.getInstance().getSelectedUnitId(), GlobalData.getInstance().getSubDomain());
        } else {
            setOfflineData(Realm.getDefaultInstance());
        }

        matrixList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                Intent intent = new Intent(MainActivity.this, InputFormActivity.class);
                intent.putExtra("SENSOR_DATA", adapterData.get(groupPosition).getSensors().get(childPosition));
                intent.putExtra("groupPosition", groupPosition);
                intent.putExtra("childPosition", childPosition);
                startActivity(intent);
                return true;
            }
        });

        matrixList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    matrixList.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawer.closeDrawers();
                int id = item.getItemId();
                Unit unit = units.get(id);
                GlobalData.getInstance().setSelectedUnitId(unit.getId());
                if(NetworkUtilities.isInternet(MainActivity.this)) {
                    getMatrixRequest(GlobalData.getInstance().getSelectedUnitId(), GlobalData.getInstance().getSubDomain());
                }else{
                    setOfflineData(Realm.getDefaultInstance());
                }return true;
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(UPDATE_METRIC_SENSOR_LIST_ROW, new IntentFilter("UPDATE_METRIC_SENSOR_LIST_ROW"));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(UPDATE_METRIC_SENSOR_LIST_ROW);
        super.onDestroy();
    }

    @Override
    protected int getStatusBarColor() {
        return 0;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.submit:
                //do this
                Intent intent = new Intent(this, SummaryActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                logout();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Do network call to fetch matrix data
    private void getMatrixRequest(String unitId, String subDomain) {

        if (NetworkUtilities.isInternet(MainActivity.this)) {

            showActivitySpinner();

            ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
            Call<MatrixResponse> call = apiInterface.getMetrics(unitId, subDomain);
            call.enqueue(new Callback<MatrixResponse>() {

                @Override
                public void onResponse(Call<MatrixResponse> call, final Response<MatrixResponse> response) {

                    if (response.isSuccessful()) {
                        setMatrixData(response.body());
                    } else if (response.code() == 401) {
                        handleAuthorizationFailed();
                    } else {
                        APIError error = ErrorUtils.parseError(response);
                        displayMessage(error.message());
                    }
                    dismissActivitySpinner();
                }

                @Override
                public void onFailure(Call<MatrixResponse> call, Throwable t) {
                    dismissActivitySpinner();
                    displayMessage(getResources().getString(R.string.error_in_network));
                }
            });
        } else {
            displayMessage(getString(R.string.error_no_internet_connection));
        }
    }

    //Set matrix data to list
    private void setMatrixData(MatrixResponse response) {
        showActivitySpinner();

        ArrayList<MatrixData> matrixDataArrayList = response.getData();
        try {
            Realm realm = Realm.getDefaultInstance();
            for (int i = 0; i < matrixDataArrayList.size(); i++) {
                MatrixData matrixData = matrixDataArrayList.get(i);
                MatrixDataRealm matrixDataRealm = new MatrixDataRealm(matrixData);
                RealmList<SensorDataRealm> sensorDataRealmList = new RealmList<>();
                for (int j = 0; j < matrixData.getSensors().size(); j++) {
                    SensorDataRealm data;
                    if (!SensorDataRealm.alreadyExists(matrixData.getSensors().get(j).getSensor_name())) {
                        realm.beginTransaction();
                        data = realm.createObject(SensorDataRealm.class);
                        data.setData(matrixData.getSensors().get(j));
                        realm.commitTransaction();
                    } else {
                        realm.beginTransaction();
                        data = realm.where(SensorDataRealm.class).equalTo("sensor_name", matrixData.getSensors().get(j).getSensor_name()).findFirst();
                        data.setData(matrixData.getSensors().get(j));
                        realm.commitTransaction();
                    }

                    sensorDataRealmList.add(data);
                }

                matrixDataRealm.setSensors(sensorDataRealmList);
                MatrixDataRealm dataRealm;
                if (!MatrixDataRealm.alreadyExists(matrixData.getUtility_id())) {
                    realm.beginTransaction();
                    dataRealm = realm.copyToRealm(matrixDataRealm);
                    realm.commitTransaction();
                } else {
                    realm.beginTransaction();
                    dataRealm = realm.where(MatrixDataRealm.class).equalTo("utility_id", matrixData.getUtility_id()).findFirst();
                    dataRealm = matrixDataRealm;
                    realm.commitTransaction();
                }
            }
            savedToDisk = true;
            setDataInList(realm);
            dismissActivitySpinner();

        } catch (RealmException e) {
            dismissActivitySpinner();
            savedToDisk = false;
            e.printStackTrace();
        }

    }

    private void setDataInList(Realm realm) {
        showActivitySpinner();
        adapterData = new ArrayList<>();
        if (savedToDisk) {
            RealmResults<MatrixDataRealm> result = realm
                    .where(MatrixDataRealm.class)
                    .equalTo("unit_id",GlobalData.getInstance().getSelectedUnitId())
                    .findAll();
            if (result.size() > 0) {
                for (int i = 0; i < result.size(); i++) {
                    MatrixData matrixData = new MatrixData();
                    matrixData.setIcon(result.get(i).getIcon());
                    matrixData.setType(result.get(i).getType());
                    matrixData.setUtility_id(result.get(i).getUtility_id());
                    ArrayList<SensorData> sensors = new ArrayList<>();
                    for (int j = 0; j < result.get(i).getSensors().size(); j++) {
                        SensorData sensorData = new SensorData(result.get(i).getSensors().get(j));
                        sensors.add(sensorData);
                    }
                    matrixData.setSensors(sensors);
                    adapterData.add(matrixData);
                }
            }
            matrixListAdapter = new MatrixListAdapter(this, adapterData);
        }


        String text = new Gson().toJson(adapterData);
        Log.d("Data saved:", text);
        matrixList.setAdapter(matrixListAdapter);
        dismissActivitySpinner();
    }

    private void setOfflineData(Realm realm) {
        showActivitySpinner();
        adapterData = new ArrayList<>();

        RealmResults<MatrixDataRealm> result = realm
                .where(MatrixDataRealm.class)
                .equalTo("unit_id",GlobalData.getInstance().getSelectedUnitId())
                .findAll();
        if (result.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
                MatrixData matrixData = new MatrixData();
                matrixData.setIcon(result.get(i).getIcon());
                matrixData.setType(result.get(i).getType());
                matrixData.setUtility_id(result.get(i).getUtility_id());
                ArrayList<SensorData> sensors = new ArrayList<>();
                for (int j = 0; j < result.get(i).getSensors().size(); j++) {
                    SensorData sensorData = new SensorData(result.get(i).getSensors().get(j));
                    sensors.add(sensorData);
                }
                matrixData.setSensors(sensors);
                adapterData.add(matrixData);
            }
        }
        matrixListAdapter = new MatrixListAdapter(this, adapterData);

        String text = new Gson().toJson(adapterData);
        Log.d("Data saved:", text);
        matrixList.setAdapter(matrixListAdapter);
        dismissActivitySpinner();
    }

    private BroadcastReceiver UPDATE_METRIC_SENSOR_LIST_ROW = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateMetricList(intent);
        }
    };

    private void updateMetricList(Intent intent) {
        int groupPosition = intent.getIntExtra("groupPosition", -1);
        int childPosition = intent.getIntExtra("childPosition", -1);

        matrixResponse.getData().get(groupPosition).getSensors().get(childPosition).setChecked(true);
        matrixListAdapter.notifyDataSetChanged();
    }

    //Fetch logged user info from shared preferences
    private void getUserInfo() {

        SharedPreferences UserInfo = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        GlobalData.getInstance().setAuthToken(UserInfo.getString("auth_token", ""));
        GlobalData.getInstance().setApi_key(UserInfo.getString("api_key", ""));
        GlobalData.getInstance().setSubDomain(UserInfo.getString("subdomain", ""));
        GlobalData.getInstance().setRole_code(UserInfo.getString("role_code", ""));

        Gson gson = new Gson();
        String jsonUnitInfo = UserInfo.getString("unit_info", "");
        User residentData = gson.fromJson(jsonUnitInfo, User.class);

        String jsonAccessPolicy = UserInfo.getString("access_policy", "");
        AccessPolicy policy = gson.fromJson(jsonAccessPolicy, AccessPolicy.class);

        GlobalData.getInstance().setUnits(residentData.getUnits());
        GlobalData.getInstance().setAccessPolicy(policy);
    }
}
