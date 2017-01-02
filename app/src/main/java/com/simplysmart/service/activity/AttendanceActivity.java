package com.simplysmart.service.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.simplysmart.service.R;
import com.simplysmart.service.adapter.AttendanceListAdapter;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.database.AttendanceTable;
import com.simplysmart.service.permission.MarshmallowPermission;
import com.simplysmart.service.service.AttendanceUploadService;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by shailendrapsp on 26/12/16.
 */

public class AttendanceActivity extends BaseActivity {
    private final int REQUEST_TAKE_PHOTO = 1;
    private final int REQUEST_GALLERY_PHOTO = 2;
    private String mCurrentPhotoPath;
    private File image;

    private ImageView take_pic;
    private ImageView view_pic;
    private TextView submit;
    private RelativeLayout mParentLayout;
    private LinearLayout takePhotoLayout;
    private TextView takePhotoText;
    private RecyclerView recyclerView;
    private CardView cardView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle(getString(R.string.attendance));

        bindViews();
        initializeViews();
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
    protected int getStatusBarColor() {
        return 0;
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
        } else if (requestCode == StringConstants.IMAGE_CHANGED && resultCode == StringConstants.IMAGE_CHANGED) {
            File oldImage = new File(mCurrentPhotoPath);
            String newPhotoPath = "";
            if (data != null && data.getExtras() != null) {
                newPhotoPath = data.getStringExtra(StringConstants.PHOTO_PATH);
            }

            if (!newPhotoPath.equals("") && oldImage.exists()) {
                oldImage.delete();
                mCurrentPhotoPath = newPhotoPath;
                saveAttendanceToDisk();
            }
        } else if (requestCode == StringConstants.IMAGE_CHANGED && resultCode == StringConstants.IMAGE_NOT_CHANGED) {
            saveAttendanceToDisk();
        }
    }

    private void checkForPermissions() {
        checkExternalStorage();
    }

    private void checkCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            // Permission is already available, start Internet preview
            dispatchTakePictureIntent();
        } else {
            new MarshmallowPermission(this, mParentLayout).checkPermissionForCamera();
        }
    }

    private void checkExternalStorage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            // Permission is already available, start Internet preview
            checkCamera();
        } else {
            new MarshmallowPermission(this, mParentLayout).checkPermissionForExternalStorage();
        }
    }

    public void customImagePicker() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_capture);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout lLayoutCameraDialog = (LinearLayout) dialog.findViewById(R.id.lLayoutCameraDialog);
        LinearLayout lLayoutGalleryDialog = (LinearLayout) dialog.findViewById(R.id.lLayoutGalleryDialog);
        LinearLayout lLayoutRemoveDialog = (LinearLayout) dialog.findViewById(R.id.lLayoutRemoveDialog);

        lLayoutGalleryDialog.setVisibility(View.GONE);

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
        Intent intent = new Intent(AttendanceActivity.this, ImageViewActivity.class);
        intent.putExtra(StringConstants.PHOTO_PATH, mCurrentPhotoPath);
        intent.putExtra(StringConstants.ALLOW_NEW_IMAGE, true);
        intent.putExtra(StringConstants.DISABLE_GALLERY, true);
        startActivityForResult(intent, StringConstants.IMAGE_CHANGED);
    }

    private void setPic(ImageView view, String imageUrl) {
        File image = new File(imageUrl);

        if (image.exists()) {
            Picasso.with(this).load(image)
                    .placeholder(R.drawable.ic_photo_black_48dp)
                    .noFade()
                    .fit().centerCrop()
//                    .resize(48, 48)
                    .error(R.drawable.ic_menu_slideshow).into(view);
            view.setAlpha(1.0f);
            view.setVisibility(View.VISIBLE);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        submit.setVisibility(View.VISIBLE);
    }

    private void bindViews() {
        mParentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        take_pic = (ImageView) findViewById(R.id.take_pic);
        view_pic = (ImageView) findViewById(R.id.view_pic);
        submit = (TextView) findViewById(R.id.submit);
        takePhotoText = (TextView) findViewById(R.id.take_pic_text);
        recyclerView = (RecyclerView) findViewById(R.id.attendanceList);
        cardView = (CardView) findViewById(R.id.cardView);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void initializeViews() {
        setDataInRecyclerView();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customImagePicker();
            }
        });
    }

    private void setDataInRecyclerView() {
        List<AttendanceTable> attendanceList = AttendanceTable.getAllAttendances();
        if(attendanceList!=null && attendanceList.size()>0) {
            Collections.sort(attendanceList, new Comparator<AttendanceTable>() {
                @Override
                public int compare(AttendanceTable lhs, AttendanceTable rhs) {
                    return (int) (rhs.timestamp - lhs.timestamp);
                }
            });

            if(attendanceList.size()>7){
                for(int i =7;i<attendanceList.size();i++){
                    if(attendanceList.get(i).synched){
                        attendanceList.get(i).delete();
                    }
                    attendanceList.remove(i);
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            for (AttendanceTable attendanceTable : attendanceList) {
                String date = sdf.format(attendanceTable.timestamp);
                String currentDate = sdf.format(Calendar.getInstance().getTimeInMillis());

                if (date.equalsIgnoreCase(currentDate)) {
                    fab.setVisibility(View.GONE);
                    break;
                }
            }

            AttendanceListAdapter attendanceListAdapter = new AttendanceListAdapter(this, attendanceList);
            RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(attendanceListAdapter);
        }
    }

    private void saveAttendanceToDisk() {
        AttendanceTable attendanceTable = new AttendanceTable();
        attendanceTable.local_photo_url = mCurrentPhotoPath;
        attendanceTable.timestamp = Calendar.getInstance().getTimeInMillis();
        attendanceTable.save();

        if (NetworkUtilities.isInternet(this)) {
            Intent i = new Intent(this, AttendanceUploadService.class);
            startService(i);
        }

        setDataInRecyclerView();
    }


}