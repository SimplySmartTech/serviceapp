package com.simplysmart.service.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.google.gson.Gson;
import com.simplysmart.service.R;
import com.simplysmart.service.adapter.ImageListAdapter;
import com.simplysmart.service.aws.AWSConstants;
import com.simplysmart.service.aws.Util;
import com.simplysmart.service.callback.ApiCallback;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.custom.HorizontalListView;
import com.simplysmart.service.model.category.Category;
import com.simplysmart.service.model.category.CategoryResponse;
import com.simplysmart.service.model.category.SubCategory;
import com.simplysmart.service.model.common.CommonResponse;
import com.simplysmart.service.model.helpdesk.Complaint;
import com.simplysmart.service.model.helpdesk.NewComplaint;
import com.simplysmart.service.request.CreateRequest;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

public class CreateComplaintActivity extends BaseActivity implements View.OnClickListener {

    private Spinner categorySpinner, subCategorySpinner, prioritySpinner;
    private EditText unitInfo, additionInfo;
    private LinearLayout subCategoryLayout;

    private CategoryResponse categoryResponse;
    private Complaint complaint;

    private String categoryId;
    private String subCategoryId;

    private RadioGroup complaintType;
    private RadioGroup isDependentRadio;

    private HorizontalListView imageListView;

    private LinearLayout ll_parent;
    private Context context;
    private static final int REQUEST_TAKE_PHOTO = 1;
    public final static int REQUEST_GALLARY_PHOTO = 2;
    private static final String INTENT_IMAGE_CAPTURED = "image_captured";
    public static final String KEY_EXTRA_DATA = "image_data";

    private static String mCurrentPhotoPath;

    private TransferUtility transferUtility;

    private ArrayList<String> imageUrlList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_complaint);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Update");

        context = CreateComplaintActivity.this;
        transferUtility = Util.getTransferUtility(getApplicationContext());

        //get complaint data from previous screen
        Intent intent = getIntent();
        if (intent != null && intent.getParcelableExtra("complaint") != null) {
            complaint = intent.getParcelableExtra("complaint");
        }
        bindViews();
    }

    //Initialize ui widgets
    private void bindViews() {
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        subCategorySpinner = (Spinner) findViewById(R.id.subCategorySpinner);
        prioritySpinner = (Spinner) findViewById(R.id.unitSpinner);

        imageListView = (HorizontalListView) findViewById(R.id.imageListView);

        unitInfo = (EditText) findViewById(R.id.unitInfo);
        additionInfo = (EditText) findViewById(R.id.additionInfo);
        additionInfo.setCursorVisible(false);

        Button createTicket = (Button) findViewById(R.id.createTicket);
        TextView addImage = (TextView) findViewById(R.id.addImage);

        additionInfo.setOnClickListener(this);
        addImage.setOnClickListener(this);
        createTicket.setOnClickListener(this);

        complaintType = (RadioGroup) findViewById(R.id.complaintType);
        isDependentRadio = (RadioGroup) findViewById(R.id.isDependentRadio);

        subCategoryLayout = (LinearLayout) findViewById(R.id.subCategoryLayout);

        ll_parent = (LinearLayout) findViewById(R.id.ll_parent);

        setCategoryData();

        if (complaint.getUnit() != null) {
            unitInfo.setText(complaint.getUnit().getInfo());
        }
    }

    //Set new complaint request body with selected values
    private void createComplaint() {
        NewComplaint newComplaint = new NewComplaint();

        newComplaint.setParent_ticket(complaint.getNumber());
        newComplaint.setResident_id(complaint.getResident().getId());
        newComplaint.setUnit_id(complaint.getUnit().getId());
        newComplaint.setUnit_info(complaint.getUnit().getInfo());

        newComplaint.setOf_type(complaintType.getCheckedRadioButtonId() == R.id.complaint ? "Complaint" : "Request");
        newComplaint.setCategory_id(categoryId);
        newComplaint.setSub_category_id(subCategoryId);
        newComplaint.setPriority(prioritySpinner.getSelectedItem().toString());
        newComplaint.setIs_dependant(isDependentRadio.getCheckedRadioButtonId() == R.id.yes);

        String description = additionInfo.getText().toString().trim();

        //check for mandatory field
        if (description.isEmpty()) {
            showSnackBar(ll_parent, "Please enter complaint description.");
            return;
        }
        newComplaint.setDescription(description);

        if (imageUrlList.size() > 0) {
            newComplaint.setAssets(imageUrlList);
        }

        createComplaintRequest(newComplaint);
    }

    //Make create ticket request call to server
    private void createComplaintRequest(NewComplaint newComplaint) {
        CreateRequest.getInstance().createNewComplaint(newComplaint, new ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                Toast.makeText(CreateComplaintActivity.this, response.getMessage(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CreateComplaintActivity.this, HelpDeskScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(CreateComplaintActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    //Set complaint/request category dropdown
    private void setCategoryData() {

        SharedPreferences preferences = getSharedPreferences("CategoryInfo", Context.MODE_PRIVATE);
        String category_string = preferences.getString("category_details", "");

        categoryResponse = new Gson().fromJson(category_string, CategoryResponse.class);
        final ArrayList<Category> categories = categoryResponse.getCategories();

        final ArrayAdapter<Category> categoryArrayAdapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(categoryArrayAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Category category = categories.get(position);
                categoryId = category.getId();

                //Check for subCategory list for selected category
                if (category.getSub_categories() != null && category.getSub_categories().size() > 0) {
                    setSubCategoryData(category.getSub_categories());
                    subCategoryLayout.setVisibility(View.VISIBLE);
                    return;
                }
                subCategorySpinner.setAdapter(null);
                subCategoryId = null;
                subCategoryLayout.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //Set complaint/request subcategory dropdown
    private void setSubCategoryData(final ArrayList<SubCategory> subCategoryData) {

        ArrayAdapter<SubCategory> subCategoryArrayAdapter = new ArrayAdapter<>(CreateComplaintActivity.this,
                R.layout.support_simple_spinner_dropdown_item, subCategoryData);

        subCategorySpinner.setAdapter(subCategoryArrayAdapter);

        subCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subCategoryId = subCategoryData.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected int getStatusBarColor() {
        return 0;
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
                super.onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //Make input field cursor
            case R.id.additionInfo:
                additionInfo.setCursorVisible(true);
                break;

            //Create ticket
            case R.id.createTicket:
                createComplaint();
                break;

            //Add assets (images)
            case R.id.addImage:
                customDialog();
                break;
        }
    }

    //displayed dialog for camera/gallery option
    public void customDialog() {
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
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, REQUEST_GALLARY_PHOTO);
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

    private static File createImageFile() throws IOException {

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

                imageUrlList.add(url);
                ImageListAdapter imageListAdapter = new ImageListAdapter(context, imageUrlList);
                imageListView.setAdapter(imageListAdapter);

            }
        }
    }


}
