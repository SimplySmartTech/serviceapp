package com.simplysmart.service.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.simplysmart.service.R;
import com.simplysmart.service.common.CommonMethod;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.ErrorUtils;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.ServiceGenerator;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.gcm.QuickstartPreferences;
import com.simplysmart.service.gcm.RegistrationIntentService;
import com.simplysmart.service.model.common.APIError;
import com.simplysmart.service.model.user.LoginRequest;
import com.simplysmart.service.model.user.LoginResponse;
import com.simplysmart.service.model.user.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    private Context context;
    private EditText editUsername, editPassword;
    private Button buttonLogin;
    private RelativeLayout llCompanySpinner;
    private Spinner companySpinner;
    private String subDomain = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = LoginActivity.this;
        initializeWidgets();
        buttonLogin.setOnClickListener(loginClick);
    }

    @Override
    protected int getStatusBarColor() {
        return 0;
    }

    private void initializeWidgets() {
        editUsername = (EditText) findViewById(R.id.user_name);
        editPassword = (EditText) findViewById(R.id.password);
        buttonLogin = (Button) findViewById(R.id.btn_login);
        llCompanySpinner = (RelativeLayout) findViewById(R.id.llCompanySpinner);
        companySpinner = (Spinner) findViewById(R.id.companySpinner);
    }

    private final View.OnClickListener loginClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (NetworkUtilities.isInternet(getApplicationContext())) {

                if (llCompanySpinner.getVisibility() == View.VISIBLE) {
                    callCompanyLogin();
                } else {
                    callNormalLogin();
                }
            } else {
                displayMessage(getString(R.string.error_no_internet_connection));
            }
        }
    };

    private void callCompanyLogin() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String gcmToken = sharedPreferences.getString(QuickstartPreferences.GCM_TOKEN, "");

        if (gcmToken.isEmpty()) {
            DebugLog.d("Unable to get gcm token from server.");
            return;
        }
        prepareCompanyLoginRequest(gcmToken);
    }

    private void callNormalLogin() {
        if (CommonMethod.checkPlayServices(LoginActivity.this)) {

            CommonMethod.hideKeyboard(LoginActivity.this);
            showActivitySpinner();
            Intent intent = new Intent(LoginActivity.this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            dismissActivitySpinner();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String gcmToken = sharedPreferences.getString(QuickstartPreferences.GCM_TOKEN, "");

            if (gcmToken.isEmpty()) {
                DebugLog.d("Unable to get gcm token from server.");
                return;
            }
            prepareNormalLoginRequest(gcmToken);
        }
    };

    private void prepareNormalLoginRequest(String gcmToken) {

        String android_id = CommonMethod.getDeviceId(context);

        DebugLog.d("login device id : " + android_id);
        DebugLog.d("login registrationId :" + gcmToken);

        if (NetworkUtilities.isInternet(LoginActivity.this)) {

            if (residentLoginValidation(editUsername.getText().toString().trim(), editPassword.getText().toString().trim())) {

                showActivitySpinner();

                LoginRequest request = new LoginRequest();
                LoginRequest.Session session = new LoginRequest.Session();
                session.setLogin(editUsername.getText().toString().trim());
                session.setPassword(editPassword.getText().toString().trim());
                session.setDevice_id(CommonMethod.getDeviceId(context));
                session.setNotification_token(gcmToken);
                request.setSession(session);
                request.setThird_party_apps(true);
                request.setUser_login(true);

                ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
                Call<LoginResponse> loginResponseCall = apiInterface.residentLogin(request);
                loginResponseCall.enqueue(new Callback<LoginResponse>() {

                    @Override
                    public void onResponse(Call<LoginResponse> call, final Response<LoginResponse> response) {

                        if (response.isSuccessful()) {
                            if (response.body().isAuthenticated()) {

                                Gson gson = new Gson();

                                Log.d("Response", ":" + gson.toJson(response.body()));
                                llCompanySpinner.setVisibility(View.GONE);
                                setUserData(response.body());

                                Intent i;
                                if (GlobalData.getInstance().getSubDomain().equalsIgnoreCase("demo")) {
                                    i = new Intent(LoginActivity.this, MainActivity_V2.class);
                                } else {
                                    i = new Intent(LoginActivity.this, MainActivity.class);
                                }
                                startActivity(i);
                                finish();

                            } else if (response.code() == 401) {
                                handleAuthorizationFailed();

                            } else {
                                llCompanySpinner.setVisibility(View.VISIBLE);

                                ArrayList<String> arrayList = new ArrayList<>();

                                arrayList.add(0, "Select Company");
                                if (response.body().getCompany_list() != null && response.body().getCompany_list().size() > 0) {

                                    for (int i = 0; i < response.body().getCompany_list().size(); i++) {
                                        arrayList.add(response.body().getCompany_list().get(i).getName());
                                    }

                                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_spinner_item, arrayList);
                                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    companySpinner.setAdapter(spinnerArrayAdapter);

                                    companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            if (position > 0) {
                                                subDomain = response.body().getCompany_list().get(position - 1).getSubdomain();
                                                DebugLog.d("subDomain : " + subDomain);
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });
                                }
                            }
                        } else

                        {
                            APIError error = ErrorUtils.parseError(response);
                            displayMessage(error.message());
                        }

                        dismissActivitySpinner();
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        dismissActivitySpinner();
                        displayMessage(getResources().getString(R.string.error_in_network));
                    }
                });
            }
        } else

        {
            displayMessage(getString(R.string.error_no_internet_connection));
        }

    }

    private void prepareCompanyLoginRequest(String gcmToken) {

        String android_id = CommonMethod.getDeviceId(context);

        DebugLog.d("login device id : " + android_id);
        DebugLog.d("login registrationId :" + gcmToken);

        if (NetworkUtilities.isInternet(LoginActivity.this)) {

            if (residentLoginValidation(editUsername.getText().toString().trim(), editPassword.getText().toString().trim())) {

                showActivitySpinner();

                LoginRequest request = new LoginRequest();
                LoginRequest.Session session = new LoginRequest.Session();
                session.setLogin(editUsername.getText().toString().trim());
                session.setPassword(editPassword.getText().toString().trim());
                session.setDevice_id(CommonMethod.getDeviceId(context));
                session.setNotification_token(gcmToken);
                request.setSession(session);
                request.setThird_party_apps(true);
                request.setUser_login(true);

                ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
                Call<LoginResponse> loginResponseCall = apiInterface.residentLoginWithSubDomain(subDomain, request);
                loginResponseCall.enqueue(new Callback<LoginResponse>() {

                    @Override
                    public void onResponse(Call<LoginResponse> call, final Response<LoginResponse> response) {

                        if (response.isSuccessful() && response.body().getData() != null) {
                            setUserData(response.body());
                            Log.d("Response:", "Response Login" + response.body());
                            //Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            Intent i = new Intent(LoginActivity.this, MainActivity_V2.class);
                            startActivity(i);
                            finish();

                        } else if (response.code() == 401) {
                            handleAuthorizationFailed();
                        } else {
                            APIError error = ErrorUtils.parseError(response);
                            displayMessage(error.message());
                        }
                        dismissActivitySpinner();
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        dismissActivitySpinner();
                        displayMessage(getResources().getString(R.string.error_in_network));
                    }
                });
            }
        } else {
            displayMessage(getString(R.string.error_no_internet_connection));
        }
    }

    private void setUserData(LoginResponse response) {

        Gson gson = new Gson();
        User user = response.getData().getUser();
        String unitInfo = gson.toJson(user);

        SharedPreferences UserInfo = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = UserInfo.edit();

        preferencesEditor.putBoolean("isLogin", true);

        preferencesEditor.putString("id", user.getId());
        preferencesEditor.putString("name", user.getName());
        preferencesEditor.putString("username", user.getUsername());
        preferencesEditor.putString("email", user.getEmail());

        preferencesEditor.putString("api_key", user.getApi_key());
        preferencesEditor.putString("auth_token", user.getAuth_token());
        preferencesEditor.putString("subdomain", response.getSubdomain());
        preferencesEditor.putString("role_code", user.getRole_code());

        GlobalData.getInstance().setSubDomain(response.getSubdomain());

        preferencesEditor.putString("unit_info", unitInfo);

        preferencesEditor.apply();

    }

    private boolean residentLoginValidation(String Username, String Password) {

        if (Username.equals("")) {
            displayMessage("Please enter username.");
            return false;
        } else if (Password.equals("")) {
            displayMessage("Please enter password.");
            return false;
        }
        return true;
    }

}
