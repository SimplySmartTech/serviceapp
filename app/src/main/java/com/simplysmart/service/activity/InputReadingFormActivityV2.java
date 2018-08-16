package com.simplysmart.service.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.simplysmart.service.adapter.ReadingListAdapterV2;
import com.simplysmart.service.aws.AWSConstants;
import com.simplysmart.service.aws.Util;
import com.simplysmart.service.common.CommonMethod;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.database.ReadingTable;
import com.simplysmart.service.interfaces.EditDialogListener;
import com.simplysmart.service.model.matrix.MatrixData;
import com.simplysmart.service.model.matrix.MatrixReadingData;
import com.simplysmart.service.model.matrix.ReadingData;
import com.simplysmart.service.model.matrix.ReadingDataResponse;
import com.simplysmart.service.model.matrix.VehicleType;
import com.simplysmart.service.permission.MarshmallowPermission;
import com.simplysmart.service.util.ParseDateFormat;
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
import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;

public class InputReadingFormActivityV2 extends BaseActivity implements EditDialogListener {

    private static final String TAG = "InputReadingActivity";
    private final int REQUEST_TAKE_PHOTO = 1;
    private final int REQUEST_GALLERY_PHOTO = 2;
    private String mCurrentPhotoPath;
    private File image;
    private TransferUtility transferUtility;
    private LinearLayout mParentLayout;
    private RelativeLayout mCustomTareWeightLayout;
    private RelativeLayout vehicleSelectionSpinnerLayout;

    private TextView unit, submitForm, titleList, mTareWeightUnit, timeOld;
    private ImageView uploadImage;

    private ImageView photoDone;
    private View middleLine;

    private RecyclerView readingList;
    private LinearLayout readingsLayout;

    private ReadingData readingData;

    private List<ReadingTable> readings;
    private ReadingListAdapterV2 readingListAdapter;
    private Paint p = new Paint();

    private boolean backdated;

    private MatrixData matrixData;

    private Spinner vehicleSelectionSpinner;
    private EditText mInputReadingValue;

    private RadioGroup readingTypeRadioGroup;
    private RadioButton bucketTypeRadio;
    private RadioButton totalReadingTypeRadio;

    protected final String DATE_FORMAT_POLICY = "dd/MM/yyyy";
    private ArrayList<MatrixReadingData> readingDataArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_form_v2);

        bindViews();

        if (getIntent() != null && getIntent().getExtras() != null) {
            matrixData = getIntent().getParcelableExtra(StringConstants.METRIC_DATA);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Input Reading");

        if (matrixData != null) {
            setUIData(matrixData);
            getSupportActionBar().setTitle(matrixData.getName());
        }
        transferUtility = Util.getTransferUtility(this);

        getDataFromPreference();
    }

    private void getDataFromPreference() {

        SharedPreferences ReadingInfo = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String readingDataString = ReadingInfo.getString("ReadingInfo", "");

        if (!readingDataString.isEmpty()) {
            Gson gson = new Gson();
            ReadingDataResponse readingDataResponse = gson.fromJson(readingDataString, ReadingDataResponse.class);

            for (int i = 0; i < readingDataResponse.getReadings().size(); i++) {
                if (matrixData.getIdentifier().equalsIgnoreCase(readingDataResponse.getReadings().get(i).getIdentifier())
                        && compareDateForReading(ParseDateFormat.getDateV2(Long.parseLong(readingDataResponse.getReadings().get(i).getTimestamp())))) {
                    readingDataArrayList.add(readingDataResponse.getReadings().get(i));
                }
            }
        }

        if (readingDataArrayList.size() > 0) {
            setDataInList(readingDataArrayList);
        }
    }

    private void setDataToPreference(MatrixReadingData readingData) {

        SharedPreferences ReadingInfo = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String readingDataString = ReadingInfo.getString("ReadingInfo", "");

        Gson gson = new Gson();
        if (!readingDataString.isEmpty()) {
            ReadingDataResponse readingDataResponse = gson.fromJson(readingDataString, ReadingDataResponse.class);
            ArrayList<MatrixReadingData> matrixReadingData = readingDataResponse.getReadings();
            matrixReadingData.add(readingData);
            readingDataResponse.setReadings(matrixReadingData);
            String finalReadingDataString = gson.toJson(readingDataResponse);
            SharedPreferences.Editor preferencesEditor = ReadingInfo.edit();
            preferencesEditor.putString("ReadingInfo", finalReadingDataString);
            preferencesEditor.apply();
        } else {
            String finalReadingDataString = gson.toJson(new ReadingDataResponse(readingDataArrayList));
            SharedPreferences.Editor preferencesEditor = ReadingInfo.edit();
            preferencesEditor.putString("ReadingInfo", finalReadingDataString);
            preferencesEditor.apply();
        }
    }

    private void setDataInList(ArrayList<MatrixReadingData> readings) {
        Collections.sort(readings, new Comparator<MatrixReadingData>() {
            @Override
            public int compare(MatrixReadingData lhs, MatrixReadingData rhs) {
                return (int) (Long.parseLong(rhs.getTimestamp()) - Long.parseLong(lhs.getTimestamp()));
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        readingListAdapter = new ReadingListAdapterV2(readings, this, getSupportFragmentManager());
        readingList.setLayoutManager(linearLayoutManager);
        readingList.setAdapter(readingListAdapter);
        readingList.setVisibility(View.VISIBLE);
        readingList.setNestedScrollingEnabled(false);
        readingsLayout.setVisibility(View.VISIBLE);
    }

    private void setUIData(MatrixData matrixData) {
        setAdditionalData(matrixData);
    }

    private void setAdditionalData(MatrixData matrixData) {

        if (matrixData.isBucket_system()) {
            readingTypeRadioGroup.setVisibility(View.VISIBLE);
            readingTypeRadioGroup.check(R.id.bucketTypeRadio);
            final ArrayAdapter<VehicleType> vehicleArrayAdapter
                    = new ArrayAdapter<>(this,
                    R.layout.support_simple_spinner_dropdown_item, matrixData.getVehicles());
            vehicleSelectionSpinner.setAdapter(vehicleArrayAdapter);
        } else {
            vehicleSelectionSpinner.setVisibility(GONE);
            readingTypeRadioGroup.setVisibility(View.GONE);
            readingTypeRadioGroup.check(R.id.totalReadingTypeRadio);
        }
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
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
    public void updateResult(int newValue, int position, String value) {
//        if (newValue == StringConstants.NEW_VALUE) {
//            readingListAdapter = new ReadingListAdapter(ReadingTable.getReadings(sensorData.utility_identifier, sensorData.sensor_name), this, getSupportFragmentManager());
//            readingList.setAdapter(readingListAdapter);
//            readingListAdapter.notifyDataSetChanged();
//        } else if (newValue == StringConstants.VALUE_DELETED) {
//            readingListAdapter.getReadingsList().remove(position);
//            readingListAdapter.notifyItemRemoved(position);
//        } else {
//            readingListAdapter.notifyItemChanged(position);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void bindViews() {

        vehicleSelectionSpinnerLayout = (RelativeLayout) findViewById(R.id.vehicleSelectionSpinnerLayout);
        vehicleSelectionSpinner = (Spinner) findViewById(R.id.vehicleSelectionSpinner);

        mParentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        mInputReadingValue = (EditText) findViewById(R.id.mInputReadingValue);

        readingTypeRadioGroup = (RadioGroup) findViewById(R.id.readingTypeRadioGroup);
        bucketTypeRadio = (RadioButton) findViewById(R.id.bucketTypeRadio);
        totalReadingTypeRadio = (RadioButton) findViewById(R.id.totalReadingTypeRadio);

        readingTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.bucketTypeRadio) {
                    mInputReadingValue.setHint("Enter No. of bucket");
                    vehicleSelectionSpinnerLayout.setVisibility(View.VISIBLE);
                } else {
                    vehicleSelectionSpinnerLayout.setVisibility(View.GONE);
                    String unitString = matrixData.getUnit() != null ? ("in " + matrixData.getUnit()) : "";
                    mInputReadingValue.setHint("Enter Total Reading " + unitString);
                }
            }
        });

        submitForm = (TextView) findViewById(R.id.submit);

        submitForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitMatrixRequest();
            }
        });

        unit = (TextView) findViewById(R.id.unit);
        uploadImage = (ImageView) findViewById(R.id.photo);

        readingList = (RecyclerView) findViewById(R.id.readingList);
        middleLine = findViewById(R.id.middleSeparator);

        mCustomTareWeightLayout = (RelativeLayout) findViewById(R.id.custom_tare_weight_layout);
        mTareWeightUnit = (TextView) findViewById(R.id.tare_weight_unit);
        readingsLayout = (LinearLayout) findViewById(R.id.readingsLayout);
        timeOld = (TextView) findViewById(R.id.time_old);

        readingsLayout.setVisibility(View.VISIBLE);
        mInputReadingValue.clearFocus();
        CommonMethod.hideKeyboard(this);
    }

    private void checkForPermissions() {
        checkExternalStorage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == StringConstants.PERMISSION_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            }
        }

        if (requestCode == StringConstants.PERMISSION_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkCamera();
            }
        }
    }

    private void checkCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            new MarshmallowPermission(InputReadingFormActivityV2.this, mParentLayout).checkPermissionForCamera();
        }
    }

    private void checkExternalStorage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            checkCamera();
        } else {
            new MarshmallowPermission(InputReadingFormActivityV2.this, mParentLayout).checkPermissionForExternalStorage();
        }
    }

    private void submitMatrixRequest() {

        if (mInputReadingValue.getText().toString().trim().isEmpty()) {
            showSnackBar(mParentLayout, "Please enter reading");
            return;
        }

        final MatrixReadingData readingData = new MatrixReadingData();
        readingData.setUnit(matrixData.getUnit());
        readingData.setSubdomain(GlobalData.getInstance().getSubDomain());
        readingData.setSite_id(GlobalData.getInstance().getSelectedUnitId());
        readingData.setIdentifier(matrixData.getIdentifier());
        readingData.setTimestamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));

        if (vehicleSelectionSpinnerLayout.isShown()) {
            double totalReading = (Integer.parseInt(mInputReadingValue.getText().toString().trim())) * matrixData.getVehicles().get(vehicleSelectionSpinner.getSelectedItemPosition()).getCapacity();
            readingData.setReading(String.valueOf(totalReading));
        } else {
            readingData.setReading(mInputReadingValue.getText().toString().trim());
        }

        showSnackBar(mParentLayout, "Data Saved");
        mInputReadingValue.setText("");
        readingDataArrayList.add(readingData);
        setDataToPreference(readingData);
        setDataInList(readingDataArrayList);

//        if (NetworkUtilities.isInternet(InputReadingFormActivityV2.this)) {
//
//            showActivitySpinner();
//            ApiInterface apiInterface = ServiceGeneratorV2.createService(ApiInterface.class);
//            Call<JsonObject> call = apiInterface.submitMatrixReading(readingData);
//            call.enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> call, final Response<JsonObject> response) {
//                    dismissActivitySpinner();
//                    if (response.isSuccessful()) {
//                        showSnackBar(mParentLayout, "Data uploaded successfully");
//                        mInputReadingValue.setText("");
////                        readingDataArrayList.add(readingData);
////                        setDataToPreference();
////                        setDataInList(readingDataArrayList);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<JsonObject> call, Throwable t) {
//                    dismissActivitySpinner();
//                    showSnackBar(mParentLayout, getString(R.string.error_in_network));
//                }
//            });
//        } else {
//            showSnackBar(mParentLayout, getString(R.string.error_no_internet_connection));
//        }
    }

    public void customImagePicker() {

        final Dialog dialog = new Dialog(InputReadingFormActivityV2.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_capture);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout lLayoutCameraDialog = (LinearLayout) dialog.findViewById(R.id.lLayoutCameraDialog);
        LinearLayout lLayoutGalleryDialog = (LinearLayout) dialog.findViewById(R.id.lLayoutGalleryDialog);
        LinearLayout lLayoutRemoveDialog = (LinearLayout) dialog.findViewById(R.id.lLayoutRemoveDialog);

        lLayoutCameraDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkForPermissions();
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
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
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
                    Log.d("CameraPhoto", mCurrentPhotoPath);
                    compressAndDeleteFile(mCurrentPhotoPath, true);
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
                    compressAndDeleteFile(mCurrentPhotoPath, false);
                    Log.d("GalleryPhoto", mCurrentPhotoPath);
                } else {
                    showSnackBar(mParentLayout, "Getting error in image file.", false);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == StringConstants.IMAGE_CHANGED && resultCode == StringConstants.IMAGE_CHANGED) {
            File oldImage = new File(mCurrentPhotoPath);
            String newPhotoPath = "";
            if (data != null && data.getExtras() != null) {
                newPhotoPath = data.getStringExtra(StringConstants.PHOTO_PATH);
            }

            if (!newPhotoPath.equals("") && oldImage.exists()) {
                oldImage.delete();
                mCurrentPhotoPath = newPhotoPath;
                setPic(uploadImage, mCurrentPhotoPath);
            }
        }
    }

    private void compressAndDeleteFile(String imageUrl, boolean delete) {
        File imageFile = new File(imageUrl);
        String compressedPath = "";

        if (imageFile.exists()) {
            try {
                compressedPath = compressImage(imageUrl);
                DebugLog.d(compressedPath);
                if (!compressedPath.equals("")) {
                    if (delete) {
                        imageFile.delete();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showSnackBar(mParentLayout, "Unable to compress image file.");
            }
        }

        mCurrentPhotoPath = compressedPath;
//        imageTaken = true;
        setPic(uploadImage, mCurrentPhotoPath);
    }

    private void setPic(ImageView view, String imageUrl) {
        File image = new File(imageUrl);

        if (image.exists()) {
            Picasso.with(InputReadingFormActivityV2.this).load(image)
                    .placeholder(R.drawable.ic_photo_black_48dp)
                    .noFade()
                    .fit().centerCrop()
//                    .resize(48, 48)
                    .error(R.drawable.ic_menu_slideshow).into(view);
            view.setAlpha(1.0f);
            view.setVisibility(View.VISIBLE);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
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
//            uploadedImage = false;
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
//                uploadedReadingUrl = url;
//                uploadedImage = true;
                dismissActivitySpinner();

//                uploadImage.setText("CHANGE IMAGE");
//                mSubmitForm.setEnabled(true);
//                mSubmitForm.setText("SUBMIT FORM");

            }
        }
    }

    public void setupUI(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    closeKeyboardAndCursor();
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public void closeKeyboardAndCursor() {
        mInputReadingValue.setCursorVisible(false);
        CommonMethod.hideKeyboard(InputReadingFormActivityV2.this);
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                MatrixReadingData readingTable = readingListAdapter.getReadingsList().get(position);

                if (direction == ItemTouchHelper.LEFT) {
                    deleteItemFromRecyclerView(position, readingTable);
                } else {
                    showEditDialog(readingTable, position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white_24dp);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mode_edit_white_24dp);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(readingList);
    }

    private void deleteItemFromRecyclerView(int position, MatrixReadingData readingTable) {
//        DeleteReadingDialog deleteReadingDialog = DeleteReadingDialog.newInstance("Alert", "Are you sure you want to delete this reading?", "No", "Yes", position, readingTable);
//        deleteReadingDialog.setCancelable(false);
//        deleteReadingDialog.show(getFragmentManager(), "deleteReadingDialog");
    }

    private void showEditDialog(MatrixReadingData readingTable, int position) {
//        EditDialog newDialog = EditDialog.newInstance(readingTable, position, true);
//        newDialog.show(getSupportFragmentManager(), "show_dialog");
    }

    protected boolean compareDateForReading(String readingDate) {

        if (readingDate == null || readingDate.isEmpty()) {
            return false;

        } else {
            SimpleDateFormat dfDate = new SimpleDateFormat(DATE_FORMAT_POLICY, Locale.getDefault());
            Date d;
            Date d1;
            try {
                d1 = dfDate.parse(readingDate);
                d = dfDate.parse(ParseDateFormat.getCurrentDate(DATE_FORMAT_POLICY));
                int diffInDays = (int) ((d.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
                DebugLog.d("diffInDays : " + diffInDays);
                return diffInDays <= 0;

            } catch (java.text.ParseException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
