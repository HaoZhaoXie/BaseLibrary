package com.lzy.imagepicker;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.isseiaoki.simplecropview.FreeCropImageView;
import com.lzy.imagepicker.bean.ImageFolder;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.loader.ImageLoader;
import com.lzy.imagepicker.util.InnerToaster;
import com.lzy.imagepicker.util.ProviderUtil;
import com.lzy.imagepicker.util.Utils;
import com.lzy.imagepicker.view.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ImagePicker {

    public static final String TAG = ImagePicker.class.getSimpleName();
    public static final int REQUEST_CODE_TAKE = 1001;
    public static final int REQUEST_CODE_CROP = 1002;
    public static final int REQUEST_CODE_PREVIEW = 1003;
    public static final int RESULT_CODE_ITEMS = 1004;
    public static final int RESULT_CODE_BACK = 1005;

    public static final String EXTRA_RESULT_ITEMS = "extra_result_items";
    public static final String EXTRA_SELECTED_IMAGE_POSITION = "selected_image_position";
    public static final String EXTRA_IMAGE_ITEMS = "extra_image_items";
    public static final String EXTRA_FROM_ITEMS = "extra_from_items";
    public static final String EXTRA_FROM_LONG_CLICK_ITEMS = "extra_from_long_click_items";
    public static final String EXTRA_CAN_EDIT = "extra_can_edit";
    public static Uri mCameraUri;

    private boolean multiMode = true;
    private int selectLimit = 9;
    private boolean crop = true;
    private boolean showCamera = true;
    private boolean isSaveRectangle = false;
    private int outPutX = 800;
    private int outPutY = 800;
    private int focusWidth = 280;
    private int focusHeight = 280;
    private ImageLoader imageLoader;
    private CropImageView.Style style = CropImageView.Style.RECTANGLE;
    private File cropCacheFolder;
    private File takeImageFile;

    public FreeCropImageView.CropMode mFreeCropMode = com.isseiaoki.simplecropview.FreeCropImageView.CropMode.FREE;
    public boolean isFreeCrop = false;
    private ArrayList<ImageItem> mSelectedImages = new ArrayList<>();
    private List<ImageFolder> mImageFolders;
    private int mCurrentImageFolderPosition = 0;
    private List<OnImageSelectedListener> mImageSelectedListeners;

    private static ImagePicker mInstance;


    private ImagePicker() {
    }

    public static ImagePicker getInstance() {
        if (mInstance == null) {
            synchronized (ImagePicker.class) {
                if (mInstance == null) {
                    mInstance = new ImagePicker();
                }
            }
        }
        return mInstance;
    }

    public boolean isMultiMode() {
        return multiMode;
    }

    public void setMultiMode(boolean multiMode) {
        this.multiMode = multiMode;
    }

    public int getSelectLimit() {
        return selectLimit;
    }

    public void setSelectLimit(int selectLimit) {
        this.selectLimit = selectLimit;
    }

    public boolean isCrop() {
        return crop;
    }

    public void setCrop(boolean crop) {
        this.crop = crop;
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public boolean isSaveRectangle() {
        return isSaveRectangle;
    }

    public void setSaveRectangle(boolean isSaveRectangle) {
        this.isSaveRectangle = isSaveRectangle;
    }

    public int getOutPutX() {
        return outPutX;
    }

    public void setOutPutX(int outPutX) {
        this.outPutX = outPutX;
    }

    public int getOutPutY() {
        return outPutY;
    }

    public void setOutPutY(int outPutY) {
        this.outPutY = outPutY;
    }

    public int getFocusWidth() {
        return focusWidth;
    }

    public void setFocusWidth(int focusWidth) {
        this.focusWidth = focusWidth;
    }

    public int getFocusHeight() {
        return focusHeight;
    }

    public void setFocusHeight(int focusHeight) {
        this.focusHeight = focusHeight;
    }

    public File getTakeImageFile() {
        return takeImageFile;
    }

    public File getCropCacheFolder(Context context) {
        if (cropCacheFolder == null) {
            cropCacheFolder = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/cropTemp/");
        }
        if (!cropCacheFolder.exists() || !cropCacheFolder.isDirectory()) cropCacheFolder.mkdirs();
        return cropCacheFolder;
    }

    public void setCropCacheFolder(File cropCacheFolder) {
        this.cropCacheFolder = cropCacheFolder;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public CropImageView.Style getStyle() {
        return style;
    }

    public void setStyle(CropImageView.Style style) {
        this.style = style;
    }

    public List<ImageFolder> getImageFolders() {
        return mImageFolders;
    }

    public void setImageFolders(List<ImageFolder> imageFolders) {
        mImageFolders = imageFolders;
    }

    public int getCurrentImageFolderPosition() {
        return mCurrentImageFolderPosition;
    }

    public void setCurrentImageFolderPosition(int mCurrentSelectedImageSetPosition) {
        mCurrentImageFolderPosition = mCurrentSelectedImageSetPosition;
    }

    public ArrayList<ImageItem> getCurrentImageFolderItems() {
        return mImageFolders.get(mCurrentImageFolderPosition).images;
    }

    public boolean isSelect(ImageItem item) {
        return mSelectedImages.contains(item);
    }

    public int getSelectImageCount() {
        if (mSelectedImages == null) {
            return 0;
        }
        return mSelectedImages.size();
    }

    public ArrayList<ImageItem> getSelectedImages() {
        return mSelectedImages;
    }

    public void clearSelectedImages() {
        if (mSelectedImages != null) mSelectedImages.clear();
    }

    public void clear() {
        if (mImageSelectedListeners != null) {
            mImageSelectedListeners.clear();
            mImageSelectedListeners = null;
        }
        if (mImageFolders != null) {
            mImageFolders.clear();
            mImageFolders = null;
        }
        if (mSelectedImages != null) {
            mSelectedImages.clear();
        }
        mCurrentImageFolderPosition = 0;
    }


    public void takePicture(Activity activity, int requestCode) {
        PackageManager packageManager = activity.getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            InnerToaster.obj(activity).show(R.string.ip_str_no_camera);
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            if (Utils.existSDCard()) {
                if (VERSION.SDK_INT < VERSION_CODES.Q) {
                    takeImageFile = new File(Environment.getExternalStorageDirectory(), "/DCIM/camera/");
                } else {
                    takeImageFile = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                }
            } else {
                takeImageFile = Environment.getDataDirectory();
            }

            takeImageFile = createFile(takeImageFile, "IMG_", ".jpg");
            if (takeImageFile != null) {
                Uri uri = null;
                if (VERSION.SDK_INT <= VERSION_CODES.M) {
                    uri = Uri.fromFile(takeImageFile);
                } else if (VERSION.SDK_INT < VERSION_CODES.Q) {
                    uri = FileProvider.getUriForFile(activity, ProviderUtil.getFileProviderName(activity), takeImageFile);
                    List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        activity.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                } else {
                    mCameraUri = uri = createImageUri(activity);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }
        activity.startActivityForResult(takePictureIntent, requestCode);
    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     */
    @RequiresApi(api = VERSION_CODES.Q)
    private Uri createImageUri(Context context) {
        String status = Environment.getExternalStorageState();
        Uri contentUri;
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else {
            contentUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, takeImageFile.getName());
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/camera/");
        return context.getContentResolver().insert(contentUri, contentValues);
    }


    private String signImage = "camera";

    //将文件保存到沙盒中
    //注意：
    //1. 这里的文件操作不再需要申请权限
    //2. 沙盒中新建文件夹只能再系统指定的子文件夹中新建
    public void saveSignImageBox(Context context, String fileName, Bitmap bitmap) {
        try {
            //图片沙盒文件夹
            File PICTURES = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File imageFileDirctory = new File(PICTURES + "/" + signImage);
            if (imageFileDirctory.exists()) {
                File imageFile = new File(PICTURES + "/" + signImage + "/" + fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } else if (imageFileDirctory.mkdir()) {//如果该文件夹不存在，则新建
                //new一个文件
                File imageFile = new File(PICTURES + "/" + signImage + "/" + fileName);
                //通过流将图片写入
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (Exception e) {
        }
    }

    //查询沙盒中的指定图片
//先指定哪个沙盒子文件夹，再指定名称
    public Bitmap querySignImageBox(Context context, String environmentType, String fileName) {
        if (TextUtils.isEmpty(fileName)) return null;
        Bitmap bitmap = null;
        try {
            //指定沙盒文件夹
            File picturesFile = context.getExternalFilesDir(environmentType);
            if (picturesFile != null && picturesFile.exists() && picturesFile.isDirectory()) {
                File[] files = picturesFile.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory() && file.getName().equals(signImage)) {
                            File[] signImageFiles = file.listFiles();
                            if (signImageFiles != null) {
                                for (File signImageFile : files) {
                                    String signFileName = signImageFile.getName();
                                    if (signImageFile.isFile() && fileName.equals(signFileName)) {
                                        bitmap = BitmapFactory.decodeFile(signImageFile.getPath());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static File uriToFileApiQ(Context context, Uri uri) {
        File file = null;
        //android10以上转换
        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            file = new File(uri.getPath());
        } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //把文件复制到沙盒目录
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                try {
                    InputStream is = contentResolver.openInputStream(uri);
                    File cache = null;
                    File imageFileDirctory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/camera");
                    if (imageFileDirctory.exists()) {
                        cache = new File(imageFileDirctory + "/" + displayName);
                    } else if (imageFileDirctory.mkdir()) {
                        cache = new File(imageFileDirctory + "/" + displayName);
                    }
                    FileOutputStream fos = new FileOutputStream(cache);
                    FileUtils.copy(is, fos);
                    file = cache;
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }


    //将文件保存到公共的媒体文件夹
    //这里的filepath不是绝对路径，而是某个媒体文件夹下的子路径，和沙盒子文件夹类似
    //这里的filename单纯的指文件名，不包含路径
    public void saveSignImage(Context context, String filePath, String fileName, Bitmap bitmap) {
        try {
            //设置保存参数到ContentValues中
            ContentValues contentValues = new ContentValues();
            //设置文件名
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            //兼容Android Q和以下版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
                //RELATIVE_PATH是相对路径不是绝对路径
                //DCIM是系统文件夹，关于系统文件夹可以到系统自带的文件管理器中查看，不可以写没存在的名字
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/signImage");
                //contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Music/signImage");
            } else {
                contentValues.put(MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
            }
            //设置文件类型
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/JPEG");
            //执行insert操作，向系统文件夹中添加文件
            //EXTERNAL_CONTENT_URI代表外部存储器，该值不变
            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (uri != null) {
                //若生成了uri，则表示该文件添加成功
                //使用流将内容写入该uri中即可
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                    outputStream.flush();
                    outputStream.close();
                }
            }
        } catch (Exception e) {
        }
    }


    public static File createFile(File folder, String prefix, String suffix) {
        if (!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String filename = prefix + dateFormat.format(new Date(System.currentTimeMillis())) + suffix;
        return new File(folder, filename);
    }


    public static void galleryAddPic(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }


    public interface OnImageSelectedListener {
        void onImageSelected(int position, ImageItem item, boolean isAdd);
    }

    public void addOnImageSelectedListener(OnImageSelectedListener l) {
        if (mImageSelectedListeners == null) mImageSelectedListeners = new ArrayList<>();
        mImageSelectedListeners.add(l);
    }

    public void removeOnImageSelectedListener(OnImageSelectedListener l) {
        if (mImageSelectedListeners == null) return;
        mImageSelectedListeners.remove(l);
    }

    public void addSelectedImageItem(int position, ImageItem item, boolean isAdd) {
        if (isAdd) mSelectedImages.add(item);
        else mSelectedImages.remove(item);
        notifyImageSelectedChanged(position, item, isAdd);
    }

    public void setSelectedImages(ArrayList<ImageItem> selectedImages) {
        if (selectedImages == null) {
            return;
        }
        this.mSelectedImages = selectedImages;
    }

    private void notifyImageSelectedChanged(int position, ImageItem item, boolean isAdd) {
        if (mImageSelectedListeners == null) return;
        for (OnImageSelectedListener l : mImageSelectedListeners) {
            l.onImageSelected(position, item, isAdd);
        }
    }

    public void restoreInstanceState(Bundle savedInstanceState) {
        cropCacheFolder = (File) savedInstanceState.getSerializable("cropCacheFolder");
        takeImageFile = (File) savedInstanceState.getSerializable("takeImageFile");
        imageLoader = (ImageLoader) savedInstanceState.getSerializable("imageLoader");
        style = (CropImageView.Style) savedInstanceState.getSerializable("style");
        multiMode = savedInstanceState.getBoolean("multiMode");
        crop = savedInstanceState.getBoolean("crop");
        showCamera = savedInstanceState.getBoolean("showCamera");
        isSaveRectangle = savedInstanceState.getBoolean("isSaveRectangle");
        selectLimit = savedInstanceState.getInt("selectLimit");
        outPutX = savedInstanceState.getInt("outPutX");
        outPutY = savedInstanceState.getInt("outPutY");
        focusWidth = savedInstanceState.getInt("focusWidth");
        focusHeight = savedInstanceState.getInt("focusHeight");
    }


    public void saveInstanceState(Bundle outState) {
        outState.putSerializable("cropCacheFolder", cropCacheFolder);
        outState.putSerializable("takeImageFile", takeImageFile);
        outState.putSerializable("imageLoader", imageLoader);
        outState.putSerializable("style", style);
        outState.putBoolean("multiMode", multiMode);
        outState.putBoolean("crop", crop);
        outState.putBoolean("showCamera", showCamera);
        outState.putBoolean("isSaveRectangle", isSaveRectangle);
        outState.putInt("selectLimit", selectLimit);
        outState.putInt("outPutX", outPutX);
        outState.putInt("outPutY", outPutY);
        outState.putInt("focusWidth", focusWidth);
        outState.putInt("focusHeight", focusHeight);
    }


    public void setIToaster(Context aContext, InnerToaster.IToaster aIToaster) {
        InnerToaster.obj(aContext).setIToaster(aIToaster);
    }

    public void setFreeCrop(boolean need, FreeCropImageView.CropMode aCropMode) {
        mFreeCropMode = aCropMode;
        isFreeCrop = need;
    }
}