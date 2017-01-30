package com.simplysmart.service.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.simplysmart.service.R;
import com.simplysmart.service.adapter.TabAdapter;
import com.simplysmart.service.dialog.SubmitReadingWithoutImageDialog;
import com.simplysmart.service.fragment.TodaySummaryFragment;
import com.simplysmart.service.fragment.YesterdaySummaryFragment;
import com.simplysmart.service.interfaces.EditDialogListener;
import com.simplysmart.service.interfaces.MandatoryReading;
import com.simplysmart.service.interfaces.SubmitWithoutInternet;

/**
 * Created by shailendrapsp on 4/11/16.
 */

public class SummaryActivity extends BaseActivity implements SubmitReadingWithoutImageDialog.SubmitWithoutImage, SubmitWithoutInternet, MandatoryReading, EditDialogListener {

    private TabAdapter tabAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Summary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        bindViews();
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
        tabAdapter.addFragment(new TodaySummaryFragment(), "New Visitor");
        tabAdapter.addFragment(new YesterdaySummaryFragment(), "Visitor List");
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

    @Override
    protected int getStatusBarColor() {
        return 0;
    }

    @Override
    public void submitWithoutImage() {

    }

    @Override
    public void updateResult(int done, int position, String value) {

    }

    @Override
    public void continueAhead() {

    }

    @Override
    public void submitWithoutInternet() {

    }
}
