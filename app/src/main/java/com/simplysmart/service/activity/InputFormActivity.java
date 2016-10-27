package com.simplysmart.service.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.google.gson.JsonObject;
import com.simplysmart.service.R;
import com.simplysmart.service.aws.AWSConstants;
import com.simplysmart.service.aws.Util;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.ErrorUtils;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.ServiceGenerator;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.model.common.APIError;
import com.simplysmart.service.model.matrix.ReadingData;
import com.simplysmart.service.model.matrix.SensorData;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InputFormActivity extends BaseActivity {

    private static final String TAG = "InputFormActivity";
    private final int REQUEST_TAKE_PHOTO = 1;
    private final int REQUEST_GALLERY_PHOTO = 2;
    private String mCurrentPhotoPath;
    private File image;
    private TransferUtility transferUtility;
    private RelativeLayout mParentLayout;
    private ImageView mReadingImage;
    private ProgressBar mHorizontalBar;
    private Button mUploadImage;
    private Button mSubmitForm;
    private EditText mInputReadingValue;

    private String uploadedReadingUrl = "";

    private SensorData sensorData;
    private int groupPosition;
    private int childPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_form);

        if (getIntent() != null && getIntent().getExtras() != null) {
            sensorData = getIntent().getParcelableExtra("SENSOR_DATA");
            groupPosition = getIntent().getIntExtra("groupPosition", -1);
            childPosition = getIntent().getIntExtra("childPosition", -1);
        } else {
            sensorData = null;
            groupPosition = -1;
            childPosition = -1;
        }

        transferUtility = Util.getTransferUtility(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle("Input Reading");

        mParentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        mReadingImage = (ImageView) findViewById(R.id.readingImage);
        mUploadImage = (Button) findViewById(R.id.uploadImage);
        mHorizontalBar = (ProgressBar) findViewById(R.id.horizontalBar);
        mSubmitForm = (Button) findViewById(R.id.submitForm);
        mInputReadingValue = (EditText) findViewById(R.id.inputReadingValue);

        if (sensorData != null && sensorData.getPhotographic_evidence() != null && sensorData.getPhotographic_evidence().equalsIgnoreCase("true")) {
            mHorizontalBar.setVisibility(View.GONE);
            mUploadImage.setVisibility(View.VISIBLE);
            mReadingImage.setVisibility(View.VISIBLE);
        } else {
            mHorizontalBar.setVisibility(View.GONE);
            mUploadImage.setVisibility(View.GONE);
            mReadingImage.setVisibility(View.GONE);
        }

        mUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customImagePicker();
            }
        });

        mSubmitForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mInputReadingValue.getText().toString().trim().equalsIgnoreCase("")) {
                    ReadingData readingData = new ReadingData();
                    readingData.setUtility_id(sensorData.getUtility_identifier());
                    readingData.setValue(mInputReadingValue.getText().toString());
                    readingData.setPhotographic_evidence_url(uploadedReadingUrl);
                    readingData.setSensor_name(sensorData.getSensor_name());
                    postReadingRequest(readingData, GlobalData.getInstance().getSubDomain());
                    postReadingRequest(readingData, GlobalData.getInstance().getSubDomain());
                } else {
                    showSnackBar(mParentLayout, "Please enter reading before submit.");
                }
            }
        });

    }

    @Override
    protected int getStatusBarColor() {
        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                supportFinishAfterTransition();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void customImagePicker() {

        final Dialog dialog = new Dialog(InputFormActivity.this);
        dialog.setContentView(R.layout.dialog_image_capture);
        dialog.setTitle(getString(R.string.txt_capture_image_selection));

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

                Intent pickPhoto = new Intent();
                if (Build.VERSION.SDK_INT >= 19) {
                    pickPhoto.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    pickPhoto.addCategory(Intent.CATEGORY_OPENABLE);
                    pickPhoto.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                } else {
                    pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
                }
                pickPhoto.setType("image/*");
                startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO);
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
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

//        File file = new File(InputFormActivity.this.getFilesDir(), imageFileName);
        image = getTempFile(InputFormActivity.this, imageFileName);
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//        image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();
        DebugLog.d("mCurrentPhotoPath : " + mCurrentPhotoPath);
        return image;
    }

    public File getTempFile(Context context, String url) {
        File file = null;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, ".jpg", context.getCacheDir());
        } catch (IOException e) {
            // Error while creating file
        }
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                if (mCurrentPhotoPath != null) {
                    if (NetworkUtilities.isInternet(this)) {
                        beginUpload(compressImage(mCurrentPhotoPath));
                        setPic();
                    } else {
                        showSnackBar(mParentLayout, getString(R.string.error_no_internet_connection), false);
                    }
                } else {
                    showSnackBar(mParentLayout, "Getting error in image file.", false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_GALLERY_PHOTO && resultCode == RESULT_OK) {
            try {
                Uri uri = data.getData();
                String path = getPath(uri);
                if (path != null) {
                    if (NetworkUtilities.isInternet(this)) {
                        beginUpload(compressImage(path));
                        setPic();
                    } else {
                        showSnackBar(mParentLayout, getString(R.string.error_no_internet_connection), false);
                    }
                } else {
                    showSnackBar(mParentLayout, "Getting error in image file.", false);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private void setPic() {

        Picasso.with(InputFormActivity.this).load(image)
                .placeholder(R.drawable.ic_menu_slideshow)
                .noFade()
                .error(R.drawable.ic_menu_slideshow).into(mReadingImage);

        mReadingImage.setVisibility(View.VISIBLE);
        mReadingImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    private void beginUpload(String filePath) {
        if (filePath == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file", Toast.LENGTH_LONG).show();
            return;
        }
        File file = new File(filePath);
        image = file;

        mHorizontalBar.setVisibility(View.VISIBLE);
        mSubmitForm.setEnabled(false);
        mSubmitForm.setText("PLEASE WAIT..");

        TransferObserver observer = transferUtility.upload(
                AWSConstants.BUCKET_NAME,
                AWSConstants.PATH_FOLDER + file.getName(),
                file, CannedAccessControlList.PublicRead);

        observer.setTransferListener(new UploadListener());
    }

    private class UploadListener implements TransferListener {

        @Override
        public void onError(int id, Exception e) {
            mHorizontalBar.setVisibility(View.INVISIBLE);
            mSubmitForm.setEnabled(true);
            mSubmitForm.setText("SUBMIT FORM");
            Log.e(TAG, "Error during upload: " + id, e);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d", id, bytesTotal, bytesCurrent));
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
            Log.d(TAG, "onStateChanged: " + id + ", " + newState);

            if (newState == TransferState.COMPLETED) {

                String url = AWSConstants.S3_URL
                        + AWSConstants.BUCKET_NAME + "/"
                        + AWSConstants.PATH_FOLDER
                        + image.getName();

                DebugLog.d("URL :::: " + url);
                uploadedReadingUrl = url;

                mUploadImage.setText("CHANGE IMAGE");
                mSubmitForm.setEnabled(true);
                mSubmitForm.setText("SUBMIT FORM");

                mHorizontalBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void postReadingRequest(ReadingData readingData, String subDomain) {

        if (NetworkUtilities.isInternet(InputFormActivity.this)) {

            showActivitySpinner();

            ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
            Call<JsonObject> call = apiInterface.submitReading(subDomain, readingData);
            call.enqueue(new Callback<JsonObject>() {

                @Override
                public void onResponse(Call<JsonObject> call, final Response<JsonObject> response) {

                    if (response.isSuccessful()) {

                        Intent i = new Intent("UPDATE_METRIC_SENSOR_LIST_ROW");
                        i.putExtra("groupPosition", groupPosition);
                        i.putExtra("childPosition", childPosition);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                        finish();

                    } else {
                        APIError error = ErrorUtils.parseError(response);
                        displayMessage(error.message());
                    }
                    dismissActivitySpinner();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    dismissActivitySpinner();
                    DebugLog.d(t.getLocalizedMessage());
                    displayMessage(getResources().getString(R.string.error_in_network));
                }
            });
        } else {
            displayMessage(getString(R.string.error_no_internet_connection));
        }
    }


}
