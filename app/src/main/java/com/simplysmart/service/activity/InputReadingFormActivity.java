package com.simplysmart.service.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.simplysmart.service.R;
import com.simplysmart.service.adapter.ReadingListAdapter;
import com.simplysmart.service.aws.AWSConstants;
import com.simplysmart.service.aws.Util;
import com.simplysmart.service.common.CommonMethod;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.database.ReadingTable;
import com.simplysmart.service.database.SensorTable;
import com.simplysmart.service.database.TareWeightTable;
import com.simplysmart.service.dialog.DeleteReadingDialog;
import com.simplysmart.service.dialog.EditDialog;
import com.simplysmart.service.dialog.SubmitReadingDialog;
import com.simplysmart.service.interfaces.EditDialogListener;
import com.simplysmart.service.model.matrix.ReadingData;
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
import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;

public class InputReadingFormActivity extends BaseActivity implements EditDialogListener {

    private static final String TAG = "InputReadingActivity";
    private final int REQUEST_TAKE_PHOTO = 1;
    private final int REQUEST_GALLERY_PHOTO = 2;
    private String mCurrentPhotoPath;
    private File image;
    private TransferUtility transferUtility;
    private LinearLayout mParentLayout;
    private RelativeLayout mCustomTareWeightLayout;
    private EditText mInputReadingValue, mTareWeightEditText;
    private TextView unit, submitForm, titleList, mTareWeightUnit, timeOld;
    private ImageView uploadImage;
    private Spinner tareWeightSpinner;
    private ImageView photoDone;
    private View middleLine;

    private RecyclerView readingList;

    private String uploadedReadingUrl = "";
    private String tare_weight = "";
    private String utility_id = "";
    private String sensor_name = "";
    private long old_date = -1;

    private SensorTable sensorData;
    private int groupPosition;
    private int childPosition;
    private LinearLayout readingsLayout;

    private ReadingData readingData;
    private boolean imageTaken = false;
    private boolean uploadedImage = false;
    private boolean needSpinner = false;

    private List<ReadingTable> readings;
    private ReadingListAdapter readingListAdapter;
    private Paint p = new Paint();

    private boolean backdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_form);

        if (getIntent() != null && getIntent().getExtras() != null) {
            utility_id = getIntent().getStringExtra(StringConstants.UTILITY_ID);
            sensor_name = getIntent().getStringExtra(StringConstants.SENSOR_NAME);
            groupPosition = getIntent().getIntExtra("groupPosition", -1);
            childPosition = getIntent().getIntExtra("childPosition", -1);
            backdated = getIntent().getBooleanExtra(StringConstants.BACKDATA, false);
        } else {
            utility_id = "";
            sensor_name = "";
            groupPosition = -1;
            childPosition = -1;
        }

        sensorData = SensorTable.getSensorInfo(utility_id, sensor_name);
        transferUtility = Util.getTransferUtility(this);
        titleList = (TextView) findViewById(R.id.title_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(sensorData.sensor_name + " reading");

        bindViews();

        initialiseViews();
        setupUI(mParentLayout);
        initSwipe();

        checkForPreviousData();
    }

    private void setDataInList(List<ReadingTable> readings) {
        Collections.sort(readings, new Comparator<ReadingTable>() {
            @Override
            public int compare(ReadingTable lhs, ReadingTable rhs) {
                return (int) (rhs.timestamp - lhs.timestamp);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        readingListAdapter = new ReadingListAdapter(readings, this, getSupportFragmentManager());
        readingList.setLayoutManager(linearLayoutManager);
        readingList.setAdapter(readingListAdapter);
        readingList.setVisibility(View.VISIBLE);
        readingsLayout.setVisibility(View.VISIBLE);
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
    public void updateResult(int newValue, int position, String value) {
        if (newValue == StringConstants.NEW_VALUE) {
            readingListAdapter = new ReadingListAdapter(ReadingTable.getReadings(sensorData.utility_identifier, sensorData.sensor_name), this, getSupportFragmentManager());
            readingList.setAdapter(readingListAdapter);
            readingListAdapter.notifyDataSetChanged();
        } else if (newValue == StringConstants.VALUE_DELETED) {
            readingListAdapter.getReadingsList().remove(position);
            readingListAdapter.notifyItemRemoved(position);
        } else {
            readingListAdapter.notifyItemChanged(position);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkForPreviousData() {
        List<ReadingTable> readings = ReadingTable.getReadings(sensorData.utility_identifier, sensorData.sensor_name);
        if (readings != null && readings.size() > 0) {
            String oldDate = getDate(readings.get(0).timestamp, "dd-MM-yyyy");
            String newDate = getDate(Calendar.getInstance().getTimeInMillis(), "dd-MM-yyyy");
            if (!oldDate.equals(newDate)) {
                SubmitReadingDialog dialog = SubmitReadingDialog.newInstance("ALERT", "You have not submitted previous data. Would you like to submit now ?", "LATER", "SUBMIT NOW");
                dialog.show(getFragmentManager(), "submitDialog");
            }

            setDataInList(readings);
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
        tareWeightSpinner = (Spinner) findViewById(R.id.tare_weight_spinner);
        mCustomTareWeightLayout = (RelativeLayout) findViewById(R.id.custom_tare_weight_layout);
        mTareWeightEditText = (EditText) findViewById(R.id.tare_weight_edittext);
        mTareWeightUnit = (TextView) findViewById(R.id.tare_weight_unit);
        readingsLayout = (LinearLayout) findViewById(R.id.readingsLayout);
        timeOld = (TextView) findViewById(R.id.time_old);

        if (backdated) {
            timeOld.setVisibility(View.VISIBLE);
        } else {
            timeOld.setVisibility(GONE);
        }

        String unitOfSensor = sensorData.unit;
        if (unitOfSensor.contains("\\")) {
            unit.setText("\u00B0 C");
        } else {
            unit.setText(sensorData.unit);
        }

        readingsLayout.setVisibility(GONE);
        mInputReadingValue.clearFocus();
        CommonMethod.hideKeyboard(this);
    }

    private void checkForPermissions() {
        checkExternalStorage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == StringConstants.PERMISSION_CAMERA) {
            // Request for Internet permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start Internet preview Activity.
                dispatchTakePictureIntent();
            } else {
                // Permission request was denied.
//                Snackbar.make(mParentLayout, "Camera permission request was denied.",
//                        Snackbar.LENGTH_SHORT)
//                        .show();
            }
        }

        if (requestCode == StringConstants.PERMISSION_EXTERNAL_STORAGE) {
            // Request for Internet permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start Internet preview Activity.
                checkCamera();
            } else {
                // Permission request was denied.
//                Snackbar.make(mParentLayout, "External Storage permission request was denied.",
//                        Snackbar.LENGTH_SHORT)
//                        .show();
            }
        }

    }

    private void checkCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            // Permission is already available, start Internet preview
            dispatchTakePictureIntent();
        } else {
            new MarshmallowPermission(InputReadingFormActivity.this, mParentLayout).checkPermissionForCamera();
        }
    }

    private void checkExternalStorage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            // Permission is already available, start Internet preview
            checkCamera();
        } else {
            new MarshmallowPermission(InputReadingFormActivity.this, mParentLayout).checkPermissionForExternalStorage();
        }
    }

    private void initialiseViews() {

        if(sensorData.placeholder!=null && !sensorData.placeholder.equalsIgnoreCase("")){
            mInputReadingValue.setHint(sensorData.placeholder);
        }

        mInputReadingValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputReadingValue.setCursorVisible(true);
            }
        });

        mInputReadingValue.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            closeKeyboardAndCursor();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        mTareWeightEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTareWeightEditText.setCursorVisible(true);
            }
        });

        if (sensorData != null && sensorData.photographic_evidence != null && sensorData.photographic_evidence.equalsIgnoreCase("true")) {
            uploadImage.setVisibility(View.VISIBLE);
            middleLine.setVisibility(View.VISIBLE);
        } else {
            uploadImage.setVisibility(GONE);
            middleLine.setVisibility(GONE);
        }

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageTaken && mCurrentPhotoPath != null && !mCurrentPhotoPath.equals("")) {
                    Intent intent = new Intent(InputReadingFormActivity.this, ImageViewActivity.class);
                    intent.putExtra(StringConstants.PHOTO_PATH, mCurrentPhotoPath);
                    intent.putExtra(StringConstants.ALLOW_NEW_IMAGE, true);
                    startActivityForResult(intent, StringConstants.IMAGE_CHANGED);
                } else {
                    customImagePicker();
                }
            }
        });

        final ArrayList<String> tareWeights = new ArrayList<>();
        final List<TareWeightTable> tareWeightTableList = TareWeightTable.getTareWeights(GlobalData.getInstance().getSelectedUnitId());
        Collections.sort(tareWeightTableList, new Comparator<TareWeightTable>() {
            @Override
            public int compare(TareWeightTable lhs, TareWeightTable rhs) {
                return lhs.name.compareToIgnoreCase(rhs.name);
            }
        });

        if (tareWeightTableList.size() > 0) {
            tareWeights.add("Enter tare weight manually");
            for (int i = 0; i < tareWeightTableList.size(); i++) {
                tareWeights.add(tareWeightTableList.get(i).name + " (" + tareWeightTableList.get(i).value + " Kg)");
            }
        }

        if (sensorData != null && sensorData.tare_weight) {
            needSpinner = true;
            ArrayAdapter<String> tareWeightAdapter = new ArrayAdapter<String>(InputReadingFormActivity.this, android.R.layout.simple_spinner_item, tareWeights);
            tareWeightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            tareWeightSpinner.setAdapter(tareWeightAdapter);

            tareWeightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0 && position < tareWeights.size() - 1) {
                        tare_weight = tareWeightTableList.get(position - 1).value;
                        mCustomTareWeightLayout.setVisibility(View.GONE);
                    } else if (position == 0) {
                        tare_weight = "";
                        mCustomTareWeightLayout.setVisibility(View.VISIBLE);
                        mTareWeightUnit.setText(sensorData.unit);
                    } else {
                        tare_weight = null;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    tare_weight = null;
                    mCustomTareWeightLayout.setVisibility(View.VISIBLE);
                    mTareWeightUnit.setText(sensorData.unit);
                }
            });

        } else {
            needSpinner = false;
            tareWeightSpinner.setVisibility(View.GONE);
            mCustomTareWeightLayout.setVisibility(View.GONE);
            tare_weight = null;
        }

        submitForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateFields()) {
                    saveReadingToDisk();

                    if (needSpinner) {
                        tareWeightSpinner.setSelection(0);
                        mCustomTareWeightLayout.setVisibility(View.VISIBLE);
                        mTareWeightEditText.setText("");
                        tare_weight = null;
                    }
                }
            }
        });

        timeOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
    }

    private void showTimePickerDialog() {
        Calendar c = Calendar.getInstance();

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                saveTimeOfReading(hourOfDay, minute);
            }
        }, hour, minute, false);

        timePickerDialog.show();
    }

    private void saveTimeOfReading(int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DATE, c.get(Calendar.DATE) - 1);

        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);

        timeOld.setText("" + hourOfDay + " : " + minute);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.getDefault());
        String time = simpleDateFormat.format(c.getTimeInMillis());
        timeOld.setText(time);
        old_date = c.getTimeInMillis();
    }

    private boolean validateFields() {
        if (mInputReadingValue.getText() == null || mInputReadingValue.getText().toString().equals("")) {
            showSnackBar(mParentLayout, "Please enter reading.", false);
            return false;
        }

        if (backdated) {
            if (old_date == -1) {
                showSnackBar(mParentLayout, "Please select time", false);
                return false;
            }
        }

        return true;
    }

    private void saveReadingToDisk() {
        if (imageTaken) {
            if (!mInputReadingValue.getText().toString().trim().equalsIgnoreCase("")) {
                submitForImage();

            } else {
                showSnackBar(mParentLayout, "Please enter reading before submit.");
            }

        } else {
            if (!mInputReadingValue.getText().toString().trim().equalsIgnoreCase("")) {
                submitWithoutImage();
            } else {
                showSnackBar(mParentLayout, "Please enter reading before submit.");
            }
        }
    }

    private void submitForImage() {
        saveToDisk();

        mInputReadingValue.setText("");
        mCurrentPhotoPath = "";
        uploadedReadingUrl = "";
        imageTaken = false;
        uploadImage.setImageResource(R.drawable.ic_camera_alt_black_48dp);
        uploadImage.setAlpha(0.4f);
        uploadedImage = false;
        old_date = -1;
        timeOld.setText(getString(R.string.old_time_text));

        if (NetworkUtilities.isInternet(InputReadingFormActivity.this)) {
            Intent i = new Intent(InputReadingFormActivity.this, PhotoUploadService.class);
            i.putExtra(StringConstants.USE_UNIT, false);
            startService(i);
        }
    }

    private void submitWithoutImage() {
        saveToDisk();

        mInputReadingValue.setText("");
        mCurrentPhotoPath = "";
        uploadedReadingUrl = "";
        old_date = -1;

        timeOld.setText(getString(R.string.old_time_text));
        imageTaken = false;
        uploadedImage = false;
        uploadImage.setAlpha(0.4f);
    }

    private void saveToDisk() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        String time = simpleDateFormat.format(calendar.getTimeInMillis());

        ReadingTable readingTable = new ReadingTable();
        readingTable.utility_id = sensorData.utility_identifier;
        readingTable.sensor_name = sensorData.sensor_name;
        readingTable.value = mInputReadingValue.getText().toString();

        if (uploadedImage) {
            readingTable.photographic_evidence_url = uploadedReadingUrl;
            readingTable.uploadedImage = true;
        } else {
            readingTable.photographic_evidence_url = "";
            readingTable.uploadedImage = false;
        }

        readingTable.local_photo_url = mCurrentPhotoPath;
        readingTable.unit = sensorData.unit;

        if (mCustomTareWeightLayout.getVisibility() == View.VISIBLE) {
            tare_weight = mTareWeightEditText.getText().toString();
            readingTable.tare_weight = tare_weight;
        } else {
            readingTable.tare_weight = tare_weight;
        }

        if (backdated) {
            readingTable.timestamp = old_date;
            readingTable.date = timeOld.getText().toString();
            readingTable.date_of_reading = sdf.format(old_date);
        } else {
            readingTable.timestamp = calendar.getTimeInMillis();
            readingTable.date = time;
            readingTable.date_of_reading = sdf.format(calendar.getTimeInMillis());
        }

        readingTable.unit_id = GlobalData.getInstance().getSelectedUnitId();
        readingTable.save();

        updateList(readingTable);
    }

    private void updateList(ReadingTable readingTable) {
        if (readingListAdapter != null) {
            int pos = readingListAdapter.addElement(readingTable);
            readingList.scrollToPosition(pos);
//            if(backdated){
//                showEditDialog(readingTable,pos);
//            }

        } else {
            List<ReadingTable> list = ReadingTable.getReadings(utility_id, sensor_name);
            setDataInList(list);
//            if(backdated){
//                showEditDialog(readingTable,0);
//            }
        }
    }

    public void customImagePicker() {

        final Dialog dialog = new Dialog(InputReadingFormActivity.this);
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
        imageTaken = true;
        setPic(uploadImage, mCurrentPhotoPath);
    }

    private void setPic(ImageView view, String imageUrl) {
        File image = new File(imageUrl);

        if (image.exists()) {
            Picasso.with(InputReadingFormActivity.this).load(image)
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
        mTareWeightEditText.setCursorVisible(false);
        CommonMethod.hideKeyboard(InputReadingFormActivity.this);
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
                ReadingTable readingTable = readingListAdapter.getReadingsList().get(position);

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

    private void deleteItemFromRecyclerView(int position, ReadingTable readingTable) {
        DeleteReadingDialog deleteReadingDialog = DeleteReadingDialog.newInstance("Alert", "Are you sure you want to delete this reading?", "No", "Yes", position, readingTable);
        deleteReadingDialog.setCancelable(false);
        deleteReadingDialog.show(getFragmentManager(), "deleteReadingDialog");
    }

    private void showEditDialog(ReadingTable readingTable, int position) {
        EditDialog newDialog = EditDialog.newInstance(readingTable, position, true);
        newDialog.show(getSupportFragmentManager(), "show_dialog");
    }

}
