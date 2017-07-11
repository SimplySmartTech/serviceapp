package com.simplysmart.service.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.simplysmart.service.R;
import com.simplysmart.service.adapter.CallPagerAdapter;
import com.simplysmart.service.common.VersionComprator;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.dialog.AlertDialogLogout;
import com.simplysmart.service.dialog.AlertDialogStandard;
import com.simplysmart.service.dialog.AlertDialogUpdateVersion;
import com.simplysmart.service.fragment.StatusInfoTodayList;
import com.simplysmart.service.interfaces.LogoutListener;
import com.simplysmart.service.model.user.AccessPolicy;
import com.simplysmart.service.model.user.Unit;
import com.simplysmart.service.model.user.User;
import com.simplysmart.service.service.FetchCategories;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SiteStatusScreenActivity extends BaseActivity implements LogoutListener {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ViewPager mPager;


    private User residentData;
    private boolean isRunning = true;
    private boolean isMenuSelected = true;

    private ImageView arrowIcon;
    private TextView unitName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_sensor_list);

        isRunning = true;
        getUserInfo();

        Intent msgIntent = new Intent(this, FetchCategories.class);
        startService(msgIntent);

        setSensorTab();
    }

    private void checkForUpdate() {
        GetVersionCode getVersionCode = new GetVersionCode();
        getVersionCode.execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        isRunning = false;
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                invalidateOptionsMenu();
                if (getFragmentManager().getBackStackEntryCount() > 1) {
                    getFragmentManager().popBackStack();
                } else {
                    super.onBackPressed();
                }
                break;
        }
        return true;
    }

    @Override
    protected int getStatusBarColor() {
        return 0;
    }

    private void getUserInfo() {

        SharedPreferences UserInfo = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        GlobalData.getInstance().setAuthToken(UserInfo.getString("auth_token", ""));
        GlobalData.getInstance().setApi_key(UserInfo.getString("api_key", ""));
        GlobalData.getInstance().setSubDomain(UserInfo.getString("subdomain", ""));
        GlobalData.getInstance().setRole_code(UserInfo.getString("role_code", ""));

        Gson gson = new Gson();
        String jsonUnitInfo = UserInfo.getString("unit_info", "");
        residentData = gson.fromJson(jsonUnitInfo, User.class);

        if (residentData.getSites() != null && residentData.getSites().size() > 0) {
            GlobalData.getInstance().setSites(residentData.getSites());
            GlobalData.getInstance().setSelectedUnitId(residentData.getSites().get(0).getId());
            GlobalData.getInstance().setSelectedUnit(residentData.getSites().get(0).getName());
        } else {
            AlertDialogStandard.newInstance(getString(R.string.app_name), "No data found for this user.", "", "CLOSE")
                    .show(getFragmentManager(), "noDataFound");
        }

        GlobalData.getInstance().setAccessPolicy(residentData.getPolicy());
        GlobalData.getInstance().setUserId(residentData.getId());

        initializeRemainingStuff();
    }

    private void initializeRemainingStuff() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Readings");

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_v2);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view_v2);

        setDataInHeader(navigationView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawer.closeDrawers();

                if (!isMenuSelected) {

                    ArrayList<Unit> sites = GlobalData.getInstance().getSites();
                    if (sites.size() < 0) return true;

                    for (int i = 0; i < navigationView.getMenu().size(); i++) {

                        if (item == navigationView.getMenu().getItem(i)) {

                            GlobalData.getInstance().setSelectedUnitId(residentData.getSites().get(i).getId());
                            GlobalData.getInstance().setSelectedUnit(residentData.getSites().get(i).getName());
                            unitName.setText(GlobalData.getInstance().getSelectedUnit());

                            getSupportActionBar().setTitle(GlobalData.getInstance().getSelectedUnit());

                            //send local broadcast message to HomeScreenFragment for update unit name.
                            Intent intent = new Intent("UnitName");
                            intent.putExtra("name", GlobalData.getInstance().getSelectedUnit());
                            LocalBroadcastManager.getInstance(SiteStatusScreenActivity.this).sendBroadcast(intent);
                            break;
                        }
                    }
                }
                return true;
            }
        });

    }

    private void setSensorTab() {

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mPager = (ViewPager) findViewById(R.id.pager);

        List<android.support.v4.app.Fragment> fragments = new Vector<>();

        tabLayout.setVisibility(View.VISIBLE);
        mPager.setVisibility(View.VISIBLE);

        tabLayout.removeAllTabs();
        mPager.removeAllViews();

        tabLayout.addTab(tabLayout.newTab().setText("Today"));
//        tabLayout.addTab(tabLayout.newTab().setText("Yesterday"));

        fragments.add(android.support.v4.app.Fragment.instantiate(this, StatusInfoTodayList.class.getName()));
//        fragments.add(android.support.v4.app.Fragment.instantiate(this, SensorInfoList.class.getName()));

        tabLayout.setVisibility(View.GONE);

        CallPagerAdapter pagerAdapter = new CallPagerAdapter(getSupportFragmentManager(), fragments);
        mPager.setAdapter(pagerAdapter);

        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void logoutUser() {
        logout();
        finish();
    }

    private void setDataInHeader(NavigationView navigationView) {

        navigationView.inflateHeaderView(R.layout.nav_header_main);
        View view = navigationView.getHeaderView(0);
        ImageView companyLogo = (ImageView) view.findViewById(R.id.companyLogo);
        TextView companyName = (TextView) view.findViewById(R.id.companyName);
        TextView userName = (TextView) view.findViewById(R.id.userName);

        RelativeLayout selectedUnitLayout = (RelativeLayout) view.findViewById(R.id.selectedUnitLayout);
        TextView unitIcon = (TextView) view.findViewById(R.id.unitIcon);
        unitName = (TextView) view.findViewById(R.id.unitName);
        arrowIcon = (ImageView) view.findViewById(R.id.arrowIcon);

        companyLogo.setImageResource(R.drawable.ic_launcher);
        companyName.setText(residentData.getCompany().getName());
        userName.setText(residentData.getName());

        if (GlobalData.getInstance().getSites().size() > 0) {
            unitName.setText(GlobalData.getInstance().getSites().get(0).getName());
            getSupportActionBar().setTitle(GlobalData.getInstance().getSites().get(0).getName());
        }

        loadMenus();

        navigationView.setBackgroundResource(R.drawable.list_drawer_item_activity_bg_selector);

        selectedUnitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMenuSelected) {
                    loadAvailableSites();
                } else {
                    loadMenus();
                }
            }
        });

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

    private void loadAvailableSites() {

        Menu menu = navigationView.getMenu();
        menu.clear();

        ArrayList<Unit> sites = GlobalData.getInstance().getSites();
        if (sites.size() < 0) {
            return;
        }
        for (int i = 0; i < sites.size(); i++) {
            menu.add(R.id.plants, i, StringConstants.ORDER_PLANTS, sites.get(i).getName()).setIcon(R.drawable.plant_icon);
        }
        arrowIcon.setImageResource(R.drawable.ic_down_arrow);
        isMenuSelected = false;
    }

    private void loadMenus() {

        Menu menu = navigationView.getMenu();
        menu.clear();

        ArrayList<AccessPolicy> menus = residentData.getPolicy();
        if (menus.size() < 0) {
            return;
        }
        for (int i = 0; i < menus.size(); i++) {
            menu.add(R.id.plants, i, StringConstants.ORDER_PLANTS, menus.get(i).getName()).setIcon(R.drawable.plant_icon);
        }
        arrowIcon.setImageResource(R.drawable.ic_up_arrow);
        isMenuSelected = true;
    }

    private class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + SiteStatusScreenActivity.this.getPackageName() + "&hl=it")
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
    public void onBackPressed() {
        invalidateOptionsMenu();
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
