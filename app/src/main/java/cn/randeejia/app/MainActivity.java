package cn.randeejia.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.randeejia.updateapp.UpgradeManager;
import com.randeejia.updateapp.bean.UpdateBean;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UpgradeManager.init(this);

        UpdateBean updateBean = new UpdateBean("http://dl001.liqucn.com/upload/2014/shenghuo/cn.wecook.app_4.2.0_liqucn.com.apk",2);
        UpgradeManager.getInstance().setUpdateBean(updateBean);

        UpgradeManager.getInstance().upgrade(new UpgradeManager.UpgradeCallback() {
            @Override
            public void onStart() {
                Log.e(TAG,"onStart");
            }

            @Override
            public void onUpgrade() {
                Log.e(TAG,"onUpgrade");
            }

            @Override
            public void onUpgradeProgress(int progress) {
                Log.e(TAG,"onUpgrade :" + progress);
            }

            @Override
            public void onSuccess() {
                Log.e(TAG,"onSuccess");
            }

            @Override
            public void onFailed(String errMsg) {
                Log.e(TAG,"onFailed:"+errMsg);
            }

            @Override
            public void onEnd() {
                Log.e(TAG,"onEnd");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
