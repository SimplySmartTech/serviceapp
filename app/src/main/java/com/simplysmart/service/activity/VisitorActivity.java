package com.simplysmart.service.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.simplysmart.service.R;
import com.simplysmart.service.adapter.TabAdapter;
import com.simplysmart.service.common.CommonMethod;
import com.simplysmart.service.fragment.NewVisitorFragment;
import com.simplysmart.service.fragment.VisitorListFragment;
import com.simplysmart.service.interfaces.ForceScrollListener;
import com.simplysmart.service.interfaces.PageSelectedListener;

/**
 * Created by shekhar on 27/12/16.
 */

public class VisitorActivity extends BaseActivity implements ForceScrollListener {

    private TabAdapter tabAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle(getString(R.string.visitor));

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        setupViewPager();

        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                    if (tab.getPosition() == 1) {
                        PageSelectedListener pageSelectedListener = (PageSelectedListener) tabAdapter.instantiateItem(viewPager, tab.getPosition());
                        pageSelectedListener.onPageSelected();
                    }
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
        tabAdapter.addFragment(new NewVisitorFragment(), "New Visitor");
        tabAdapter.addFragment(new VisitorListFragment(), "Visitor List");
        viewPager.setAdapter(tabAdapter);
        viewPager.setCurrentItem(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position);
                if (position == 1) {
                    PageSelectedListener pageSelectedListener = (PageSelectedListener) tabAdapter.instantiateItem(viewPager, position);
                    pageSelectedListener.onPageSelected();
                    CommonMethod.hideKeyboard(VisitorActivity.this);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                supportFinishAfterTransition();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getStatusBarColor() {
        return 0;
    }

    @Override
    public void scrollTo() {
        viewPager.setCurrentItem(1);
        PageSelectedListener pageSelectedListener = (PageSelectedListener) tabAdapter.instantiateItem(viewPager, 1);
        pageSelectedListener.onPageSelected();
        CommonMethod.hideKeyboard(this);
    }
}
