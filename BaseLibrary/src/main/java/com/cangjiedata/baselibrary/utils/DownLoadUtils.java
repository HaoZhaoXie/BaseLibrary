/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: DownLoadUtils
 * Author: 星河
 * Date: 2021/4/12 11:13
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.cangjiedata.baselibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.caijia.download.CallbackInfo;
import com.caijia.download.DownloadListener;
import com.caijia.download.FileDownloader;
import com.caijia.download.FileRequest;
import com.cangjiedata.baselibrary.R;
import com.cangjiedata.lib_widget.dialog.AlertDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import static android.os.Environment.MEDIA_MOUNTED;
import static androidx.core.content.FileProvider.getUriForFile;

/**
 * @ClassName: DownLoadUtils
 * @Description:
 * @Author: 星河
 * @Date: 2021/4/12 11:13
 */
public class DownLoadUtils{
    public static void download(Context context, String url, TextView textView) {


        String dir = getFilePath(context);
        File appDir = new File(dir);
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        FileRequest fileRequest = new FileRequest.Builder()
                .url(url)
//                .header("Authorization", "Bearer " + token)
                .build();
        final FileDownloader fileDownloader = new FileDownloader.Builder()
                .threadCount(2)
                .supportBreakPoint(false)
                .saveFileDirPath(dir)
                .fileRequest(fileRequest)
                .debug(true)
                .build();

        fileDownloader.download(new DownloadListener() {
            @Override
            public void onStart(CallbackInfo state) {
                if (textView != null) {
                    textView.setText("开始下载");
                    textView.setEnabled(false);
                }
            }

            @Override
            public void onPrepared(CallbackInfo state) {
                if (textView != null) {
                    textView.setText("准备中...");
                    textView.setEnabled(false);
                }
            }

            @Override
            public void onDownloading(CallbackInfo state) {
                if (textView != null) {
                    if (state != null && state.getFileSize() > 0) {
                        String progress = CalculateUtils.round(CalculateUtils.divide(state.getDownloadSize(), state.getFileSize()) * 100, 2) + "%";
                        textView.setText("已下载：" + progress);
                        textView.setEnabled(false);
                    } else {
                        textView.setText("文件有误");
                    }
                }
            }

            @Override
            public void onComplete(CallbackInfo state) {
                if (textView != null) {
                    textView.setText("查看");
                    textView.setEnabled(true);
                }
                if (context != null) {
                    AlertDialog dialog = new AlertDialog(context).builder();
                    dialog.setTitle("温馨提示");
                    dialog.setMsg("下载成功，是否打开?");
                    dialog.setPositiveButton("打开", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.startActivity(openFile(context, state.getSavePath()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    dialog.setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }

                File file = new File(state.getSavePath());
                if (file != null) {
                    String end = file.getName().substring(file.getName().lastIndexOf(".") + 1).
                            toLowerCase(Locale.getDefault());
                    if (end.contains("jpg") || end.contains("gif") || end.contains("png") || end.contains("jpeg") || end.contains("bmp")) {
                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    }
                }
            }

            @Override
            public void onPausing(CallbackInfo state) {
                if (textView != null) {
                    textView.setText("等待下载");
                }
            }

            @Override
            public void onPause(CallbackInfo state) {
                if (textView != null) {
                    textView.setText("暂停下载");
                }
            }
        });
    }


    /**
     *  
     *
     * @param context 上下文对象
     * @return
     */
    public static String getFilePath(Context context) {
        String directoryPath = "";
        //判断SD卡是否可用 
        if (MEDIA_MOUNTED.contains(Environment.getExternalStorageState())) {
            if (context != null && context.getExternalFilesDir("ican") != null) {
                directoryPath = context.getExternalFilesDir("ican").getAbsolutePath();
            }
        } else {
            //没内存卡就存机身内存  
            directoryPath = context.getFilesDir() + File.separator + "ican";
        }
        File file = new File(directoryPath);
        if (!file.exists()) {//判断文件目录是否存在  
            file.mkdirs();
        }
        return directoryPath;
    }

    /**
     * @return 图片路径
     */
    public static String getImagePath() {
        String path = Environment.getExternalStorageDirectory() + "/iCan/image/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }


    public static int getFileType(File file) {
        /* 取得扩展名 */
        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1).
                toLowerCase(Locale.getDefault());


        if (end.contains("m4a") || end.contains("mp3") || end.contains("mid") || end.contains("xmf") || end.contains("ogg") || end.contains("wav")) {
            return R.mipmap.icon_explain;
        } else if (end.contains("3gp") || end.contains("mp4")) {
            return R.mipmap.icon_explain;
        } else if (end.contains("jpg") || end.contains("gif") || end.contains("png") || end.contains("jpeg") || end.contains("bmp")) {
            return R.mipmap.icon_explain;
        } else if (end.contains("apk")) {
            return R.mipmap.icon_explain;
        } else if (end.contains("ppt") || end.contains("pps") || end.contains("ppam")) {
            return R.mipmap.icon_ppt;
        } else if (end.contains("xls") || end.contains("xlt") || end.contains("xla")) {
            return R.mipmap.icon_excel;
        } else if (end.contains("doc") || end.contains("dot")) {
            return R.mipmap.icon_word;
        } else if (end.contains("pdf")) {
            return R.mipmap.icon_pdf;
        } else if (end.contains("chm")) {
            return R.mipmap.icon_explain;
        } else if (end.contains("txt")) {
            return R.mipmap.icon_explain;
        } else {
            return R.mipmap.icon_explain;
        }
    }

    public static Intent openFile(Context context, String filePath) throws Exception {

        File file = new File(filePath);
        if (!file.exists())
            return null;
        /* 取得扩展名 */
        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).
                toLowerCase(Locale.getDefault());
        /* 依扩展名的类型决定MimeType */
        if (end.contains("m4a") || end.contains("mp3") || end.contains("mid") || end.contains("xmf") || end.contains("ogg") || end.contains("wav")) {
            return getAudioFileIntent(context, filePath);
        } else if (end.contains("3gp") || end.contains("mp4")) {
            return getVideoFileIntent(context, filePath);
        } else if (end.contains("jpg") || end.contains("gif") || end.contains("png") || end.contains("jpeg") || end.contains("bmp")) {
            return getImageFileIntent(context, filePath);
        } else if (end.contains("apk")) {
            return getApkFileIntent(context, filePath);
        } else if (end.contains("ppt")) {
            return getPptFileIntent(context, filePath);
        } else if (end.contains("xls")) {
            return getExcelFileIntent(context, filePath);
        } else if (end.contains("doc")) {
            return getWordFileIntent(context, filePath);
        } else if (end.contains("pdf")) {
            return getPdfFileIntent(context, filePath);
        } else if (end.contains("chm")) {
            return getChmFileIntent(context, filePath);
        } else if (end.contains("txt")) {
            return getTextFileIntent(context, filePath, false);
        } else {
            return getAllIntent(context, filePath);
        }
    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getAllIntent(Context context, String param) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getUriForFile(context, "com.cangjiedata.supperapp.fileProvider", new File(param));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(new File(param));
        }

        intent.setDataAndType(uri, "*/*");
        return intent;
    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getApkFileIntent(Context context, String param) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getUriForFile(context, "com.cangjiedata.supperapp.fileProvider", new File(param));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(new File(param));
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }

    // Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent(Context context, String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getUriForFile(context, "com.cangjiedata.supperapp.fileProvider", new File(param));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(new File(param));
        }
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    // Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent(Context context, String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getUriForFile(context, "com.cangjiedata.supperapp.fileProvider", new File(param));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(new File(param));
        }
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    // Android获取一个用于打开Html文件的intent
    public static Intent getHtmlFileIntent(String param) {
        Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    // Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(Context context, String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getUriForFile(context, "com.cangjiedata.supperapp.fileProvider", new File(param));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(new File(param));
        }

        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    // Android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(Context context, String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getUriForFile(context, "com.cangjiedata.supperapp.fileProvider", new File(param));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(new File(param));
        }
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    // Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(Context context, String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getUriForFile(context, "com.cangjiedata.supperapp.fileProvider", new File(param));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(new File(param));
        }
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    // Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(Context context, String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getUriForFile(context, "com.cangjiedata.supperapp.fileProvider", new File(param));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(new File(param));
        }
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    // Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(Context context, String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getUriForFile(context, "com.cangjiedata.supperapp.fileProvider", new File(param));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(new File(param));
        }
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    // Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(Context context, String param, boolean paramBoolean) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean) {
            Uri uri1 = Uri.parse(param);
            intent.setDataAndType(uri1, "text/plain");
        } else {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = getUriForFile(context, "com.cangjiedata.supperapp.fileProvider", new File(param));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else {
                uri = Uri.fromFile(new File(param));
            }
            intent.setDataAndType(uri, "text/plain");
        }
        return intent;
    }

    // Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(Context context, String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getUriForFile(context, "com.cangjiedata.supperapp.fileProvider", new File(param));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(new File(param));
        }
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    public static String getJsonFromAssets(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}