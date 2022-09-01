package com.simplysmart.service.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.simplysmart.service.R;
import com.simplysmart.service.adapter.CommentListAdapter;
import com.simplysmart.service.aws.AWSConstants;
import com.simplysmart.service.aws.Util;
import com.simplysmart.service.callback.ApiCallback;
import com.simplysmart.service.common.CommonMethod;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.AppConstant;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.model.helpdesk.Complaint;
import com.simplysmart.service.model.helpdesk.ComplaintChat;
import com.simplysmart.service.model.helpdesk.ComplaintChatResponse;
import com.simplysmart.service.model.helpdesk.ComplaintDetailResponse;
import com.simplysmart.service.request.CreateRequest;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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

    private static final int REQUEST_TAKE_PHOTO = 1;
    public final static int REQUEST_GALLARY_PHOTO = 2;
    private static final String INTENT_IMAGE_CAPTURED = "image_captured";
    public static final String KEY_EXTRA_DATA = "image_data";
    private static Context context;
    private static String mCurrentPhotoPath;

    private ImageView cameraButton;
    private TextView category_logo;

    private TransferUtility transferUtility;

    private HashMap<String, Integer> priorityColorMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_detail_screen);

        context = ComplaintDetailScreenActivity.this;
        transferUtility = Util.getTransferUtility(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Help desk");

        priorityColorMap.put("Regular", R.drawable.circle_priority_regular);
        priorityColorMap.put("High", R.drawable.circle_priority_high);
        priorityColorMap.put("Medium", R.drawable.circle_priority_medium);
        priorityColorMap.put("Low", R.drawable.circle_priority_low);

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
            Intent intent = new Intent(ComplaintDetailScreenActivity.this, HelpDeskScreenActivity.class);
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
                    Intent intent = new Intent(ComplaintDetailScreenActivity.this, HelpDeskScreenActivity.class);
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

        category_logo = (TextView) findViewById(R.id.category_logo);
        TextView unit_logo = (TextView) findViewById(R.id.unit_logo);

        textSubcategory = (TextView) findViewById(R.id.txt_subcategory);
        textUnitNo = (TextView) findViewById(R.id.txt_unit_no);
        textComplaintNo = (TextView) findViewById(R.id.complaint_no);
        editComment = (EditText) findViewById(R.id.edit_comment);

        complaintStatus = (TextView) findViewById(R.id.complaint_status);

        commentList = (ListView) findViewById(R.id.comment_list);
        buttonSend = (TextView) findViewById(R.id.btn_send);

        cameraButton = (ImageView) findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(cameraClick);

        Typeface iconTypeface = Typeface.createFromAsset(getAssets(), AppConstant.FONT_BOTSWORTH);
        unit_logo.setTypeface(iconTypeface);
        buttonSend.setTypeface(iconTypeface);

        complaintStatus.setTypeface(iconTypeface);

        category_logo.setText(getString(R.string.icon_electricity));
        unit_logo.setText(getString(R.string.icon_myflat));
        buttonSend.setText(getString(R.string.icon_send));

        buttonSend.setOnClickListener(postCommentClick);

        ll_new_comment = (RelativeLayout) findViewById(R.id.ll_new_comment);

        commentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getData().get(position).getImage_url() != null && !adapter.getData().get(position).getImage_url().equalsIgnoreCase("")) {
                    Intent intent = new Intent(ComplaintDetailScreenActivity.this, ImageViewNormalActivity.class);
                    intent.putExtra("photoPath", adapter.getData().get(position).getImage_url());
                    startActivity(intent);
                }
            }
        });
    }

    private void setComplaintInfo() {

        if (complaint.getAasm_state() != null && !complaint.getAasm_state().equalsIgnoreCase("closed")) {
            ll_new_comment.setVisibility(View.VISIBLE);
        } else {
            ll_new_comment.setVisibility(View.GONE);
        }

        if (complaint.getAasm_state().equalsIgnoreCase("closed") || complaint.getAasm_state().equalsIgnoreCase("blocked")) {
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

        if (complaint.getAasm_state() != null && complaint.getAasm_state().equalsIgnoreCase("assigned")) {
            complaintStatus.setOnClickListener(openDialogClick);
        } else {
            complaintStatus.setOnClickListener(null);
        }

        category_logo.setText(complaint.getCategory_short_name());
        if (priorityColorMap.containsKey(complaint.getPriority())) {
            category_logo.setBackgroundResource(priorityColorMap.get(complaint.getPriority()));
        }

        adapter = new CommentListAdapter(this, complaint.getSorted_activities());
        commentList.setAdapter(adapter);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                commentList.smoothScrollToPosition(adapter.getCount());
            }
        }, 100);
    }

    private final View.OnClickListener openDialogClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialogComplaintInfo dialog = new AlertDialogComplaintInfo(ComplaintDetailScreenActivity.this);
            dialog.createCustomDialog();
        }
    };

    private final View.OnClickListener cameraClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CustomDialog();
        }
    };

    private final View.OnClickListener postCommentClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (editComment.getText().toString().trim().isEmpty()) {
                showSnackBar(ll_new_comment, "Please write your comment or upload photo before submit.");
                return;
            }
            postComplaintComment(complaint_id, editComment.getText().toString().trim(), "");
        }
    };

    private void getComplaintDetail(String compliant_id) {

        showActivitySpinner();
        CreateRequest.getInstance().getComplaintDetails(compliant_id, new ApiCallback<ComplaintDetailResponse>() {
            @Override
            public void onSuccess(ComplaintDetailResponse response) {
                dismissActivitySpinner();
                parseComplaintDetailResponse(response);
            }

            @Override
            public void onFailure(String error) {
                dismissActivitySpinner();
                showSnackBar(ll_new_comment, error);
            }
        });
    }

    private void parseComplaintDetailResponse(ComplaintDetailResponse result) {
        complaint = result.getData().getComplaint();
        if (complaint != null) {
            setComplaintInfo();
        }
    }

    private void postComplaintComment(String compliant_id, String comment, String image_url) {

        buttonSend.setClickable(false);

        CreateRequest.getInstance().postComment(compliant_id, comment, image_url, new ApiCallback<ComplaintChatResponse>() {
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
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                commentList.smoothScrollToPosition(adapter.getCount());
            }
        }, 100);

    }

    private BroadcastReceiver UpdateActivity = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ComplaintChat activity = intent.getParcelableExtra("UPDATED_ACTIVITY_INFO");

            DebugLog.d("NEW MESSAGE COMING-------------------------- service");

            if (activity != null && intent.getStringExtra("complaint_id").equalsIgnoreCase(complaint_id)) {
                ArrayList<ComplaintChat> chats = new ArrayList<>();
                chats.add(activity);

                try {
                    if (adapter != null && adapter.getData().size() > 0) {
                        if (!adapter.getData().get(adapter.getData().size() - 1).getId().equalsIgnoreCase(activity.getId())) {
                            adapter.addData(chats);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        adapter = new CommentListAdapter(ComplaintDetailScreenActivity.this, complaint.getSorted_activities());
                        commentList.setAdapter(adapter);
                    }
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            commentList.smoothScrollToPosition(adapter.getCount());
                        }
                    }, 100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                editComment.setText("");
            }
        }
    };

    private BroadcastReceiver UpdateDetails = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("complaint_id") != null && intent.getStringExtra("complaint_id").equalsIgnoreCase(complaint_id)) {
                getComplaintDetail(intent.getStringExtra("complaint_id"));
            }
        }
    };

    public void CustomDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_image_capture);
        dialog.setTitle(context.getResources().getString(R.string.txt_capture_image_selection));

        LinearLayout lLayoutCameraDialog = (LinearLayout) dialog.findViewById(R.id.lLayoutCameraDialog);
        LinearLayout lLayoutGalleryDialog = (LinearLayout) dialog.findViewById(R.id.lLayoutGalleryDialog);
        LinearLayout lLayoutRemoveDialog = (LinearLayout) dialog.findViewById(R.id.lLayoutRemoveDialog);

        lLayoutCameraDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                dialog.dismiss();
            }
        });

        lLayoutGalleryDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                ((Activity) context).startActivityForResult(pickPhoto, REQUEST_GALLARY_PHOTO);
                dialog.dismiss();
            }
        });

        lLayoutRemoveDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                ((Activity) context).startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic(mCurrentPhotoPath);

        } else if (requestCode == REQUEST_GALLARY_PHOTO && resultCode == RESULT_OK) {
            Uri photoUri = data.getData();
            if (photoUri != null) {
                try {
                    //We get the file path from the media info returned by the content resolver
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(photoUri, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = 0;
                        columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = null;
                        filePath = cursor.getString(columnIndex);
                        cursor.close();

                        setPic(filePath);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic(final String photoPath) {
        beginUpload(compressImage(photoPath));
    }

    private void beginUpload(final String photoPath) {

        DebugLog.d("Local photo url:" + photoPath);
        String filePath = photoPath;
        try {
            try {
                File file = new File(filePath);
                TransferObserver observer = transferUtility.upload(
                        AWSConstants.BUCKET_NAME,
                        AWSConstants.PATH_FOLDER + file.getName(),
                        file, CannedAccessControlList.PublicRead);

                showActivitySpinner();
                observer.setTransferListener(new UploadListener(file.getName()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String message = (String) msg.obj;
            postComplaintComment(complaint_id, editComment.getText().toString().trim(), message);
        }
    };

    private class UploadListener implements TransferListener {

        private String fileName;

        UploadListener(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public void onError(int id, Exception e) {
            Log.e("AAA", "Error during upload: " + id, e);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d("AAA", String.format("onProgressChanged: %d, total: %d, current: %d", id, bytesTotal, bytesCurrent));
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
            Log.d("AAA", "onStateChanged: " + id + ", " + newState);

            if (newState == TransferState.COMPLETED) {

                dismissActivitySpinner();
                String url = AWSConstants.S3_URL + AWSConstants.BUCKET_NAME + "/" + AWSConstants.PATH_FOLDER + fileName;
                DebugLog.d("URL :::: " + url);

                Message msg = Message.obtain();
                msg.obj = url;
                msg.setTarget(handler);
                msg.sendToTarget();
            }
        }
    }

}


