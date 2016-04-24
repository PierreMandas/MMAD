package com.bignerdranch.android.tingle.PictureUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class PictureUtils {
    /**
     * This method loads in a picture and scales it down. Afterwards a bitmap is created
     * to draw and display the picture.
     *
     * @param path - Path of the picture
     * @param destWidth - how much width the picture should have
     * @param destHeight - how much height the picture should have
     * @return - Returns a bitmap where the picture is drawed on.
     */
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        // Read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // Figure out how much to scale down by
        int inSampleSize = 1;
        if(srcHeight > destHeight || srcWidth > destWidth) {
            if(srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        // Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * This method makes use of the other getScaledBitmap method. It gets the size of a
     * default display form the window manager and scales the picture down according to the
     * x (width) and y (height) values.
     *
     * @param path - Path of the picture. Used to call the other getScaledBitmap method.
     * @param activity - Activity to get default display width and height from.
     * @return - Returns a call to the other getScaledBitmap method, which is a Bitmap.
     */
    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }
}
