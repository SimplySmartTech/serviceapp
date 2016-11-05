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
        Realm realm = Realm.getDefaultInstance();
        RealmList<MatrixDataRealm> matrixDataList = new RealmList<>();
        matrixDataList=MatrixDataRealm.getAll();
        if(matrixDataList.size()>0){
            for(int i=0;i<matrixDataList.size();i++){
                MatrixDataRealm mdr = matrixDataList.get(i);
                Summary s = new Summary();
                s.setValue(mdr.getType());
                s.setTab(1);
                data.add(s);

                RealmList<SensorDataRealm> sensorList = new RealmList<>();
                sensorList = SensorDataRealm.getForUtilityId(mdr.getUtility_id());
                for(int j=0;j<sensorList.size();j++){
                    SensorDataRealm sdr = sensorList.get(j);
                    Summary s2 = new Summary();
                    s2.setValue(sdr.getSensor_name());
                    s2.setTab(2);
                    data.add(s2);

                    RealmList<ReadingDataRealm> readingList = new RealmList<>();
                    readingList = ReadingDataRealm.findAllForThisSensor(sdr.getUtility_identifier(),sdr.getSensor_name());
                    if(readingList!=null) {
                        for (int k = 0; k < readingList.size(); k++) {
                            ReadingDataRealm rdr = readingList.get(k);
                            Summary s3 = new Summary();
                            s3.setValue(rdr.getValue() + " " + rdr.getUnit());
                            s3.setTab(3);
                            data.add(s3);
                        }
                    }
                }

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
