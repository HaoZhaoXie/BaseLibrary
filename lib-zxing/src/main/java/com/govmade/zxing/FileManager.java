package com.govmade.zxing;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;

public class FileManager {
    private Context context;

    public FileManager(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * 保存图片至公共下载下载中
     *
     * @param bitmap
     * @return
     */
    public SingleSourceLiveData<Resource<String>> saveBitmapToPictures(final Bitmap bitmap, final String fileName) {
        final SingleSourceLiveData<Resource<String>> result = new SingleSourceLiveData<>();
        result.postValue(Resource.loading(""));
        ThreadManager.getInstance().runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                String path = FileUtils.saveBitmapToPublicPictures(bitmap, fileName);
                if (TextUtils.isEmpty(path)) {
                    result.postValue(Resource.error(-1, ""));
                } else {
                    result.postValue(Resource.success(path));
                }
            }
        });
        return result;
    }

    /**
     * 保存图片至缓存文件中
     *
     * @param bitmap
     * @return
     */
    public SingleSourceLiveData<Resource<String>> saveBitmapToCache(final Bitmap bitmap, final String fileName) {
        final SingleSourceLiveData<Resource<String>> result = new SingleSourceLiveData<>();
        result.postValue(Resource.loading(""));
        ThreadManager.getInstance().runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                String path = FileUtils.saveBitmapToCache(context, bitmap, fileName);
                result.postValue(Resource.success(path));
            }
        });
        return result;
    }

    /**
     * 保存图片至公共下载下载中,使用时间作为文件名
     *
     * @param bitmap
     * @return
     */
    public SingleSourceLiveData<Resource<String>> saveBitmapToPictures(Bitmap bitmap) {
        String fileName = System.currentTimeMillis() + ".png";
        return saveBitmapToPictures(bitmap, fileName);
    }

    /**
     * 保存图片至缓存文件中,使用时间作为文件名
     *
     * @param bitmap
     * @return
     */
    public SingleSourceLiveData<Resource<String>> saveBitmapToCache(Bitmap bitmap) {
        String fileName = System.currentTimeMillis() + ".png";
        return saveBitmapToCache(bitmap, fileName);
    }

    /**
     * 获取本地文件真实 uri
     *
     * @param contentUri
     * @return
     */
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public String getSavePath() {
        File saveFileDirectory = context.getExternalCacheDir();
        if (saveFileDirectory == null) {
            saveFileDirectory = context.getCacheDir();
        }
        if (!saveFileDirectory.exists()) {
            saveFileDirectory.mkdirs();
        }

        return saveFileDirectory.getAbsolutePath();
    }
}
