package com.simplysmart.service.service;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.simplysmart.service.activity.InputFormActivity;
import com.simplysmart.service.aws.AWSConstants;
import com.simplysmart.service.aws.Util;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.database.MatrixDataRealm;
import com.simplysmart.service.database.ReadingDataRealm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Queue;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static android.content.ContentValues.TAG;

/**
 * Created by shailendrapsp on 7/11/16.
 */

public class PhotoUploadService extends Service {

    private TransferUtility transferUtility;
    private int count = 0;

    public PhotoUploadService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        Toast.makeText(getApplicationContext(), "Network Available : UploadingPhoto", Toast.LENGTH_LONG).show();
//        Log.d("TAG","Reached photo upload service.");
        transferUtility = Util.getTransferUtility(getApplicationContext());
        try {
            boolean useUnitId = intent.getBooleanExtra(StringConstants.USE_UNIT, false);
            if (useUnitId) {
                uploadImage(intent.getStringExtra(StringConstants.UNIT_ID));
            } else {
                uploadImage();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }


    private void uploadImage() {
        //TODO : Remove toast.
//        Toast.makeText(getApplicationContext(), "Network Available : uploadImage", Toast.LENGTH_LONG).show();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ReadingDataRealm> readingsList = realm
                .where(ReadingDataRealm.class)
                .equalTo("uploadedImage",false)
                .findAll();

        for (ReadingDataRealm reading : readingsList) {
            if (reading.getLocal_photo_url()!=null && !reading.getLocal_photo_url().equals("")) {
                beginUpload(reading);
            }
        }
    }

    private void uploadImage(String unit_id) {
        //TODO : Remove toast.
//        Toast.makeText(getApplicationContext(), "Network Available : uploadImage", Toast.LENGTH_LONG).show();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ReadingDataRealm> readingsList = realm
                .where(ReadingDataRealm.class)
                .equalTo("uploadedImage",false)
                .equalTo("unit_id",unit_id)
                .findAll();

        for (ReadingDataRealm reading : readingsList) {
            if (reading.getLocal_photo_url()!=null && !reading.getLocal_photo_url().equals("")) {
                beginUpload(reading);
            }
        }
    }

    private void beginUpload(ReadingDataRealm readingDataRealm) {
        String filePath = compressImage(readingDataRealm.getLocal_photo_url());
        if (filePath == null) {
//            Toast.makeText(this, "Could not find the filepath of the selected file", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            File file = new File(filePath);
            TransferObserver observer = transferUtility.upload(
                    AWSConstants.BUCKET_NAME,
                    AWSConstants.PATH_FOLDER + file.getName(),
                    file, CannedAccessControlList.PublicRead);

            observer.setTransferListener(new UploadListener(readingDataRealm.getTimestamp(), file.getName()));
            count++;
//            Toast.makeText(getApplicationContext(), "Network Available : Set for uploading"+filePath, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;

        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    private void sendUploadCompleteBroadcast() {
        Intent i = new Intent("uploadComplete");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
    }


    class UploadListener implements TransferListener {

        private String fileName;
        private long timestamp;

        UploadListener(long timestamp, String fileName) {
            this.fileName = fileName;
            this.timestamp = timestamp;
        }

        @Override
        public void onError(int id, Exception e) {
            Log.e(TAG, "Error during upload: " + id, e);
            count--;
            if(count==0){
                sendUploadCompleteBroadcast();
            }
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
                        + fileName;

                DebugLog.d("URL :::: " + url);

                Realm realm = Realm.getDefaultInstance();
                ReadingDataRealm reading = realm.where(ReadingDataRealm.class).equalTo("timestamp",timestamp).findFirst();

                realm.beginTransaction();
                reading.setUploadedImage(true);
                reading.setPhotographic_evidence_url(url);
                realm.commitTransaction();

                count --;
                Log.d("COUNT : ", count+"");
                if(count==0){
                    sendUploadCompleteBroadcast();
                }

            }
        }
    }
}

