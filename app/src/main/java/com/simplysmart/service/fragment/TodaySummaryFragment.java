package com.simplysmart.service.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.simplysmart.service.R;
import com.simplysmart.service.activity.MainActivityV2;
import com.simplysmart.service.adapter.SummaryListAdapter;
import com.simplysmart.service.common.CommonMethod;
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
import com.simplysmart.service.dialog.AlertDialogMandatoryV2;
import com.simplysmart.service.dialog.SubmitReadingWithoutImageDialogV2;
import com.simplysmart.service.dialog.SubmitWithoutInternetDialogV2;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.interfaces.EditDialogListener;
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
 * Created by shekhar on 28/1/17.
 */

public class TodaySummaryFragment extends BaseFragment implements EditDialogListener {

    private RecyclerView summary;
    private ArrayList<Summary> summaryList;
    private SummaryListAdapter adapter;

    private RelativeLayout mParentLayout;
    private Button submit, add_new_data;
    private TextView no_data_found;

    private String dateForReadings = "";
    private SimpleDateFormat sdf;
    private boolean allDone = false;

    private ArrayList<String> dates;

    private View rootView;

    int mStackLevel = 0;
    public static final int MANDATORY_DIALOG_FRAGMENT = 1;
    public static final int SUBMIT_DATA_WITHOUT_IMAGE_DIALOG_FRAGMENT = 2;
    public static final int SUBMIT_DATA_WITHOUT_INTERNET = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_todays_reading, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (NetworkUtilities.isInternet(getActivity())) {
            Intent i = new Intent(getActivity(), PhotoUploadService.class);
            i.putExtra(StringConstants.USE_UNIT, true);
            i.putExtra(StringConstants.UNIT_ID, GlobalData.getInstance().getSelectedUnitId());
            getActivity().startService(i);
        }

        bindViews();
        showActivitySpinner();
        setDataForSummary();
        dismissActivitySpinner();
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(uploadComplete, new IntentFilter("uploadComplete"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(uploadImage, new IntentFilter("imageUploadComplete"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(uploadStarted, new IntentFilter("uploadStarted"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(uploadComplete);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(uploadImage);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(uploadStarted);
    }

    private void bindViews() {

        mParentLayout = (RelativeLayout) rootView.findViewById(R.id.parentLayout);
        summary = (RecyclerView) rootView.findViewById(R.id.summary);
        submit = (Button) rootView.findViewById(R.id.submit);

        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Calendar c = Calendar.getInstance();
        dateForReadings = sdf.format(c.getTimeInMillis());

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

//        if (dates.size() > 0) {
//            if (dates.contains(dateForReadings) && dates.size()>1) {
//                setDataForSummary();
//                yesterday = true;
//                dates.remove(dateForReadings);
//                getSupportActionBar().setTitle("Summary : " + dateForReadings);
//                fab.setVisibility(View.VISIBLE);
//            } else {
//                yesterday = false;
//                dateForReadings = sdf.format(Calendar.getInstance().getTimeInMillis());
//                setDataForSummary();
//                getSupportActionBar().setTitle("Summary : " + dateForReadings);
//                fab.setVisibility(View.GONE);
//            }
//
//        } else {
//            yesterday = false;
//            fab.setVisibility(View.GONE);
//            dateForReadings = sdf.format(Calendar.getInstance().getTimeInMillis());
//            setDataForSummary();
//            getSupportActionBar().setTitle("Summary : "+dateForReadings);
//        }
    }

    private void showMandatoryDialog(String mandatory) {

        showDialog(MANDATORY_DIALOG_FRAGMENT, mandatory);
    }

    private void setDataForSummary() {

        findListOfDates();

        int count = 0;
        summaryList = new ArrayList<>();
        List<MatrixTable> matrixTableList = MatrixTable.getMatrixList(GlobalData.getInstance().getSelectedUnitId());

        if (matrixTableList != null && matrixTableList.size() > 0) {

            for (MatrixTable matrixTable : matrixTableList) {

                List<SensorTable> sensorTableList = SensorTable.getSensorList(matrixTable.utility_id);

                if (sensorTableList != null && sensorTableList.size() > 0) {

                    for (SensorTable sensorTable : sensorTableList) {

                        String date = "";
                        if (dates.size() > 1) {
                            if (CommonMethod.diffInDays(dates.get(0), "dd/MM/yyyy", dates.get(1), "dd/MM/yyyy") > 0) {
                                date = dates.get(0);
                            } else {
                                date = dates.get(1);
                            }
                        } else if (dates.size() > 0) {
                            if (CommonMethod.diffInDays(dates.get(0), "dd/MM/yyyy", dateForReadings, "dd/MM/yyyy") >= 0) {
                                date = dateForReadings;
                            } else if (CommonMethod.diffInDays(dates.get(0), "dd/MM/yyyy", dateForReadings, "dd/MM/yyyy") == -1) {
                                date = dates.get(0);
                            } else {
                                date = "";
                            }
                        } else {
                            date = "";
                        }
                        List<ReadingTable> readingsList = ReadingTable.getReadings(matrixTable.utility_id, sensorTable.sensor_name, date);

                        if (readingsList != null && readingsList.size() > 0) {

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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        adapter = new SummaryListAdapter(summaryList, getActivity(), getFragmentManager());
        summary.setLayoutManager(linearLayoutManager);
        summary.setAdapter(adapter);

        no_data_found = (TextView) rootView.findViewById(R.id.no_data_found);
        add_new_data = (Button) rootView.findViewById(R.id.add_reading_now);
        add_new_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MainActivityV2.class);
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

    private void checkAndSubmitData() {
        if (allDone) {
            submitData();
        } else {
            showDialog(SUBMIT_DATA_WITHOUT_IMAGE_DIALOG_FRAGMENT, "");
        }
    }

    private void submitData() {

        AllReadingsData allReadingData = getDataToSubmit();

        if (NetworkUtilities.isInternet(getActivity())) {
            showActivitySpinner();
            ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
            Call<JsonObject> submitAllReadings = apiInterface.submitAllReadings(GlobalData.getInstance().getSubDomain(), allReadingData);
            submitAllReadings.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        for (String date : dates) {
                            removeLocalData(GlobalData.getInstance().getSelectedUnitId(), date);
                        }
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
            showDialog(SUBMIT_DATA_WITHOUT_INTERNET, "");
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

                        List<ReadingTable> readingsList = new Vector<>();//ReadingTable.getReadings(sensorTable.utility_identifier, sensorTable.sensor_name, dateForReadings);
                        for (String date : dates) {
                            List<ReadingTable> readings = ReadingTable.getReadings(matrixTable.utility_id, sensorTable.sensor_name, date);
                            if (readings != null && readings.size() > 0) {
                                for (ReadingTable readingTable : readings) {
                                    readingsList.add(readingTable);
                                }
                            }
                        }
                        if (readingsList.size() > 0) {

                            ArrayList<Reading> readingToSend = new ArrayList<>();
                            for (ReadingTable readingTable : readingsList) {
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

    void showDialog(int type, String contentString) {

        mStackLevel++;

        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        Fragment prev = getActivity().getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        switch (type) {

            case MANDATORY_DIALOG_FRAGMENT:
                AlertDialogMandatoryV2 alertDialogMandatory = AlertDialogMandatoryV2.newInstance("Alert", getString(R.string.alert_txt_madatory) + contentString, "", "OK");
                alertDialogMandatory.setTargetFragment(this, MANDATORY_DIALOG_FRAGMENT);
                alertDialogMandatory.show(getFragmentManager().beginTransaction(), "alertDialogMandatory");
                break;

            case SUBMIT_DATA_WITHOUT_IMAGE_DIALOG_FRAGMENT:
                SubmitReadingWithoutImageDialogV2 submitReadingWithoutImageDialog = SubmitReadingWithoutImageDialogV2.newInstance("Alert", getString(R.string.alert_txt_upload_without_image), "No", "Yes");
                submitReadingWithoutImageDialog.setTargetFragment(this, SUBMIT_DATA_WITHOUT_IMAGE_DIALOG_FRAGMENT);
                submitReadingWithoutImageDialog.show(getFragmentManager().beginTransaction(), "submitReadingWithoutImageDialog");
                break;

            case SUBMIT_DATA_WITHOUT_INTERNET:
                SubmitWithoutInternetDialogV2 dataToSend = SubmitWithoutInternetDialogV2.newInstance("Alert", getString(R.string.after_internet_send), "", "OK");
                dataToSend.setTargetFragment(this, SUBMIT_DATA_WITHOUT_INTERNET);
                dataToSend.show(getFragmentManager().beginTransaction(), "dataToSend");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case MANDATORY_DIALOG_FRAGMENT:

                if (resultCode == Activity.RESULT_OK) {
                    DebugLog.d("CALLED MANDATORY_DIALOG_FRAGMENT");
                    checkAndSubmitData();

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // After Cancel code.
                }
                break;

            case SUBMIT_DATA_WITHOUT_IMAGE_DIALOG_FRAGMENT:

                if (resultCode == Activity.RESULT_OK) {
                    DebugLog.d("CALLED SUBMIT_DATA_WITHOUT_IMAGE_DIALOG_FRAGMENT");
                    submitData();

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // After Cancel code.
                }
                break;

            case SUBMIT_DATA_WITHOUT_INTERNET:

                if (resultCode == Activity.RESULT_OK) {
                    DebugLog.d("CALLED SUBMIT_DATA_WITHOUT_INTERNET");
                    saveToDisk(getDataToSubmit());

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // After Cancel code.
                }
                break;
        }
    }
}

