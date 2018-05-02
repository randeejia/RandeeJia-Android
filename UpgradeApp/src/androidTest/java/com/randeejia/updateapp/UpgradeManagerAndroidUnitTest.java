package com.randeejia.updateapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.randeejia.updateapp.bean.UpdateBean;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class UpgradeManagerAndroidUnitTest {

    public static final String TAG = "UpgradeManagerAndroidUnitTest";
    @Before
    public void init() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        UpdateBean updateBean = new UpdateBean("http://dl001.liqucn.com/upload/2014/shenghuo/cn.wecook.app_4.2.0_liqucn.com.apk",2);
        UpgradeManager.init(appContext,updateBean);
    }

    @Test
    public void testUpgrade() {
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
}
