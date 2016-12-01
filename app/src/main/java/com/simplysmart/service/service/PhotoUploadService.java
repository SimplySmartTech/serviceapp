package com.simplysmart.service.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.simplysmart.service.aws.AWSConstants;
import com.simplysmart.service.aws.Util;
import com.simplysmart.service.common.DebugLog;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.database.ReadingDataRealm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import io.realm.Realm;
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
            if (intent != null && intent.getExtras() != null) {
                boolean useUnitId = intent.getBooleanExtra(StringConstants.USE_UNIT, false);
                if (useUnitId) {
                    uploadImage(intent.getStringExtra(StringConstants.UNIT_ID));
                } else {
                    uploadImage();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void sendUploadCompleteBroadcast() {
        Intent i = new Intent("uploadComplete");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
    }

    private void uploadImage() {
        //TODO : Remove toast.
//        Toast.makeText(getApplicationContext(), "Network Available : uploadImage", Toast.LENGTH_LONG).show();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ReadingDataRealm> readingsList = realm
                .where(ReadingDataRealm.class)
                .equalTo("uploadedImage", false)
                .findAll();

        for (ReadingDataRealm reading : readingsList) {
            if (reading.getLocal_photo_url() != null && !reading.getLocal_photo_url().equals("")) {
                DebugLog.d("Got photo URL : " + reading.getLocal_photo_url());
                beginUpload(reading);
            } else {
                DebugLog.d("No photo URL");
            }
        }
    }

    private void uploadImage(String unit_id) {
        //TODO : Remove toast.
//        Toast.makeText(getApplicationContext(), "Network Available : uploadImage", Toast.LENGTH_LONG).show();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ReadingDataRealm> readingsList = realm
                .where(ReadingDataRealm.class)
                .equalTo("uploadedImage", false)
                .equalTo("unit_id", unit_id)
                .findAll();

        for (ReadingDataRealm reading : readingsList) {
            if (reading.getLocal_photo_url() != null && !reading.getLocal_photo_url().equals("")) {
                beginUpload(reading);
            } else {
                sendUploadCompleteBroadcast();
            }
        }
    }

    private void beginUpload(ReadingDataRealm readingDataRealm) {
        Log.d("Local photo url:", readingDataRealm.getLocal_photo_url());
        String filePath = readingDataRealm.getLocal_photo_url();
        try {
            try {
                File file = new File(filePath);
                TransferObserver observer = transferUtility.upload(
                        AWSConstants.BUCKET_NAME,
                        AWSConstants.PATH_FOLDER + file.getName(),
                        file, CannedAccessControlList.PublicRead);

                observer.setTransferListener(new UploadListener(readingDataRealm.getTimestamp(), file.getName(), readingDataRealm));
                count++;
//            Toast.makeText(getApplicationContext(), "Network Available : Set for uploading"+filePath, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            count--;
            if (count == 0) {
                sendUploadCompleteBroadcast();
            }
        }
    }

    public void onUploadComplete(ReadingDataRealm readingDataRealm, String aws_url) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        readingDataRealm.setPhotographic_evidence_url(aws_url);
        readingDataRealm.setUploadedImage(true);
        realm.commitTransaction();
        DebugLog.d("URL:" + readingDataRealm.getPhotographic_evidence_url());
    }

    class UploadListener implements TransferListener {

        private String fileName;
        private long timestamp;
        private ReadingDataRealm rdr;

        UploadListener(long timestamp, String fileName, ReadingDataRealm rdr) {
            this.fileName = fileName;
            this.timestamp = timestamp;
            this.rdr = rdr;
        }

        @Override
        public void onError(int id, Exception e) {
            Log.e(TAG, "Error during upload: " + id, e);
            count--;
            if (count == 0) {
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

                count--;
                Log.d("COUNT : ", count + "");
                if (count == 0) {
                    sendUploadCompleteBroadcast();
                }

                onUploadComplete(rdr, url);
            }
        }


    }

//    @SuppressLint("NewApi")
//    protected String getPath(Uri uri) throws URISyntaxException {
//        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
//        String selection = null;
//        String[] selectionArgs = null;
//        // Uri is different in versions after KITKAT (Android 4.4), we need to
//        // deal with different Uris.
//        if (needToCheckUri && DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {
//            if (isExternalStorageDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                return Environment.getExternalStorageDirectory() + "/" + split[1];
//            } else if (isDownloadsDocument(uri)) {
//                final String id = DocumentsContract.getDocumentId(uri);
//                uri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
//            } else if (isMediaDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//                if ("image".equals(type)) {
//                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                } else if ("video".equals(type)) {
//                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                } else if ("audio".equals(type)) {
//                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                }
//                selection = "_id=?";
//                selectionArgs = new String[]{
//                        split[1]
//                };
//            }
//        }
//        if ("content".equalsIgnoreCase(uri.getScheme())) {
//            String[] projection = {
//                    MediaStore.Images.Media.DATA
//            };
//            Cursor cursor = null;
//            try {
//                cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                if (cursor.moveToFirst()) {
//                    return cursor.getString(column_index);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            return uri.getPath();
//        }
//        return null;
//    }
//
//    public static boolean isExternalStorageDocument(Uri uri) {
//        return "com.android.externalstorage.documents".equals(uri.getAuthority());
//    }
//
//    public static boolean isDownloadsDocument(Uri uri) {
//        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
//    }
//
//    public static boolean isMediaDocument(Uri uri) {
//        return "com.android.providers.media.documents".equals(uri.getAuthority());
//    }
//
//    public String compressImage(String imageUri) {
//
//        String filePath = getRealPathFromURI(imageUri);
//        Bitmap scaledBitmap = null;
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//
//        options.inJustDecodeBounds = true;
//        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
//
//        int actualHeight = options.outHeight;
//        int actualWidth = options.outWidth;
//
//        float maxHeight = 816.0f;
//        float maxWidth = 612.0f;
//        float imgRatio = actualWidth / actualHeight;
//        float maxRatio = maxWidth / maxHeight;
//
//        if (actualHeight > maxHeight || actualWidth > maxWidth) {
//            if (imgRatio < maxRatio) {
//                imgRatio = maxHeight / actualHeight;
//                actualWidth = (int) (imgRatio * actualWidth);
//                actualHeight = (int) maxHeight;
//            } else if (imgRatio > maxRatio) {
//                imgRatio = maxWidth / actualWidth;
//                actualHeight = (int) (imgRatio * actualHeight);
//                actualWidth = (int) maxWidth;
//            } else {
//                actualHeight = (int) maxHeight;
//                actualWidth = (int) maxWidth;
//            }
//        }
//
//        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
//        options.inJustDecodeBounds = false;
//
//        options.inPurgeable = true;
//        options.inInputShareable = true;
//        options.inTempStorage = new byte[16 * 1024];
//
//        try {
//            bmp = BitmapFactory.decodeFile(filePath, options);
//        } catch (OutOfMemoryError exception) {
//            exception.printStackTrace();
//        }
//        try {
//            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
//        } catch (OutOfMemoryError exception) {
//            exception.printStackTrace();
//        }
//
//        float ratioX = actualWidth / (float) options.outWidth;
//        float ratioY = actualHeight / (float) options.outHeight;
//        float middleX = actualWidth / 2.0f;
//        float middleY = actualHeight / 2.0f;
//
//        Matrix scaleMatrix = new Matrix();
//        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
//
//        Canvas canvas = new Canvas(scaledBitmap);
//        canvas.setMatrix(scaleMatrix);
//        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
//
////      check the rotation of the image and display it properly
//        ExifInterface exif;
//        try {
//            exif = new ExifInterface(filePath);
//
//            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
//            Log.d("EXIF", "Exif: " + orientation);
//            Matrix matrix = new Matrix();
//            if (orientation == 6) {
//                matrix.postRotate(90);
//                Log.d("EXIF", "Exif: " + orientation);
//            } else if (orientation == 3) {
//                matrix.postRotate(180);
//                Log.d("EXIF", "Exif: " + orientation);
//            } else if (orientation == 8) {
//                matrix.postRotate(270);
//                Log.d("EXIF", "Exif: " + orientation);
//            }
//            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        FileOutputStream out = null;
//        String filename = getFilename();
//        try {
//            out = new FileOutputStream(filename);
//            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        return filename;
//    }
//
//    public String getFilename() {
//        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
//        return uriSting;
//    }
//
//    private String getRealPathFromURI(String contentURI) {
//        Uri contentUri = Uri.parse(contentURI);
//        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
//        if (cursor == null) {
//            return contentUri.getPath();
//        } else {
//            cursor.moveToFirst();
//            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//            return cursor.getString(index);
//        }
//    }
//
//    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//            final int heightRatio = Math.round((float) height / (float) reqHeight);
//            final int widthRatio = Math.round((float) width / (float) reqWidth);
//            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
//        }
//        final float totalPixels = width * height;
//        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
//        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
//            inSampleSize++;
//        }
//        return inSampleSize;
//    }

}
