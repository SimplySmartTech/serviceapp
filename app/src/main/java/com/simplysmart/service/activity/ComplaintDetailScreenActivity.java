package com.simplysmart.service.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

    private Typeface textTypeface;
    private Complaint complaint;
    private EditText editComment;
    private TextView complaintStatus, textSubcategory, textUnitNo, textComplaintNo, buttonSend;
    private ListView commentList;
    private String complaint_id = "";
    private CommentListAdapter adapter;
    private RelativeLayout ll_new_comment;

    private boolean isClosed ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_detail_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Complaints Details");

        initializeView();
        CommonMethod.hideKeyboard(this);

        if (getIntent().getExtras() != null) {
            complaint_id = getIntent().getStringExtra("complaint_id");
            getComplaintDetail(complaint_id);
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem reset = menu.findItem(R.id.update_menu);
        reset.setVisible(isClosed);

        return true;
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.colorPrimaryDark;
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
            case R.id.update_menu :
                Log.d("Update","Upadte clicked");
                Intent updateStatusActivity = new Intent(this,UpdateComplaintStatusActivity.class);
                updateStatusActivity.putExtra("complaint",complaint);
                startActivity(updateStatusActivity);
                break;
        }
        return true;
    }

    private void initializeView() {

      //  textTypeface = Typeface.createFromAsset(getAssets(), AppConstant.FONT_EUROSTILE_REGULAR_MID);

        TextView helpdesk_logo = (TextView) findViewById(R.id.helpdesk_logo);
        TextView category_logo = (TextView) findViewById(R.id.category_logo);
        TextView unit_logo = (TextView) findViewById(R.id.unit_logo);

        textSubcategory = (TextView) findViewById(R.id.txt_subcategory);
        textUnitNo = (TextView) findViewById(R.id.txt_unit_no);
        textComplaintNo = (TextView) findViewById(R.id.complaint_no);
        editComment = (EditText) findViewById(R.id.edit_comment);

        complaintStatus = (TextView) findViewById(R.id.complaint_status);
        TextView imgCountTextView = (TextView) findViewById(R.id.img_count);

        commentList = (ListView) findViewById(R.id.comment_list);
        buttonSend = (TextView) findViewById(R.id.btn_send);

        Typeface iconTypeface = Typeface.createFromAsset(getAssets(), AppConstant.FONT_BOTSWORTH);
        helpdesk_logo.setTypeface(iconTypeface);
        category_logo.setTypeface(iconTypeface);
        unit_logo.setTypeface(iconTypeface);
        buttonSend.setTypeface(iconTypeface);

        complaintStatus.setTypeface(iconTypeface);
        textSubcategory.setTypeface(textTypeface);
        textUnitNo.setTypeface(textTypeface);
        textComplaintNo.setTypeface(textTypeface);
        editComment.setTypeface(textTypeface);

        helpdesk_logo.setText(getString(R.string.icon_helpdesk));
        category_logo.setText(getString(R.string.icon_electricity));
        unit_logo.setText(getString(R.string.icon_myflat));
        buttonSend.setText(getString(R.string.icon_send));

       // imgCountTextView.setOnClickListener(openGalleryClick);
        buttonSend.setOnClickListener(postCommentClick);

        ll_new_comment = (RelativeLayout) findViewById(R.id.ll_new_comment);
    }

    private void setComplaintInfo() {

        if (complaint.getAasm_state() != null && !complaint.getAasm_state().equalsIgnoreCase("closed")) {
            ll_new_comment.setVisibility(View.VISIBLE);

        } else {
            ll_new_comment.setVisibility(View.GONE);

        }

        if (complaint.getAasm_state().equalsIgnoreCase("closed")){
            isClosed = false;
            invalidateOptionsMenu();
        }
        else{
            isClosed = true;
            invalidateOptionsMenu();
        }

        complaintStatus.setText(getString(R.string.icon_assign) + complaint.getAasm_state());
        textSubcategory.setText(complaint.getSub_category_name());
        textComplaintNo.setText("# " + complaint.getNumber());
        textUnitNo.setText(complaint.getUnit_info());

//        if (complaint.getAasm_state().equalsIgnoreCase("assigned")) {
            complaintStatus.setOnClickListener(openDialogClick);
//        } else {
//            complaintStatus.setOnClickListener(null);
//        }

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

//    private final View.OnClickListener openGalleryClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Intent newIntent = new Intent(ComplaintDetailScreenActivity.this, PhotoGalleryActivity.class);
//            startActivity(newIntent);
//        }
//    };

    private final View.OnClickListener postCommentClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            postComplaintComment(complaint_id);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }



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
}
