package com.simplysmart.service.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.simplysmart.service.R;
import com.simplysmart.service.adapter.TabAdapter;
import com.simplysmart.service.common.CommonMethod;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.database.ReadingTable;
import com.simplysmart.service.fragment.TodaySummaryFragment;
import com.simplysmart.service.fragment.YesterdaySummaryFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by shekhar on 4/11/16.
 */

public class SummaryActivity extends BaseActivity {

    private TabAdapter tabAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reading Summary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        bindViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.summary_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Intent i = new Intent(this, MainActivityV2.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                supportFinishAfterTransition();
                return true;

            default:
                return false;
        }
    }

    @Override
    protected int getStatusBarColor() {
        return 0;
    }

    private void bindViews() {

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        setupViewPager();

        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
//                    if (tab.getPosition() == 1) {
//                        PageSelectedListener pageSelectedListener = (PageSelectedListener) tabAdapter.instantiateItem(viewPager, tab.getPosition());
//                        pageSelectedListener.onPageSelected();
//                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }

    private void setupViewPager() {

        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Long> timeStamps = new ArrayList<>();
        List<ReadingTable> allReadings = ReadingTable.getAllReadings(GlobalData.getInstance().getSelectedUnitId());

        for (int i = 0; i < allReadings.size(); i++) {

            String date = allReadings.get(i).date_of_reading;
            if (dates.size() > 0) {
                if (!dates.contains(date)) {
                    dates.add(date);
                    timeStamps.add(allReadings.get(i).timestamp);
                }
            } else {
                dates.add(date);
                timeStamps.add(allReadings.get(i).timestamp);
            }
        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateForReadings = sdf.format(c.getTimeInMillis());

        tabAdapter = new TabAdapter(getSupportFragmentManager(), this);

        if (timeStamps.size() > 1) {

            String date1 = getDate(timeStamps.get(0), "dd-MM-yyyy");
            String date2 = getDate(timeStamps.get(1), "dd-MM-yyyy");
            String today = getDate(Calendar.getInstance().getTimeInMillis(), "dd-MM-yyyy");

            if (date1.equals(today) || date2.equals(today)) {
                tabAdapter.addFragment(new TodaySummaryFragment(), "Today");
                tabAdapter.addFragment(new YesterdaySummaryFragment(), "Yesterday");
            } else {
                if (CommonMethod.diffInDays(date1, "dd-MM-yyyy", date2, "dd-MM-yyyy") > 0) {
                    tabAdapter.addFragment(new TodaySummaryFragment(), date1);
                    tabAdapter.addFragment(new YesterdaySummaryFragment(), date2);
                } else {
                    tabAdapter.addFragment(new TodaySummaryFragment(), date2);
                    tabAdapter.addFragment(new YesterdaySummaryFragment(), date1);
                }
            }
        } else if (timeStamps.size() > 0) {

            if (CommonMethod.diffInDays(dates.get(0), "dd/MM/yyyy", dateForReadings, "dd/MM/yyyy") == -1) {
                tabAdapter.addFragment(new TodaySummaryFragment(), dates.get(0));
                tabAdapter.addFragment(new YesterdaySummaryFragment(), CommonMethod.getNextPrevDate(dates.get(0), "dd/MM/yyyy", true));
            } else if (CommonMethod.diffInDays(dates.get(0), "dd/MM/yyyy", dateForReadings, "dd/MM/yyyy") <= -2) {
                tabAdapter.addFragment(new TodaySummaryFragment(), CommonMethod.getNextPrevDate(dates.get(0), "dd/MM/yyyy", false));
                tabAdapter.addFragment(new YesterdaySummaryFragment(), dates.get(0));
            } else {
                tabAdapter.addFragment(new TodaySummaryFragment(), "Today");
                tabAdapter.addFragment(new YesterdaySummaryFragment(), "Yesterday");
            }
        } else {
            tabAdapter.addFragment(new TodaySummaryFragment(), "Today");
            tabAdapter.addFragment(new YesterdaySummaryFragment(), "Yesterday");
        }

        viewPager.setAdapter(tabAdapter);
        viewPager.setCurrentItem(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position);
//                if (position == 1) {
//                    PageSelectedListener pageSelectedListener = (PageSelectedListener) tabAdapter.instantiateItem(viewPager, position);
//                    pageSelectedListener.onPageSelected();
//                    CommonMethod.hideKeyboard(SummaryActivity.this);
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

}
