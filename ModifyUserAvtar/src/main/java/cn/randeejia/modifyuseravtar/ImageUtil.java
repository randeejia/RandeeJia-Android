package cn.randeejia.modifyuseravtar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;

import cn.randeejia.util.LogUtil;

/**
 * Created by randeejia on 2017/2/5.
 */

public class ImageUtil {

    private static final String TAG = LogUtil.makeLogTag(ImageUtil.class);

    public static Bitmap getScaleBitmap(Context context, String filePath) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;

        int bmpWidth = opt.outWidth;
        int bmpHeght = opt.outHeight;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();

        opt.inSampleSize = 1;
        if (bmpWidth > bmpHeght) {
            if (bmpWidth > screenWidth)
                opt.inSampleSize = bmpWidth / screenWidth;
        } else {
            if (bmpHeght > screenHeight)
                opt.inSampleSize = bmpHeght / screenHeight;
        }
        opt.inJustDecodeBounds = false;

        Log.e(TAG,"Bitmap Path = " + filePath);
        File file = new File(filePath);
        Log.e(TAG,"File exist = " + file.exists());

        Bitmap bmp = BitmapFactory.decodeFile(filePath, opt);
        return bmp;
    }
}
