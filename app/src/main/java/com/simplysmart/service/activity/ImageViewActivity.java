package com.simplysmart.service.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.simplysmart.service.R;
import com.simplysmart.service.config.StringConstants;
import com.simplysmart.service.custom_views.TouchImageView;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by shailendrapsp on 30/11/16.
 */

public class ImageViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("View Image");

        String photoPath = "";
        if (getIntent() != null && getIntent().getExtras() != null) {
            photoPath = getIntent().getStringExtra(StringConstants.PHOTO_PATH);
        }

        RelativeLayout parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        TouchImageView touchImageView = (TouchImageView) findViewById(R.id.viewImage);
        File image = null;
        Bitmap bitmap = null;

        try {
            if (!photoPath.equals("")) {
                image = new File(photoPath);
                if (image.exists()) {
//                    bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
//                    touchImageView.setImageBitmap(bitmap);
                    setPic(touchImageView,image.getAbsolutePath());

                } else {
                    showSnackBar(parentLayout, "Cannot find image file.");
                }
            } else {
                showSnackBar(parentLayout, "Cannot find image file.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSnackBar(parentLayout, "Some error occured. Cannot open image file.");
        }
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
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
                    .error(R.drawable.ic_menu_slideshow).into(view);

            view.setVisibility(View.VISIBLE);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }
}
