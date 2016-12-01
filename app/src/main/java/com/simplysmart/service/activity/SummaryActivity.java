package com.simplysmart.service.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.simplysmart.service.R;
import com.simplysmart.service.adapter.SummaryListAdapter;
import com.simplysmart.service.config.ErrorUtils;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.ServiceGenerator;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.database.MatrixDataRealm;
import com.simplysmart.service.database.ReadingDataRealm;
import com.simplysmart.service.database.SensorDataRealm;
import com.simplysmart.service.dialog.EditDialog;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.model.common.APIError;
import com.simplysmart.service.model.matrix.AllReadingsData;
import com.simplysmart.service.model.matrix.MatrixData;
import com.simplysmart.service.model.matrix.Metric;
import com.simplysmart.service.model.matrix.Reading;
import com.simplysmart.service.model.matrix.Summary;
import com.simplysmart.service.service.PhotoUploadService;

import java.util.ArrayList;

import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shailendrapsp on 4/11/16.
 */

public class SummaryActivity extends BaseActivity implements EditDialog.EditDialogListener {

    private RecyclerView summary;
    private ArrayList<Summary> summaryList;
    private AllReadingsData allReadingData;
    private RelativeLayout mParentLayout;
    private SummaryListAdapter adapter;
    private boolean allDone = false;
    private boolean initializeUpload = false;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Summary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (NetworkUtilities.isInternet(this)) {
            Intent i = new Intent(this, PhotoUploadService.class);
            i.putExtra(StringConstants.USE_UNIT, true);
            i.putExtra(StringConstants.UNIT_ID, GlobalData.getInstance().getSelectedUnitId());
            startService(i);
        }

        mParentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        allReadingData = new AllReadingsData();

        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkUtilities.isInternet(SummaryActivity.this)) {
                    initializeUpload = true;
                    checkAndSubmitData();
                }else {
                    showSnackBar(mParentLayout,getString(R.string.error_no_internet_connection),false);
                }
            }
        });

        summary = (RecyclerView) findViewById(R.id.summary);
        summaryList = new ArrayList<>();
        showActivitySpinner();
        setDataForSummary();
        dismissActivitySpinner();
    }

    private void setDataForSummary() {
        int count = 0;
        ArrayList<Metric> metrics = new ArrayList<>();
        ArrayList<MatrixData> matrixList = MatrixDataRealm.getAll();
        if (matrixList != null && matrixList.size() > 0) {
            for (int i = 0; i < matrixList.size(); i++) {
                MatrixData data = matrixList.get(i);

                RealmList<SensorDataRealm> sensorList = SensorDataRealm.getForUtilityId(data.getUtility_id());

                if (sensorList != null && sensorList.size() > 0) {

                    for (int j = 0; j < sensorList.size(); j++) {
                        SensorDataRealm sdr = sensorList.get(j);

                        Metric metric = new Metric();
                        metric.setType(data.getType());
                        metric.setUtility_id(data.getUtility_id());
                        metric.setSensor_name(sdr.getSensor_name());

                        ArrayList<Reading> readings = new ArrayList<>();

                        RealmList<ReadingDataRealm> readingsList = ReadingDataRealm.findAllForThisSensor(sdr.getUtility_identifier(), sdr.getSensor_name());

                        if (readingsList != null && readingsList.size() > 0) {

                            Summary header = new Summary();
                            header.setName(data.getType());
                            header.setValue(data.getIcon());
                            header.setHeader(true);
                            summaryList.add(header);

                            for (int k = 0; k < readingsList.size(); k++) {
                                ReadingDataRealm rdr = readingsList.get(k);
                                Summary summary = new Summary();

                                summary.setName(rdr.getSensor_name());
                                summary.setValue(rdr.getValue() + " " + rdr.getUnit());
                                summary.setTime(rdr.getDate());
                                summary.setType(data.getType());
                                summary.setLocalPhotoUrl(rdr.getLocal_photo_url());
                                summary.setTimestamp(rdr.getTimestamp());

                                Reading reading = new Reading();
                                reading.setValue(rdr.getValue());
                                reading.setPhotographic_evidence_url(rdr.getPhotographic_evidence_url());
                                reading.setTimestamp(rdr.getTimestamp());
                                reading.setTare_weight(rdr.getTare_weight());
                                readings.add(reading);
                                if (!rdr.isUploadedImage()) {
                                    count++;
                                }

                                Log.d("TAG", rdr.getPhotographic_evidence_url());

                                summaryList.add(summary);
                            }

                            metric.setReadings(readings);
                            metrics.add(metric);
                        }
                    }
                }
            }
        }

        if (count == 0) {
            allDone = true;
        }

        allReadingData.setMetrics(metrics);
        setDataInList();
    }

    private void setDataInList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        adapter = new SummaryListAdapter(summaryList, this, getFragmentManager());
        summary.setLayoutManager(linearLayoutManager);
        summary.setAdapter(adapter);

        TextView no_data_found = (TextView) findViewById(R.id.no_data_found);
        TextView add_new_data = (TextView) findViewById(R.id.add_reading_now);
        add_new_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SummaryActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        if (summaryList.size() > 0) {
            summary.setVisibility(View.VISIBLE);
            no_data_found.setVisibility(View.GONE);
            add_new_data.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);
        } else {
            summary.setVisibility(View.GONE);
            no_data_found.setVisibility(View.VISIBLE);
            add_new_data.setVisibility(View.VISIBLE);
            submit.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(uploadComplete, new IntentFilter("uploadComplete"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(uploadComplete);
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

            default:
                return false;
        }
    }

    private void checkAndSubmitData() {
        if (summaryList != null && summaryList.size() > 0) {
            if (!allDone) {
                showActivitySpinner("Uploading images . . .");
            } else {
                showActivitySpinner("Submitting readings . . .");
                submitData();
            }
        }
    }

    @Override
    public void updateResult(int done, int position, String value) {
        if (done == StringConstants.NEW_VALUE) {
            summaryList.get(position).setValue(value);
            adapter.notifyItemChanged(position);
        }else if(done == StringConstants.VALUE_DELETED){
            summaryList.remove(position);
            adapter.notifyItemRemoved(position);
        }
    }

    private void submitData() {
        if (NetworkUtilities.isInternet(this)) {
            ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
            Call<JsonObject> submitAllReadings = apiInterface.submitAllReadings(GlobalData.getInstance().getSubDomain(), allReadingData);
            submitAllReadings.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
//                        ashowSnckBar(mParentLayout, "Data successfully submitted.", true);
                        Toast.makeText(getApplicationContext(),"Data successfully submitted.",Toast.LENGTH_SHORT).show();
                        removeLocalData(GlobalData.getInstance().getSelectedUnitId());
                        exitScreen();
                        dismissActivitySpinner();
                    } else if (response.code() == 401) {
                        handleAuthorizationFailed();
                        dismissActivitySpinner();
                    } else {
                        APIError error = ErrorUtils.parseError(response);
                        showSnackBar(mParentLayout, error.message(), false);
                        dismissActivitySpinner();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    dismissActivitySpinner();
                    showSnackBar(mParentLayout, getString(R.string.error_in_network), false);
                }
            });
        } else {
            showSnackBar(mParentLayout, getString(R.string.error_no_internet_connection), false);
        }
    }

    private void exitScreen() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }


    @Override
    protected int getStatusBarColor() {
        return 0;
    }


    private BroadcastReceiver uploadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            allDone = true;
            dismissActivitySpinner();
            if (initializeUpload) {
                showActivitySpinner("Submitting readings . . .");
                submitData();
            }
        }
    };
}
