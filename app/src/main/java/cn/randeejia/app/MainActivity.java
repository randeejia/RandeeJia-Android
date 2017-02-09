package cn.randeejia.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;

import cn.randeejia.modifyuseravtar.AppCallback;
import cn.randeejia.modifyuseravtar.AppUtil;
import cn.randeejia.modifyuseravtar.DefaultChoosePhotoWidget;
import cn.randeejia.util.ToastUtil;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onClick(View view){
        DefaultChoosePhotoWidget choosePhotoWidget = new DefaultChoosePhotoWidget(this);
        choosePhotoWidget.show();
        AppUtil.setAppCallback(new AppCallback() {
            @Override
            public void onCallback(File file) {
                ToastUtil.showToast(MainActivity.this,"保存图品");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppUtil.onActivityResult(this,requestCode,resultCode,data);
//        switch (requestCode) {
//            case TAKE_PHOTO_REQUEST_CODE:
//                if (resultCode == RESULT_OK) {
//                    //压缩图片
//                    Bitmap bmp = getScaleBitmap(this, mFile.getAbsolutePath());
//                    Log.e(TAG,"Bitmap is " + bmp);
//                    if (null != bmp) {
//                        mImageView.setImageBitmap(bmp);
//                    }
//                }
//                break;
//        }
    }
}
