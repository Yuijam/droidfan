package com.arenas.droidfan.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Arenas on 2016/7/14.
 */
public class ImageUtils {

    public static Bitmap scalePic(Context context , String path , int maxDp){
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path , bfo);
        int maxPx = dip2px(context , maxDp);
        int sampleSize = getSampleSize(bfo.outWidth , bfo.outHeight , maxPx);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(path , options);
        return bitmap;
    }

    public static int getSampleSize(int width , int height , int px){
        int max = Math.max(width , height);
        if (max < px){
            return 1;
        }else {
            return max/px;
        }
    }
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
