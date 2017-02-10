package cn.randeejia.modifyuseravtar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import cn.randeejia.util.StorageUtil;

/**
 * Created by randeejia on 2017/2/5.
 */

public class AppUtil {

    private static final String TAG = "AppUtil";
    private static final String AVATAR_NAME = "avatar.jpg";

    public static final int REQUEST_TAKE_PHOTO = 10001;
    public static final int REQUEST_OPEN_ALBUM = 10002;

    public static final int REQUEST_CLIP_PHOTO = 10003;


    private static AppCallback appCallback;

    public static void setAppCallback(AppCallback appCallback){
        AppUtil.appCallback = appCallback;
    }

    /**
     * 打开系统相册
     */
    public static void goToAlbumOfSystem(FragmentActivity activity, int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 去相机拍照
     */
    public static void goToCamera(FragmentActivity activity, int requestCode) {
        File file = StorageUtil.getImageFile(activity, AVATAR_NAME);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        activity.startActivityForResult(intent, requestCode);
    }


    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    context.getContentResolver(), uri);
            if (bitmap != null) {
                Bitmap smallBitmap = comp(bitmap);
                //释放原始图片占用的内存，防止out of memory异常发生
                bitmap.recycle();
                return smallBitmap;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        return null;
    }

    private static Bitmap comp(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            //如果内存不足
            if (baos.toByteArray().length / 1024 > 500) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
                baos.reset();//重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
            }
        } catch (Exception e) {
            Log.e(TAG, "ERROR===OutOfMemoryError");
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        }
        image.recycle();
        image = null;
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        try {
            baos.close();
            baos = null;
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    private static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        image.recycle();
        image = null;
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }


    /**
     * 缩放Bitmap图片
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
//    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
//        int w = bitmap.getWidth();
//        int h = bitmap.getHeight();
//        Matrix matrix = new Matrix();
//        float scaleWidth = ((float) width / w);
//        float scaleHeight = ((float) height / h);
//        matrix.postScale(scaleWidth, scaleHeight);// 利用矩阵进行缩放不会造成内存溢出
//        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
//        return newbmp;
//    }

    /**
     * 裁剪图片
     */
    public static void clipPhotoActivity(FragmentActivity activity, Uri uri, int requestCode) {
        Intent intent = new Intent(activity, ClipPhotoActivity.class);
        intent.setData(uri);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 裁剪图片
     */
    public static void goToClipPhoto(FragmentActivity activity, Intent data, int requestCode, boolean isFromCamera) {
        Uri uri = null;
        if (isFromCamera) {
            File imageFile = StorageUtil.getImageFile(activity, AVATAR_NAME);
            if (!imageFile.exists()) {
                return;
            }
            uri = Uri.fromFile(imageFile);
        } else {
            if (data != null) {
                uri = data.getData();
            }
        }
        if (uri != null) {
            AppUtil.clipPhotoActivity(activity, uri, requestCode);
        }
    }

    public static void onActivityResult(FragmentActivity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == AppUtil.REQUEST_TAKE_PHOTO) {
            AppUtil.goToClipPhoto(activity, data, AppUtil.REQUEST_CLIP_PHOTO, true);
        } else if (requestCode == AppUtil.REQUEST_OPEN_ALBUM) {
            AppUtil.goToClipPhoto(activity, data, AppUtil.REQUEST_CLIP_PHOTO, false);
        } else if (requestCode == AppUtil.REQUEST_CLIP_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String picName = data.getStringExtra(ClipPhotoActivity.PHOTO_NAME);
                    File file = StorageUtil.getImageFile(activity, picName);
                    if (appCallback !=null){
                        appCallback.onCallback(file);
                    }
                }
            }
        }
    }
}