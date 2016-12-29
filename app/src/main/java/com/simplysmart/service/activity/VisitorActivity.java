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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.simplysmart.service.R;
import com.simplysmart.service.adapter.VisitorListAdapter;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.NetworkUtilities;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.database.VisitorTable;
import com.simplysmart.service.permission.MarshmallowPermission;
import com.simplysmart.service.service.VisitorInfoUploadService;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by shailendrapsp on 27/12/16.
 */

public class VisitorActivity extends BaseActivity {
    private final int REQUEST_TAKE_PHOTO = 1;
    private final int REQUEST_GALLERY_PHOTO = 2;
    private String mCurrentPhotoPath;
    private File image;

    private EditText number_of_visitor;
    private EditText details;
    private LinearLayout take_pic_layout;
    private RecyclerView recyclerView;
    private RelativeLayout parentLayout;
    private Button submit;

    private String local_image_urls = "";
    private ArrayList<String> imageUrls;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle(getString(R.string.visitor));

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
                    showSnackBar(parentLayout, "Getting error in image file.", false);
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
                    showSnackBar(parentLayout, "Getting error in image file.", false);
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
                imageUrls.add(mCurrentPhotoPath);
                local_image_urls += mCurrentPhotoPath + ",";
                addPicToGrid();
            }
        } else if (requestCode == StringConstants.IMAGE_CHANGED && resultCode == StringConstants.IMAGE_NOT_CHANGED) {
            imageUrls.add(mCurrentPhotoPath);
            local_image_urls += mCurrentPhotoPath + ",";
            addPicToGrid();
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
            new MarshmallowPermission(this, parentLayout).checkPermissionForCamera();
        }
    }

    private void checkExternalStorage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            // Permission is already available, start Internet preview
            checkCamera();
        } else {
            new MarshmallowPermission(this, parentLayout).checkPermissionForExternalStorage();
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
                showSnackBar(parentLayout, "Unable to compress image file.");
            }
        }

        mCurrentPhotoPath = compressedPath;
        Intent intent = new Intent(VisitorActivity.this, ImageViewActivity.class);
        intent.putExtra(StringConstants.PHOTO_PATH, mCurrentPhotoPath);
        intent.putExtra(StringConstants.ALLOW_NEW_IMAGE, true);
        startActivityForResult(intent, StringConstants.IMAGE_CHANGED);

    }

    private void bindViews() {
        number_of_visitor = (EditText) findViewById(R.id.noOfPersons);
        details = (EditText) findViewById(R.id.details);
        recyclerView = (RecyclerView) findViewById(R.id.photoList);
        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        submit = (Button) findViewById(R.id.submit);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void initializeViews() {
        imageUrls = new ArrayList<>();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customImagePicker();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInfo()) {
                    VisitorTable visitorTable = new VisitorTable();
                    visitorTable.num_of_person = Integer.parseInt(number_of_visitor.getText().toString());
                    visitorTable.details = details.getText().toString();
                    visitorTable.timestamp = Calendar.getInstance().getTimeInMillis();
                    visitorTable.local_image_urls = local_image_urls;
                    visitorTable.save();

                    if (NetworkUtilities.isInternet(VisitorActivity.this)) {
                        Intent i = new Intent(VisitorActivity.this, VisitorInfoUploadService.class);
                        startService(i);
                    }

                    finish();
                }
            }
        });
    }

    private boolean validateInfo() {
        if (number_of_visitor.getText().toString().equalsIgnoreCase("")) {
            showSnackBar(parentLayout, "Please enter number of people for visit.");
            return false;
        }

        if (details.getText().toString().equalsIgnoreCase("")) {
            showSnackBar(parentLayout, "Please enter details of people for visit.");
            return false;
        }

        return true;
    }

    private void addPicToGrid() {
        if (imageUrls != null && imageUrls.size() > 0) {
            VisitorListAdapter visitorListAdapter = new VisitorListAdapter(this, imageUrls);
            GridLayoutManager glm = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(glm);
            recyclerView.setAdapter(visitorListAdapter);
        }
    }
}
