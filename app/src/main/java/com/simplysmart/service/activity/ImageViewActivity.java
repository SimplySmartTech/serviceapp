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
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.simplysmart.service.R;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.custom_views.TouchImageView;
import com.simplysmart.service.permission.MarshmallowPermission;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by shailendrapsp on 30/11/16.
 */

public class ImageViewActivity extends BaseActivity {

    private final int REQUEST_TAKE_PHOTO = 1;
    private final int REQUEST_GALLERY_PHOTO = 2;
    private RelativeLayout parentLayout;
    private TouchImageView touchImageView;
    private String mCurrentPhotoPath;
    private String mPreviousPhotoPath;
    private File image;
    private boolean takeNewImage = false;
    private Button newPhoto, done;
    private LinearLayout anotherPhotoLayout;
    private TextView errorLayout;
    private boolean disableGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_view);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mCurrentPhotoPath = "";
        if (getIntent() != null && getIntent().getExtras() != null) {
            mPreviousPhotoPath = getIntent().getStringExtra(StringConstants.PHOTO_PATH);
            takeNewImage = getIntent().getBooleanExtra(StringConstants.ALLOW_NEW_IMAGE, false);
            disableGallery = getIntent().getBooleanExtra(StringConstants.DISABLE_GALLERY,false);
        }

        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        touchImageView = (TouchImageView) findViewById(R.id.viewImage);
        errorLayout = (TextView) findViewById(R.id.errorLayout);

        File image = null;

        try {
            if (!mPreviousPhotoPath.equals("")) {
                image = new File(mPreviousPhotoPath);
                if (image.exists()) {
                    setPic(touchImageView, image.getAbsolutePath());
                } else {
                    errorLayout.setVisibility(View.VISIBLE);
                }
            } else {
                errorLayout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLayout.setVisibility(View.VISIBLE);
        }

        addFunctionForButton();
    }

    private void addFunctionForButton() {
        newPhoto = (Button) findViewById(R.id.newPhoto);
        done = (Button) findViewById(R.id.done);
        anotherPhotoLayout = (LinearLayout) findViewById(R.id.afterPhotoLayout);

        if (takeNewImage) {
            anotherPhotoLayout.setVisibility(View.VISIBLE);
        } else {
            anotherPhotoLayout.setVisibility(View.GONE);
        }

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCurrentPhotoPath.equals("")) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(StringConstants.PHOTO_PATH, mCurrentPhotoPath);
                    setResult(StringConstants.IMAGE_CHANGED, returnIntent);
                    finish();
                } else {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(StringConstants.PHOTO_PATH, mCurrentPhotoPath);
                    setResult(StringConstants.IMAGE_NOT_CHANGED, returnIntent);
                    finish();
                }
            }
        });

        newPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                customImagePicker();
            }
        });
    }

    @Override
    protected int getStatusBarColor() {
        return 0;
    }

    private void setPic(ImageView view, String imageUrl) {
        File image = new File(imageUrl);
        if (image.exists()) {
            Picasso.with(ImageViewActivity.this).load(image)
                    .placeholder(R.drawable.ic_menu_slideshow)
                    .noFade()
                    .fit().centerInside()
                    .error(R.drawable.ic_menu_slideshow).into(view);
        }
    }

    public void customImagePicker() {
        final Dialog dialog = new Dialog(ImageViewActivity.this);
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

        if(disableGallery){
            lLayoutGalleryDialog.setVisibility(View.GONE);
        }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                if (mCurrentPhotoPath != null) {
                    Log.d("CameraPhoto", mCurrentPhotoPath);
                    compressAndDeleteFile(mCurrentPhotoPath, true);
                } else {
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayout.setText("Error in getting image file.");
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
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayout.setText("Error in getting image file.");
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
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
                } else {
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayout.setText("Error in getting image file.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorLayout.setVisibility(View.VISIBLE);
                errorLayout.setText("Error in getting image file.");
            }
        }

        mCurrentPhotoPath = compressedPath;
        if (!mCurrentPhotoPath.equals(""))
            setPic(touchImageView, mCurrentPhotoPath);
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
//                Snackbar.make(parentLayout, "Camera permission request was denied.",
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
//                Snackbar.make(parentLayout, "External Storage permission request was denied.",
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
            new MarshmallowPermission(ImageViewActivity.this, parentLayout).checkPermissionForCamera();
        }
    }

    private void checkExternalStorage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            // Permission is already available, start Internet preview
            checkCamera();
        } else {
            new MarshmallowPermission(ImageViewActivity.this, parentLayout).checkPermissionForExternalStorage();
        }
    }
}
