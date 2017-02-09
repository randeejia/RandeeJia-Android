package cn.randeejia.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by randeejia on 2017/2/8.
 */

public class StorageUtil {

    private static final String IMAGE_BASE_PATH = "image";

    public static String getImageDirectory(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            StringBuffer stringBuffer = new StringBuffer(Environment.getExternalStorageDirectory().getPath());
            stringBuffer.append(File.separator);
            stringBuffer.append(AppInfoUtil.getPackageName(context));
            stringBuffer.append(File.separator);
            stringBuffer.append(IMAGE_BASE_PATH);
            return stringBuffer.toString();
        }
        return "";
    }

    public static File getImageFile(Context context, String imageName) {
        String imageDir = getImageDirectory(context);
        if (!TextUtils.isEmpty(imageDir)) {
            File fileDir = new File(imageDir);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            return new File(imageDir, imageName);
        }
        return null;
    }

    /**
     * Save image to the SD card*
     *
     * @param photoName
     * @param photoBitmap
     */
    public static boolean savePhotoToSDCard(Context context,String photoName, Bitmap photoBitmap) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File photoFile = getImageFile(context,photoName); // 在指定路径下创建文件
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap != null) {
                    if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                            fileOutputStream)) {
                        fileOutputStream.flush();
                    }
                }
            } catch (FileNotFoundException e) {
                photoFile.delete();
            } catch (IOException e) {
                photoFile.delete();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                }
            }
            return true;
        }
        return false;
    }
}
