package com.simplysmart.service.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.simplysmart.service.R;
import com.simplysmart.service.activity.ReadingDetailsActivity;
import com.simplysmart.service.adapter.SensorListTodayAdapter;
import com.simplysmart.service.callback.ApiCallback;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.model.sensor.SensorList;
import com.simplysmart.service.request.CreateRequest;
import com.simplysmart.service.util.ParseDateFormat;

/**
 * Created by shekhar on 22/5/17.
 */
public class SensorInfoTodayList extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    //    private ProgressBar progressBar;
    private RelativeLayout content_layout;
    private TextView no_data_found;
    private ListView sensorInfoList;
    private View rootView;
    private SensorListTodayAdapter sensorListAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sensor_info_list, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeView();

        if (NetworkUtilities.isInternet(getActivity())) {
            swipeRefreshLayout.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(true);
                            getSensorsReadings();
                        }
                    }
            );
        } else {
            no_data_found.setVisibility(View.VISIBLE);
            no_data_found.setText(getActivity().getString(R.string.error_no_internet_connection));
//            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            sensorInfoList.setVisibility(View.GONE);
        }
    }

    private void setHeaderColor(int color) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

//    private void setHeaderTitle(String title) {
//        SpannableString titleString = new SpannableString(title);
//        titleString.setSpan(new TypefaceSpan(getActivity(), AppConstant.FONT_EUROSTILE_REGULAR_MID), 0, titleString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
//    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("HELLO", "ON RESUME CALLED");
        IntentFilter intentFilter = new IntentFilter("UnitName");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onUnitIDSelection, intentFilter);

//        setHeaderTitle(GlobalData.getInstance().getUpdatedUnitName());
//        setHeaderColor(getActivity().getResources().getColor(R.color.bw_color_purple));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onUnitIDSelection);
    }

    private void initializeView() {
//        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar2);
        content_layout = (RelativeLayout) rootView.findViewById(R.id.content_layout);
        no_data_found = (TextView) rootView.findViewById(R.id.no_data_found);
        sensorInfoList = (ListView) rootView.findViewById(R.id.sensorInfoList);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        sensorInfoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent newIntent = new Intent(getActivity(), ReadingDetailsActivity.class);
                newIntent.putExtra("date", ParseDateFormat.getCurrentTimeStamp("dd/MM/yyyy"));
                newIntent.putExtra("sensorData", sensorListAdapter.getSensorListData().getData().get(i));
                getActivity().startActivity(newIntent);
            }
        });
    }

    private void getSensorsReadings() {

//        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(true);

        CreateRequest.getInstance().getSensorsReadings(GlobalData.getInstance().getSelectedUnitId(), "", new ApiCallback<SensorList>() {

            @Override
            public void onSuccess(SensorList response) {
//                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                setDataInList(response);
            }

            @Override
            public void onFailure(String error) {
//                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void setDataInList(SensorList response) {

        if (response != null && response.getData().size() > 0) {
            sensorListAdapter = new SensorListTodayAdapter(response, getActivity(), getParentFragmentManager());
            sensorInfoList.setAdapter(sensorListAdapter);
            sensorInfoList.setVisibility(View.VISIBLE);
        } else {
            sensorInfoList.setVisibility(View.GONE);
        }
    }

    private final BroadcastReceiver onUnitIDSelection = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            DebugLog.d("message received unit name: " + intent.getStringExtra("name"));
            DebugLog.d("message received unit id: " + intent.getStringExtra("id"));

            if (NetworkUtilities.isInternet(getActivity())) {
                getSensorsReadings();
            }
        }
    };

    @Override
    public void onRefresh() {
        if (NetworkUtilities.isInternet(getActivity())) {
            swipeRefreshLayout.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(true);
                            getSensorsReadings();
                        }
                    }
            );
        } else {
            no_data_found.setVisibility(View.VISIBLE);
            no_data_found.setText(getActivity().getString(R.string.error_no_internet_connection));
//            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            sensorInfoList.setVisibility(View.GONE);
        }
    }
}
