package com.simplysmart.service.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.simplysmart.service.R;
import com.simplysmart.service.adapter.SensorListAdapter;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.model.matrix.MatrixData;
import com.simplysmart.service.model.matrix.SensorData;

import java.util.ArrayList;

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

        getSupportActionBar().setTitle(GlobalData.getInstance().getSelectedUnit());

        if (getIntent() != null && getIntent().getExtras() != null) {
            data = getIntent().getParcelableExtra(StringConstants.METRIC_DATA);
            if (data != null) {
                if (data.getSensors() != null && data.getSensors().size() > 0) {
                    sensors = data.getSensors();
                    initializeWidgets();
                }
            }
        }

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

    private void initializeWidgets() {
        RecyclerView sensorList = (RecyclerView) findViewById(R.id.sensorList);
        SensorListAdapter adapter = new SensorListAdapter(SensorListActivity.this, sensors);
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(SensorListActivity.this, 2);
        sensorList.setLayoutManager(gridLayoutManager);
        sensorList.setAdapter(adapter);
    }

    @Override
    protected int getStatusBarColor() {
        return 0;
    }


}
