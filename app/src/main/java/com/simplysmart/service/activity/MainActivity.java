package com.simplysmart.service.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.simplysmart.service.adapter.MatrixListAdapter;
import com.simplysmart.service.adapter.MatrixTableAdapter;
import com.simplysmart.service.common.VersionComprator;
import com.simplysmart.service.config.ErrorUtils;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.ServiceGenerator;
import com.simplysmart.service.database.MatrixDataRealm;
import com.simplysmart.service.database.MatrixTable;
import com.simplysmart.service.database.ReadingDataRealm;
import com.simplysmart.service.database.SensorDataRealm;
import com.simplysmart.service.database.TareWeightRealm;
import com.simplysmart.service.dialog.AlertDialogLogout;
import com.simplysmart.service.dialog.AlertDialogStandard;
import com.simplysmart.service.dialog.AlertDialogUpdateVersion;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.interfaces.LogoutListener;
import com.simplysmart.service.model.common.APIError;
import com.simplysmart.service.model.matrix.MatrixData;
import com.simplysmart.service.model.matrix.MatrixResponse;
import com.simplysmart.service.model.matrix.SensorData;
import com.simplysmart.service.model.matrix.TareWeight;
import com.simplysmart.service.model.user.AccessPolicy;
import com.simplysmart.service.model.user.Unit;
import com.simplysmart.service.model.user.User;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements LogoutListener {


    private TextView no_data_found;
    private RecyclerView matrixList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MatrixListAdapter matrixListAdapter;
    private Button submitButton;
    private MatrixResponse matrixResponse;
    private int lastExpandedPosition = -1;
    private NavigationView navigationView;

    private boolean savedToDisk = false;
    private ArrayList<MatrixData> adapterData;

    private ArrayList<Unit> units;
    private User residentData;

    private boolean isRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUserInfo();
        isRunning = true;
        checkForUpdate();
    }

    private void checkForUpdate() {
        GetVersionCode getVersionCode = new GetVersionCode();
        getVersionCode.execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(UPDATE_METRIC_SENSOR_LIST_ROW, new IntentFilter("UPDATE_METRIC_SENSOR_LIST_ROW"));
        isRunning = true;
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
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        GetVersionCode getVersionCode = new GetVersionCode();
        getVersionCode.execute();
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
                AlertDialogLogout.newInstance("Logout", "Do you want to logout?", "No", "Logout")
                        .show(getFragmentManager(), "logout");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshLayout() {
        if (NetworkUtilities.isInternet(this)) {
            swipeRefreshLayout.setRefreshing(true);
            getMatrixRequest(GlobalData.getInstance().getSelectedUnitId(), GlobalData.getInstance().getSubDomain());
        } else {
            swipeRefreshLayout.setRefreshing(true);
            setOfflineData();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setOfflineData() {
        List<MatrixTable> list = MatrixTable.getMatrixList(GlobalData.getInstance().getSelectedUnitId());
        setDataInList(list);
    }

    private void uncheckAllMenuItems(NavigationView navigationView) {
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

        companyLogo.setImageResource(R.drawable.ic_launcher);
        companyName.setText(residentData.getCompany().getName());
        userName.setText(residentData.getName());

        navigationView.setBackgroundResource(R.drawable.list_drawer_item_activity_bg_selector);
    }

    //Do network call to fetch matrix data
    private void getMatrixRequest(String unitId, String subDomain) {

        if (NetworkUtilities.isInternet(MainActivity.this)) {
            ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
            Call<MatrixResponse> call = apiInterface.getMetrics(unitId, subDomain);
            call.enqueue(new Callback<MatrixResponse>() {

                @Override
                public void onResponse(Call<MatrixResponse> call, final Response<MatrixResponse> response) {

                    if (response.isSuccessful()) {
                        deleteAllMatrixData();
                        setMatrixData(response.body());
                    } else if (response.code() == 401) {
                        handleAuthorizationFailed();
                    } else {
                        APIError error = ErrorUtils.parseError(response);
                        displayMessage(error.message());
                        no_data_found.setText(getResources().getString(R.string.error_in_network));
                        no_data_found.setVisibility(View.VISIBLE);
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<MatrixResponse> call, Throwable t) {
                    swipeRefreshLayout.setRefreshing(false);
                    displayMessage(getResources().getString(R.string.error_in_network));
                    no_data_found.setText(getResources().getString(R.string.error_in_network));
                    no_data_found.setVisibility(View.VISIBLE);
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
        new Delete().from(MatrixTable.class).where("unit_id = ?", GlobalData.getInstance().getSelectedUnitId()).execute();
    }

    //Set matrix data to list
    private void setMatrixData(MatrixResponse response) {
        ArrayList<MatrixData> matrixDataArrayList = response.getData();
        ArrayList<TareWeight> tareWeights = response.getTare_weights();

        try {
            for (int i = 0; i < matrixDataArrayList.size(); i++) {
                MatrixTable matrixTable = new MatrixTable(matrixDataArrayList.get(i), GlobalData.getInstance().getSelectedUnitId());
                matrixTable.save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<MatrixTable> matrixList;
        matrixList = MatrixTable.getMatrixList(GlobalData.getInstance().getSelectedUnitId());

        setDataInList(matrixList);
    }

    private void setDataInList(List<MatrixTable> list) {
        Collections.sort(list, new Comparator<MatrixTable>() {
            @Override
            public int compare(MatrixTable lhs, MatrixTable rhs) {
                return lhs.order-rhs.order;
            }
        });

        MatrixTableAdapter adapter = new MatrixTableAdapter(this,list);
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        matrixList.setLayoutManager(gridLayoutManager);
        matrixList.setAdapter(adapter);
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
        residentData = gson.fromJson(jsonUnitInfo, User.class);

        String jsonAccessPolicy = UserInfo.getString("access_policy", "");
        AccessPolicy policy = gson.fromJson(jsonAccessPolicy, AccessPolicy.class);

        if (residentData.getUnits() != null && residentData.getUnits().size() > 0) {
            GlobalData.getInstance().setUnits(residentData.getUnits());
            GlobalData.getInstance().setSelectedUnitId(residentData.getUnits().get(0).getId());
            GlobalData.getInstance().setSelectedUnit(residentData.getUnits().get(0).getName());
            initializeRemainingStuff();
        } else {
            AlertDialogStandard.newInstance(getString(R.string.app_name), "No data found for this user.", "", "CLOSE")
                    .show(getFragmentManager(), "noDataFound");
        }
        GlobalData.getInstance().setAccessPolicy(policy);
    }

    private void initializeRemainingStuff() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(GlobalData.getInstance().getSelectedUnit());

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setDataInHeader(navigationView);

        Menu menu = navigationView.getMenu();
        units = GlobalData.getInstance().getUnits();
        for (int i = 0; i < units.size(); i++) {
            menu.add(i, i, i, "");
        }

        menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            MenuItemCompat.setActionView(item, R.layout.nav_item_view);
            TextView plantName = (TextView) MenuItemCompat.getActionView(item).findViewById(R.id.plant_name);
            plantName.setText(units.get(i).getName());
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        submitButton = (Button) findViewById(R.id.submit);
        no_data_found = (TextView) findViewById(R.id.no_data_found);
        matrixList = (RecyclerView) findViewById(R.id.matrixList);

        String buttonText;
        Calendar calendar = Calendar.getInstance();
        long time = calendar.getTimeInMillis();
        calendar.setTimeInMillis(time);
        String month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        int dateOfMonth = calendar.get(Calendar.DATE);
        buttonText = "Submit readings for " + dateOfMonth + " " + month;

        Realm realm = Realm.getDefaultInstance();
        ReadingDataRealm readingDataRealm = realm.where(ReadingDataRealm.class).findFirst();
        if (readingDataRealm != null) {
            String oldDate = getDate(readingDataRealm.getTimestamp(), "dd-MM-yyyy");
            String newDate = getDate(Calendar.getInstance().getTimeInMillis(), "dd-MM-yyyy");

            if (!oldDate.equals(newDate)) {
                calendar.setTimeInMillis(time - 86400000);
                month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
                dateOfMonth = calendar.get(Calendar.DATE);
                buttonText = "Submit readings for " + dateOfMonth + " " + month;
            }
        }

        submitButton.setText(buttonText);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SummaryActivity.class);
                startActivity(i);
            }
        });

        if (NetworkUtilities.isInternet(this)) {
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
                int id = item.getItemId();
                Unit unit = units.get(id);

                uncheckAllMenuItems(navigationView);
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
                    setOfflineData();
                }
                return true;
            }
        });
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private void setPic(ImageView view, String image) {
        view.setVisibility(View.VISIBLE);
        Picasso.with(this).load(image)
                .placeholder(R.drawable.ic_menu_slideshow)
                .noFade()
                .resize(64, 64)
                .error(R.drawable.ic_menu_slideshow).into(view);
    }

    @Override
    public void logoutUser() {
        logout();
        finish();
    }


    private class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName() + "&hl=it")
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
                    AlertDialogUpdateVersion update = AlertDialogUpdateVersion.newInstance("New update available!", getResources().getString(R.string.update_app_message), "Later", "Update");
                    update.setCancelable(false);
                    update.show(getFragmentManager(), "Show update dialog");
                }
            }
        }
    }

}
