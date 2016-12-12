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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.simplysmart.service.R;
import com.simplysmart.service.adapter.SummaryListAdapter;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.ErrorUtils;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.ServiceGenerator;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.database.FinalReadingData;
import com.simplysmart.service.database.MatrixDataRealm;
import com.simplysmart.service.database.ReadingDataRealm;
import com.simplysmart.service.database.SensorDataRealm;
import com.simplysmart.service.dialog.AlertDialogMandatory;
import com.simplysmart.service.dialog.SubmitReadingWithoutImageDialog;
import com.simplysmart.service.dialog.SubmitWithoutInternetDialog;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.interfaces.EditDialogListener;
import com.simplysmart.service.interfaces.MandatoryReading;
import com.simplysmart.service.interfaces.SubmitWithoutInternet;
import com.simplysmart.service.model.common.APIError;
import com.simplysmart.service.model.matrix.AllReadingsData;
import com.simplysmart.service.model.matrix.MatrixData;
import com.simplysmart.service.model.matrix.Metric;
import com.simplysmart.service.model.matrix.Reading;
import com.simplysmart.service.model.matrix.Summary;
import com.simplysmart.service.service.PhotoUploadService;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shailendrapsp on 4/11/16.
 */

public class SummaryActivity extends BaseActivity implements SubmitReadingWithoutImageDialog.SubmitWithoutImage, SubmitWithoutInternet, MandatoryReading ,EditDialogListener{

    private RecyclerView summary;
    private ArrayList<Summary> summaryList;
    private AllReadingsData allReadingData;
    private RelativeLayout mParentLayout;
    private SummaryListAdapter adapter;
    private boolean allDone = false;
    private boolean initializeUpload = false;
    private Button submit, add_new_data;
    private TextView no_data_found;

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
                String mandatory = checkAllMandatoryReadings();
                if (mandatory.equals("")) {
                    checkAndSubmitData();
                } else {
                    showMandatoryDialog(mandatory);
                }
            }
        });

        summary = (RecyclerView) findViewById(R.id.summary);
        summaryList = new ArrayList<>();
        showActivitySpinner();
        setDataForSummary();
        dismissActivitySpinner();
    }

    private void showMandatoryDialog(String mandatory) {

        AlertDialogMandatory alertDialogMandatory = AlertDialogMandatory.newInstance("Alert", "We strongly recommend you enter the mandatory readings :" + mandatory, "", "OK");
        alertDialogMandatory.show(getFragmentManager(), "alertDialogMandatory");
    }

    private void setDataForSummary() {
        int count = 0;
        ArrayList<MatrixData> matrixList = MatrixDataRealm.getAll();
        if (matrixList != null && matrixList.size() > 0) {
            for (int i = 0; i < matrixList.size(); i++) {
                MatrixData data = matrixList.get(i);

                RealmList<SensorDataRealm> sensorList = SensorDataRealm.getForUtilityId(data.getUtility_id());

                if (sensorList != null && sensorList.size() > 0) {

                    for (int j = 0; j < sensorList.size(); j++) {
                        SensorDataRealm sdr = sensorList.get(j);

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
                                summary.setUploaded(rdr.isUploadedImage());

                                if (!rdr.isUploadedImage()) {
                                    count++;
                                }

                                summaryList.add(summary);
                            }
                        }
                    }
                }
            }
        }

        if (count > 0) {
            allDone = false;
        }

        setDataInList();
    }

    private void setDataInList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        adapter = new SummaryListAdapter(summaryList, this, getFragmentManager());
        summary.setLayoutManager(linearLayoutManager);
        summary.setAdapter(adapter);

        no_data_found = (TextView) findViewById(R.id.no_data_found);
        add_new_data = (Button) findViewById(R.id.add_reading_now);
        add_new_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        LocalBroadcastManager.getInstance(this).registerReceiver(uploadImage, new IntentFilter("imageUploadComplete"));
        LocalBroadcastManager.getInstance(this).registerReceiver(uploadStarted, new IntentFilter("uploadStarted"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(uploadComplete);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(uploadImage);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(uploadStarted);
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
        if (allDone) {
            submitData();
        } else {
            showImageNotUploadedDialog();
        }
    }

    private void showImageNotUploadedDialog() {
        SubmitReadingWithoutImageDialog submitReadingWithoutImageDialog = SubmitReadingWithoutImageDialog.newInstance("Alert", "All images have not been uploaded yet. Do you want to submit readings without uploading all images ?", "No", "Yes");
        submitReadingWithoutImageDialog.setCancelable(false);
        submitReadingWithoutImageDialog.show(getFragmentManager(), "submitReadingWithoutImageDialog");
    }

    private void submitData() {
        AllReadingsData allReadingData = getDataToSubmit();
        if (NetworkUtilities.isInternet(this)) {
            showActivitySpinner();
            ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
            Call<JsonObject> submitAllReadings = apiInterface.submitAllReadings(GlobalData.getInstance().getSubDomain(), allReadingData);
            submitAllReadings.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        removeLocalData(GlobalData.getInstance().getSelectedUnitId());
                        dismissActivitySpinner();
                        hideList();
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
            SubmitWithoutInternetDialog dataToSend = SubmitWithoutInternetDialog.newInstance("Alert", getString(R.string.after_internet_send), "", "OK");
            dataToSend.setCancelable(false);
            dataToSend.show(getFragmentManager(), "dataToSend");
        }
    }


    private void saveToDisk(AllReadingsData allReadingData) {
        String jsonToSend = new Gson().toJson(allReadingData);
        DebugLog.d(jsonToSend);
        FinalReadingData finalReadingData = new FinalReadingData(jsonToSend);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(finalReadingData);
        realm.commitTransaction();

        removeLocalData(GlobalData.getInstance().getSelectedUnitId());
        hideList();
    }

    private void hideList() {
        summary.setVisibility(View.GONE);
        submit.setVisibility(View.GONE);
        add_new_data.setVisibility(View.VISIBLE);
        no_data_found.setVisibility(View.VISIBLE);
        no_data_found.setText(getString(R.string.readings_submitted));
    }

    private AllReadingsData getDataToSubmit() {
        AllReadingsData allReadingData = new AllReadingsData();
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

                            for (int k = 0; k < readingsList.size(); k++) {
                                ReadingDataRealm rdr = readingsList.get(k);

                                Reading reading = new Reading();
                                reading.setValue(rdr.getValue());
                                reading.setPhotographic_evidence_url(rdr.getPhotographic_evidence_url());
                                reading.setTimestamp(rdr.getTimestamp());
                                reading.setTare_weight(rdr.getTare_weight());
                                readings.add(reading);
                            }

                            metric.setReadings(readings);
                            metrics.add(metric);
                        }
                    }
                }
            }
        }
        allReadingData.setMetrics(metrics);
        return allReadingData;
    }

    private void findAndUpdateElement(ReadingDataRealm rdr) {
        String local_photo_url = rdr.getLocal_photo_url();

        for (int i = 0; i < summaryList.size(); i++) {
            if (summaryList.get(i).getLocalPhotoUrl() != null && summaryList.get(i).getLocalPhotoUrl().equals(local_photo_url)) {
                adapter.getData().get(i).setUploaded(true);
                adapter.notifyItemChanged(i);
                DebugLog.d("Position of notifying " + i);
            }
        }
    }

    @Override
    protected int getStatusBarColor() {
        return 0;
    }

    private BroadcastReceiver uploadImage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ReadingDataRealm rdr = null;
            if (intent != null && intent.getExtras() != null) {
                rdr = intent.getParcelableExtra(StringConstants.READING_DATA);
            }

            DebugLog.d("Broadcast recieved for image upload complete " + rdr.getPhotographic_evidence_url());

            if (rdr != null) {
                findAndUpdateElement(rdr);
            }
        }
    };

    private BroadcastReceiver uploadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            allDone = true;
        }
    };

    private BroadcastReceiver uploadStarted = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void submitWithoutImage() {
        submitData();
    }

    @Override
    public void submitWithoutInternet() {
        saveToDisk(getDataToSubmit());
    }

    @Override
    public void continueAhead() {
        checkAndSubmitData();
    }

    private String checkAllMandatoryReadings() {

        String mandatory = "\n";
        Realm realm = Realm.getDefaultInstance();
        RealmResults<MatrixDataRealm> mandatoryReadings = realm
                .where(MatrixDataRealm.class)
                .equalTo("mandatory", true)
                .equalTo("unit_id", GlobalData.getInstance().getSelectedUnitId())
                .findAll();

        if (mandatoryReadings.size() > 0) {
            for (MatrixDataRealm data : mandatoryReadings) {
                RealmResults<SensorDataRealm> sensorResults = realm
                        .where(SensorDataRealm.class)
                        .equalTo("utility_identifier", data.getUtility_id())
                        .findAll();
                if (sensorResults.size() > 0) {
                    for (SensorDataRealm sensorDataRealm : sensorResults) {
                        RealmList<ReadingDataRealm> readingsList = ReadingDataRealm.findAllForThisSensor(data.getUtility_id(), sensorDataRealm.getSensor_name());
                        if (readingsList == null || readingsList.size() == 0) {
                            mandatory += "\n " + data.getType() + " : " + sensorDataRealm.getSensor_name();
                        }
                    }
                }
            }
        }

        return mandatory;
    }

    @Override
    public void updateResult(int done, int position, String value) {
        if (done == StringConstants.NEW_VALUE) {
            adapter.getData().get(position).setValue(value);
            adapter.notifyItemChanged(position);
        } else {
            adapter.notifyItemChanged(position);
        }
    }
}
