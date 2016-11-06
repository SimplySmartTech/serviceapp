package com.simplysmart.service.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.simplysmart.service.R;
import com.simplysmart.service.database.MatrixDataRealm;
import com.simplysmart.service.database.ReadingDataRealm;
import com.simplysmart.service.database.SensorDataRealm;
import com.simplysmart.service.model.common.Summary;
import com.simplysmart.service.model.matrix.MatrixData;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by shailendrapsp on 4/11/16.
 */

public class SummaryActivity extends BaseActivity {

    private ListView summary;
    private ArrayList<Summary> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Summary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        summary = (ListView)findViewById(R.id.summary);
        data = new ArrayList<>();
        showActivitySpinner();
        setDataForSummary();
        dismissActivitySpinner();

    }

    private void setDataForSummary() {
        ArrayList<MatrixData> matrixList = MatrixDataRealm.getAll();
        if(matrixList!=null && matrixList.size()>0){
            for(int i=0;i<matrixList.size();i++){
                MatrixData data = matrixList.get(i);

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getStatusBarColor() {
        return 0;
    }
}
