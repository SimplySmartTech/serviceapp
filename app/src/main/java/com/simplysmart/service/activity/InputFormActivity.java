package com.simplysmart.service.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.google.gson.JsonObject;
import com.simplysmart.service.R;
import com.simplysmart.service.adapter.ReadingListAdapter;
import com.simplysmart.service.aws.AWSConstants;
import com.simplysmart.service.aws.Util;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.ErrorUtils;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.ServiceGenerator;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.database.ReadingDataRealm;
import com.simplysmart.service.dialog.EditDialog;
import com.simplysmart.service.dialog.SubmitReadingDialog;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.model.common.APIError;
import com.simplysmart.service.model.matrix.ReadingData;
import com.simplysmart.service.model.matrix.SensorData;
import com.simplysmart.service.permission.MarshmallowPermission;
import com.simplysmart.service.service.PhotoUploadService;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InputFormActivity extends BaseActivity implements EditDialog.EditDialogListener {

    private static final int PERMISSION_REQUEST_INTERNET = 0;
    private static final int PERMISSION_EXTERNAL_STORAGE = 1;
    private static final int PERMISSION_CAMERA = 2;

    private static final String TAG = "InputFormActivity";
    private final int REQUEST_TAKE_PHOTO = 1;
    private final int REQUEST_GALLERY_PHOTO = 2;
    private String mCurrentPhotoPath;
    private File image;
    private TransferUtility transferUtility;
    private LinearLayout mParentLayout;
    private EditText mInputReadingValue;
    private TextView unit, submitForm, titleList;
    private ImageView uploadImage;
    //    private ImageView photoDone;
    private View middleLine;

    private RecyclerView readingList;

    private String uploadedReadingUrl = "";

    private SensorData sensorData;
    private int groupPosition;
    private int childPosition;

    private ReadingData readingData;
    private boolean imageTaken = false;
    private boolean uploadedImage = false;

    private ReadingListAdapter readingListAdapter;

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
        titleList = (TextView) findViewById(R.id.title_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Input Reading");

        bindViews();
        Realm realm = Realm.getDefaultInstance();
        ReadingDataRealm readingDataRealm = realm.where(ReadingDataRealm.class).findFirst();
        if (readingDataRealm != null) {
            String oldDate = getDate(readingDataRealm.getTimestamp(), "dd-MM-yyyy");
            String newDate = getDate(Calendar.getInstance().getTimeInMillis(), "dd-MM-yyyy");

            if (!oldDate.equals(newDate)) {
                SubmitReadingDialog dialog = SubmitReadingDialog.newInstance("ALERT", "You have not submitted previous data. Would you like to submit now ?", "LATER", "SUBMIT NOW");
                dialog.show(getFragmentManager(), "submitDialog");
            }
        }


        RealmList<ReadingDataRealm> localDataList = ReadingDataRealm.findExistingReading(sensorData.getUtility_identifier(), sensorData.getSensor_name());
        if (localDataList == null || localDataList.size() == 0) {
            titleList.setVisibility(View.INVISIBLE);
        } else {
            titleList.setVisibility(View.VISIBLE);
            setList(localDataList);
        }

        initialiseViews();
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
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

    @Override
    public void updateResult(boolean newValue, int position, String value) {
        if (newValue) {
            readingListAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSION_CAMERA) {
            // Request for Internet permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start Internet preview Activity.
                Snackbar.make(mParentLayout, "Camera permission was granted.",
                        Snackbar.LENGTH_SHORT)
                        .show();
                dispatchTakePictureIntent();
            } else {
                // Permission request was denied.
                Snackbar.make(mParentLayout, "Camera permission request was denied.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }

        if (requestCode == PERMISSION_EXTERNAL_STORAGE) {
            // Request for Internet permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start Internet preview Activity.
                Snackbar.make(mParentLayout, "External storage permission was granted.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                // Permission request was denied.
                Snackbar.make(mParentLayout, "External Storage permission request was denied.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }

    }


    private void bindViews() {
        mParentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        mInputReadingValue = (EditText) findViewById(R.id.reading);
        unit = (TextView) findViewById(R.id.unit);
        uploadImage = (ImageView) findViewById(R.id.photo);
        submitForm = (TextView) findViewById(R.id.submit);
        readingList = (RecyclerView) findViewById(R.id.readingList);
        middleLine = findViewById(R.id.middleSeparator);
        unit.setText(sensorData.getUnit());

    }

    private void initialiseViews() {
        if (sensorData != null && sensorData.getPhotographic_evidence() != null && sensorData.getPhotographic_evidence().equalsIgnoreCase("true")) {
            uploadImage.setVisibility(View.VISIBLE);
            middleLine.setVisibility(View.VISIBLE);
        } else {
            uploadImage.setVisibility(View.GONE);
            middleLine.setVisibility(View.GONE);
        }

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customImagePicker();
            }
        });

        submitForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputReadingValue.clearFocus();

                if (imageTaken) {
                    if (!mInputReadingValue.getText().toString().trim().equalsIgnoreCase("")) {

                        readingData = new ReadingData();
                        readingData.setUtility_id(sensorData.getUtility_identifier());
                        readingData.setValue(mInputReadingValue.getText().toString());
                        readingData.setPhotographic_evidence_url(uploadedReadingUrl);
                        readingData.setSensor_name(sensorData.getSensor_name());
                        saveToDisk(readingData);

                        mInputReadingValue.setText("");
                        mCurrentPhotoPath = "";
                        uploadedReadingUrl = "";
                        imageTaken = false;
                        uploadedImage = false;
                        if(NetworkUtilities.isInternet(InputFormActivity.this)) {
                            Intent i = new Intent(InputFormActivity.this, PhotoUploadService.class);
                            i.putExtra(StringConstants.USE_UNIT, false);
                            startService(i);
                        }

                    } else {
                        showSnackBar(mParentLayout, "Please enter reading before submit.");
                    }
                } else {
                    if (!mInputReadingValue.getText().toString().trim().equalsIgnoreCase("")) {
                        readingData = new ReadingData();
                        readingData.setUtility_id(sensorData.getUtility_identifier());
                        readingData.setValue(mInputReadingValue.getText().toString());
                        readingData.setPhotographic_evidence_url(uploadedReadingUrl);
                        readingData.setSensor_name(sensorData.getSensor_name());

                        saveToDisk(readingData);

                        mInputReadingValue.setText("");
                        mCurrentPhotoPath = "";
                        uploadedReadingUrl = "";
                        imageTaken = false;
                        uploadedImage = false;
                    } else {
                        showSnackBar(mParentLayout, "Please enter reading before submit.");
                    }
                }

            }
        });
    }

    private void setList(ReadingDataRealm dataRealm) {
        titleList.setVisibility(View.VISIBLE);

        ArrayList<ReadingDataRealm> readingsList = new ArrayList<>();
        readingsList.add(dataRealm);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        readingListAdapter = new ReadingListAdapter(readingsList, this, getFragmentManager());
        readingList.setLayoutManager(linearLayoutManager);
        readingList.setAdapter(readingListAdapter);

    }

    private void setList(RealmList<ReadingDataRealm> localDataList) {
        RealmList<ReadingDataRealm> list = ReadingDataRealm.findExistingReading(sensorData.getUtility_identifier(), sensorData.getSensor_name());
        ArrayList<ReadingDataRealm> readingsList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            readingsList.add(list.get(i));
        }

        Collections.sort(readingsList, new Comparator<ReadingDataRealm>() {
            @Override
            public int compare(ReadingDataRealm o1, ReadingDataRealm o2) {
                return (int) (o2.getTimestamp() - o1.getTimestamp());
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        readingListAdapter = new ReadingListAdapter(readingsList, this, getFragmentManager());
        readingList.setLayoutManager(linearLayoutManager);
        readingList.setAdapter(readingListAdapter);
    }

    private void saveToDisk(ReadingData readingData) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh-mm aa");
        String time = simpleDateFormat.format(calendar.getTimeInMillis());

        Realm realm = Realm.getDefaultInstance();
        ReadingDataRealm readingDataRealm;

        realm.beginTransaction();
        readingDataRealm = realm.createObject(ReadingDataRealm.class);
        readingDataRealm.setData(readingData);
        readingDataRealm.setDate(time);
        readingDataRealm.setTimestamp(calendar.getTimeInMillis());
        readingDataRealm.setLocal_photo_url(mCurrentPhotoPath);
        readingDataRealm.setUnit(sensorData.getUnit());
        readingDataRealm.setUnit_id(GlobalData.getInstance().getSelectedUnitId());

        if (uploadedImage) {
            readingDataRealm.setPhotographic_evidence_url(uploadedReadingUrl);
            readingDataRealm.setUploadedImage(true);
        } else {
            readingDataRealm.setPhotographic_evidence_url("");
            readingDataRealm.setUploadedImage(false);
        }
        realm.commitTransaction();
        updateList(readingDataRealm);

    }

    private void updateList(ReadingDataRealm dataRealm) {
        if (readingListAdapter != null) {
            readingListAdapter.addElement(dataRealm);
            readingList.scrollToPosition(0);
        } else {
            setList(dataRealm);
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
                checkCamera();
//                dispatchTakePictureIntent();
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

    private void checkCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            // Permission is already available, start Internet preview
            Snackbar.make(mParentLayout,
                    "Camera permission is available.",
                    Snackbar.LENGTH_SHORT).show();
            dispatchTakePictureIntent();
        } else {
            new MarshmallowPermission(InputFormActivity.this, mParentLayout).checkPermissionForCamera();
        }
    }

    private void checkExternalStorage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            // Permission is already available, start Internet preview
            Snackbar.make(mParentLayout,
                    "WRITE_EXTERNAL_STORAGE permission is available.",
                    Snackbar.LENGTH_SHORT).show();

        } else {
            new MarshmallowPermission(InputFormActivity.this, mParentLayout).checkPermissionForExternalStorage();
        }
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

        checkExternalStorage();

        File storageDir = new File(Environment.getExternalStorageDirectory(), StringConstants.STORAGE_DIRECTORY);

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
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
                    imageTaken = true;
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
                    mCurrentPhotoPath = path;
                    imageTaken = true;
                } else {
                    showSnackBar(mParentLayout, "Getting error in image file.", false);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private void setPic(ImageView view) {

        Picasso.with(InputFormActivity.this).load(image)
                .placeholder(R.drawable.ic_menu_slideshow)
                .noFade()
                .error(R.drawable.ic_menu_slideshow).into(view);

        view.setVisibility(View.VISIBLE);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    private void beginUpload(String filePath) {
        if (filePath == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file", Toast.LENGTH_LONG).show();
            return;
        }
        File file = new File(filePath);
        image = file;


        TransferObserver observer = transferUtility.upload(
                AWSConstants.BUCKET_NAME,
                AWSConstants.PATH_FOLDER + file.getName(),
                file, CannedAccessControlList.PublicRead);

        observer.setTransferListener(new UploadListener());
    }

    private class UploadListener implements TransferListener {

        @Override
        public void onError(int id, Exception e) {
//            mSubmitForm.setEnabled(true);
//            mSubmitForm.setText("SUBMIT FORM");
            uploadedImage = false;
            dismissActivitySpinner();
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
                uploadedImage = true;
                dismissActivitySpinner();

//                uploadImage.setText("CHANGE IMAGE");
//                mSubmitForm.setEnabled(true);
//                mSubmitForm.setText("SUBMIT FORM");

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

                    } else if (response.code() == 401) {
                        handleAuthorizationFailed();
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
