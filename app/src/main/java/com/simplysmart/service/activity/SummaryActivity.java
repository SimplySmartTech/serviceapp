package com.simplysmart.service.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.simplysmart.service.database.FinalReadingTable;
import com.simplysmart.service.database.MatrixTable;
import com.simplysmart.service.database.ReadingTable;
import com.simplysmart.service.database.SensorTable;
import com.simplysmart.service.dialog.AlertDialogMandatory;
import com.simplysmart.service.dialog.SubmitReadingWithoutImageDialog;
import com.simplysmart.service.dialog.SubmitWithoutInternetDialog;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.interfaces.EditDialogListener;
import com.simplysmart.service.interfaces.MandatoryReading;
import com.simplysmart.service.interfaces.SubmitWithoutInternet;
import com.simplysmart.service.model.common.APIError;
import com.simplysmart.service.model.matrix.AllReadingsData;
import com.simplysmart.service.model.matrix.Metric;
import com.simplysmart.service.model.matrix.Reading;
import com.simplysmart.service.model.matrix.Summary;
import com.simplysmart.service.service.PhotoUploadService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shailendrapsp on 4/11/16.
 */

public class SummaryActivity extends BaseActivity implements SubmitReadingWithoutImageDialog.SubmitWithoutImage, SubmitWithoutInternet, MandatoryReading, EditDialogListener {

    private RecyclerView summary;
    private ArrayList<Summary> summaryList;
    private AllReadingsData allReadingData;
    private RelativeLayout mParentLayout;
    private SummaryListAdapter adapter;
    private boolean allDone = false;
    private boolean initializeUpload = false;
    private Button submit, add_new_data;
    private TextView no_data_found;
    private ArrayList<String> dates;
    private FloatingActionButton fab;
    private String dateForReadings = "";
    private boolean yesterday = true;
    private SimpleDateFormat sdf;

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

        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        bindViews();
        showActivitySpinner();
        findListOfDates();
        setDataForSummary();
        dismissActivitySpinner();
    }

    private void bindViews() {
        mParentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        allReadingData = new AllReadingsData();
        dates = new ArrayList<>();

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

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesterday) {
                    yesterday = false;
                    dateForReadings = sdf.format(Calendar.getInstance().getTimeInMillis());
                    setDataForSummary();
                    fab.setImageResource(R.drawable.yesterday);
                    getSupportActionBar().setTitle("Summary : " + dateForReadings);
                } else {
                    yesterday = true;
                    Calendar c = Calendar.getInstance();
                    dateForReadings = sdf.format(c.getTimeInMillis());
                    setDataForSummary();
                    fab.setImageResource(R.drawable.today);
                    getSupportActionBar().setTitle("Summary : " + dateForReadings);
                }
            }
        });

    }

    private void findListOfDates() {
        dates = new ArrayList<>();
        List<ReadingTable> allReadings = ReadingTable.getAllReadings(GlobalData.getInstance().getSelectedUnitId());
        for (int i = 0; i < allReadings.size(); i++) {
            String date = allReadings.get(i).date_of_reading;
            if (dates.size() > 0) {
                if (!dates.contains(date)) {
                    dates.add(date);
                }
            } else {
                dates.add(date);
            }
        }

        Calendar c = Calendar.getInstance();
        dateForReadings = sdf.format(c.getTimeInMillis());

        if (dates.size() > 0) {
            if (dates.contains(dateForReadings) && dates.size()>1) {
                setDataForSummary();
                yesterday = true;
                dates.remove(dateForReadings);
                getSupportActionBar().setTitle("Summary : " + dateForReadings);
                fab.setVisibility(View.VISIBLE);
            } else {
                yesterday = false;
                dateForReadings = sdf.format(Calendar.getInstance().getTimeInMillis());
                setDataForSummary();
                getSupportActionBar().setTitle("Summary : " + dateForReadings);
                fab.setVisibility(View.GONE);
            }

        } else {
            yesterday = false;
            fab.setVisibility(View.GONE);
            dateForReadings = sdf.format(Calendar.getInstance().getTimeInMillis());
            setDataForSummary();
            getSupportActionBar().setTitle("Summary : "+dateForReadings);
        }
    }

    private void showMandatoryDialog(String mandatory) {
        AlertDialogMandatory alertDialogMandatory = AlertDialogMandatory.newInstance("Alert", "We strongly recommend you enter the mandatory readings :" + mandatory, "", "OK");
        alertDialogMandatory.show(getFragmentManager(), "alertDialogMandatory");
    }

    private void setDataForSummary() {
        int count = 0;
        summaryList = new ArrayList<>();
        List<MatrixTable> matrixTableList = MatrixTable.getMatrixList(GlobalData.getInstance().getSelectedUnitId());
        if (matrixTableList != null && matrixTableList.size() > 0) {
            for (MatrixTable matrixTable : matrixTableList) {
                List<SensorTable> sensorTableList = SensorTable.getSensorList(matrixTable.utility_id);
                if (sensorTableList != null && sensorTableList.size() > 0) {
                    for (SensorTable sensorTable : sensorTableList) {
                        List<ReadingTable> readingsList = new Vector<>();
                        if(yesterday){
                            for(String date :dates){
                                List<ReadingTable> readings = ReadingTable.getReadings(matrixTable.utility_id,sensorTable.sensor_name,date);
                                if(readings!=null && readings.size()>0){
                                    for(ReadingTable readingTable : readings){
                                        readingsList.add(readingTable);
                                    }
                                }
                            }
                        }else {
                            readingsList = ReadingTable.getReadings(matrixTable.utility_id,sensorTable.sensor_name,dateForReadings);
                        }
                        if (readingsList.size() > 0) {
                            Summary header = new Summary();
                            header.setName(matrixTable.type);
                            header.setValue(matrixTable.icon);
                            header.setHeader(true);
                            summaryList.add(header);

                            for (ReadingTable readingTable : readingsList) {
                                Summary summary = new Summary();

                                summary.setName(readingTable.sensor_name);
                                summary.setValue(readingTable.value + " " + readingTable.unit);
                                summary.setTime(readingTable.date);
                                summary.setType(matrixTable.type);
                                summary.setLocalPhotoUrl(readingTable.local_photo_url);
                                summary.setTimestamp(readingTable.timestamp);
                                summary.setUploaded(readingTable.uploadedImage);

                                if (readingTable.remark != null && !readingTable.remark.equalsIgnoreCase("") && readingTable.updated_at != 0) {
                                    summary.setEdited(true);
                                    summary.setTime(summary.getTime() + "  (Edited)");
                                }

                                if (!readingTable.uploadedImage) {
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
        adapter = new SummaryListAdapter(summaryList, this, getFragmentManager(), yesterday);
        summary.setLayoutManager(linearLayoutManager);
        summary.setAdapter(adapter);

        no_data_found = (TextView) findViewById(R.id.no_data_found);
        add_new_data = (Button) findViewById(R.id.add_reading_now);
        add_new_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SummaryActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
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
                Intent i = new Intent(this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
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
                        removeLocalData(GlobalData.getInstance().getSelectedUnitId(),dateForReadings);
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
        FinalReadingTable finalReadingTable = new FinalReadingTable(jsonToSend);
        finalReadingTable.save();
        removeLocalData(GlobalData.getInstance().getSelectedUnitId());
        hideList();
    }

    private void hideList() {
        summary.setVisibility(View.GONE);
        submit.setVisibility(View.GONE);
        add_new_data.setVisibility(View.VISIBLE);
        no_data_found.setVisibility(View.VISIBLE);
        no_data_found.setText(getString(R.string.readings_submitted));
        findListOfDates();
    }

    private AllReadingsData getDataToSubmit() {
        AllReadingsData allReadingData = new AllReadingsData();
        ArrayList<Metric> metrics = new ArrayList<>();

        List<MatrixTable> matrixTableList = MatrixTable.getMatrixList(GlobalData.getInstance().getSelectedUnitId());
        if (matrixTableList != null && matrixTableList.size() > 0) {

            for (MatrixTable matrixTable : matrixTableList) {
                List<SensorTable> sensorTableList = SensorTable.getSensorList(matrixTable.utility_id);
                if (sensorTableList != null && sensorTableList.size() > 0) {
                    for (SensorTable sensorTable : sensorTableList) {

                        List<ReadingTable> readings = ReadingTable.getReadings(sensorTable.utility_identifier, sensorTable.sensor_name, dateForReadings);
                        if (readings != null && readings.size() > 0) {

                            ArrayList<Reading> readingToSend = new ArrayList<>();
                            for (ReadingTable readingTable : readings) {
                                Reading reading = new Reading();
                                if (readingTable.tare_weight != null && !readingTable.tare_weight.equalsIgnoreCase("")) {
                                    reading.setTare_weight(readingTable.tare_weight);
                                }
                                reading.setValue(readingTable.value);
                                reading.setPhotographic_evidence_url(readingTable.photographic_evidence_url);
                                reading.setTimestamp(readingTable.timestamp);
                                if (readingTable.updated_at != 0) {
                                    reading.setUpdatedAt(readingTable.updated_at);
                                }

                                if (readingTable.remark != null && !readingTable.remark.equalsIgnoreCase("")) {
                                    reading.setRemark(readingTable.remark);
                                }

                                readingToSend.add(reading);
                            }

                            Metric metric = new Metric();
                            metric.setType(matrixTable.type);
                            metric.setSensor_name(sensorTable.sensor_name);
                            metric.setUtility_id(sensorTable.utility_identifier);
                            metric.setReadings(readingToSend);
                            metrics.add(metric);
                        }
                    }
                }
            }
        }

        allReadingData.setMetrics(metrics);
        return allReadingData;
    }


    private void findAndUpdateElement(ReadingTable table) {
        String local_photo_url = table.local_photo_url;

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
            ReadingTable rdr = null;
            if (intent != null && intent.getExtras() != null) {
                long timestamp = intent.getLongExtra(StringConstants.TIMESTAMP, 0);
                if (timestamp != 0) {
                    rdr = ReadingTable.getReading(timestamp);
                }
            }

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
        List<MatrixTable> matrixMandatoryTables = MatrixTable.getMandatoryList(GlobalData.getInstance().getSelectedUnitId());
        if (matrixMandatoryTables != null && matrixMandatoryTables.size() > 0) {
            for (MatrixTable matrixTable : matrixMandatoryTables) {
                List<SensorTable> sensorTableList = SensorTable.getSensorList(matrixTable.utility_id);
                if (sensorTableList != null && sensorTableList.size() > 0) {
                    for (SensorTable sensorTable : sensorTableList) {
                        List<ReadingTable> readings = ReadingTable.getReadings(sensorTable.utility_identifier, sensorTable.sensor_name);
                        if (readings == null || readings.size() == 0) {
                            mandatory += "\n" + matrixTable.type + " : " + sensorTable.sensor_name;
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
            setDataForSummary();
            summary.scrollToPosition(position);
        }
    }
}
