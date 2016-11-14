package com.simplysmart.service.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.JsonObject;
import com.simplysmart.service.R;
import com.simplysmart.service.adapter.SummaryListAdapter;
import com.simplysmart.service.config.ErrorUtils;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.ServiceGenerator;
import com.simplysmart.service.database.MatrixDataRealm;
import com.simplysmart.service.database.ReadingDataRealm;
import com.simplysmart.service.database.SensorDataRealm;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.model.common.APIError;
import com.simplysmart.service.model.matrix.Summary;
import com.simplysmart.service.model.matrix.AllReadingsData;
import com.simplysmart.service.model.matrix.MatrixData;
import com.simplysmart.service.model.matrix.Metric;
import com.simplysmart.service.model.matrix.Reading;

import java.util.ArrayList;

import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shailendrapsp on 4/11/16.
 */

public class SummaryActivity extends BaseActivity {

    private ListView summary;
    private ArrayList<Summary> summaryList;
    private SummaryListAdapter adapter;
    private AllReadingsData allReadingData;
    private RelativeLayout mParentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Summary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mParentLayout = (RelativeLayout)findViewById(R.id.parentLayout);
        allReadingData = new AllReadingsData();

        summary = (ListView)findViewById(R.id.summary);
        summaryList = new ArrayList<>();
        showActivitySpinner();
        setDataForSummary();
        dismissActivitySpinner();

    }

    private void setDataForSummary() {
        ArrayList<Metric> metrics = new ArrayList<>();
        ArrayList<MatrixData> matrixList = MatrixDataRealm.getAll();
        if(matrixList!=null && matrixList.size()>0){
            for(int i=0;i<matrixList.size();i++){
                MatrixData data = matrixList.get(i);

                RealmList<SensorDataRealm> sensorList = SensorDataRealm.getForUtilityId(data.getUtility_id());

                if(sensorList!=null && sensorList.size()>0) {

                    for (int j = 0; j < sensorList.size();j++){
                        SensorDataRealm sdr = sensorList.get(j);

                        Metric metric = new Metric();
                        metric.setType(data.getType());
                        metric.setUtility_id(data.getUtility_id());
                        metric.setSensor_name(sdr.getSensor_name());

                        ArrayList<Reading> readings = new ArrayList<>();

                        RealmList<ReadingDataRealm> readingsList = ReadingDataRealm.findAllForThisSensor(sdr.getUtility_identifier(),sdr.getSensor_name());
                        if(readingsList!=null && readingsList.size()>0){
                            for(int k=0;k<readingsList.size();k++) {
                                ReadingDataRealm rdr = readingsList.get(k);
                                Summary summary = new Summary();

                                summary.setName(rdr.getSensor_name());
                                summary.setValue(rdr.getValue() + " " + rdr.getUnit());
                                summary.setTime(rdr.getDate());

                                Reading reading = new Reading();
                                reading.setValue(rdr.getValue());
                                reading.setPhotographic_evidence_url(rdr.getPhotographic_evidence_url());
                                reading.setTimestamp(rdr.getTimestamp());
                                readings.add(reading);

                                summaryList.add(summary);
                            }
                        }
                        metric.setReadings(readings);
                        metrics.add(metric);
                    }
                }
            }
        }
        allReadingData.setMetrics(metrics);
        setDataInList();
    }

    private void setDataInList() {
        adapter = new SummaryListAdapter(summaryList,this);
        summary.setAdapter(adapter);
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
        getMenuInflater().inflate(R.menu.summary_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.submit:
                submitData();
                return true;
            default:
                return false;
        }
    }

    private void submitData() {
        if(NetworkUtilities.isInternet(this)) {
            showActivitySpinner();
            ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
            Call<JsonObject> submitAllReadings = apiInterface.submitAllReadings(GlobalData.getInstance().getSubDomain(),allReadingData);
            submitAllReadings.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    dismissActivitySpinner();
                    if(response.isSuccessful()) {
                        showSnackBar(mParentLayout, "Data successfully submitted.", true);
                        removeLocalData();
                    }else if(response.code()==401){
                        handleAuthorizationFailed();
                    }else{
                        APIError error = ErrorUtils.parseError(response);
                        showSnackBar(mParentLayout,error.message(),false);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    dismissActivitySpinner();
                    showSnackBar(mParentLayout,getString(R.string.error_in_network),false);
                }
            });
        }else{
            showSnackBar(mParentLayout,getString(R.string.error_no_internet_connection),false);
        }
    }



    @Override
    protected int getStatusBarColor() {
        return 0;
    }
}
