package com.simplysmart.service.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.google.gson.Gson;
import com.simplysmart.service.R;
import com.simplysmart.service.adapter.MatrixListAdapter;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.ErrorUtils;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.ServiceGenerator;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.model.common.APIError;
import com.simplysmart.service.model.matrix.MatrixResponse;
import com.simplysmart.service.model.user.AccessPolicy;
import com.simplysmart.service.model.user.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    private ExpandableListView matrixList;
    private MatrixListAdapter matrixListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        matrixList = (ExpandableListView) findViewById(R.id.matrixList);

        getUserInfo();

        getMatrixRequest(GlobalData.getInstance().getUnits().get(0).getId(), GlobalData.getInstance().getSubDomain());
    }

    @Override
    protected int getStatusBarColor() {
        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getMatrixRequest(String unitId, String subDomain) {

        if (NetworkUtilities.isInternet(MainActivity.this)) {

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
                    DebugLog.d("Failed " + t.getLocalizedMessage());
                    displayMessage(getResources().getString(R.string.error_in_network));
                }
            });
        } else {
            displayMessage(getString(R.string.error_no_internet_connection));
        }
    }

    //Set matrix data to list
    private void setMatrixData(MatrixResponse matrixResponse) {
        matrixListAdapter = new MatrixListAdapter(MainActivity.this, matrixResponse.getData());
        matrixList.setAdapter(matrixListAdapter);
    }

    //Fetch logged user info from shared preferences
    private void getUserInfo() {

        SharedPreferences UserInfo = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        GlobalData.getInstance().setAuthToken(UserInfo.getString("auth_token", ""));
        GlobalData.getInstance().setApi_key(UserInfo.getString("api_key", ""));
        GlobalData.getInstance().setSubDomain(UserInfo.getString("subdomain", ""));
        GlobalData.getInstance().setRole_code(UserInfo.getString("role_code", ""));

        Gson gson = new Gson();
        String jsonUnitInfo = UserInfo.getString("unit_info", "");
        User residentData = gson.fromJson(jsonUnitInfo, User.class);

        String jsonAccessPolicy = UserInfo.getString("access_policy", "");
        AccessPolicy policy = gson.fromJson(jsonAccessPolicy, AccessPolicy.class);

        GlobalData.getInstance().setUnits(residentData.getUnits());
        GlobalData.getInstance().setAccessPolicy(policy);
    }
}
