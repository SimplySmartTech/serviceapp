package com.simplysmart.service.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.simplysmart.service.R;
import com.simplysmart.service.callback.ApiCallback;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.model.sensor.SensorItem;
import com.simplysmart.service.model.sensor.SensorReadingGraphResponse;
import com.simplysmart.service.request.CreateRequest;
import com.simplysmart.service.util.ParseDateFormat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by shekhar on 22/5/17.
 */
public class StatusReadingDetailsActivity extends BaseActivity {

    private ProgressBar progressBar;
    private LinearLayout content_layout;
    private TextView no_data_found;
    private TextView yAxisLogo;
    private SensorItem sensorItem;
    private LineChart graphView;
    private YAxis leftAxis;
    private String date;
    private String xAxisLable = "";

    private Spinner durationSpinner;
    private Spinner typeSpinner;

    private TextView readingValue, readingAtValue;

    private LinkedHashMap<String, ArrayList<ArrayList<String>>> graphData = new LinkedHashMap<>();

    private final String TYPE = "status_graph";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_graph_reading_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Reading Details");

        if (getIntent().getExtras() != null) {
            date = getIntent().getStringExtra("date");
            sensorItem = getIntent().getParcelableExtra("sensorData");
        }

        initializeWidgets();
    }

    @Override
    protected int getStatusBarColor() {
        return 0;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportActionBar().show();
                    getFragmentManager().popBackStack();
                } else {
                    super.onBackPressed();
                }
        }
        return true;
    }

    private void initializeWidgets() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        content_layout = (LinearLayout) findViewById(R.id.content_layout);
        no_data_found = (TextView) findViewById(R.id.no_data_found);
        graphView = (LineChart) findViewById(R.id.graphView);
        yAxisLogo = (TextView) findViewById(R.id.y_axis_logo);

        durationSpinner = (Spinner) findViewById(R.id.durationSpinner);
        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);

        readingValue = (TextView) findViewById(R.id.readingValue);
        readingAtValue = (TextView) findViewById(R.id.readingAtValue);

        if (sensorItem.getLast_reading() != null && !sensorItem.getLast_reading().equalsIgnoreCase("")) {
            readingValue.setText(sensorItem.getLast_reading());
            if (sensorItem.getUnit() != null) {
                readingValue.setText(sensorItem.getLast_reading() + " " + sensorItem.getUnit());
            }
        } else {
            readingValue.setText("---");
        }

        if (sensorItem.getLast_reading_at() != null && !sensorItem.getLast_reading_at().equalsIgnoreCase("")) {
            readingAtValue.setText(sensorItem.getLast_reading_at());
        } else {
            readingAtValue.setText("---");
        }

        durationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (durationSpinner.getSelectedItem().toString().toLowerCase().equalsIgnoreCase("today")) {

                    if (NetworkUtilities.isInternet(StatusReadingDetailsActivity.this)) {
                        no_data_found.setVisibility(View.GONE);
                        getSensorsReadingGraph(date, date, TYPE);
                    } else {
                        no_data_found.setVisibility(View.VISIBLE);
                        no_data_found.setText(getResources().getString(R.string.error_no_internet_connection));
                        progressBar.setVisibility(View.GONE);
                        graphView.setVisibility(View.GONE);
                    }

                } else if (durationSpinner.getSelectedItem().toString().toLowerCase().equalsIgnoreCase("yesterday")) {

                    if (NetworkUtilities.isInternet(StatusReadingDetailsActivity.this)) {
                        no_data_found.setVisibility(View.GONE);
                        getSensorsReadingGraph(ParseDateFormat.getYesterdayDateString("dd/MM/yyyy"), ParseDateFormat.getYesterdayDateString("dd/MM/yyyy"), TYPE);
                    } else {
                        no_data_found.setVisibility(View.VISIBLE);
                        no_data_found.setText(getResources().getString(R.string.error_no_internet_connection));
                        progressBar.setVisibility(View.GONE);
                        graphView.setVisibility(View.GONE);
                    }

                } else if (durationSpinner.getSelectedItem().toString().toLowerCase().equalsIgnoreCase("Last 7 days")) {
                    try {
                        if (NetworkUtilities.isInternet(StatusReadingDetailsActivity.this)) {
                            no_data_found.setVisibility(View.GONE);
                            getSensorsReadingGraph(ParseDateFormat.getLastSevenDate(), date, TYPE);
                        } else {
                            no_data_found.setVisibility(View.VISIBLE);
                            no_data_found.setText(getResources().getString(R.string.error_no_internet_connection));
                            progressBar.setVisibility(View.GONE);
                            graphView.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (durationSpinner.getSelectedItem().toString().toLowerCase().equalsIgnoreCase("This month")) {
                    try {
                        if (NetworkUtilities.isInternet(StatusReadingDetailsActivity.this)) {
                            no_data_found.setVisibility(View.GONE);
                            getSensorsReadingGraph(ParseDateFormat.getCurrentMonthFirstDate(), date, TYPE);
                        } else {
                            no_data_found.setVisibility(View.VISIBLE);
                            no_data_found.setText(getResources().getString(R.string.error_no_internet_connection));
                            progressBar.setVisibility(View.GONE);
                            graphView.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (graphData != null) {
                    setGraphValue(typeSpinner.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getSensorsReadingGraph(String dateStart, String dateEnd, String type) {

        progressBar.setVisibility(View.VISIBLE);
        graphView.setVisibility(View.GONE);

        try {
            CreateRequest.getInstance().getSensorsReadingGraph(sensorItem.getKey(), dateStart, dateEnd, type, new ApiCallback<SensorReadingGraphResponse>() {

                @Override
                public void onSuccess(SensorReadingGraphResponse response) {
                    progressBar.setVisibility(View.GONE);
                    graphView.setVisibility(View.VISIBLE);

                    if (response != null && response.getData() != null && response.getData().size() > 0) {
                        if (response.getAxis() != null) {
                            xAxisLable = response.getAxis().getX();
                            yAxisLogo.setText(response.getAxis().getY());
                        }
                        graphData = response.getData();
                        setGraphData();
                    }
                }

                @Override
                public void onFailure(String error) {
                    progressBar.setVisibility(View.GONE);
                    graphView.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setGraphData() {

        initGraphProperties();

        ArrayList<String> sensorTypeList = new ArrayList<>();

        for (Map.Entry<String, ArrayList<ArrayList<String>>> entry : graphData.entrySet()) {
            sensorTypeList.add(entry.getKey());
        }

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sensorTypeList);
        typeSpinner.setAdapter(typeAdapter);
    }

    private void initGraphProperties() {

        graphView.setDrawGridBackground(false);
        graphView.setDescription("");
        graphView.setNoDataTextDescription("");
        graphView.setHighlightEnabled(false);
        graphView.setTouchEnabled(true);
        graphView.setDragEnabled(true);
        graphView.setScaleEnabled(true);
        graphView.setPinchZoom(true);
        graphView.getAxisRight().setEnabled(false);

        leftAxis = graphView.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLimitLinesBehindData(true);

        XAxis bottomAxis = graphView.getXAxis();
        bottomAxis.setDrawGridLines(false);
        bottomAxis.setDrawAxisLine(true);
        bottomAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        bottomAxis.setSpaceBetweenLabels(1);
        bottomAxis.setTextSize(9);
        bottomAxis.setAvoidFirstLastClipping(true);
    }

    private void setGraphValue(String sensorType) {

        ArrayList<ArrayList<String>> timeList = new ArrayList<>();
        ArrayList<ArrayList<Float>> yValuesList = new ArrayList<>();

        ArrayList<String> legendLabels = new ArrayList<>();

        int count = 0;

        for (Map.Entry<String, ArrayList<ArrayList<String>>> entry : graphData.entrySet()) {
            String key = entry.getKey();

            if (key.equalsIgnoreCase(sensorType)) {
                count++;
                ArrayList<ArrayList<String>> value = entry.getValue();

                ArrayList<String> time = new ArrayList<>();
                ArrayList<Float> yValues = new ArrayList<>();

                for (int i = 0; i < value.size(); i++) {

                    if (xAxisLable.equalsIgnoreCase("time")) {
                        time.add(ParseDateFormat.getTimeFromTimestamp(value.get(i).get(0), "HH:mm"));
                    } else {
                        time.add(ParseDateFormat.getDateFromTimestamp(value.get(i).get(0), "dd MMM"));
                    }
                    yValues.add(Float.parseFloat(value.get(i).get(1)));
                }
                legendLabels.add(key);
                timeList.add(time);
                yValuesList.add(yValues);
            }
        }

        if (count == 1) {
            renderGraph(yValuesList, timeList, legendLabels);
        }
    }

    private void renderGraph(ArrayList<ArrayList<Float>> yValuesList, ArrayList<ArrayList<String>> timeList, ArrayList<String> legendLabels) {

        ArrayList<String> colors = randomCodeGenerator(yValuesList.size());

        ArrayList<LineDataSet> dataSets = new ArrayList<>();

        for (int j = 0; j < timeList.size(); j++) {

            ArrayList<Float> yValues = yValuesList.get(j);

            ArrayList<Entry> MainGraphLine = new ArrayList<>();

            for (int i = 0; i < timeList.get(0).size(); i++) {
                MainGraphLine.add(new Entry(yValues.get(i), i));
            }

            LineDataSet set1 = new LineDataSet(MainGraphLine, legendLabels.get(j));
            set1.setValueTextColor(Color.DKGRAY);
            set1.setValueTextSize(9f);
            set1.setColor(Color.parseColor(colors.get(j)));

            set1.setLineWidth(2f);
            set1.setCircleSize(0f);
            set1.setDrawCircleHole(false);
            set1.setFillAlpha(65);
            set1.setDrawFilled(false);
            set1.setDrawValues(false);
            set1.setDrawCubic(false);
            set1.setCubicIntensity(0.2f);

            dataSets.add(set1);
        }

        ArrayList<String> time = timeList.get(0);

        // create a data object with the data sets
        LineData data = new LineData(time, dataSets);
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(9f);

        graphView.setData(data);        // set data

        graphView.animateX(1000, Easing.EasingOption.EaseInOutQuart);
        graphView.invalidate();

        Legend legend = graphView.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
    }

    public ArrayList<String> randomCodeGenerator(int colorCount) {

        ArrayList<String> hexColorMap = new ArrayList<>();

//        hexColorMap.add("#26A69A");
//        hexColorMap.add("#607D8B");
//        hexColorMap.add("#33CCFF");
//        hexColorMap.add("#CC68BD");

        for (int a = 0; a < colorCount; a++) {
            String code = "" + (int) (Math.random() * 256);
            code = code + code + code;
            int i = Integer.parseInt(code);

            hexColorMap.add("#" + Integer.toHexString(0x1000000 | i).substring(1).toUpperCase());

            DebugLog.d("Color code : " + Integer.toHexString(0x1000000 | i).substring(1).toUpperCase());
        }
        return hexColorMap;
    }
}
