package com.simplysmart.service.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.google.gson.Gson;
import com.simplysmart.service.R;
import com.simplysmart.service.adapter.MatrixTableAdapterV2;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.common.LocationAddress;
import com.simplysmart.service.config.ErrorUtils;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.ServiceGeneratorV2;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.database.MatrixTable;
import com.simplysmart.service.database.SensorTable;
import com.simplysmart.service.database.TareWeightTable;
import com.simplysmart.service.dialog.AlertDialogLogout;
import com.simplysmart.service.dialog.AlertDialogStandard;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.interfaces.LogoutListener;
import com.simplysmart.service.model.common.APIError;
import com.simplysmart.service.model.matrix.MatrixData;
import com.simplysmart.service.model.matrix.MatrixResponse;
import com.simplysmart.service.model.user.AccessPolicy;
import com.simplysmart.service.model.user.Unit;
import com.simplysmart.service.model.user.User;
import com.yayandroid.locationmanager.LocationConfiguration;
import com.yayandroid.locationmanager.constants.FailType;
import com.yayandroid.locationmanager.constants.ProviderType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityV2 extends GetLocationBaseActivity implements LogoutListener {

    private TextView no_data_found, add_previous_reading;
    private RecyclerView matrixList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button submitButton;
    private NavigationView navigationView;
    private DrawerLayout drawer;

    private ArrayList<Unit> sites;
    private User residentData;

    private boolean isRunning = true;
    private boolean backdated = false;
    private MatrixTableAdapterV2 matrixTableAdapter;

    MenuItem yesterdayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isRunning = true;
        getUserInfo();
//        getLocation();

        TextView comingSoon = (TextView) findViewById(R.id.coming_soon);
        if (comingSoon != null) {
            comingSoon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (drawer != null) {
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    }
                    AlertDialogLogout.newInstance("Logout", "Do you want to logout?", "No", "Logout")
                            .show(getFragmentManager(), "logout");
                }
            });
        }
    }

    private void checkForUpdate() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(UPDATE_METRIC_SENSOR_LIST_ROW, new IntentFilter("UPDATE_METRIC_SENSOR_LIST_ROW"));
        isRunning = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        checkForUpdate();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(UPDATE_METRIC_SENSOR_LIST_ROW);
        isRunning = false;
        super.onDestroy();
    }

    @Override
    protected int getStatusBarColor() {
        return 0;
    }

    @Override
    public void onBackPressed() {
        if (backdated) {
            backdated = false;
            submitButton.setVisibility(View.VISIBLE);
            add_previous_reading.setVisibility(View.GONE);

            yesterdayButton.setTitle("Add Yesterday's Reading");

            getSupportActionBar().setTitle(GlobalData.getInstance().getSelectedUnit());
            if (NetworkUtilities.isInternet(getApplicationContext())) {
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                        getMatrixRequest(GlobalData.getInstance().getSelectedUnitId(), GlobalData.getInstance().getSubDomain());
                    }
                });
            } else {
                setOfflineData();
            }
        } else {
            if (drawer.isDrawerOpen(navigationView)) {
                drawer.closeDrawers();
            } else {
                super.onBackPressed();
            }
        }
    }

    public void refreshLayout() {
        if (NetworkUtilities.isInternet(this)) {
            swipeRefreshLayout.setRefreshing(true);
            setOfflineData();
            getMatrixRequest(GlobalData.getInstance().getSelectedUnitId(), GlobalData.getInstance().getSubDomain());
        } else {
            swipeRefreshLayout.setRefreshing(true);
//            setOfflineData();
            getDataFromPreference();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setOfflineData() {
        List<MatrixTable> list = MatrixTable.getMatrixList(GlobalData.getInstance().getSelectedUnitId());
        if (list != null && list.size() > 0) {
            setDataInList(list);
        }
    }

    private void unCheckAllMenuItems(NavigationView navigationView) {
        final Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.hasSubMenu()) {
                SubMenu subMenu = item.getSubMenu();
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    subMenuItem.setChecked(false);
                }
            } else {
                item.setChecked(false);
            }
        }
    }

    private void setDataInHeader(NavigationView navigationView) {

        navigationView.inflateHeaderView(R.layout.nav_header_main);
        View view = navigationView.getHeaderView(0);
        ImageView companyLogo = (ImageView) view.findViewById(R.id.companyLogo);
        TextView companyName = (TextView) view.findViewById(R.id.companyName);
        TextView userName = (TextView) view.findViewById(R.id.userName);

        companyLogo.setImageResource(R.mipmap.ic_launcher);
        companyName.setText(residentData.getCompany().getName());
        userName.setText(residentData.getName());

        navigationView.setBackgroundResource(R.drawable.list_drawer_item_activity_bg_selector);
    }

    //Do network call to fetch matrix data
    private void getMatrixRequest(String siteId, String subDomain) {

        if (NetworkUtilities.isInternet(MainActivityV2.this)) {

            ApiInterface apiInterface = ServiceGeneratorV2.createService(ApiInterface.class);
            Call<MatrixResponse> call = apiInterface.getMetrics(siteId, subDomain);

            call.enqueue(new Callback<MatrixResponse>() {
                @Override
                public void onResponse(Call<MatrixResponse> call, final Response<MatrixResponse> response) {

                    if (response.isSuccessful()) {
//                        deleteAllMatrixData();
                        setMatrixData(response.body());

                    } else if (response.code() == 401) {
                        handleAuthorizationFailed();
                        swipeRefreshLayout.setRefreshing(false);
                    } else {

                        if (matrixTableAdapter != null) {
                            APIError error = ErrorUtils.parseError(response);
                            swipeRefreshLayout.setRefreshing(false);
                            no_data_found.setText(error.message());
                            no_data_found.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<MatrixResponse> call, Throwable t) {
                    swipeRefreshLayout.setRefreshing(false);
                    if (matrixTableAdapter != null) {
                        no_data_found.setText(getResources().getString(R.string.error_in_network));
                        no_data_found.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            swipeRefreshLayout.setRefreshing(false);
            displayMessage(getString(R.string.error_no_internet_connection));
            no_data_found.setText(getString(R.string.error_no_internet_connection));
            no_data_found.setVisibility(View.VISIBLE);
        }
    }

    private void deleteAllMatrixData() {
        List<MatrixTable> matrixTables = MatrixTable.getMatrixList(GlobalData.getInstance().getSelectedUnitId());
        for (int i = 0; i < matrixTables.size(); i++) {
            new Delete().from(SensorTable.class).where("utility_identifier = ?", matrixTables.get(i).utility_id).execute();
        }

        new Delete().from(MatrixTable.class).where("unit_id = ?", GlobalData.getInstance().getSelectedUnitId()).execute();
        new Delete().from(TareWeightTable.class).where("unit_id = ?", GlobalData.getInstance().getSelectedUnitId()).execute();
    }

    //Set matrix data to list
    private void setMatrixData(MatrixResponse response) {

        swipeRefreshLayout.setRefreshing(false);

        ArrayList<MatrixData> matrixData = response.getFinalMetrics();
        setDataToPreference(matrixData);

        matrixTableAdapter = new MatrixTableAdapterV2(this, matrixData, backdated);
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(MainActivityV2.this, 3);
        matrixList.setLayoutManager(gridLayoutManager);
        matrixList.setAdapter(matrixTableAdapter);

    }

    private void getDataFromPreference() {

        SharedPreferences ReadingInfo = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String MatrixResponseString = ReadingInfo.getString("MatrixResponse", "");

        if (!MatrixResponseString.isEmpty()) {
            Gson gson = new Gson();
            MatrixResponse readingDataResponse = gson.fromJson(MatrixResponseString, MatrixResponse.class);

            matrixTableAdapter = new MatrixTableAdapterV2(this, readingDataResponse.getMetrics(), backdated);
            RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(MainActivityV2.this, 3);
            matrixList.setLayoutManager(gridLayoutManager);
            matrixList.setAdapter(matrixTableAdapter);
        }
    }

    private void setDataToPreference(ArrayList<MatrixData> matrixData) {

        Gson gson = new Gson();
        MatrixResponse response = new MatrixResponse();
        response.setMetrics(matrixData);
        String MatrixResponseString = gson.toJson(response);
        SharedPreferences ReadingInfo = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = ReadingInfo.edit();
        preferencesEditor.putString("MatrixResponse", MatrixResponseString);
        preferencesEditor.apply();
    }

    private void setDataInList(List<MatrixTable> list) {

//        Collections.sort(list, new Comparator<MatrixTable>() {
//            @Override
//            public int compare(MatrixTable lhs, MatrixTable rhs) {
//                return lhs.order - rhs.order;
//            }
//        });
//
//        matrixTableAdapter = new MatrixTableAdapter(this, list, backdated);
//        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(MainActivityV2.this, 2);
//        matrixList.setLayoutManager(gridLayoutManager);
//        matrixList.setAdapter(matrixTableAdapter);
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
        residentData = gson.fromJson(jsonUnitInfo, User.class);

        String jsonAccessPolicy = UserInfo.getString("access_policy", "");
        AccessPolicy policy = gson.fromJson(jsonAccessPolicy, AccessPolicy.class);

        if (residentData.getSites() != null && residentData.getSites().size() > 0) {
            GlobalData.getInstance().setSites(residentData.getSites());
            GlobalData.getInstance().setSelectedUnitId(residentData.getSites().get(0).getId());
            GlobalData.getInstance().setSelectedUnit(residentData.getSites().get(0).getName());
            initializeRemainingStuff();
        } else {
            AlertDialogStandard.newInstance(getString(R.string.app_name), "No data found for this user.", "", "CLOSE")
                    .show(getFragmentManager(), "noDataFound");
        }
//        GlobalData.getInstance().setAccessPolicy(policy);
    }

    private void initializeRemainingStuff() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(GlobalData.getInstance().getSelectedUnit());

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        setDataInHeader(navigationView);

        Menu menu = navigationView.getMenu();
        sites = GlobalData.getInstance().getSites();
        for (int i = 0; i < sites.size(); i++) {
            menu.add(R.id.plants, i, StringConstants.ORDER_PLANTS, sites.get(i).getName()).setIcon(R.drawable.plant_icon);
        }

        yesterdayButton = menu.findItem(R.id.backdated);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        submitButton = (Button) findViewById(R.id.submit);
        no_data_found = (TextView) findViewById(R.id.no_data_found);
        matrixList = (RecyclerView) findViewById(R.id.matrixList);
        add_previous_reading = (TextView) findViewById(R.id.add_previous_reading);

        String buttonText;
        Calendar calendar = Calendar.getInstance();
        long time = calendar.getTimeInMillis();
        calendar.setTimeInMillis(time);
        String month;
        int dateOfMonth;

//        ReadingTable readings = new Select().from(ReadingTable.class).executeSingle();
//        if (readings != null) {
//            String oldDate = getDate(readings.timestamp, "dd-MM-yyyy");
//            String newDate = getDate(Calendar.getInstance().getTimeInMillis(), "dd-MM-yyyy");
//            if (!oldDate.equals(newDate)) {
//                calendar.setTimeInMillis(time - 86400000);
//                month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
//                dateOfMonth = calendar.get(Calendar.DATE);
//                buttonText = "Submit readings for " + dateOfMonth + " " + month;
//            } else {
//                month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
//                dateOfMonth = calendar.get(Calendar.DATE);
//                buttonText = "Submit readings for " + dateOfMonth + " " + month;
//            }
//            buttonText = "Submit readings";
//        } else {
            month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
            dateOfMonth = calendar.get(Calendar.DATE);
            buttonText = "Submit readings for " + dateOfMonth + " " + month;
//        }

        submitButton.setText(buttonText);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivityV2.this, SummaryActivityV2.class);
                startActivity(i);
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                drawer.closeDrawers();

                switch (item.getItemId()) {
                    case R.id.attendance:
                        Intent i = new Intent(MainActivityV2.this, AttendanceActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.visitors:
                        Intent i2 = new Intent(MainActivityV2.this, VisitorActivity.class);
                        startActivity(i2);
                        return true;
//                    case R.id.complaints:
//                        return true;
                    case R.id.backdated:
                        setUpActivityForBackdatedEntries();
                        return true;
                    default:
                        int id = item.getItemId();
                        Unit unit = sites.get(id);

                        backdated = false;
                        submitButton.setVisibility(View.VISIBLE);
                        add_previous_reading.setVisibility(View.GONE);
                        unCheckAllMenuItems(navigationView);
                        item.setChecked(true);
                        GlobalData.getInstance().setSelectedUnitId(unit.getId());
                        GlobalData.getInstance().setSelectedUnit(unit.getName());

                        getSupportActionBar().setTitle(GlobalData.getInstance().getSelectedUnit());
                        if (NetworkUtilities.isInternet(getApplicationContext())) {
                            swipeRefreshLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefreshLayout.setRefreshing(true);
                                    getMatrixRequest(GlobalData.getInstance().getSelectedUnitId(), GlobalData.getInstance().getSubDomain());
                                }
                            });
                        } else {
//                            setOfflineData();
                            getDataFromPreference();
                        }
                        return true;
                }
            }

        });
    }

    private void setUpActivityForBackdatedEntries() {

        if (add_previous_reading.getVisibility() == View.VISIBLE) {
            yesterdayButton.setTitle("Add Yesterday's Reading");
            add_previous_reading.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
            backdated = false;
            if (matrixTableAdapter != null) {
                matrixTableAdapter.setBackdated(false);
            }
        } else {
            yesterdayButton.setTitle("Add Today's Reading");
            add_previous_reading.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
            backdated = true;
            if (matrixTableAdapter != null) {
                matrixTableAdapter.setBackdated(true);
            }
        }
    }

    @Override
    public void logoutUser() {
        logout();
        finish();
    }

    @Override
    public LocationConfiguration getLocationConfiguration() {
        return new LocationConfiguration()
                .keepTracking(true)
                .askForGooglePlayServices(true)
                .askForSettingsApi(true)
                .failOnConnectionSuspended(true)
                .failOnSettingsApiSuspended(false)
                .doNotUseGooglePlayServices(false)
                .askForEnableGPS(true)
                .setMinAccuracy(200.0f)
                .setWaitPeriod(ProviderType.GOOGLE_PLAY_SERVICES, 5 * 1000)
                .setWaitPeriod(ProviderType.GPS, 10 * 1000)
                .setWaitPeriod(ProviderType.NETWORK, 5 * 1000)
                .setGPSMessage("Would you mind to turn GPS on?")
                .setRationalMessage("Gimme the permission!");
    }

    @Override
    public void onLocationFailed(int failType) {

        switch (failType) {
            case FailType.PERMISSION_DENIED: {
                DebugLog.d("Couldn't get location, because user didn't give permission!");
                break;
            }
            case FailType.GP_SERVICES_NOT_AVAILABLE:
            case FailType.GP_SERVICES_CONNECTION_FAIL: {
                DebugLog.d("Couldn't get location, because Google Play Services not available!");
                break;
            }
            case FailType.NETWORK_NOT_AVAILABLE: {
                DebugLog.d("Couldn't get location, because network is not accessible!");
                break;
            }
            case FailType.TIMEOUT: {
                DebugLog.d("Couldn't get location, and timeout!");
                break;
            }
            case FailType.GP_SERVICES_SETTINGS_DENIED: {
                DebugLog.d("Couldn't get location, because user didn't activate providers via settingsApi!");
                break;
            }
            case FailType.GP_SERVICES_SETTINGS_DIALOG: {
                DebugLog.d("Couldn't display settingsApi dialog!");
                break;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        DebugLog.d("coordinates : " + location.getLatitude() + "," + location.getLongitude());
        GlobalData.getInstance().setCoordinates(location.getLatitude() + "," + location.getLongitude());

        LocationAddress.getAddressFromLocation(location.getLatitude(), location.getLongitude(), MainActivityV2.this, addressHandler);
    }

    // Handler for get user's current location data
    @SuppressLint("HandlerLeak")
    private final Handler addressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    Bundle bundle = msg.getData();
                    if (!String.valueOf(bundle.getDouble("Latitude")).trim().equalsIgnoreCase("0.0")) {
                        GlobalData.getInstance().setUserCurrentLocationAddress(bundle.getString("address", ""));
                    }
                    break;
                default:
                    break;
            }
        }
    };

}
