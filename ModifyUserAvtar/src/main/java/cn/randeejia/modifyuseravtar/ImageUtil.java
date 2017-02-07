package cn.randeejia.modifyuseravtar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.lemotion.lemotionutil.LogUtil;
import cn.lemotion.lemotionutil.MD5Util;

/**
 * Created by randeejia on 2017/2/5.
 */

public class ImageUtil {

    private static final String TAG = LogUtil.makeLogTag(ImageUtil.class);
    private static ImageUtil imageUtil;

    private ImageUtil(){}

    public static ImageUtil getInstance(){
        if (imageUtil==null){
            imageUtil=new ImageUtil();
        }
        return imageUtil;
    }

//    public static final String PATH = "huofar";
//    public static final String IMAGE_PATH=PATH+"/image";//用户保存图片路径

    public void saveDrawable(BitmapDrawable drawable, String filename) {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return;
        }
        String sdcard = Environment.getExternalStorageDirectory()
                .getAbsolutePath();

        String fileString = sdcard + "/" + MD5Util.md5(filename);

        File dir = new File(
                fileString.substring(0, fileString.lastIndexOf("/")));
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File image = new File(fileString);
        if (!image.exists()) {
            try {
                image.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(image);
                if (drawable.getBitmap().compress(Bitmap.CompressFormat.PNG,
                        100, fileOutputStream)) {
                    fileOutputStream.flush();
                }
                fileOutputStream.close();
            } catch (IOException e) {
                LogUtil.e(TAG, fileString + " error:" + e.getMessage());
            }
        }
    }

    /**
     * 保存图片到本地，并将path／image路径添加到系统图库
     * @param context 应用上下文
     * @param drawable 要保存的图片
     */
    public void saveImageToGallery(Context context, BitmapDrawable drawable) {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return;
        }
        String sdcard = Environment.getExternalStorageDirectory()
                .getAbsolutePath();

        String fileName=MD5Util.md5(System.currentTimeMillis()+"")+".png";
        String fileString = sdcard + "/" + fileName ;

        File dir = new File(
                fileString.substring(0, fileString.lastIndexOf("/")));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        LogUtil.v(TAG,"图片保存路径："+fileString);
        File image = new File(fileString);
        if (!image.exists()) {
            try {
                image.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(image);
                if (drawable.getBitmap().compress(Bitmap.CompressFormat.PNG,
                        100, fileOutputStream)) {
                    fileOutputStream.flush();
                }
                fileOutputStream.close();
            } catch (IOException e) {
                LogUtil.e(TAG, fileString + " error:" + e.getMessage());
            }
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    image.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://")));
    }

    public BitmapDrawable getDrawable(String filename) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_UNMOUNTED)) {
            return null;
        }
        String sdcard = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        File image = new File(sdcard + "/"+ MD5Util.md5(filename));
        if (image.exists() && image.length() > 1000) {
            try {
                FileInputStream fileInputStream = new FileInputStream(image);
                BitmapDrawable drawable = new BitmapDrawable(fileInputStream);
                return drawable;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getFileName(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }

        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return url;
        }

        String sdcard = Environment.getExternalStorageDirectory()
                .getAbsolutePath();

        String filePath = sdcard + "/";

        String fileUrl = formatName(url);
        if (fileUrl.endsWith(".gif") || fileUrl.endsWith(".GIF")) {
            filePath += ".GIF/" + MD5Util.getMD5Str(fileUrl) + ".gif";
        } else {
            filePath += MD5Util.getMD5Str(fileUrl);
        }

        return filePath;
    }

    public static boolean isExist(String urlString) {
        File file = new File(getFileName(urlString));
        file.getParentFile().mkdirs();
        return file.length() > 1000;

    }

    /**
     * Save image to the SD card*
     *
     * @param photoBitmap
     */
    public static boolean savePhotoToSDCard(Context context, Bitmap photoBitmap) {

        // if sdcard is Exist
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            File photoFile = new File(MyPreference.getInstance(context).getImageCache()); // 在指定路径下创建文件
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
                LogUtil.e(TAG, e.getLocalizedMessage());
            } catch (IOException e) {
                photoFile.delete();
                LogUtil.e(TAG, e.getLocalizedMessage());
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, e.getLocalizedMessage());
                }
            }
            return true;
        }
        return false;
    }

    /**
     * delete photo
     *
     * @param photoName
     */
    public static void deletePhotoFromSdcard(String photoName) {
        if (TextUtils.isEmpty(photoName)) return;
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                String path = getAppDirectory(null) + "/" + photoName;
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getLocalizedMessage());
        }
    }

    /**
     * 从本地获取图片
     *
     * @param name
     * @return
     */
    public static Bitmap getBitmap(String name) {
        try {
            String imgFilePath = getAppDirectory(null) + "/" + name;
            FileInputStream fis = new FileInputStream(new File(imgFilePath));// 文件输入流
            Bitmap bmp = BitmapFactory.decodeStream(fis);
            return bmp;
        } catch (Exception e) {
            LogUtil.e(TAG, e.getLocalizedMessage());
        }
        return null;
    }

    public static String getAppDirectory(Context context) {
//        return Environment.getExternalStorageDirectory().toString();
        return context.getFilesDir().toString();
    }

    private static String formatName(String url) {
        if (url == null || "".equals(url)) {
            return url;
        }
        int start = url.lastIndexOf(".com");

        if (start > 0) {
            start += 5;
        }

        if (start == -1) {
            return url;
        }

        int end = url.indexOf("!");
        if (end == -1) {
            end = url.length();
        }
        return url.substring(start, end);
    }
}
