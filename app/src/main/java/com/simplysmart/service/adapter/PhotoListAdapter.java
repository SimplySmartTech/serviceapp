package com.simplysmart.service.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.simplysmart.service.R;
import com.simplysmart.service.activity.ImageViewActivity;
import com.simplysmart.service.config.StringConstants;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by shailendrapsp on 30/12/16.
 */

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.PhotoViewHolder> {
    private Context mContext;
    private ArrayList<String> imageUrls;

    public PhotoListAdapter(Context mContext, ArrayList<String> imageUrls) {
        this.mContext = mContext;
        this.imageUrls = imageUrls;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoViewHolder(LayoutInflater.from(mContext).inflate(R.layout.photo_view_holder_small,parent,false));
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        final String imageUrl = imageUrls.get(position);
        File image = new File(imageUrl);
        setPic(holder.view_pic,image);

        holder.view_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImageViewActivity.class);
                intent.putExtra(StringConstants.PHOTO_PATH, imageUrl);
                intent.putExtra(StringConstants.ALLOW_NEW_IMAGE, false);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    private void setPic(ImageView view, File image) {
        view.setVisibility(View.VISIBLE);
        Picasso.with(mContext).load(image)
                .placeholder(R.drawable.photo_default)
                .noFade()
                .fit().centerCrop()
//                .resize(48, 48)
                .error(R.drawable.photo_default).into(view);

    }

    class PhotoViewHolder extends RecyclerView.ViewHolder{
        private ImageView view_pic;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            view_pic = (ImageView)itemView.findViewById(R.id.view_pic);
        }
    }
}
