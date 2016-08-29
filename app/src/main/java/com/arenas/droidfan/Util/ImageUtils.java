package com.arenas.droidfan.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Arenas on 2016/7/14.
 */
public class ImageUtils {

    public static final String TAG = ImageUtils.class.getSimpleName();

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

    public static File getPhotoDir(){
        File photoDir = new File(Environment.getExternalStorageDirectory() + "/DCIM/DroidFan");
        if (!photoDir.exists()) {
            photoDir.mkdirs();
        }
        return photoDir;
    }

    public static String getPhotoName(String photoUrl){
        String ext = photoUrl.toLowerCase().endsWith(".gif") ? ".gif" : ".jpg";
        return getPhotoDir().getPath() + "/" + System.currentTimeMillis() + ext;
    }

    public static boolean copyFile(File src, File dest) {
        try {
            FileChannel srcChannel = new FileInputStream(src).getChannel();
            FileChannel destChannel = new FileOutputStream(dest).getChannel();
            srcChannel.transferTo(0, srcChannel.size(), destChannel);//(long position ,long size , WritableByteChannel target)
            srcChannel.close();
            destChannel.close();
            return true;
        } catch (Exception ex) {

        } finally {

        }
        return false;


    }

    public static void saveImage(Context context , Bitmap image , Fragment fragment){
        if (null == image){
            Log.d(TAG , "image == null");
            return;
        }
        File path = new File(Environment.getExternalStorageDirectory() + "/DCIM/DroidFan/" + Utils.getCurTimeStr() + ".png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(path);
            boolean success = image.compress(Bitmap.CompressFormat.PNG , 100 , fos);
            fos.flush();
            fos.close();
            if (success){
                Utils.showToast(context , "已保存");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
