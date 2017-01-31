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
import com.simplysmart.service.fragment.TodaySummaryFragment;
import com.simplysmart.service.fragment.YesterdaySummaryFragment;

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
                Intent i = new Intent(this, MainActivity.class);
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
        tabAdapter = new TabAdapter(getSupportFragmentManager(), this);
        tabAdapter.addFragment(new TodaySummaryFragment(), "Today");
        tabAdapter.addFragment(new YesterdaySummaryFragment(), "Yesterday");
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

}
