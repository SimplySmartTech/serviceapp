package com.simplysmart.service.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.simplysmart.service.R;
import com.simplysmart.service.adapter.CommentListAdapter;
import com.simplysmart.service.callback.ApiCallback;
import com.simplysmart.service.common.CommonMethod;
import com.simplysmart.service.config.AppConstant;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.model.helpdesk.Complaint;
import com.simplysmart.service.model.helpdesk.ComplaintChat;
import com.simplysmart.service.model.helpdesk.ComplaintChatResponse;
import com.simplysmart.service.model.helpdesk.ComplaintDetailResponse;
import com.simplysmart.service.request.CreateRequest;

import java.util.ArrayList;

/**
 * Created by shekhar on 4/8/15.
 */
public class ComplaintDetailScreenActivity extends BaseActivity {

    private Complaint complaint;
    private EditText editComment;
    private TextView complaintStatus, textSubcategory, textUnitNo, textComplaintNo, buttonSend;
    private ListView commentList;
    private String complaint_id = "";
    private CommentListAdapter adapter;
    private RelativeLayout ll_new_comment;
    private boolean isFromPush;

    private boolean isClosed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_detail_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Help desk");

        initializeView();
        CommonMethod.hideKeyboard(this);

        if (getIntent() != null && getIntent().getExtras() != null) {
            isFromPush = getIntent().getBooleanExtra("UPDATED_FROM_PUSH", false);
            if (isFromPush) loadUserData();

            complaint_id = getIntent().getStringExtra("complaint_id");
            getComplaintDetail(complaint_id);
        }
    }

    @Override
    public void onBackPressed() {
        if (isFromPush) {
            Intent intent = new Intent(ComplaintDetailScreenActivity.this, MainActivity_V2.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void loadUserData() {

        SharedPreferences UserInfo = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        GlobalData.getInstance().setAuthToken(UserInfo.getString("auth_token", ""));
        GlobalData.getInstance().setApi_key(UserInfo.getString("api_key", ""));
        GlobalData.getInstance().setSubDomain(UserInfo.getString("subdomain", ""));
        GlobalData.getInstance().setRole_code(UserInfo.getString("role_code", ""));
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem reset = menu.findItem(R.id.update_menu);
        reset.setVisible(isClosed);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if (isFromPush) {
                    Intent intent = new Intent(ComplaintDetailScreenActivity.this, MainActivity_V2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    if (getFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportActionBar().show();
                        getFragmentManager().popBackStack();
                    } else {
                        super.onBackPressed();
                    }
                    super.onBackPressed();
                }
                break;
            case R.id.update_menu:
                Intent updateStatusActivity = new Intent(this, UpdateComplaintStatusActivity.class);
                updateStatusActivity.putExtra("complaint", complaint);
                startActivity(updateStatusActivity);
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(UpdateActivity,
                new IntentFilter("UpdateActivity"));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(UpdateDetails,
                new IntentFilter("UpdateDetails"));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(UpdateActivity);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(UpdateDetails);
        super.onDestroy();
    }

    private void initializeView() {

        TextView category_logo = (TextView) findViewById(R.id.category_logo);
        TextView unit_logo = (TextView) findViewById(R.id.unit_logo);

        textSubcategory = (TextView) findViewById(R.id.txt_subcategory);
        textUnitNo = (TextView) findViewById(R.id.txt_unit_no);
        textComplaintNo = (TextView) findViewById(R.id.complaint_no);
        editComment = (EditText) findViewById(R.id.edit_comment);

        complaintStatus = (TextView) findViewById(R.id.complaint_status);

        commentList = (ListView) findViewById(R.id.comment_list);
        buttonSend = (TextView) findViewById(R.id.btn_send);

        Typeface iconTypeface = Typeface.createFromAsset(getAssets(), AppConstant.FONT_BOTSWORTH);
        category_logo.setTypeface(iconTypeface);
        unit_logo.setTypeface(iconTypeface);
        buttonSend.setTypeface(iconTypeface);

        complaintStatus.setTypeface(iconTypeface);

        category_logo.setText(getString(R.string.icon_electricity));
        unit_logo.setText(getString(R.string.icon_myflat));
        buttonSend.setText(getString(R.string.icon_send));

        buttonSend.setOnClickListener(postCommentClick);

        ll_new_comment = (RelativeLayout) findViewById(R.id.ll_new_comment);
    }

    private void setComplaintInfo() {

        if (complaint.getAasm_state() != null && !complaint.getAasm_state().equalsIgnoreCase("closed")) {
            ll_new_comment.setVisibility(View.VISIBLE);
        } else {
            ll_new_comment.setVisibility(View.GONE);
        }

        if (complaint.getAasm_state().equalsIgnoreCase("closed")) {
            isClosed = false;
            invalidateOptionsMenu();
        } else {
            isClosed = true;
            invalidateOptionsMenu();
        }

        complaintStatus.setText(getString(R.string.icon_assign) + complaint.getAasm_state());
        textSubcategory.setText(complaint.getSub_category_name());
        textComplaintNo.setText("# " + complaint.getNumber());
        textUnitNo.setText(complaint.getUnit_info());

        if (complaint.getAasm_state().equalsIgnoreCase("assigned")) {
            complaintStatus.setOnClickListener(openDialogClick);
        } else {
            complaintStatus.setOnClickListener(null);
        }

        adapter = new CommentListAdapter(this, complaint.getSorted_activities());
        commentList.setAdapter(adapter);
        commentList.smoothScrollToPosition(adapter.getCount());
    }

    private final View.OnClickListener openDialogClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialogComplaintInfo dialog = new AlertDialogComplaintInfo(ComplaintDetailScreenActivity.this);
            dialog.createCustomDialog();
        }
    };

    private final View.OnClickListener postCommentClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            postComplaintComment(complaint_id);
        }
    };

    private void getComplaintDetail(String compliant_id) {

        CreateRequest.getInstance().getComplaintDetails(compliant_id, new ApiCallback<ComplaintDetailResponse>() {
            @Override
            public void onSuccess(ComplaintDetailResponse response) {
                parseComplaintDetailResponse(response);
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    private void parseComplaintDetailResponse(ComplaintDetailResponse result) {
        complaint = result.getData().getComplaint();
        if (complaint != null) {
            setComplaintInfo();
        }
    }

    private void postComplaintComment(String compliant_id) {

        buttonSend.setClickable(false);

        CreateRequest.getInstance().postComment(compliant_id, editComment.getText().toString().trim(), new ApiCallback<ComplaintChatResponse>() {
            @Override
            public void onSuccess(ComplaintChatResponse response) {
                buttonSend.setClickable(true);
                parseComplaintChatResponse(response);
            }

            @Override
            public void onFailure(String error) {
                buttonSend.setClickable(true);
            }
        });
    }

    private void parseComplaintChatResponse(ComplaintChatResponse result) {

        ArrayList<ComplaintChat> chats = new ArrayList<>();
        ComplaintChat chat = result.getActivity();
        chats.add(chat);
        adapter.addData(chats);
        adapter.notifyDataSetChanged();
        editComment.setText("");
        commentList.smoothScrollToPosition(adapter.getCount());

    }

    private BroadcastReceiver UpdateActivity = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ComplaintChat activity = intent.getParcelableExtra("UPDATED_ACTIVITY_INFO");
            if (activity != null) {
                ArrayList<ComplaintChat> chats = new ArrayList<>();
                chats.add(activity);

                if (adapter != null) {
                    adapter.addData(chats);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new CommentListAdapter(ComplaintDetailScreenActivity.this, complaint.getSorted_activities());
                    commentList.setAdapter(adapter);
                }
                editComment.setText("");
                commentList.smoothScrollToPosition(adapter.getCount());
            }
        }
    };

    private BroadcastReceiver UpdateDetails = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            getComplaintDetail(intent.getStringExtra("complaint_id"));
        }
    };


}


