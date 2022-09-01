package com.simplysmart.service.permission;

import android.Manifest;
import android.app.Activity;
import android.view.View;
import android.view.accessibility.CaptioningManager;

import androidx.core.app.ActivityCompat;

public class MarshmallowPermission {

    Activity context;
    private View mLayout;
    private static final int PERMISSION_EXTERNAL_STORAGE = 1;
    private static final int PERMISSION_CAMERA = 2;

    public MarshmallowPermission(Activity context, View mLayout) {
        this.context = context;
        this.mLayout = mLayout;
    }

    public void checkPermissionForExternalStorage(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
//            Snackbar.make(mLayout, "External Storage Access is required.",
//                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    // Request the permission
//
//                }
//            }).show();

            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_EXTERNAL_STORAGE);

        } else {
//            Snackbar.make(mLayout,
//                    "Permission is not available. Requesting External Storage permission.",
//                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_EXTERNAL_STORAGE);
        }

    }

    public void checkPermissionForCamera(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                Manifest.permission.CAMERA)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
//            Snackbar.make(mLayout, "Camera Access is required.",
//                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    // Request the permission
//
//                }
//            }).show();

            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_CAMERA);

        } else {
//            Snackbar.make(mLayout,
//                    "Permission is not available. Requesting Camera access permission.",
//                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CAMERA},
                    PERMISSION_CAMERA);
        }
    }

}