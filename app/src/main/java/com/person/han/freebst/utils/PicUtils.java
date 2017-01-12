package com.person.han.freebst.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.Base64;
import java.io.ByteArrayOutputStream;

public class PicUtils {
    /**
     * 图片转成string
     *
     * @param  bitmap @Bitmap
     *
     * @return string
     */
    public static String convertIconToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);
    }

    /**
     * string转成bitmap
     *
     * @param st @String
     * @return bitmap or null
     */
    @Nullable
    public static Bitmap convertStringToIcon(String st) {
        // OutputStream out;
        Bitmap bitmap;
        try {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap =
                    BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据原图和变长绘制圆形图片
     *
     * @return bitmap
     */
    public static Bitmap createCircleImage(String bitmapStr)
    {
        Bitmap source=PicUtils.convertStringToIcon(bitmapStr);
        int min;
        if (source!=null){
            min = source.getWidth();
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
            /**
             * 产生一个同样大小的画布
             */
            Canvas canvas = new Canvas(target);
            /**
             * 首先绘制圆形
             */
            canvas.drawCircle(min / 2, min / 2, min / 2, paint);
            /**
             * 使用SRC_IN
             */
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            /**
             * 绘制图片
             */
            canvas.drawBitmap(source, 0, 0, paint);
            return target;
        }
        return source;
    }

    /**
     * 根据原图和变长绘制圆形图片
     *
     * @return bitmap
     */
    public static Bitmap createCircleImage(Bitmap source) {
        int min;
        if (source != null) {
            min = source.getWidth();
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
            /**
             * 产生一个同样大小的画布
             */
            Canvas canvas = new Canvas(target);
            /**
             * 首先绘制圆形
             */
            canvas.drawCircle(min / 2, min / 2, min / 2, paint);
            /**
             * 使用SRC_IN
             */
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            /**
             * 绘制图片
             */
            canvas.drawBitmap(source, 0, 0, paint);
            return target;
        }
        return source;
    }
}