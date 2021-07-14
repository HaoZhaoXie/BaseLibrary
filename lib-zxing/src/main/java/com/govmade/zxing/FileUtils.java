package com.govmade.zxing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class FileUtils {
    public static String saveBitmapToFile(Bitmap bitmap, File toFile) {
        try {
            FileOutputStream fos = new FileOutputStream(toFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();
            return toFile.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String saveBitmapToPublicPictures(Bitmap bitmap, String fileName) {
        File saveFileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!saveFileDirectory.exists()) {
            saveFileDirectory.mkdirs();
        }

        File saveFile = new File(saveFileDirectory, fileName);
        return saveBitmapToFile(bitmap, saveFile);
    }

    public static String saveBitmapToCache(Context context, Bitmap bitmap, String fileName) {
        File saveFileDirectory = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
            saveFileDirectory = context.getExternalCacheDir();
        }
        if (saveFileDirectory == null) {
            saveFileDirectory = context.getCacheDir();
        }
        if (!saveFileDirectory.exists()) {
            saveFileDirectory.mkdirs();
        }

        File saveFile = new File(saveFileDirectory, fileName);
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    saveFile.getAbsolutePath(), fileName, null);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(saveFile.getPath()))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return saveBitmapToFile(bitmap, saveFile);
    }
}
