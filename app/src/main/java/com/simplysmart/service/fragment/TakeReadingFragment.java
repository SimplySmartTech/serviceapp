package com.simplysmart.service.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.activeandroid.ActiveAndroid;
import com.simplysmart.service.R;
import com.simplysmart.service.activity.InputFormActivity;
import com.simplysmart.service.adapter.MatrixListAdapter;
import com.simplysmart.service.config.ErrorUtils;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.ServiceGenerator;
import com.simplysmart.service.database.MatrixDataTable;
import com.simplysmart.service.database.SensorDataTable;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.model.common.APIError;
import com.simplysmart.service.model.matrix.MatrixData;
import com.simplysmart.service.model.matrix.MatrixResponse;
import com.simplysmart.service.model.matrix.SensorData;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shailendrapsp on 2/11/16.
 */
public class TakeReadingFragment extends BaseFragment {

    private View rootView;
    private ExpandableListView matrixList;
    private MatrixListAdapter matrixListAdapter;
    private MatrixResponse matrixResponse;
    private int lastExpandedPosition = -1;


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserInfo();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_reading,container,false);
        matrixList = (ExpandableListView)rootView.findViewById(R.id.matrixList);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getMatrixRequest(GlobalData.getInstance().getUnits().get(0).getId(), GlobalData.getInstance().getSubDomain());

        matrixList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                Intent intent = new Intent(getContext(), InputFormActivity.class);
                intent.putExtra("SENSOR_DATA", matrixResponse.getData().get(groupPosition).getSensors().get(childPosition));
                intent.putExtra("groupPosition", groupPosition);
                intent.putExtra("childPosition", childPosition);
                startActivity(intent);
                return true;
            }
        });

        matrixList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    matrixList.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(UPDATE_METRIC_SENSOR_LIST_ROW, new IntentFilter("UPDATE_METRIC_SENSOR_LIST_ROW"));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(UPDATE_METRIC_SENSOR_LIST_ROW);

    }

    private void getMatrixRequest(String unitId, String subDomain) {

        if (NetworkUtilities.isInternet(getContext())) {

            showActivitySpinner();

            ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
            Call<MatrixResponse> call = apiInterface.getMetrics(unitId, subDomain);
            call.enqueue(new Callback<MatrixResponse>() {

                @Override
                public void onResponse(Call<MatrixResponse> call, final Response<MatrixResponse> response) {

                    if (response.isSuccessful()) {
                        setMatrixData(response.body());
                    } else {
                        APIError error = ErrorUtils.parseError(response);
                        displayMessage(error.message());
                    }
                    dismissActivitySpinner();
                }

                @Override
                public void onFailure(Call<MatrixResponse> call, Throwable t) {
                    dismissActivitySpinner();
                    displayMessage(getResources().getString(R.string.error_in_network));
                }
            });
        } else {
            displayMessage(getString(R.string.error_no_internet_connection));
        }
    }

    //Set matrix data to list
    private void setMatrixData(MatrixResponse response) {

        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < response.getData().size(); i++) {
                MatrixData matrixData = response.getData().get(i);
                MatrixDataTable matrixDataTable = new MatrixDataTable(matrixData);
                matrixDataTable.save();
                for (int j = 0; j < matrixData.getSensors().size(); j++) {
                    SensorData sensorData = matrixData.getSensors().get(j);
                    SensorDataTable sensorDataTable = new SensorDataTable(sensorData);
                    sensorDataTable.save();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            showSnackBar(rootView,"Unable to save data.",false);
        }finally {
            ActiveAndroid.endTransaction();
        }

        ArrayList<MatrixData> matrixList = new ArrayList<>();

        matrixListAdapter = new MatrixListAdapter(getContext(), response.getData());
        matrixList.setAdapter(matrixListAdapter);
    }

    private void updateMetricList(Intent intent) {
        int groupPosition = intent.getIntExtra("groupPosition", -1);
        int childPosition = intent.getIntExtra("childPosition", -1);

        matrixResponse.getData().get(groupPosition).getSensors().get(childPosition).setChecked(true);
        matrixListAdapter.notifyDataSetChanged();
    }

    private BroadcastReceiver UPDATE_METRIC_SENSOR_LIST_ROW = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateMetricList(intent);
        }
    };

}
