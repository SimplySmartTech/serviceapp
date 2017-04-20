package com.simplysmart.service.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.simplysmart.service.R;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.ServiceGenerator;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.model.helpdesk.Complaint;
import com.simplysmart.service.model.helpdesk.ComplaintUpdateRequest;
import com.simplysmart.service.model.helpdesk.MessageResponseClass;
import com.simplysmart.service.model.helpdesk.PermittedActions;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateComplaintStatusActivity extends BaseActivity {

    private Spinner permittedActionSpinner;
    private Complaint complaint;
    private EditText commentEditText;
    ArrayList<String> permissionsList;
    ArrayList<PermittedActions> permitedActions;
    private boolean isTextFieldEnabled;
    Intent intent;
    String spinnerSelectedOption;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_complaint_status);


        intent = getIntent();
        complaint = intent.getParcelableExtra("complaint");




        permissionsList = new ArrayList<>();
        permittedActionSpinner = (Spinner) findViewById(R.id.permissions_spinner);
        permittedActionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                             @Override
                                                             public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                 spinnerSelected(position,id);
                                                                 spinnerSelectedOption = permissionsList.get(position);
                                                             }

                                                             @Override
                                                             public void onNothingSelected(AdapterView<?> parent) {

                                                             }
                                                         });
                commentEditText = (EditText) findViewById(R.id.comment_text);


         getPermissionList();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,permissionsList);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        permittedActionSpinner.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Update");
    }

    private void spinnerSelected(int position, long id) {

        if (!permitedActions.get(position).isComment_required() ){
            commentEditText.setEnabled(false);
            isTextFieldEnabled = false;
            //commentEditText.setBackground(R.drawable);

        }
        else {
            commentEditText.setEnabled(true);
            isTextFieldEnabled = true;

        }

    }

    private void getPermissionList() {

        permitedActions = complaint.getPermittedActions();
        for (int i = 0 ; i < permitedActions.size() ; i++ ){
            if (!(permitedActions.get(i).isComment_required())){
                commentEditText.setEnabled(false);
                isTextFieldEnabled = false;
            }
            else {
                commentEditText.setEnabled(true);
                isTextFieldEnabled = true;
            }
            permissionsList.add(i,permitedActions.get(i).getEvent());
        }

    }



    @Override
    protected int getStatusBarColor() {
        return 0;
    }

    public void callUpdate(View view){

    if (isTextFieldEnabled && !commentEditText.getText().toString().equals("")) {

        Toast.makeText(UpdateComplaintStatusActivity.this, "Calling API", Toast.LENGTH_LONG).show();

        Complaint complaintObject = new Complaint();
        complaintObject.setId(complaint.getId());
        complaintObject.setPriority(complaint.getPriority());
        complaintObject.setOf_type(complaint.getOf_type());
        complaintObject.setUnit_info(complaint.getUnit_info());
        complaintObject.setCategory_name(complaint.getCategory_name());
        complaintObject.setState_action(spinnerSelectedOption);


        // cos

        ComplaintUpdateRequest complaintUpdateRequest = new ComplaintUpdateRequest();



        if (isTextFieldEnabled) {
            String reasonText = commentEditText.getText().toString();
            if (spinnerSelectedOption.equals("Resolve")) {
                complaintObject.setResolved_reason(reasonText);
            }
            if (spinnerSelectedOption.equals("Block")) {
                complaintObject.setBlocked_reason(reasonText);
            }
            if (spinnerSelectedOption.equals("Reject")) {
                complaintObject.setRejected_reason(reasonText);
            }
            if (spinnerSelectedOption.equals("Close")) {

                complaintObject.setClosed_reason(reasonText);
            }

        }

        complaintUpdateRequest.setComplaint(complaintObject);



        Gson gson = new Gson();
        Log.d("ComplaintReq:", gson.toJson(complaintUpdateRequest));
        Log.d("SpinnerSel:", spinnerSelectedOption);
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<MessageResponseClass> complaintsResponseCall = apiInterface.updateComplaintStatus(complaintObject.getId(), GlobalData.getInstance().getSubDomain() ,complaintUpdateRequest);
        complaintsResponseCall.enqueue(new Callback<MessageResponseClass>() {
            @Override
            public void onResponse(Call<MessageResponseClass> call, Response<MessageResponseClass> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateComplaintStatusActivity.this, "Complaint status is updated successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(UpdateComplaintStatusActivity.this, "Response Error" + response.code(), Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<MessageResponseClass> call, Throwable t) {
                Toast.makeText(UpdateComplaintStatusActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }
    else {
        Toast.makeText(UpdateComplaintStatusActivity.this, "Please Enter Comment in Text boxF", Toast.LENGTH_LONG).show();
    }
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportActionBar().show();
                    getFragmentManager().popBackStack();
                } else {
                    super.onBackPressed();
                }
                break;
        }
        return true;
    }
}
