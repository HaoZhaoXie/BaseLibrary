package com.lzy.imagepicker.adapter;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.util.Utils;

import java.util.ArrayList;

import androidx.viewpager.widget.PagerAdapter;


public class ImagePageAdapter extends PagerAdapter {

    private int screenWidth;
    private int screenHeight;
    private ImagePicker imagePicker;
    private ArrayList<ImageItem> images = new ArrayList<>();
    private Activity mActivity;
    public PhotoViewClickListener listener;
    public PhotoViewLongClickListener longListener;

    public ImagePageAdapter(Activity activity, ArrayList<ImageItem> images) {
        this.mActivity = activity;
        this.images = images;

        DisplayMetrics dm = Utils.getScreenPix(activity);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        imagePicker = ImagePicker.getInstance();
    }

    public void setData(ArrayList<ImageItem> images) {
        this.images = images;
    }

    public void setPhotoViewClickListener(PhotoViewClickListener listener) {
        this.listener = listener;
    }

    public void setPhotoViewLongClickListener(PhotoViewLongClickListener listener) {
        this.longListener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(mActivity);
        ImageItem imageItem = images.get(position);
        if (imageItem != null) {
            imagePicker.getImageLoader().displayImagePreview(mActivity, imageItem.path, photoView, screenWidth, screenHeight);
            photoView.setOnPhotoTapListener((view, x, y) -> {
                if (listener != null) listener.OnPhotoTapListener(view, x, y);
            });

            photoView.setOnLongClickListener(v -> {
                if (longListener != null) {
                    longListener.OnPhotoLongListener(v, imageItem);
                }
                return false;
            });
            container.addView(photoView);
        }
        return photoView;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public interface PhotoViewClickListener {
        void OnPhotoTapListener(View view, float v, float v1);
    }

    public interface PhotoViewLongClickListener {
        void OnPhotoLongListener(View view, ImageItem imageItem);
    }
}
