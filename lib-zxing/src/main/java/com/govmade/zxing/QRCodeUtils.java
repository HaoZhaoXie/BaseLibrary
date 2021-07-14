package com.govmade.zxing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

public class QRCodeUtils {
    private static final Set<BarcodeFormat> PRODUCT_FORMATS;

    static {
        PRODUCT_FORMATS = EnumSet.of(BarcodeFormat.UPC_A,
                BarcodeFormat.UPC_E,
                BarcodeFormat.EAN_13,
                BarcodeFormat.EAN_8,
                BarcodeFormat.RSS_14,
                BarcodeFormat.RSS_EXPANDED,
                BarcodeFormat.CODE_39,
                BarcodeFormat.CODE_93,
                BarcodeFormat.CODE_128,
                BarcodeFormat.ITF,
                BarcodeFormat.CODABAR,
                BarcodeFormat.QR_CODE,
                BarcodeFormat.DATA_MATRIX,
                BarcodeFormat.AZTEC,
                BarcodeFormat.PDF_417,
                BarcodeFormat.MAXICODE,
                BarcodeFormat.UPC_EAN_EXTENSION);
    }

    /**
     * 生成带 logo 的二维码
     *
     * @param text 生成二维码的字符串
     * @param w    生成二维码的宽
     * @param h    生成二维码的高
     * @param logo 生成二维码中间的 logo 如果生成不带 logo 的二维码参数传 null 即可
     * @return 生成二维码的 Bitmap
     */
    public static Bitmap generateImage(String text, int w, int h, String logo) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        try {
            Bitmap scaleLogo = getScaleLogo(getHtmlByteArray(logo), w, h);
            int offsetX = w / 2;
            int offsetY = h / 2;

            int scaleWidth = 0;
            int scaleHeight = 0;
            if (scaleLogo != null) {
                scaleWidth = scaleLogo.getWidth();
                scaleHeight = scaleLogo.getHeight();
                offsetX = (w - scaleWidth) / 2;
                offsetY = (h - scaleHeight) / 2;
            }
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            //设置空白边距的宽度
            hints.put(EncodeHintType.MARGIN, 0);
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, w, h, hints);
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (x >= offsetX && x < offsetX + scaleWidth && y >= offsetY && y < offsetY + scaleHeight) {
                        int pixel = scaleLogo.getPixel(x - offsetX, y - offsetY);
                        if (pixel == 0) {
                            if (bitMatrix.get(x, y)) {
                                pixel = 0xff000000;
                            } else {
                                pixel = 0xffffffff;
                            }
                        }
                        pixels[y * w + x] = pixel;
                    } else {
                        if (bitMatrix.get(x, y)) {
                            pixels[y * w + x] = 0xff000000;
                        } else {
                            pixels[y * w + x] = 0xffffffff;
                        }
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(w, h,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap getScaleLogo(Bitmap logo, int w, int h) {
        if (logo == null) return null;
        Matrix matrix = new Matrix();
        float scaleFactor = Math.min(w * 1.0f / 5 / logo.getWidth(), h * 1.0f / 5 / logo.getHeight());
        matrix.postScale(scaleFactor, scaleFactor);
        return Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), matrix, true);
    }

    public static Bitmap getHtmlByteArray(String path) {
        if (path != null) {
            try {
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
//            return Bitmap.createScaledBitmap(BitmapFactory.decodeStream(input), 150, 150, true);
                return BitmapFactory.decodeStream(input);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 识别图片
     *
     * @param path file:// 或 http:// 资源
     */
    public static String analyzeImage(final String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        if (path.startsWith("http://")) {
            return analyzeBitmap(getImage(path));
        } else {
            //首先判断图片的大小,若图片过大,则执行图片的裁剪操作,防止OOM
            Bitmap mBitmap;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // 先获取原大小
            BitmapFactory.decodeFile(path, options);
            int sampleSize = (int) (options.outHeight / (float) 400);
            if (sampleSize <= 0)
                sampleSize = 1;
            options.inSampleSize = sampleSize;
            options.inJustDecodeBounds = false; // 获取新的大小
            mBitmap = BitmapFactory.decodeFile(path, options);
            mBitmap = zoomImg(mBitmap, mBitmap.getWidth() * 3, mBitmap.getHeight() * 3);
            return analyzeBitmap(mBitmap);
        }
    }

    /**
     * 将网络 http:// 图片转换为 bitmap
     *
     * @param path http:// 路径
     * @return Bitmap
     */
    private static Bitmap getImage(String path) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    /**
     * 解析二维码位图
     *
     * @param bitmap 图
     * @return 结果字符串
     */
    public static String analyzeBitmap(Bitmap bitmap) {
        MultiFormatReader multiFormatReader = new MultiFormatReader();

        // 解码的参数
        Hashtable<DecodeHintType, Object> hints = new Hashtable<>(2);
        // 可以解析的编码类型
        Vector<BarcodeFormat> decodeFormats = new Vector<>();
        if (decodeFormats.isEmpty()) {

            // 这里设置可扫描的类型，这里选择了都支持
            decodeFormats = new Vector<>(PRODUCT_FORMATS);
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        // 设置继续的字符编码格式为UTF8
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
//         设置解析配置参数
        multiFormatReader.setHints(hints);

        // 开始对图像资源解码
        Result rawResult;
        String result = null;
        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            rawResult = multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(width, height, pixels))));
            result = rawResult.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
