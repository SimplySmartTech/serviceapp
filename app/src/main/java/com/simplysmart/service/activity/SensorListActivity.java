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
import com.simplysmart.service.database.SensorTable;
import com.simplysmart.service.model.matrix.MatrixData;

import java.util.List;

public class SensorListActivity extends BaseActivity {

    private MatrixData data;
    private boolean backdated;

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
            backdated = getIntent().getBooleanExtra(StringConstants.BACKDATA,false);
            String utility_id = getIntent().getStringExtra(StringConstants.UTILITY_ID);
            if (utility_id != null && !utility_id.equalsIgnoreCase("")) {
                getSensorList(utility_id);
            }
        }

    }

    private void getSensorList(String utility_id){
        List<SensorTable> sensors = SensorTable.getSensorList(utility_id);
        RecyclerView sensorList = (RecyclerView) findViewById(R.id.sensorList);
        SensorListAdapter adapter = new SensorListAdapter(SensorListActivity.this, sensors,backdated);
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(SensorListActivity.this, 2);
        sensorList.setLayoutManager(gridLayoutManager);
        sensorList.setAdapter(adapter);
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


}
