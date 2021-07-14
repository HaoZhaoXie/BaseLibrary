/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: GlideUtil
 * Author: 星河
 * Date: 2021/1/6 11:23
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.cangjiedata.baselibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cangjiedata.baselibrary.R;
import com.lzy.imagepicker.loader.ImageLoader;

import java.io.File;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * @ClassName: GlideUtil
 * @Description:
 * @Author: 星河
 * @Date: 2021/1/6 11:23
 */
public class GlideUtil implements ImageLoader {
    public static void loadImageWithSizeAllRound(Context context, String url, ImageView iv, int size) {
        if (TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(R.mipmap.icon_default_image)
                    .apply(bitmapTransform(new RoundedCornersTransformation(UtilsKt.dip2px(size), 0,
                            RoundedCornersTransformation.CornerType.ALL))).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(iv);
            return;
        }
        if (!url.startsWith("http")) {
            url = "http:" + url;
        }
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                .centerCrop().error(R.mipmap.icon_default_image)
                .placeholder(R.mipmap.icon_default_image)
                .apply(bitmapTransform(new RoundedCornersTransformation(UtilsKt.dip2px(size), 0,
                        RoundedCornersTransformation.CornerType.ALL))).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv);
    }
    public static void loadImageWithSizeTopRound(Context context, String url, ImageView iv, int size) {
        if (TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(R.mipmap.icon_default_image)
                    .apply(bitmapTransform(new RoundedCornersTransformation(UtilsKt.dip2px(size), 0,
                            RoundedCornersTransformation.CornerType.TOP))).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(iv);
            return;
        }
        if (!url.startsWith("http")) {
            url = "http:" + url;
        }
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                .centerCrop().error(R.mipmap.icon_default_image)
                .placeholder(R.mipmap.icon_default_image)
                .apply(bitmapTransform(new RoundedCornersTransformation(UtilsKt.dip2px(size), 0,
                        RoundedCornersTransformation.CornerType.ALL))).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv);
    }
    public static void loadImage(Context context, String url, ImageView iv, int size) {
        if (TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(R.mipmap.pic_loading_9)
                    .apply(bitmapTransform(new RoundedCornersTransformation(UtilsKt.dip2px(size), 0,
                            RoundedCornersTransformation.CornerType.ALL))).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(iv);
            return;
        }
        if (!url.startsWith("http")) {
            url = "http:" + url;
        }
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                .error(R.mipmap.icon_default_image)
                .placeholder(R.mipmap.icon_default_image)
                .apply(bitmapTransform(new RoundedCornersTransformation(UtilsKt.dip2px(size), 0,
                        RoundedCornersTransformation.CornerType.ALL)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv);
    }


    public static void loadLocalImage(Context context, int url, ImageView iv, int size) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                .error(R.mipmap.icon_default_image)
                .placeholder(R.mipmap.icon_default_image)
                .apply(bitmapTransform(new RoundedCornersTransformation(UtilsKt.dip2px(size), 0,
                        RoundedCornersTransformation.CornerType.ALL)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv);
    }



    public static void loadImageWithCircle(Context context, String url, ImageView iv) {
        if (TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(R.mipmap.icon_default_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .into(iv);
            return;
        }
        if (!url.startsWith("http")) {
            url = "http:" + url;
        }
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                .error(R.mipmap.icon_default_image)
                .placeholder(R.mipmap.icon_default_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop()
                .into(iv);
    }

    public static void loadImageWithNoPlace(Context context, String url, ImageView iv, int size) {
        if (TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(R.mipmap.icon_default_image)
                    .apply(bitmapTransform(new RoundedCornersTransformation(UtilsKt.dip2px(size), 0,
                            RoundedCornersTransformation.CornerType.ALL))).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(iv);
            return;
        }
        if (!url.startsWith("http")) {
            url = "http:" + url;
        }
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                .error(R.mipmap.icon_default_image)
                .placeholder(R.mipmap.icon_default_image)
                .apply(bitmapTransform(new RoundedCornersTransformation(UtilsKt.dip2px(size), 0,
                        RoundedCornersTransformation.CornerType.ALL)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv);
    }

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        if (path == null) {
            return;
        }
        if (path.contains("http")) {
            Glide.with(activity)
                    .load(path)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                    .error(R.mipmap.icon_default_image)
                    .placeholder(R.mipmap.icon_default_image)
                    .apply(bitmapTransform(new RoundedCornersTransformation(UtilsKt.dip2px(8), 0,
                            RoundedCornersTransformation.CornerType.ALL)))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        } else {
            Glide.with(activity)                             //配置上下文
                    .load(Uri.fromFile(new File(path)))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .error(R.mipmap.icon_default_image)           //设置错误图片
                    .placeholder(R.mipmap.icon_default_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                    .apply(bitmapTransform(new RoundedCornersTransformation(UtilsKt.dip2px(8),
                            0))).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }

    }

    @Override
    public void displayImagePreview(Activity activity, String path, ImageView imageView, int width, int height) {
        if (path == null) {
            return;
        }
        if (path.contains("http")) {
            Glide.with(activity)
                    .load(path)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                    .error(R.mipmap.icon_default_image)
                    .placeholder(R.mipmap.icon_default_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        } else {
            Glide.with(activity)                             //配置上下文
                    .load(Uri.fromFile(new File(path)))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .error(R.mipmap.icon_default_image)           //设置错误图片
                    .placeholder(R.mipmap.icon_default_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                    .into(imageView);
        }

    }

    @Override
    public void clearMemoryCache() {

    }
}