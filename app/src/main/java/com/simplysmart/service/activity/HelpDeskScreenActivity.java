package com.simplysmart.service.activity;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.simplysmart.service.R;
import com.simplysmart.service.adapter.HelpdeskListAdapter;
import com.simplysmart.service.callback.ApiCallback;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.common.LocationAddress;
import com.simplysmart.service.common.VersionComprator;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.dialog.AlertDialogLogout;
import com.simplysmart.service.dialog.AlertDialogUpdateVersion;
import com.simplysmart.service.fragment.HelpDeskScreenClose;
import com.simplysmart.service.interfaces.LogoutListener;
import com.simplysmart.service.model.helpdesk.ComplaintLists;
import com.simplysmart.service.model.helpdesk.HelpDeskResponse;
import com.simplysmart.service.model.user.User;
import com.simplysmart.service.request.CreateRequest;
import com.simplysmart.service.service.FetchCategories;
import com.yayandroid.locationmanager.LocationConfiguration;
import com.yayandroid.locationmanager.constants.FailType;
import com.yayandroid.locationmanager.constants.ProviderType;

import org.jsoup.Jsoup;

import java.util.ArrayList;

public class HelpDeskScreenActivity extends GetLocationBaseActivity implements LogoutListener {

    private TextView no_data_found;
    private ListView complaintList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private HelpdeskListAdapter complaintsListAdapter;

    private User residentData;

    private boolean isRunning = true;
    private boolean isHome = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__v2);

        isRunning = true;
        getUserInfo();

        Intent msgIntent = new Intent(this, FetchCategories.class);
        startService(msgIntent);
    }

    private void checkForUpdate() {
        GetVersionCode getVersionCode = new GetVersionCode();
        getVersionCode.execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isRunning = true;
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(UpdateDetails,
                new IntentFilter("UpdateDetails"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
//        checkForUpdate();
        getComplaintResponse();

    }

    @Override
    protected void onDestroy() {
        isRunning = false;
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(UpdateDetails);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem reset = menu.findItem(R.id.action_history);
        reset.setVisible(isHome);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                isHome = true;
                invalidateOptionsMenu();
                if (getFragmentManager().getBackStackEntryCount() > 1) {
                    getFragmentManager().popBackStack();
                } else {
                    super.onBackPressed();
                }
                break;
            case R.id.action_history:
                isHome = false;
                invalidateOptionsMenu();
                Fragment historyComplaint = new HelpDeskScreenClose();
                getFragmentManager().beginTransaction().addToBackStack(null)
                        .replace(R.id.llContent, historyComplaint).commitAllowingStateLoss();
                break;

            case R.id.logout:
                AlertDialogLogout.newInstance("Logout", "Do you want to logout?", "No", "Logout")
                        .show(getFragmentManager(), "logout");
                break;
        }
        return true;
    }

    @Override
    protected int getStatusBarColor() {
        return 0;
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

        Log.d("Json Unit :", gson.toJson(residentData));

        GlobalData.getInstance().setAccessPolicy(residentData.getPolicy());

        initializeRemainingStuff();
    }

    private void initializeRemainingStuff() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Complaints");

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_v2);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view_v2);

        setDataInHeader(navigationView);

        Menu menu = navigationView.getMenu();
//        units = GlobalData.getInstance().getUnits();

        for (int i = 0; i < residentData.getPolicy().size(); i++) {
            menu.add(R.id.plants, i, StringConstants.ORDER_PLANTS, residentData.getPolicy().get(i).getName()).setIcon(R.drawable.plant_icon);
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        no_data_found = (TextView) findViewById(R.id.no_data_found);
        complaintList = (ListView) findViewById(R.id.complaintList);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                swipeRefreshLayout.setRefreshing(true);
                getComplaintResponse();

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getComplaintResponse();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                return false;
            }
        });
    }

    @Override
    public void logoutUser() {
        logout();
        finish();
    }

    public void getComplaintResponse() {

        CreateRequest.getInstance().getComplaintList("Pending", "1", new ApiCallback<HelpDeskResponse>() {
            @Override
            public void onSuccess(HelpDeskResponse response) {

                if (response.getData().hasComplaints()) {
                    swipeRefreshLayout.setRefreshing(false);
                    complaintList.setVisibility(View.VISIBLE);
                    no_data_found.setVisibility(View.GONE);
                    setComplaintsData(response);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    complaintList.setVisibility(View.GONE);
                    no_data_found.setVisibility(View.VISIBLE);
                    no_data_found.setText("No complaint assigned to you.");
                }

            }

            @Override
            public void onFailure(String error) {
                swipeRefreshLayout.setRefreshing(false);
                no_data_found.setVisibility(View.VISIBLE);
                no_data_found.setText(error);
            }
        });
    }

    public void setComplaintsData(HelpDeskResponse complaintsResponse) {
        ArrayList<ComplaintLists> complaintDataArrayList = complaintsResponse.getData().getComplaintLists();
        Log.d("Response Size:", ":" + complaintDataArrayList.size());
        setComplaintsDatainList(complaintDataArrayList);
    }

    public void setComplaintsDatainList(ArrayList<ComplaintLists> complaintDataList) {

        complaintsListAdapter = new HelpdeskListAdapter(this, complaintDataList);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.canScrollVertically();
        //complaintList.setLayoutManager(linearLayoutManager);

        complaintList.setAdapter(complaintsListAdapter);
        complaintList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!complaintsListAdapter.getData().get(position).getAasm_state().equalsIgnoreCase("")) {
                    if (NetworkUtilities.isInternet(HelpDeskScreenActivity.this)) {
                        Intent detailedActivityIntent = new Intent(HelpDeskScreenActivity.this, ComplaintDetailScreenActivity.class);
                        detailedActivityIntent.putExtra("complaint_id", complaintsListAdapter.getData().get(position).getId());
                        startActivity(detailedActivityIntent);
                    }
                }
            }
        });
    }

    private void setDataInHeader(NavigationView navigationView) {

        navigationView.inflateHeaderView(R.layout.nav_header_main);
        View view = navigationView.getHeaderView(0);
        ImageView companyLogo = (ImageView) view.findViewById(R.id.companyLogo);
        TextView companyName = (TextView) view.findViewById(R.id.companyName);
        TextView userName = (TextView) view.findViewById(R.id.userName);

        companyLogo.setImageResource(R.drawable.ic_launcher);
        companyName.setText(residentData.getCompany().getName());
        userName.setText(residentData.getName());

        navigationView.setBackgroundResource(R.drawable.list_drawer_item_activity_bg_selector);
    }

    private class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + HelpDeskScreenActivity.this.getPackageName() + "&hl=it")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);

            String currentVersion = "";
            try {
                currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);

            if (onlineVersion != null && !onlineVersion.isEmpty()) {

                VersionComprator cmp = new VersionComprator();
                int result = cmp.compare(onlineVersion, currentVersion);

                if (result > 0 && isRunning) {
                    try {
                        AlertDialogUpdateVersion update = AlertDialogUpdateVersion.newInstance("New update available!", getResources().getString(R.string.update_app_message), "Later", "Update");
                        update.setCancelable(false);
                        update.show(getFragmentManager(), "Show update dialog");
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
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

        LocationAddress.getAddressFromLocation(location.getLatitude(), location.getLongitude(), HelpDeskScreenActivity.this, addressHandler);
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

    private BroadcastReceiver UpdateDetails = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            getComplaintResponse();
        }
    };

    @Override
    public void onBackPressed() {
        isHome = true;
        invalidateOptionsMenu();
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
