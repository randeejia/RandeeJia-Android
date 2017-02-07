package cn.randeejia.app;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;

import cn.randeejia.modifyuseravtar.AppUtil;
import cn.randeejia.modifyuseravtar.DefaultChoosePhotoWidget;
import cn.randeejia.modifyuseravtar.MyPreference;
import cn.randeejia.util.ToastUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view){
        DefaultChoosePhotoWidget choosePhotoWidget = new DefaultChoosePhotoWidget(this);
        choosePhotoWidget.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppUtil.REQUEST_TAKE_PHOTO) {
            goToZoomPhoto(this, data,AppUtil.REQUEST_CLIP_PHOTO, true);
        } else if (requestCode == AppUtil.REQUEST_OPEN_ALBUM) {
            goToZoomPhoto(this, data, AppUtil.REQUEST_CLIP_PHOTO, false);
        }else if (requestCode == AppUtil.REQUEST_CLIP_PHOTO){
            ToastUtil.showToast(this,"获取图片");
        }
    }

    /**
     * 裁剪图片
     *
     * @param data
     * @param requestCode
     */
    public static void goToZoomPhoto(FragmentActivity activity, Intent data, int requestCode, boolean isFromCamera) {
        Uri uri = null;
        if (isFromCamera) {
            File picture = new File( MyPreference.getInstance(activity).getImageCache());
            if(!picture.isFile()){
                return;
            }
            uri = Uri.fromFile(picture);
        } else {
            if (data != null) {
                uri = data.getData();
            }
        }
        if (uri != null){
            AppUtil.clipPhotoActivity(activity, uri, requestCode);
        }
    }
}
